package pt.lsts.asa.comms.IMCConsumers;

import android.util.Log;
import android.util.Pair;

import pt.lsts.asa.ASA;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.IMCMessage;

/**
 * Created by jloureiro on 2/25/15.
 */
public class IndicatedSpeedIMCConsumer implements IMCConsumer{

    public static final String TAG = "IndicatedSpeed";
    private SystemList systemList;

    public IndicatedSpeedIMCConsumer(){
        this.systemList = ASA.getInstance().getSystemList();
    }
    public IndicatedSpeedIMCConsumer(SystemList systemList){
        this.systemList=systemList;
    }


    @Override
    public void consume(IMCMessage msg) {
        Sys sys = systemList.findSysById((Integer) msg.getHeaderValue("src"));
        boolean isFromActive = IMCUtils.isMsgFromActive(msg);

        Log.i(TAG, "IndicatedSpeed");
        Double ias = (Double) msg.getValue("value");
        Log.v(TAG,"IndicatedSpedd received: ias="+ias);
        sys.setIas(ias);
        int iasInt = (int) Math.round(ias);
        Log.i(TAG,"iasDouble= "+ias+" | iasInt="+iasInt+" | sys.getIasInt()="+sys.getIasInt());
        if (iasInt!=sys.getIasInt()){
            sys.setIasInt(iasInt);
            if (isFromActive){
                Log.i(TAG,"getActiveSys().equals(sys)");
                ASA.getInstance().getBus().post(new Pair<String,Integer>("ias",iasInt));
            }
        }
        //call OTTO

    }
}
