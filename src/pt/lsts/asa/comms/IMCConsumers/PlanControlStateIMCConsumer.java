package pt.lsts.asa.comms.IMCConsumers;

import android.util.Log;

import pt.lsts.asa.ASA;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanDB;

/**
 * Created by jloureiro on 2/25/15.
 */
public class PlanControlStateIMCConsumer implements IMCConsumer{

    public static final String TAG = "PlanControlState";
    private SystemList systemList;

    public PlanControlStateIMCConsumer(){
        this.systemList = ASA.getInstance().getSystemList();
    }

    public PlanControlStateIMCConsumer(SystemList systemList){
        this.systemList=systemList;
    }

    @Override
    public void consume(IMCMessage msg) {
        Sys sys = systemList.findSysById((Integer) msg.getHeaderValue("src"));
        boolean isFromActive = IMCUtils.isMsgFromActive(msg);

        if (isFromActive) {
            Log.i(TAG, "PlanControlState: \n" + msg.toString());
            PlanControlState planControlState = (PlanControlState) msg;
            if (planControlState.getState() == PlanControlState.STATE.EXECUTING) {
                String planID = planControlState.getPlanId();
                boolean changed = sys.setPlanID(planID);
                if (changed == true) {
                    Log.i(TAG, "PlanControlState: \n" + "Changed Plan to: " + planID);
                    //notification to user
                    //planDB.request(planID) -> planDB.reply(arg=Plan Spec)
                    
                }
            }
        }
    }

}
