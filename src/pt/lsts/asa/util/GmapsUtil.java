package pt.lsts.asa.util;

import com.google.android.gms.maps.GoogleMap;

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

}
