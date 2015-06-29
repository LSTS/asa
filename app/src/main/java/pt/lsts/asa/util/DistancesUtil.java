package pt.lsts.asa.util;

import com.google.android.gms.maps.model.LatLng;

import pt.lsts.asa.ASA;
import pt.lsts.asa.sys.Sys;

/**
 * Created by jloureiro on 29-06-2015.
 */
public class DistancesUtil {

    /**
     * Calc distance in meters, betweeen 2 lat,lon in degress
     * @param latLng1 point origin lat,lon in degress
     * @param latLng2 point of destination lat,lon in degress
     * @return the distance in meters
     */
    public static double distanceBetweenTwoPoints(LatLng latLng1, LatLng latLng2){
        double earthRadius = 6371000; // metres
        double lat1Rad = Math.toRadians(latLng1.latitude);
        double lat2Rad = Math.toRadians(latLng1.latitude);
        double latDiffRad = Math.toRadians((latLng1.latitude - latLng1.latitude));
        double lonDiffRad = Math.toRadians((latLng2.longitude-latLng1.longitude));

        double a = Math.sin(latDiffRad/2) * Math.sin(latDiffRad/2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(lonDiffRad/2) * Math.sin(lonDiffRad/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double distance = earthRadius * c;
        return distance;
    }

    /**
     * Calc the relative landing value for TextToSpeech
     * @param activeSys the main sys
     * @param targetLandingSys the target landing sys
     * @param angle the inclination angle defined in settings
     * @return the value to be used by TextToSpeech (integer)
     */
    public static int calcRelativeLandingValue(Sys activeSys, Sys targetLandingSys, double angle){
        double distance = distanceBetweenTwoPoints(activeSys.getLatLng(), targetLandingSys.getLatLng());
        double result = activeSys.getAlt() - distance*Math.tan(angle);
        int resultInt = ((int) result);
        return resultInt;
    }
}
