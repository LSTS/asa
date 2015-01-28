package pt.lsts.asa.fragments;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.listenners.MyLocationListener;
import pt.lsts.asa.subscribers.GmapIMCSubscriber;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.GmapsUtil;

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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GmapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "GmapFragment";
    private FragmentActivity fragmentActivity=null;

    private GoogleMap googleMap = null;
    private MapFragment mapFragment = null;
    private GmapIMCSubscriber gmapIMCSubscriber = null;

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
        //initIMCSubscriber();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (Sys sys : ASA.getInstance().getSystemList().getList())
            sys.resetVisualizations();
        ASA.getInstance().removeSubscriber(gmapIMCSubscriber);
        gmapIMCSubscriber=null;

    }

    public void initIMCSubscriber(){
        gmapIMCSubscriber = new GmapIMCSubscriber(this);
        ASA.getInstance().addSubscriber(gmapIMCSubscriber);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.i("onMapReady", "onMapReady");
        googleMap=map;
        googleMap.setMyLocationEnabled(true);//set a blue dot on position
        initIMCSubscriber();
    }

    public Marker addMarkerToPos(final Sys sys){//standard googles red marker
        final String title = sys.getName();
        final LatLng latLng = sys.getLatLng();
        if (googleMap!=null && ASA.getInstance().UIThread==false) {
            ASA.getInstance().UIThread=true;
            fragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(title)
                                    .anchor(0.5f,0.5f)//center
                    );
                    sys.setMaker(marker);
                    ASA.getInstance().UIThread=false;
                }
            });
        }
        return sys.getMaker();
    }

    public Marker addMarkerToPos(final Sys sys, final int iconRid){//R.drawable.icon
        if (googleMap!=null && ASA.getInstance().UIThread==false) {
            ASA.getInstance().UIThread=true;
            final String sysName = sys.getName();
            final LatLng latLng = sys.getLatLng();
            final float bearing = sys.getPsi();
            fragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    BitmapDescriptor image = BitmapDescriptorFactory.fromResource(iconRid); //image
                    GroundOverlay groundOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions()
                                    .bearing(bearing)
                                    .image(image)
                                    .position(latLng, 100, 100)
                                    .anchor(0.5f, 0.5f)
                                    .transparency(0.25f)
                    );

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(sysName)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.transparent))
                    );

                    sys.setMaker(marker);
                    sys.setGroundOverlay(groundOverlay);
                    ASA.getInstance().UIThread=false;
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
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat,lon),4.0f,1.0f,1.0f)));
            initZoom=true;
        }
        this.myLatLng = new LatLng(lat, lon);
    }

    public void setActiveSysLatLng(final LatLng vehicleLatLng) {
        ASA.getInstance().getActiveSys().setLatLng(vehicleLatLng);
        final String vehicleName = ASA.getInstance().getActiveSys().getName().toString();
        Log.i(TAG, vehicleName + ".setPosition("+vehicleLatLng.toString()+")");
    }

    public void setActiveSysPsi(final float psi){
        ASA.getInstance().getActiveSys().setPsi(psi);
        Log.i(TAG, "setRotation("+psi+")");
    }

    public void updateActiveSysMarker(){
        if (googleMap==null)
            return;
        final Sys sys = ASA.getInstance().getActiveSys();
        if (sys.isOnMap()==false){
            addMarkerToPos(sys, R.drawable.ic_orange_arrow);
            Log.i(TAG,"sys.isOnMap()==false");
        }else
            Log.i(TAG,"sys.isOnMap()!=false");

        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Marker marker = sys.getMaker();
                LatLng latLng = sys.getLatLng();
                float psi = sys.getPsi();
                GroundOverlay groundOverlay = sys.getGroundOverlay();
                marker.setPosition(latLng);
                groundOverlay.setPosition(latLng);
                groundOverlay.setBearing(psi);
                marker.showInfoWindow();//show info

            }
        });

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
