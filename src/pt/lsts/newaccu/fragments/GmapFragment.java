package pt.lsts.newaccu.fragments;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.R.id;
import pt.lsts.newaccu.R.layout;

import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link GmapFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link GmapFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class GmapFragment extends Fragment {
	
	Context context;
	GoogleMap googleMap=null;

	public GmapFragment() {
		// Required empty public constructor
	}
	
	public GmapFragment(Context context) {
		this.context=context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_gmaps, container, false);
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
	
	public void startGmap(){
		googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
	}

}
