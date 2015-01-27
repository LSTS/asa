package pt.lsts.asa.fragments;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.listenners.MyLocationListener;
import pt.lsts.asa.subscribers.GmapIMCSubscriber;
import pt.lsts.asa.sys.Sys;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GmapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "GmapFragment";
    private FragmentActivity fragmentActivity=null;

    private GoogleMap googleMap = null;
    private MapFragment mapFragment = null;

    private LatLng myLatLng = new LatLng(0,0);
    private MyLocationListener myLocationListener = null;
    private Boolean initZoom = false;

    private ArrayList<Marker> markersArrayList = new ArrayList<Marker>();//markers of vehicles positions


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
        return this.myLatLng;
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

    public void initIMCSubscriber(){
        GmapIMCSubscriber gmapIMCSubscriber = new GmapIMCSubscriber(this);
        ASA.getInstance().addSubscriber(gmapIMCSubscriber);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.i("onMapReady", "onMapReady");
        googleMap=map;
        googleMap.setMyLocationEnabled(true);//set a blue dot on position
        initIMCSubscriber();
    }

    public Marker addMarkerToPos(final Sys sys, final String title, final LatLng latLng){//standard googles red marker
        if (googleMap!=null) {

            fragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(title)
                    );
                    sys.setMaker(marker);
                }
            });
        }
        return sys.getMaker();
    }

    public Marker addMarkerToPos(final Sys sys, final String title, final LatLng latLng, final int iconRid){//R.drawable.icon
        if (googleMap!=null) {

            fragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(iconRid))

                    );
                    sys.setMaker(marker);
                }
            });
        }
        return sys.getMaker();
    }

    public void updateMyLocation(Location location){
        double lat = 0;
        double lon = 0;

        if (location!=null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
        if (initZoom==false && googleMap!=null){//center camera initialy on my position
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat,lon),6.0f,1.0f,1.0f)));
            initZoom=true;
        }
        this.myLatLng = new LatLng(lat, lon);
    }

    public void setActiveSysLatLng(final LatLng vehicleLatLng) {
        ASA.getInstance().getActiveSys().setLatLng(vehicleLatLng);
        final String vehicleName = ASA.getInstance().getActiveSys().getName().toString();
        if (ASA.getInstance().getActiveSys().getMaker()==null)
            addMarkerToPos(ASA.getInstance().getActiveSys(), vehicleName, vehicleLatLng, R.drawable.ic_orange_arrow);
        else {
            fragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ASA.getInstance().getActiveSys().getMaker().setPosition(vehicleLatLng);
                    Log.i(TAG, "setPosition("+vehicleLatLng.toString()+")");
                }
            });
        }
    }

    public void removeMarker(String title){
        for (Marker marker : markersArrayList){
            if (marker.getTitle().equalsIgnoreCase(title)) {
                markersArrayList.remove(marker);
                marker.remove();
                return;
            }
        }
    }

}
