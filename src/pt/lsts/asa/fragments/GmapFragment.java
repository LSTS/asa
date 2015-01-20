package pt.lsts.asa.fragments;

import pt.lsts.asa.R;
import pt.lsts.asa.listenners.MyLocationListener;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GmapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentActivity fragmentActivity=null;

    private GoogleMap googleMap = null;
    private MapFragment mapFragment = null;
    private LatLng latLng = new LatLng(0,0);
    private MyLocationListener myLocationListener = null;
    private Boolean initZoom = false;


	public GmapFragment() {
		// Required empty public constructor
	}

	public GmapFragment(FragmentActivity fragmentActivity) {
		this.fragmentActivity = fragmentActivity;
        myLocationListener = new MyLocationListener(this);
        myLocationListener.initLocationListener();
	}

    public FragmentActivity getFragmentActivity() {
        return fragmentActivity;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public LatLng getLastKnowLatLng(){
        return this.latLng;
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
        mapFragment = (MapFragment) fragmentActivity.getFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap map) {
        Log.i("onMapReady", "onMapReady");
        googleMap=map;
        googleMap.setMyLocationEnabled(true);//set a blue dot on position
    }

    public void addMarkerToPos(String title, LatLng latLng){
        this.googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
    }

    public void updateLocation(Location location){
        double lat = 0;
        double lon = 0;

        if (location!=null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
        if (initZoom==false && googleMap!=null){//center camera initialy on my position
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat,lon),16.0f,1.0f,1.0f)));
            initZoom=true;
        }

        this.latLng = new LatLng(lat, lon);

    }

}
