package pt.lsts.asa.activities;

import pt.lsts.asa.ASA;
import pt.lsts.asa.fragments.AltitudeBarFragment;
import pt.lsts.asa.fragments.BatteryIndicatorFragment;
import pt.lsts.asa.fragments.ChangeActiveSysDialogButtonFragment;
import pt.lsts.asa.fragments.ChangeActivityButtonFragment;
import pt.lsts.asa.fragments.GmapFragment;
import pt.lsts.asa.R;
import pt.lsts.asa.util.AndroidUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


public class AutoActivity extends FragmentActivity {

    public static final String TAG = "AutoActivity";
    private GmapFragment gmapFragment = null;
    private ChangeActivityButtonFragment settingsButtonFragment = null;
    private ChangeActivityButtonFragment manualButtonFragment = null;
    private ChangeActiveSysDialogButtonFragment changeActiveSysDialogButtonFragment=null;
    private BatteryIndicatorFragment batteryIndicatorFragment=null;
    private AltitudeBarFragment altitudeBarFragment=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_auto);

		//loadFragments(savedInstanceState);

	}

    @Override
    protected void onPause(){
        Log.i(TAG, "AutoActivity.onPause() called");
        AndroidUtil.removeAllFragments(this);
        super.onPause();
        finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
        ASA.getInstance().setMode(ASA.MODE.AUTO);
        loadFragments(null);//always load fragments from null
    }

	public void loadFragments(Bundle savedInstanceState) {
		if (findViewById(R.id.fragment_container_auto) != null) {
			if (savedInstanceState != null) {
				return;// restoring state
			}

            gmapFragment = new GmapFragment(this);
            AndroidUtil.loadFragment(this, gmapFragment, R.id.fragment_container_auto);

            settingsButtonFragment = new ChangeActivityButtonFragment(this,SettingsActivity.class,R.layout.fragment_settings_button_with_margin,R.id.settingsButtonWithMargin);
            AndroidUtil.loadFragment(this, settingsButtonFragment,R.id.fragment_container_auto);

            manualButtonFragment = new ChangeActivityButtonFragment(this,ManualActivity.class,R.layout.fragment_manual_button,R.id.manualButton);
            AndroidUtil.loadFragment(this, manualButtonFragment,R.id.fragment_container_auto);

            changeActiveSysDialogButtonFragment = new ChangeActiveSysDialogButtonFragment(this);
            AndroidUtil.loadFragment(this, changeActiveSysDialogButtonFragment,R.id.fragment_container_auto);

            batteryIndicatorFragment = new BatteryIndicatorFragment(this);
            AndroidUtil.loadFragment(this, batteryIndicatorFragment,R.id.fragment_container_auto);

            altitudeBarFragment = new AltitudeBarFragment(this);
            AndroidUtil.loadFragment(this, altitudeBarFragment,R.id.fragment_container_auto);

		}
	}

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startMainActivity();
    }

    public void startMainActivity(){
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(i);
    }


}
