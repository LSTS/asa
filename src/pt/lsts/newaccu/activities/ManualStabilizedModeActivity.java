package pt.lsts.newaccu.activities;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.feedback.CallOut;
import pt.lsts.newaccu.fragments.SoundControlFragment;
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
		implements SoundControlFragment.OnFragmentInteractionListener {

	AudioManager audioManager;
	ImageButton imageButtonMute;
	CallOut callOut;
	SoundManager soundManager = SoundManager.getInstance();
	SoundControlFragment soundControlFragment=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadSoundControlFragment(savedInstanceState);
		loadVideoViewFragment(savedInstanceState);
		
	}
	
	public void loadVideoViewFragment(Bundle savedInstanceState){
		
		
		
	}
	
	public void loadSoundControlFragment(Bundle savedInstanceState){
        FrameLayout frame = new FrameLayout(this);
        frame.setId(R.id.fragment_sound_control);
        setContentView(frame, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        if (savedInstanceState == null) {
            soundControlFragment = new SoundControlFragment(this.getApplicationContext());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_sound_control, soundControlFragment).commit();
        }
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
		soundControlFragment.shutdown();
	}

	@Override
	public void onFragmentInteraction(Uri uri){
		
	}

}
