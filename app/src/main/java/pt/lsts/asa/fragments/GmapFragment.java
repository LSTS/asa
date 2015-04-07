package pt.lsts.asa.fragments;


import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.listenners.MyLocationListener;
import pt.lsts.asa.listenners.sysUpdates.GmapSysUpdaterListenner;
import pt.lsts.asa.subscribers.GmapIMCSubscriber;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.util.PlanUtilities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class GmapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "GmapFragment";

    private GoogleMap googleMap = null;
    private MapFragment mapFragment = null;

    private GmapIMCSubscriber gmapIMCSubscriber = null;
    private GmapSysUpdaterListenner gmapSysUpdaterListenner = null;

    private LatLng myLatLng = new LatLng(0,0);
    private MyLocationListener myLocationListener = null;
    private Boolean initZoom = false;

    private ArrayList<Marker> markersArrayList = new ArrayList<Marker>();//markers of vehicles positions
    private ArrayList<Marker> currentPlanMarkersList = new ArrayList<Marker>();//markers for current Plan
    private ArrayList<Circle> loiterCircleList = new ArrayList<Circle>();//list of loiters circles
    private ArrayList<Polyline> linesPolylineList = new ArrayList<Polyline>();

    public GmapFragment() {
        // Required empty public constructor
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
        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.googleMap);
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
    public void onResume(){
        //initIMCSubscriber();
        initGmapSysUpdaterListenner();
        initMyLocationListener();
        Log.i(TAG,"onAttach");
        super.onResume();

    }


    public void initMyLocationListener(){
        myLocationListener = new MyLocationListener(this);
        myLocationListener.initLocationListener();
    }

    @Override
    public void onPause(){
        for (Sys sys : ASA.getInstance().getSystemList().getList())
            sys.resetVisualizations();
        ASA.getInstance().getBus().unregister(gmapSysUpdaterListenner);
        gmapSysUpdaterListenner=null;
        super.onPause();
    }
/*
    public void initIMCSubscriber(){
        for (Sys sys : ASA.getInstance().getSystemList().getList()){
            updateSysMarker(sys);
        }
        gmapIMCSubscriber = new GmapIMCSubscriber(this);
        ASA.getInstance().addSubscriber(gmapIMCSubscriber);
    }
*/
    public void initGmapSysUpdaterListenner(){
        for (Sys sys : ASA.getInstance().getSystemList().getList()) {
            updateSysMarker(sys);
        }
        if (gmapSysUpdaterListenner!=null) {
            ASA.getInstance().getBus().unregister(gmapSysUpdaterListenner);
            gmapSysUpdaterListenner=null;
        }
        gmapSysUpdaterListenner = new GmapSysUpdaterListenner(this);
        ASA.getInstance().getBus().register(gmapSysUpdaterListenner);
    }

    public void initGmapCameraChangedListenner(){
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.i(TAG, "cameraPosition.bearing= "+(360-cameraPosition.bearing));
                for (Sys sys : ASA.getInstance().getSystemList().getList()){
                    if(sys.getMarker()!=null)
                        sys.getMarker().setRotation(AndroidUtil.calcRotation((360-cameraPosition.bearing), sys.getPsi()));
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.i("onMapReady", "onMapReady");
        googleMap=map;
        googleMap.setMyLocationEnabled(true);//set a blue dot on position
        //initIMCSubscriber();
        for (Sys sys : ASA.getInstance().getSystemList().getList()){
            updateSysMarker(sys);
        }
        initGmapSysUpdaterListenner();
        initGmapCameraChangedListenner();
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
    }

    /**
     * Generic marker for sys
     */
    public void addMarkerToPos(final Sys sys){//add marker for sys
        final String type = sys.getType();
        String typeUpperCase = type.toUpperCase();
        Log.i(TAG, "type= "+type);
        Log.i(TAG, "typeUpperCase= "+typeUpperCase);
        switch (typeUpperCase){
            case "CCU":
                Log.i(TAG, sys.getName()+", ccu pc_icon");
                addMarkerToPos(sys, R.drawable.green_pc_icon);
                break;
            case "HUMANSENSOR":
            case "MOBILESENSOR":
            case "WSN":
            case "STATICSENSOR":
                Log.i(TAG, sys.getName()+", human/mobile sensor anchor_icon");
                addMarkerToPos(sys, R.drawable.anchor_icon);
                break;
            case "UAV":
                Log.i(TAG, sys.getName()+", uav vehicle orange_arrow_icon");
                addMarkerToPos(sys, R.drawable.orange_arrow_with_background_icon);
                break;
            case "USV":
            case "UUV":
            case "UGV":
                Log.i(TAG, sys.getName()+", other vehicle orange_arrow_icon");
                addMarkerToPos(sys, R.drawable.blue_arrow_with_background_icon);
                break;

            default:
                Log.e(TAG, "default: return null");
                Log.e(TAG, "type= "+type+" | typeUpperCase= "+typeUpperCase);
                break;
        }
    }

    /**
     *
     * @param sys System with latlng and orientation info
     * @param iconRid icon location for this sys
     */
    public void addMarkerToPos(final Sys sys, final int iconRid){//marker for sys with iconRid
        Log.d(TAG,"addMarkerToPos, sys= "+sys.getName()+" | marker="+(sys.getMarker()==null)+" | isOnMap="+sys.isOnMap());
        if (googleMap!=null) {
            final String sysName = sys.getName();
            final LatLng latLng = sys.getLatLng();
            final float bearing = sys.getPsi();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ASA.getInstance().UIThread=true;
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(sysName)
                                    .icon(BitmapDescriptorFactory.fromResource(iconRid))
                                    .draggable(false)
                                    .rotation(AndroidUtil.calcRotation((360-googleMap.getCameraPosition().bearing), bearing))
                    );

                    sys.setMarker(marker);
                }
            });
        }

    }


    /**
     * Marker for waypoints of plans
     * @param id The index of waypoint in current plan
     * @param latLng Position of waypoint
     * @param radius radius in case of loiter, 0 if goTo
     * @param colorCode The color to paint, different colors may apply for: already visited, next, first.
     */

    public void addMarkerToPos(final int id, final LatLng latLng, final float radius, final float colorCode){//generic marker for waypoints
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("" + id)
                                .icon(BitmapDescriptorFactory.defaultMarker(colorCode))
                );
                if (radius>0){
                    CircleOptions circleOptions = new CircleOptions()
                            .center(latLng)
                            .radius(radius)
                            .strokeWidth(5);
                    Circle circle = googleMap.addCircle(circleOptions); // In meters
                    loiterCircleList.add(circle);
                }
                markersArrayList.add(marker);
            }
        });
    }

    public void paintLine(final LatLng latLng1, final LatLng latLng2){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PolylineOptions polylineOptions=
                        new PolylineOptions().add(latLng1,latLng2)
                                .width(5)
                                .color(Color.BLACK);

                Polyline polyline = googleMap.addPolyline(polylineOptions);
                linesPolylineList.add(polyline);

            }
        });
    }

    public void updateSysMarker(final Sys sys){

        if (googleMap==null || sys==null)
            return;
        Log.d(TAG,"updateSysMarker, "+sys.getName()+" | isOnMap="+sys.isOnMap()+" | m="+(sys.getMarker()==null));
        if (sys.isOnMap()==false){
            addMarkerToPos(sys);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = sys.getMarker();
                LatLng latLng = sys.getLatLng();
                float psi = sys.getPsi();

                Log.d(TAG, sys.getName()+", marker==null: "+(marker==null));
                marker.setPosition(latLng);
                marker.setRotation(AndroidUtil.calcRotation((360-googleMap.getCameraPosition().bearing), psi));
                if (sys==ASA.getInstance().getActiveSys())
                    marker.showInfoWindow();//show info

            }
        });

    }

    public void updateMyLocation(Location location){
        double lat = 0;
        double lon = 0;

        if (location!=null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
        if (initZoom==false && googleMap!=null && ASA.getInstance().getActiveSys()!=null && !ASA.getInstance().getActiveSys().getLatLng().equals(new LatLng(0,0))){
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(ASA.getInstance().getActiveSys().getLatLng(),17f,1.0f,1.0f)));
            initZoom=true;
        }

        if (initZoom==false && googleMap!=null && !(lat==0 && lon==0)){//center camera initialy on my position
            //lat = 41.2907;lon= -8.569;//lipa
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat, lon), 17f, 1.0f, 1.0f)));
            initZoom=true;
        }
        this.myLatLng = new LatLng(lat, lon);
    }

    public void updateCurrentPlanMarkers(List<PlanUtilities.Waypoint> waypointList){
        Log.v(TAG,"updateCurrentPlanMarkers");
        clearCurrentPlanMarkerList();
        int id=0;
        for (PlanUtilities.Waypoint waypoint : waypointList){
            LatLng latLng = new LatLng(waypoint.getLatitude(),waypoint.getLongitude());
            float radius = waypoint.getRadius();
            float colorCode = BitmapDescriptorFactory.HUE_BLUE;
            addMarkerToPos(id,latLng,radius,colorCode);
            id++;
        }
        paintLinesBettweenWaypoints(waypointList);
        if (ASA.getInstance().getActiveSys()!=null)
            ASA.getInstance().getActiveSys().setPaintedPlanID(ASA.getInstance().getActiveSys().getPlanID());
    }

    public void paintLinesBettweenWaypoints(List<PlanUtilities.Waypoint> waypointList){
        Log.v(TAG,"paintLinesBettweenWaypoints");
        for (int i=1;i<waypointList.size();i++){
            LatLng latLng1 = new LatLng(waypointList.get(i-1).getLatitude(),waypointList.get(i-1).getLongitude());
            LatLng latLng2 = new LatLng(waypointList.get(i).getLatitude(),waypointList.get(i).getLongitude());
            paintLine(latLng1,latLng2);
        }
    }

    public void clearCurrentPlanMarkerList(){
        Log.v(TAG,"clearCurrentPlanMarkerList");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Marker marker : markersArrayList){
                    marker.remove();
                }
                for (Polyline polyline : linesPolylineList){
                    polyline.remove();
                }
                for (Circle circle : loiterCircleList){
                    circle.remove();
                }
            }
        });

        markersArrayList.clear();
        linesPolylineList.clear();
        loiterCircleList.clear();
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
