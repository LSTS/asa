package pt.lsts.asa.activities;

import pt.lsts.asa.R;
import pt.lsts.asa.fragments.SettingsButtonFragment;
import pt.lsts.asa.fragments.SystemListFragment;
import pt.lsts.asa.util.AndroidUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.more_info_menu:
                return true;
            case R.id.select_as_active_system_menu:
                systemListFragment.selectActiveSystem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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