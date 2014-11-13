package pt.lsts.newaccu.activities;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.feedback.CallOut;
import pt.lsts.newaccu.fragments.SoundControlFragment;
import pt.lsts.newaccu.fragments.VideoViewFragment;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public class ManualStabilizedModeActivity extends FragmentActivity
		implements SoundControlFragment.OnFragmentInteractionListener, VideoViewFragment.OnFragmentInteractionListener {

	AudioManager audioManager;
	ImageButton imageButtonMute;
	CallOut callOut;
	SoundManager soundManager = SoundManager.getInstance();
	SoundControlFragment soundControlFragment=null;
	VideoViewFragment videoViewFragment =null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_manual_stabilized);
		
		loadFragments(savedInstanceState);
		
	}
	
	public void loadFragments(Bundle savedInstanceState){
		if (findViewById(R.id.fragment_container_manual_stabilized) != null) {
            if (savedInstanceState != null) {
                return;//restoring state
            }
            loadVideoViewFragment();
            loadSoundControlFragment();
        }
	}
	
	private void loadVideoViewFragment(){
        // Create a new Fragment to be placed in the activity layout
        videoViewFragment = new VideoViewFragment(this.getApplicationContext());
        
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_manual_stabilized, videoViewFragment).commit();
	}
	
	private void loadSoundControlFragment(){
        // Create a new Fragment to be placed in the activity layout
        soundControlFragment = new SoundControlFragment(this.getApplicationContext());
        
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_manual_stabilized, soundControlFragment).commit();
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
		if (soundControlFragment!=null)
			soundControlFragment.shutdown();
	}

	@Override
	public void onFragmentInteraction(Uri uri){
		
	}

}
