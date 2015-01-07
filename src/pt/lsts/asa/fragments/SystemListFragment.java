package pt.lsts.asa.fragments;

import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.sys.Sys;

/**
 * Created by jloureiro on 1/5/15.
 */
public class SystemListFragment extends Fragment {

    private FragmentActivity fragmentActivity = null;
    private ListView systemListView = null;
    private String TAG = "SystemListFragment";

    private ScheduledFuture handle;
    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);
    private Runnable runnable;
    private int interval=2500;

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
        //showToastShort("populating...");
        Log.i(TAG, "populating");

        ArrayList<String> arrayListName = ASA.getInstance().getSystemList().getNameList();
        if (arrayAdapter == null) {
            createListViewAdapter(arrayListName);
            setListViewOnItemClickListener();
        } else {
            updateListView(arrayListName);
        }
    }

    public void createListViewAdapter(ArrayList<String> arrayListName){
        arrayAdapter = new ArrayAdapter<String>(
                fragmentActivity,
                android.R.layout.simple_list_item_1,
                arrayListName);
        systemListView.setAdapter(arrayAdapter);
    }

    public void setListViewOnItemClickListener(){
        systemListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                final ArrayList<Sys> arrayListSys = ASA.getInstance().getSystemList().getList();
                ASA.getInstance().setActiveSys(arrayListSys.get(position));
                showToastLong("Active System: "+ASA.getInstance().getActiveSys().getName());
            }
        });
    }

    public void updateListView(final ArrayList<String> arrayListNameFinal){
        fragmentActivity.runOnUiThread(new Runnable() {
            public void run() {
                arrayAdapter.clear();
                arrayAdapter.addAll(arrayListNameFinal);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    public void showToastShort(final String msg){
        fragmentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(fragmentActivity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showToastLong(final String msg){
        fragmentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(fragmentActivity, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

}