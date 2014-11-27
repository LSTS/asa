package pt.lsts.asa.activities;

import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.fragments.SoundControlFragment;
import pt.lsts.asa.fragments.VideoViewFragment;
import pt.lsts.asa.managers.SoundManager;
import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.support.v4.app.FragmentActivity;

public class ManualStabilizedModeActivity extends FragmentActivity {

	AudioManager audioManager;
	ImageButton imageButtonMute;
	CallOut callOut;
	SoundManager soundManager = SoundManager.getInstance();
	SoundControlFragment soundControlFragment = null;
	VideoViewFragment videoViewFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_manual_stabilized);
		
		ASA.getInstance().callOut.initCallOuts();
		loadFragments(savedInstanceState);
		//ASA.getInstance().callOut.initCallOuts();
	}

	public void loadFragments(Bundle savedInstanceState) {
		if (findViewById(R.id.fragment_container_manual_stabilized) != null) {
			if (savedInstanceState != null) {
				return;// restoring state
			}
			loadVideoViewFragment();
			loadSoundControlFragment();
		}
	}

	private void loadVideoViewFragment() {
		videoViewFragment = new VideoViewFragment(this.getApplicationContext());
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container_manual_stabilized,
						videoViewFragment).commit();
	}

	private void loadSoundControlFragment() {
		soundControlFragment = new SoundControlFragment(
				this.getApplicationContext());
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container_manual_stabilized,
						soundControlFragment).commit();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
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
	}

}
