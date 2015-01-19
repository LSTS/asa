package pt.lsts.asa.fragments;

import pt.lsts.asa.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GmapFragment extends Fragment implements OnMapReadyCallback {

    FragmentActivity fragmentActivity=null;
	GoogleMap googleMap = null;
    MapFragment mapFragment = null;


	public GmapFragment() {
		// Required empty public constructor
	}

	public GmapFragment(FragmentActivity fragmentActivity) {
		this.fragmentActivity = fragmentActivity;
        mapFragment = MapFragment.newInstance();
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

    public void initMapFragment() {
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        //map.setMyLocationEnabled(true);//set a blue dot on position
        Log.i("onMapReady", "onMapReady");
        this.googleMap=map;
        addMarkerToMyPos();
    }

    public void addMarkerToMyPos(){
        addMarkerToPos("myLocation",getLastKnowLatLng());
    }

    public void addMarkerToPos(String title, LatLng latLng){
        this.googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
    }

    public LatLng getLastKnowLatLng(){
        double lat = 0;
        double lon = 0;

        LocationManager lm;
        lm = (LocationManager) fragmentActivity.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLoc != null) {
            lat = (int) lastKnownLoc.getLatitude() ;
            lon = (int) lastKnownLoc.getLongitude();
        }
        LatLng latlng = new LatLng(lat, lon);
        return latlng;
    }

}
