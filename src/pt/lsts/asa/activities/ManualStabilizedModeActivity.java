package pt.lsts.asa.activities;

import pt.lsts.asa.fragments.SettingsButtonFragment;
import pt.lsts.asa.fragments.SoundControlFragment;
import pt.lsts.asa.fragments.VideoViewFragment;
import pt.lsts.asa.managers.SoundManager;
import pt.lsts.asa.ASA;
import pt.lsts.asa.R;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.support.v4.app.FragmentActivity;

public class ManualStabilizedModeActivity extends FragmentActivity {

	private SoundControlFragment soundControlFragment = null;
	private VideoViewFragment videoViewFragment = null;
    private SettingsButtonFragment settingsButtonFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_manual_stabilized);

		ASA.getInstance().getCallOut().initCallOuts();
		loadFragments(savedInstanceState);
		// ASA.getInstance().getCallOut().startCallOuts();
	}

	public void loadFragments(Bundle savedInstanceState) {
		if (findViewById(R.id.fragment_container_manual_stabilized) != null) {
			if (savedInstanceState != null) {
				return;// restoring state
			}

			videoViewFragment = new VideoViewFragment(this);
			loadFragment(videoViewFragment);

			soundControlFragment = new SoundControlFragment(this);
			loadFragment(soundControlFragment);

            settingsButtonFragment = new SettingsButtonFragment(this);
            loadFragment(settingsButtonFragment);
		}
	}

	public void loadFragment(Fragment fragment){
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container_manual_stabilized,
						fragment).commit();
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
		ASA.getInstance().getCallOut().shutdown();
	}

}
