package pt.lsts.newaccu.activities;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.feedback.CallOut;
import pt.lsts.newaccu.fragments.SoundControlFragment;
import pt.lsts.newaccu.fragments.VideoViewFragment;
import pt.lsts.newaccu.managers.SoundManager;
import pt.lsts.newaccu.ui.components.VerticalSeekBar;
import pt.lsts.newaccu.util.AccuTimer;
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
import pt.lsts.newaccu.GmapFragment;

public class AutoModeActivity extends FragmentActivity 
		implements GmapFragment.OnFragmentInteractionListener{

	GmapFragment gmapFragment=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_auto);
		
		loadFragments(savedInstanceState);
	}
	
	public void loadFragments(Bundle savedInstanceState){
		if (findViewById(R.id.fragment_container_auto) != null) {
            if (savedInstanceState != null) {
                return;//restoring state
            }
            loadGmapsFragment();
        }
	}
	
	private void loadGmapsFragment(){
        // Create a new Fragment to be placed in the activity layout
        gmapFragment = new GmapFragment(this.getApplicationContext());
        
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
        	.add(R.id.fragment_container_auto, gmapFragment).commit();
	}
	
	public void onFragmentInteraction(Uri uri){
		
	}
	
}
