package pt.lsts.asa.util;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import pt.lsts.asa.ASA;
import pt.lsts.asa.sys.Sys;

/**
 * Created by jloureiro on 1/27/15.
 */
public class GmapsUtil {

    /**
     *
     * @param psi 0=N 90=E 180=S 270=W Clockwise
     * @return Gmaps Rotation 0=E 90=S 180=W 270=N Clockwise
     */
    public static float getRotation(float psi){
        float result = psi-90;
        return result;
    }

    /**
     *
     * @param psi 0=N 90=E 180=S 270=W Clockwise
     * @param googleMap include user rotation of map
     * @return Gmaps Rotation 0=E 90=S 180=W 270=N Clockwise
     */
    public static float getRotation(float psi, GoogleMap googleMap){
        float result = psi-90;
        result = result + googleMap.getCameraPosition().bearing;
        return result;
    }

    public static LatLng translateCoordinates(final LatLng origpoint, final float offsetX, final float offsetY) {
        /*
        final double earthRadius = 6371000;

        final double newLat = origpoint.latitude + (offsetX / earthRadius) * 180 / Math.PI;
        final double newLon = origpoint.longitude + (offsetY / (earthRadius * Math.cos(newLat * 180 / Math.PI))) * 180 / Math.PI;

        return new LatLng(newLat, newLon);
        */
        //Position, decimal degrees
        double lat = origpoint.latitude;
        double lon = origpoint.longitude;

        //Earth’s radius, sphere
        double R=6378137;

        //offsets in meters
        double dn = offsetX;
        double de = offsetY;

        //Coordinate offsets in radians
        double dLat = dn/R;
        double dLon = de/(R*Math.cos(Math.PI * lat / 180));

        //OffsetPosition, decimal degrees
        double latO = lat + dLat * 180/Math.PI;
        double lonO = lon + dLon * 180/Math.PI;

        return new LatLng(latO, lonO);
    }

}
