package pt.lsts.asa.activities;

import pt.lsts.asa.fragments.GmapFragment;
import pt.lsts.asa.R;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.MapFragment;

public class AutoModeActivity extends FragmentActivity {

	GmapFragment gmapFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_auto);

		loadFragments(savedInstanceState);

	}

	public void loadFragments(Bundle savedInstanceState) {
		if (findViewById(R.id.fragment_container_auto) != null) {
			if (savedInstanceState != null) {
				return;// restoring state
			}
            gmapFragment = new GmapFragment(this);
            gmapFragment.initMapFragment();
		}
	}

}
