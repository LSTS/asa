package pt.lsts.asa.activities;

import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.feedback.CallOutService;
import pt.lsts.asa.fragments.BatteryIndicatorFragment;
import pt.lsts.asa.fragments.ChangeActiveSysDialogButtonFragment;
import pt.lsts.asa.fragments.ChangeActivityButtonFragment;
import pt.lsts.asa.fragments.ManualIndicatorsFragment;
import pt.lsts.asa.fragments.SoundControlFragment;
import pt.lsts.asa.fragments.VideoViewFragment;
import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.util.AndroidUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class ManualActivity extends FragmentActivity {

    public static final String TAG = "ManualActivity";

    private SoundControlFragment soundControlFragment = null;
    private VideoViewFragment videoViewFragment = null;
    private ChangeActivityButtonFragment settingsButtonFragment = null;
    private ChangeActivityButtonFragment autoButtonFragment=null;
    private ManualIndicatorsFragment manualIndicatorsFragment = null;
    private ChangeActiveSysDialogButtonFragment changeActiveSysDialogButtonFragment=null;
    private BatteryIndicatorFragment batteryIndicatorFragment=null;

    private CallOut callOut;
    private CallOutService callOutService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_manual);

        //callOut = new CallOut(ASA.getContext());
        //callOut.initCallOuts();
        //loadFragments(savedInstanceState);
        // ASA.getInstance().getCallOut().startCallOuts();
    }

    @Override
    public void onResume(){
        super.onResume();
        ASA.getInstance().setMode(ASA.MODE.MANUAL);
        loadFragments(null);//always load fragments from null
        soundControlFragment.unmute();

        callOutService = new CallOutService(this);
        Intent intent = new Intent(this,CallOutService.class);
        callOutService.onStartCommand(intent,1,0);
        callOutService.onBind(intent);

        //AndroidUtil.showToastShort("onResume()");
    }

    @Override
    public void onPause(){
        Log.i(TAG, "ManualActivity.onPause() called");
        AndroidUtil.removeAllFragments(this);
        super.onPause();
        //AndroidUtil.removeAllFragments(this);
        //AndroidUtil.showToastShort("onPause()");
        Intent intent = new Intent(this,CallOutService.class);
        callOutService.onUnbind(intent);
        stopService(intent);
        callOutService.onDestroy();
        finish();
    }

    public void loadFragments(Bundle savedInstanceState) {
        if (findViewById(R.id.fragment_container_manual) != null) {
            if (savedInstanceState != null) {
                return;// restoring state
            }

            videoViewFragment = new VideoViewFragment(this);
            AndroidUtil.loadFragment(this,videoViewFragment,R.id.fragment_container_manual);

            soundControlFragment = new SoundControlFragment(this);
            AndroidUtil.loadFragment(this,soundControlFragment,R.id.fragment_container_manual);

            settingsButtonFragment = new ChangeActivityButtonFragment(this,SettingsActivity.class,R.layout.fragment_settings_button,R.id.settingsButton);
            AndroidUtil.loadFragment(this, settingsButtonFragment,R.id.fragment_container_manual);

            manualIndicatorsFragment = new ManualIndicatorsFragment(this);
            AndroidUtil.loadFragment(this,manualIndicatorsFragment,R.id.fragment_container_manual);

            autoButtonFragment = new ChangeActivityButtonFragment(this,AutoActivity.class,R.layout.fragment_auto_button,R.id.autoButton);
            AndroidUtil.loadFragment(this, autoButtonFragment,R.id.fragment_container_manual);

            changeActiveSysDialogButtonFragment = new ChangeActiveSysDialogButtonFragment(this);
            AndroidUtil.loadFragment(this, changeActiveSysDialogButtonFragment,R.id.fragment_container_manual);

            batteryIndicatorFragment = new BatteryIndicatorFragment(this);
            AndroidUtil.loadFragment(this, batteryIndicatorFragment,R.id.fragment_container_manual);

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //soundControlFragment.unmute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (soundControlFragment != null)
            soundControlFragment.shutdown();
        //callOut.shutdown();
        startMainActivity();

    }

    public void startMainActivity(){
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(i);
    }

}
