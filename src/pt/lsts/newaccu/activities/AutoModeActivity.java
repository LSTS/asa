package pt.lsts.newaccu.activities;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.fragments.GmapFragment;

import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;


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
        gmapFragment = new GmapFragment(this.getApplicationContext());
        getSupportFragmentManager().beginTransaction()
        	.add(R.id.fragment_container_auto, gmapFragment).commit();
	}
	
	public void onFragmentInteraction(Uri uri){
		
	}
	
}
