package pt.lsts.asa.fragments;

import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.managers.SoundManager;
import pt.lsts.asa.ui.components.VerticalSeekBar;
import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.util.AndroidUtil;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.support.v4.app.Fragment;

public class SoundControlFragment extends Fragment {

	FragmentActivity fragmentActivity = null;
	AudioManager audioManager;
	ImageButton muteImageButton;
	CallOut callOut;
	SoundManager soundManager = SoundManager.getInstance();
	VerticalSeekBar volumeVerticalSeekBar;

	public SoundControlFragment() {
		// Required empty public constructor
	}

	public SoundControlFragment(FragmentActivity fragmentActivity) {
		this.fragmentActivity = fragmentActivity;
		callOut = ASA.getInstance().getCallOut();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_sound_control, container,
				false);
		volumeVerticalSeekBar = (VerticalSeekBar) v.findViewById(R.id.volumeVerticalSeekBar);
		muteImageButton = (ImageButton) v.findViewById(R.id.muteImageButton);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onStart() {
		super.onStart();
		setVolumeControl();
		//callOut.startCallOuts();

	}

	public void setVolumeControl() {
		volumeVerticalSeekBar = (VerticalSeekBar) getView().findViewById(
				R.id.volumeVerticalSeekBar);
		volumeVerticalSeekBar.setMax(soundManager.getMaxVolume());
		volumeVerticalSeekBar.setProgress(soundManager.getCurrentVolume());
		setVolumeControlChanger(volumeVerticalSeekBar, audioManager);
		setImageButtonMute();

	}

	public void setImageButtonMute() {
		setImageButtonMuteIcon();
		muteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundManager.checkMute())
                    unmute();
                else
                    mute();
                // showToastWithVolume();
            }
        });
	}

	public void setImageButtonMuteIcon() {
		if (soundManager.checkMute())
			muteImageButton.setImageResource(R.drawable.sound_off);
		else
			muteImageButton.setImageResource(R.drawable.sound_on);
	}

	public void setVolumeControlChanger(VerticalSeekBar volControl,
			final AudioManager audioManager) {
		volControl
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar arg0) {
					}

					@Override
					public void onStartTrackingTouch(SeekBar arg0) {
					}

					@Override
					public void onProgressChanged(SeekBar arg0, int arg1,
							boolean arg2) {
						unmute();
						soundManager.setCurrentVolume(arg1);
						adjustVolumeBarAndIcon();
						AndroidUtil.showToastShort(fragmentActivity, "volume: "+soundManager.getCurrentVolume());
					}
				});
	}

	private void adjustVolumeBarAndIcon() {
		int curVolume = soundManager.getCurrentVolume();
		volumeVerticalSeekBar.setProgress(curVolume);
		setImageButtonMuteIcon();
	}

	public void mute() {
		soundManager.mute();
		setImageButtonMuteIcon();
	}

	public void unmute() {
		soundManager.unmute();
		soundManager.setCurrentVolume(soundManager.getCurrentVolume());
		setImageButtonMuteIcon();
		callOut.startCallOuts();
	}

	public void shutdown() {
		unmute();
		callOut.shutdown();
	}

}
