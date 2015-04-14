package pt.lsts.asa.activities;

import pt.lsts.asa.ASA;
import pt.lsts.asa.fragments.AltBarFragment;
import pt.lsts.asa.fragments.BatteryIndicatorFragment;
import pt.lsts.asa.fragments.ChangeActiveSysDialogButtonFragment;
import pt.lsts.asa.fragments.ChangeActivityButtonFragment;
import pt.lsts.asa.fragments.GmapFragment;
import pt.lsts.asa.R;
import pt.lsts.asa.fragments.CentralTextViewFragment;
import pt.lsts.asa.util.AndroidUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Subscribe;


public class AutoActivity extends FragmentActivity {

    public static final String TAG = "AutoActivity";
    private GmapFragment gmapFragment = null;
    private ChangeActivityButtonFragment settingsButtonFragment = null;
    private ChangeActivityButtonFragment manualButtonFragment = null;
    private ChangeActiveSysDialogButtonFragment changeActiveSysDialogButtonFragment=null;
    private BatteryIndicatorFragment batteryIndicatorFragment=null;
    private AltBarFragment altBarFragment =null;
    private CentralTextViewFragment centralTextViewFragment =null;

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
        ASA.getInstance().getBus().unregister(this);
        finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
        ASA.getInstance().setMode(ASA.MODE.AUTO);
        loadFragments(null);//always load fragments from null
        ASA.getInstance().getBus().register(this);
    }

	public void loadFragments(Bundle savedInstanceState) {
		if (findViewById(R.id.fragment_container_auto) != null) {
			if (savedInstanceState != null) {
				return;// restoring state
			}

            gmapFragment = new GmapFragment();
            AndroidUtil.loadFragment(this, gmapFragment, R.id.fragment_container_auto);

            settingsButtonFragment = new ChangeActivityButtonFragment();
            Bundle args = new Bundle();
            args.putInt("layout",R.layout.fragment_settings_button_with_margin);
            args.putInt("id",R.id.settingsButtonWithMargin);
            args.putSerializable("class",SettingsActivity.class);
            settingsButtonFragment.setArguments(args);
            AndroidUtil.loadFragment(this, settingsButtonFragment,R.id.fragment_container_auto);

            manualButtonFragment = new ChangeActivityButtonFragment();
            args=null;
            args = new Bundle();
            args.putInt("layout",R.layout.fragment_manual_button);
            args.putInt("id",R.id.manualButton);
            args.putSerializable("class",ManualActivity.class);
            manualButtonFragment.setArguments(args);
            AndroidUtil.loadFragment(this, manualButtonFragment,R.id.fragment_container_auto);

            changeActiveSysDialogButtonFragment = new ChangeActiveSysDialogButtonFragment();
            AndroidUtil.loadFragment(this, changeActiveSysDialogButtonFragment,R.id.fragment_container_auto);

            batteryIndicatorFragment = new BatteryIndicatorFragment();
            AndroidUtil.loadFragment(this, batteryIndicatorFragment,R.id.fragment_container_auto);

            altBarFragment = new AltBarFragment();
            AndroidUtil.loadFragment(this, altBarFragment,R.id.fragment_container_auto);

            centralTextViewFragment = new CentralTextViewFragment();
            AndroidUtil.loadFragment(this, centralTextViewFragment,R.id.fragment_container_auto);

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

    @Subscribe
    public void showToast(final Toast toast){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast.show();
            }
        });
    }

}
