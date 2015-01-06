package pt.lsts.asa.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import pt.lsts.asa.R;
import pt.lsts.asa.fragments.SystemListFragment;

/**
 * Created by jloureiro on 1/5/15.
 */
public class SystemListActivity extends FragmentActivity {

    private SystemListFragment systemListFragment = null;

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
            loadFragment(systemListFragment);
        }
    }

    public void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_system_list,
                        fragment).commit();
    }
}