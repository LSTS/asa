package pt.lsts.asa.subscribers;

import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.fragments.GmapFragment;
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
                if (IMCUtils.isMsgFromActive(msg)){
                    Log.v(TAG,"Message from active:"+msg.getAbbrev());
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
                        gmapfragment.setActiveSysLatLng(latLng);

                        float psi = msg.getFloat("psi");
                        double psiDouble = psi;
                        gmapfragment.setActiveSysPsi((float) Math.toDegrees(psiDouble));
                        gmapfragment.updateActiveSysMarker();
                    }
                }

            }
        };
        thread.start();
    }

    public LatLng translateCoordinates(final LatLng origpoint, final float offsetX, final float offsetY) {
        final double earthRadius = 6371000;

        final double newLat = origpoint.latitude + (offsetX / earthRadius) * 180 / Math.PI;
        final double newLon = origpoint.longitude + (offsetY / (earthRadius * Math.cos(newLat * 180 / Math.PI))) * 180 / Math.PI;

        return new LatLng(newLat, newLon);
    }
}
