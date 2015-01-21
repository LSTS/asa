package pt.lsts.asa.activities;

import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.fragments.ManualIndicatorsFragment;
import pt.lsts.asa.fragments.SettingsButtonFragment;
import pt.lsts.asa.fragments.SoundControlFragment;
import pt.lsts.asa.fragments.VideoViewFragment;
import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.util.AndroidUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ManualActivity extends FragmentActivity {

	private SoundControlFragment soundControlFragment = null;
	private VideoViewFragment videoViewFragment = null;
    private SettingsButtonFragment settingsButtonFragment = null;
    private ManualIndicatorsFragment manualIndicatorsFragment = null;
    private CallOut callOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_manual_stabilized);

        callOut = new CallOut(ASA.getContext());
        callOut.initCallOuts();
		loadFragments(savedInstanceState);
		// ASA.getInstance().getCallOut().startCallOuts();
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
		soundControlFragment.unmute();
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Exit")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton(android.R.string.no, null)
				.setNegativeButton(android.R.string.yes, new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						back();
					}
				}).create().show();

	}

	public void back() {
		super.onBackPressed();
		if (soundControlFragment != null)
			soundControlFragment.shutdown();
		callOut.shutdown();
	}

}
