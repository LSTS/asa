package pt.lsts.asa.comms.IMCConsumers;

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCConsumers.IMCConsumer;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.GmapsUtil;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.IMCMessage;

/**
 * Created by jloureiro on 2/25/15.
 */
public class EstimatedStateIMCConsumer implements IMCConsumer {

    public static final String TAG = "EstimatedState";
    private SystemList systemList;

    public EstimatedStateIMCConsumer(){
        systemList=ASA.getInstance().getSystemList();
    }

    public EstimatedStateIMCConsumer(SystemList systemList){
        this.systemList=systemList;
    }

    @Override
    public void consume(IMCMessage msg) {
        Sys sys = systemList.findSysById((Integer) msg.getHeaderValue("src"));
        boolean isFromActive = IMCUtils.isMsgFromActive(msg);

        Float alt = - ((Float) msg.getValue("z"));
        sys.setAlt(alt);
        int altInt = Math.round(alt);
        Log.i(TAG,"altDouble= "+alt+" | altInt="+altInt+" | sys.getAltInt()="+sys.getAltInt());
        if (altInt!=sys.getAltInt()){
            sys.setAltInt(altInt);
            if (isFromActive){
                Log.i(TAG,"alt: getActiveSys().equals(sys)");
                ASA.getInstance().getBus().post(new Pair<String,Integer>("alt",altInt));
            }

        }
        Double latRad = msg.getDouble("lat");
        Double lonRad = msg.getDouble("lon");
        Double latDeg = (Double) Math.toDegrees(latRad);
        Double lonDeg = (Double) Math.toDegrees(lonRad);

        float offsetX = msg.getFloat("x");//offset north
        float offsetY = msg.getFloat("y");//offset east

        LatLng latLng = GmapsUtil.translateCoordinates(new LatLng(latDeg, lonDeg), offsetX, offsetY);

        Log.i(TAG,"latLng="+latLng.toString());
        //gmapfragment.setActiveSysLatLng(latLng);
        sys.setLatLng(latLng);

        float psi = msg.getFloat("psi");
        double psiDouble = psi;
        sys.setPsi((float) Math.toDegrees(psiDouble));
        //gmapfragment.updateSysMarker(sys);
        //call OTTO
    }
}
