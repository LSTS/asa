package pt.lsts.asa.fragments;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.settings.Settings;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.asa.util.StringUtils;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by jloureiro on 1/5/15.
 */
public class SystemListFragment extends Fragment {

    private FragmentActivity fragmentActivity = null;
    private ListView systemListView = null;
    private int selectedInt = -1;
    private Button selectActiveSystemButton = null;
    private String TAG = "SystemListFragment";

    private ScheduledFuture handle;
    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);
    private Runnable runnable;
    private int interval=100;

    ArrayAdapter<String> arrayAdapter = null;

    public SystemListFragment(){
        // Required empty public constructor
    }

    public SystemListFragment(FragmentActivity fragmentActivity){
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_system_list_view, container, false);
        systemListView = (ListView) v.findViewById(R.id.systemListView);
        selectActiveSystemButton = (Button) v.findViewById(R.id.selectAsActiveButton);
        setSelectActiveSystemButtonOnClickListener();
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        //populateSystemListView();
        initScheduler();
    }

    @Override
    public void onPause(){
        super.onPause();
        shutdown();
    }

    public void shutdown(){
        if (handle!=null)
            handle.cancel(true);
    }

    public void setSelectActiveSystemButtonOnClickListener(){
        selectActiveSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActiveSystem();
            }
        });
    }

    public void initScheduler(){
        runnable = new Runnable() {
            @Override
            public void run() {
                populateSystemListView();
            }
        };
        if (handle!=null)
            handle.cancel(true);
        handle = scheduler.scheduleAtFixedRate(runnable, 0,
                interval, TimeUnit.MILLISECONDS);
    }

    public void populateSystemListView() {
        Log.i(TAG, "populating");

        ArrayList<String> arrayListName = addSysStatus();
        if (arrayAdapter == null) {
            createListViewAdapter(arrayListName);
            setListViewOnItemClickListener();
        } else {
            updateListView(arrayListName);
        }
    }

    public String showMoreInfo(){

        String sysName = StringUtils.removeSysExtraInfo(systemListView.getAdapter().getItem(selectedInt).toString());
        Sys sys = ASA.getInstance().getSystemList().findSysByName(sysName);
        if (sys==null){
            AndroidUtil.showToastLong(fragmentActivity,"sys==null");
            return "";
        }

        String message = "\n";
        message += "Address: "+sys.getAddress();
        message += "\n";
        message += StringUtils.timeSinceLastMessage(System.currentTimeMillis(), sys.lastMessageReceived);;
        message += "\n";

        if (!sys.isConnected()) {
            message += "Connected: " + sys.isConnected();
            message += "\n";
        }
        if (sys.isError()) {
            message += " Error: " + sys.isError();
            message += "\n";
        }
        return message;

    }

    public ArrayList<String> addSysStatus(){
        ArrayList<String> arrayListName = ASA.getInstance().getSystemList().getNameList();
        ArrayList<String> newArrayListName = new ArrayList<>();
        final ArrayList<Sys> arrayListSys = ASA.getInstance().getSystemList().getList();

        int nSys=arrayListSys.size();
        if (arrayListSys.size()>arrayListName.size())
            nSys=arrayListName.size();

        for (int i =0;i<nSys;i++){
            Sys sys = arrayListSys.get(i);
            if (filterSys(sys))
                continue;
            String s = "";
            s += arrayListName.get(i);
            s += " | ";
            s += sys.getType();
            if(sys.equals(ASA.getInstance().getActiveSys())) {
                s += " (M)";
            }
            if (i==selectedInt){
                s += showMoreInfo();
            }

            newArrayListName.add(s);
        }
        return newArrayListName;
    }

    public boolean filterSys(Sys sys){
        if (!Settings.getBoolean("systems_hide_non_vehicles",false)){
            return false;
        }else{
            String[] sysList = {"UUV", "USV", "UAV", "UGV"};
            for (String s :sysList){
                if (s.equalsIgnoreCase(sys.getType())) {
                    return false;
                }
            }
            return true;
        }
    }

    public void createListViewAdapter(ArrayList<String> arrayListName){
        arrayAdapter = new ArrayAdapter<String>(
                fragmentActivity,
                android.R.layout.simple_list_item_1,
                arrayListName);
        systemListView.setAdapter(arrayAdapter);
        colorItems();
    }

    public void setListViewOnItemClickListener(){
        systemListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                if (selectedInt==position){
                    selectActiveSystemButton.setVisibility(View.INVISIBLE);
                    selectedInt=-1;
                }else{
                    selectedInt=position;
                    selectActiveSystemButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void selectActiveSystem(){
        if (selectedInt<0 || selectedInt>=systemListView.getAdapter().getCount()) {
            AndroidUtil.showToastLong(fragmentActivity,"ERROR no system selected");
            return;
        }
        String sysName = systemListView.getAdapter().getItem(selectedInt).toString();
        sysName = StringUtils.removeSysExtraInfo(sysName);
        ASA.getInstance().setActiveSys(ASA.getInstance().getSystemList().findSysByName(sysName));
        if (ASA.getInstance().getActiveSys()==null){
            AndroidUtil.showToastLong(fragmentActivity,"ERROR no system selected");
            return;
        }
        AndroidUtil.showToastLong(fragmentActivity,"Active System: "+ASA.getInstance().getActiveSys().getName());
        selectedInt = -1;
        selectActiveSystemButton.setVisibility(View.INVISIBLE);
    }

    public void updateListView(final ArrayList<String> arrayListNameFinal){
        fragmentActivity.runOnUiThread(new Runnable() {
            public void run() {
                arrayAdapter.clear();
                arrayAdapter.addAll(arrayListNameFinal);
                arrayAdapter.notifyDataSetChanged();
                colorItems();
            }
        });
    }

    public void colorItems(){

        //color codes
        final String ORANGE = "#FF8000";//orange
        final String RED = "#FF1428";//red
        final String GREEN = "#006400";//green
        final String BLUE = "#2BB6E3";//cyan
        final String BLUE_LIGHTER = "#B5C6D8";//very light blue
        final String BLUE_DARKER = "#15596F";//dark blue
        final String OLD = "#253F3F";//gray
        final String IDLE = "#6E6E6E";//brighter gray
        final String BLACK = "#000000";//black
        final String WHITE = "#FFFFFF";//white

        final ArrayList<Sys> arrayListSys = ASA.getInstance().getSystemList().getList();
        int nSys=arrayListSys.size();
        if (arrayListSys.size()>systemListView.getAdapter().getCount())
            nSys=systemListView.getAdapter().getCount();
        for (int i =0;i<nSys;i++){
            if (i==selectedInt)
                continue;
            String sysName = systemListView.getAdapter().getItem(i).toString();
            sysName = StringUtils.removeSysExtraInfo(sysName);
            Sys sys = ASA.getInstance().getSystemList().findSysByName(sysName);

            TextView textView = (TextView) systemListView.getChildAt(i);

            //generic color for text
            String textColorCode = BLACK;//#000000

            String backgroundColorCode = IDLE;//#6E6E6E
            if (sys.getType().equalsIgnoreCase("CCU")){
                textColorCode = "#FFFFFF";//white
            }


            if (!sys.getType().equalsIgnoreCase("CCU")) {
                if (sys.isConnected() && sys.isError())
                    backgroundColorCode = ORANGE;//#FF8000
                if (sys.isConnected() && !sys.isError())
                    backgroundColorCode = BLUE;//#2BB6E3
                if (!sys.isConnected() && sys.isError())
                    backgroundColorCode = RED;//#FF1428
                if (!sys.isConnected() && !sys.isError()) {
                    backgroundColorCode = BLUE_DARKER;//#15596F
                    textColorCode = "#FFFFFF";//white
                }
            }

            try {
                textView.setTextColor(Color.parseColor(textColorCode));
                textView.setBackgroundColor(Color.parseColor(backgroundColorCode));
            }catch (Exception e){
                String message = "error in setTextColor/setBackgroudColor";
                if (e.getMessage()!=null)
                    message += ": "+e.getMessage();
                Log.e(TAG,message);
            }

        }
    }

}