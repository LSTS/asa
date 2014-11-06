package pt.lsts.newaccu.activities;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.comms.CallOut;
import pt.lsts.newaccu.managers.SoundManager;
import pt.lsts.newaccu.ui.components.VerticalSeekBar;
import pt.lsts.newaccu.util.AccuTimer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;


public class ManualStabilizedModeActivity extends Activity {

	AudioManager audioManager;
	ImageButton imageButtonMute;
	SoundManager soundManager = SoundManager.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual_stabilized_mode);
		
		setVolumeControl();
	}

	public void setVolumeControl(){
	    VerticalSeekBar volControl = (VerticalSeekBar)findViewById(R.id.seekBarVolume);
	    volControl.setMax(soundManager.getMaxVolume());
	    volControl.setProgress(soundManager.getCurrentVolume());
	    setVolumeControlChanger(volControl, audioManager);
	    setImageButtonMute();
	}
	
	public void setImageButtonMute(){
		imageButtonMute = (ImageButton)findViewById(R.id.imageButtonMute);
		setImageButtonMuteIcon();
		imageButtonMute.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            if (soundManager.checkMute())
	            	unmute();
	            else
	            	mute();
	            showToastWithVolume();
	        }
	    });
	}
	
	public void setImageButtonMuteIcon(){
		if (soundManager.checkMute())
			imageButtonMute.setImageResource(R.drawable.sound_off);
		else
			imageButtonMute.setImageResource(R.drawable.sound_on);
	}
	
	public void setVolumeControlChanger(VerticalSeekBar volControl, final AudioManager audioManager){
		volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		    @Override
		    public void onStopTrackingTouch(SeekBar arg0) {
		    }
		
		    @Override
		    public void onStartTrackingTouch(SeekBar arg0) {
		    }
		
		    @Override
		    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		        unmute();
		    	soundManager.setCurrentVolume(arg1);
		    	adjustVolumeBarAndIcon();
		    	showToastWithVolume();
		    }
		});
	}
	
	private void adjustVolumeBarAndIcon(){
		int curVolume = soundManager.getCurrentVolume();
	    VerticalSeekBar volControl = (VerticalSeekBar)findViewById(R.id.seekBarVolume);
	    volControl.setProgress(curVolume);
		setImageButtonMuteIcon();
	}
	
	private void mute() {
	    soundManager.mute();
	    setImageButtonMuteIcon();
	}

	public void unmute() {
		soundManager.unmute();
		setImageButtonMuteIcon();
	}
	
	public void showToastWithVolume(){
		int vol = soundManager.getCurrentVolume();
	    Toast.makeText(this.getApplicationContext(), "vol="+vol, Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
        .setTitle("Exit")
        .setMessage("Are you sure you want to exit?")
        .setNegativeButton(android.R.string.no, null)
        .setNegativeButton(android.R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            	back();
            }
        }).create().show();
		
	}
	
	public void back(){
		super.onBackPressed();
		unmute();
	}


}
