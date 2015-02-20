package pt.lsts.asa.activities;

import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.feedback.CallOutService;
import pt.lsts.asa.fragments.ManualIndicatorsFragment;
import pt.lsts.asa.fragments.SettingsButtonFragment;
import pt.lsts.asa.fragments.SoundControlFragment;
import pt.lsts.asa.fragments.VideoViewFragment;
import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.subscribers.SystemsUpdaterServiceIMCSubscriber;
import pt.lsts.asa.util.AndroidUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class ManualActivity extends FragmentActivity {

    public static final String TAG = "ManualActivity";

    private SoundControlFragment soundControlFragment = null;
    private VideoViewFragment videoViewFragment = null;
    private SettingsButtonFragment settingsButtonFragment = null;
    private ManualIndicatorsFragment manualIndicatorsFragment = null;
    private CallOut callOut;
    private CallOutService callOutService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_manual_stabilized);

        //callOut = new CallOut(ASA.getContext());
        //callOut.initCallOuts();
        //loadFragments(savedInstanceState);
        // ASA.getInstance().getCallOut().startCallOuts();
    }

    @Override
    public void onResume(){
        super.onResume();
        loadFragments(null);//always load fragments from null
        soundControlFragment.unmute();

        callOutService = new CallOutService(this);
        Intent intent = new Intent(this,CallOutService.class);
        callOutService.onStartCommand(intent,1,0);
        callOutService.onBind(intent);

        //AndroidUtil.showToastShort(this, "onResume()");
    }

    @Override
    public void onPause(){
        Log.i(TAG, "ManualActivity.onPause() called");
        AndroidUtil.removeAllFragments(this);
        super.onPause();
        //AndroidUtil.removeAllFragments(this);
        //AndroidUtil.showToastShort(this, "onPause()");
        Intent intent = new Intent(this,CallOutService.class);
        callOutService.onUnbind(intent);
        stopService(intent);
        callOutService.onDestroy();
    }

    public void loadFragments(Bundle savedInstanceState) {
        if (findViewById(R.id.fragment_container_manual_stabilized) != null) {
            if (savedInstanceState != null) {
                return;// restoring state
            }

            videoViewFragment = new VideoViewFragment(this);
            AndroidUtil.loadFragment(this,videoViewFragment,R.id.fragment_container_manual_stabilized);

            soundControlFragment = new SoundControlFragment(this);
            AndroidUtil.loadFragment(this,soundControlFragment,R.id.fragment_container_manual_stabilized);

            settingsButtonFragment = new SettingsButtonFragment(this);
            AndroidUtil.loadFragment(this,settingsButtonFragment,R.id.fragment_container_manual_stabilized);

            manualIndicatorsFragment = new ManualIndicatorsFragment(this);
            AndroidUtil.loadFragment(this,manualIndicatorsFragment,R.id.fragment_container_manual_stabilized);

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

    }

}
