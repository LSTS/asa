package pt.lsts.asa.managers;

import pt.lsts.asa.App;
import pt.lsts.asa.ui.components.VerticalSeekBar;

import android.media.AudioManager;
import android.widget.SeekBar;

public class SoundManager {

	private static SoundManager instance = null;
	private AudioManager audioManager;

	public int getMaxVolume() {
		return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	private SoundManager() {
		audioManager = App.getAudioManager();
	}

	public static SoundManager getInstance() {
		if (instance == null)
			instance = new SoundManager();
		return instance;
	}

	public boolean checkMute() {
		if (getCurrentVolume() > 0)
			return false;
		return true;
	}

	public int getCurrentVolume() {
		int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		// Toast.makeText(this.getApplicationContext(), "vol="+vol,
		// Toast.LENGTH_LONG).show();
		return vol;
	}

	public void setCurrentVolume(int val) {
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, val, 0);
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
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
								arg1, 0);
					}
				});
	}

	public void mute() {
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
	}

	public void unmute() {
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

}
