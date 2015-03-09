package pt.lsts.asa.listenners;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.location.LocationListener;

import pt.lsts.asa.fragments.GmapFragment;


/**
 * Created by jloureiro on 1/20/15.
 */
public class MyLocationListener{

    private LocationManager locationManager = null;
    private GmapFragment gmapFragment = null;


    public MyLocationListener(final GmapFragment gmapFragment) {
        this.gmapFragment=gmapFragment;

        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) gmapFragment.getFragmentActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    public void initLocationListener(){

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                gmapFragment.updateMyLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
}