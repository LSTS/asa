package pt.lsts.asa.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.util.AndroidUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeActiveSysDialogButtonFragment extends Fragment {

    public static final String TAG = "ChooseActiveSysDialogButtonFragment";
    private FragmentActivity fragmentActivity=null;
    private Button changeActiveSysButton = null;

    public ChangeActiveSysDialogButtonFragment() {
        // Required empty public constructor
    }

    public ChangeActiveSysDialogButtonFragment(FragmentActivity fragmentActivity){
        this.fragmentActivity=fragmentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_active_sys_dialog_button, container, false);
        changeActiveSysButton = (Button) v.findViewById(R.id.changeActiveSysButton);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        changeActiveSysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeActiveSysDialog();
            }
        });
    }

    public void showChangeActiveSysDialog(){

        final ArrayList<Sys> arrayListSys = ASA.getInstance().getSystemList().getList();
        final ArrayList<String> arrayListName = ASA.getInstance().getSystemList().getNameList();
        final String[] array = new String[arrayListName.size()];
        for (int i=0;i<array.length;i++){
            array[i]=arrayListName.get(i);
        }

        fragmentActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(fragmentActivity).setTitle("Choose a System:")
                                .setItems(array, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Sys sys = ASA.getInstance().getActiveSys();
                                        ASA.getInstance().setActiveSys(arrayListSys.get(which));
                                        AndroidUtil.showToastShort(fragmentActivity,"Active Sys: "+ASA.getInstance().getActiveSys().getName());
                                        if (!ASA.getInstance().getActiveSys().equals(sys)){
                                            Intent intent = fragmentActivity.getIntent();
                                            fragmentActivity.finish();
                                            startActivity(intent);
                                        }
                                    }
                                }).create().show();
                    }
                }
        );
    }

}
