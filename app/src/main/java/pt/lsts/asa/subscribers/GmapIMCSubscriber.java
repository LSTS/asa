package pt.lsts.asa.subscribers;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.fragments.GmapFragment;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;



/**
 * Created by jloureiro on 1/22/15.
 */
public class GmapIMCSubscriber implements IMCSubscriber {

    private final String TAG = "GmapIMCSubscriber";
    private GmapFragment gmapfragment = null;
    private Thread thread;
    private float r_earth = 6378 * 1000; //meters

    public GmapIMCSubscriber(GmapFragment gmapFragment){
        this.gmapfragment=gmapFragment;
    }

    @Override
    public void onReceive(final IMCMessage msg) {
        if (thread!=null)//if there is a previous message, finish going through that one
            while (thread.isAlive());

        thread = new Thread() {
            @Override
            public void run() {
                if (gmapfragment.getGoogleMap()==null)
                    return;
                Log.v(TAG, "Received Message");
                Sys sys = ASA.getInstance().getSystemList().findSysById(msg.getSrc());
                if (sys==null)
                    return;
                Log.v(TAG, "Received Message from "+sys.getId()+", "+sys.getName());
                final int ID_MSG = msg.getMgid();
                if (ID_MSG == EstimatedState.ID_STATIC){
                    Double latRad = msg.getDouble("lat");
                    Double lonRad = msg.getDouble("lon");
                    Double latDeg = (Double) Math.toDegrees(latRad);
                    Double lonDeg = (Double) Math.toDegrees(lonRad);

                    float offsetX = msg.getFloat("x");//offset north
                    float offsetY = msg.getFloat("y");//offset east

                    LatLng latLng = translateCoordinates(new LatLng(latDeg,lonDeg), offsetX, offsetY);

                    Log.i(TAG,"latLng="+latLng.toString());
                    //gmapfragment.setActiveSysLatLng(latLng);
                    sys.setLatLng(latLng);

                    float psi = msg.getFloat("psi");
                    double psiDouble = psi;
                    //gmapfragment.setActiveSysPsi((float) Math.toDegrees(psiDouble));
                    sys.setPsi((float) Math.toDegrees(psiDouble));
                    //gmapfragment.updateActiveSysMarker();
                    gmapfragment.updateSysMarker(sys);
                }

            }
        };
        thread.start();
    }

    public LatLng translateCoordinates(final LatLng origpoint, final float offsetX, final float offsetY) {
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
