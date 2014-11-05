package pt.lsts.newaccu.activities;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.ui.components.VerticalSeekBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;


public class ManualStabilizedModeActivity extends Activity {

	AudioManager audioManager;
	ImageButton imageButtonMute;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual_stabilized);
		
		setVolumeControl();
		
	}

	public void setVolumeControl(){
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	    VerticalSeekBar volControl = (VerticalSeekBar)findViewById(R.id.seekBarVolume);
	    volControl.setMax(maxVolume);
	    volControl.setProgress(curVolume);
	    setVolumeControlChanger(volControl, audioManager);
	    setImageButtonMute();
	}
	
	public void setImageButtonMute(){
		imageButtonMute = (ImageButton)findViewById(R.id.imageButtonMute);
		setImageButtonMuteIcon();
		imageButtonMute.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            if (checkVolume()>0)
	            	mute();
	            else
	            	unmute();
	            showToastWithVolume();
	        }
	    });
	}
	
	public void setImageButtonMuteIcon(){
		if (checkVolume()==0)
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
		    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
		    	adjustVolumeBarAndIcon();
		    	showToastWithVolume();
		    }
		});
	}
	
	private void adjustVolumeBarAndIcon(){
		int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	    VerticalSeekBar volControl = (VerticalSeekBar)findViewById(R.id.seekBarVolume);
	    volControl.setProgress(curVolume);
		setImageButtonMuteIcon();
	}
	
	private void mute() {
	    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
	    setImageButtonMuteIcon();
	}

	public void unmute() {
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
		setImageButtonMuteIcon();
	}
	
	public int checkVolume(){
		int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	    //Toast.makeText(this.getApplicationContext(), "vol="+vol, Toast.LENGTH_LONG).show();
	    return vol;
	}
	
	public void showToastWithVolume(){
		int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
