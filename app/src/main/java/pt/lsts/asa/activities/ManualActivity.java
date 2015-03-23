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
import android.widget.Toast;

import com.squareup.otto.Subscribe;

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
        ASA.getInstance().getBus().register(this);
    }

    @Override
    public void onPause(){
        Log.i(TAG, "ManualActivity.onPause() called");
        AndroidUtil.removeAllFragments(this);
        super.onPause();
        ASA.getInstance().getBus().unregister(this);
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

            videoViewFragment = new VideoViewFragment();
            AndroidUtil.loadFragment(this,videoViewFragment,R.id.fragment_container_manual);

            soundControlFragment = new SoundControlFragment();
            AndroidUtil.loadFragment(this,soundControlFragment,R.id.fragment_container_manual);

            manualIndicatorsFragment = new ManualIndicatorsFragment();
            AndroidUtil.loadFragment(this,manualIndicatorsFragment,R.id.fragment_container_manual);

            settingsButtonFragment = new ChangeActivityButtonFragment();
            Bundle args = new Bundle();
            args.putInt("layout",R.layout.fragment_settings_button);
            args.putInt("id",R.id.settingsButton);
            args.putSerializable("class",SettingsActivity.class);
            settingsButtonFragment.setArguments(args);
            AndroidUtil.loadFragment(this, settingsButtonFragment,R.id.fragment_container_manual);

            autoButtonFragment = new ChangeActivityButtonFragment();
            args=null;
            args = new Bundle();
            args.putInt("layout",R.layout.fragment_auto_button);
            args.putInt("id",R.id.autoButton);
            args.putSerializable("class",AutoActivity.class);
            autoButtonFragment.setArguments(args);
            AndroidUtil.loadFragment(this, autoButtonFragment,R.id.fragment_container_manual);

            changeActiveSysDialogButtonFragment = new ChangeActiveSysDialogButtonFragment();
            AndroidUtil.loadFragment(this, changeActiveSysDialogButtonFragment,R.id.fragment_container_manual);

            batteryIndicatorFragment = new BatteryIndicatorFragment();
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
