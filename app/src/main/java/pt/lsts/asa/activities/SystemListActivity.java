package pt.lsts.asa.activities;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.fragments.ChangeActivityButtonFragment;
import pt.lsts.asa.fragments.SystemListFragment;
import pt.lsts.asa.util.AndroidUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by jloureiro on 1/5/15.
 */
public class SystemListActivity extends FragmentActivity {

    public static final String TAG="SystemListActivity";
    private SystemListFragment systemListFragment = null;
    private ChangeActivityButtonFragment settingsButtonFragment = null;

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
            case R.id.select_as_active_system_system_list_menu:
                systemListFragment.selectActiveSystem();
                return true;
            case R.id.more_info_system_list_menu:
                systemListFragment.showMoreInfo();
                return true;
            case R.id.refresh_system_list_menu:
                systemListFragment.populateSystemListView();
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

            settingsButtonFragment = new ChangeActivityButtonFragment(this,SettingsActivity.class,R.layout.fragment_settings_button,R.id.settingsButton);
            AndroidUtil.loadFragment(this, settingsButtonFragment,R.id.fragment_container_system_list);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        ASA.getInstance().setMode(ASA.MODE.SYSTEMLIST);
    }

    @Override
    protected void onPause(){
        Log.i(TAG, "SettingsActivity.onPause() called");
        //AndroidUtil.removeAllFragments(this);
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if (systemListFragment!=null)
            systemListFragment.shutdown();
        startMainActivity();
    }

    public void startMainActivity(){
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(i);
    }

}