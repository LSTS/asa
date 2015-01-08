package pt.lsts.asa.activities;

import pt.lsts.asa.R;
import pt.lsts.asa.fragments.SettingsButtonFragment;
import pt.lsts.asa.fragments.SystemListFragment;
import pt.lsts.asa.util.AndroidUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by jloureiro on 1/5/15.
 */
public class SystemListActivity extends FragmentActivity {

    private SystemListFragment systemListFragment = null;
    private SettingsButtonFragment settingsButtonFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_system_list);

        loadFragments(savedInstanceState);
    }


    public void loadFragments(Bundle savedInstanceState){

        if (findViewById(R.id.fragment_container_system_list) != null) {
            if (savedInstanceState != null) {
                return;// restoring state
            }

            systemListFragment = new SystemListFragment(this);
            AndroidUtil.loadFragment(this, systemListFragment, R.id.fragment_container_system_list);

            settingsButtonFragment = new SettingsButtonFragment(this);
            AndroidUtil.loadFragment(this,settingsButtonFragment,R.id.fragment_container_system_list);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        systemListFragment.shutdown();
    }
}