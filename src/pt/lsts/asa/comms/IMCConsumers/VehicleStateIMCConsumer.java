package pt.lsts.asa.comms.IMCConsumers;

import android.util.Log;

import pt.lsts.asa.ASA;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.IMCMessage;

/**
 * Created by jloureiro on 2/25/15.
 */
public class VehicleStateIMCConsumer implements IMCConsumer{

    public static final String TAG = "VehicleState";
    private SystemList systemList;

    public VehicleStateIMCConsumer(){
        this.systemList= ASA.getInstance().getSystemList();
    }

    public VehicleStateIMCConsumer(SystemList systemList){
        this.systemList=systemList;
    }


    @Override
    public void consume(IMCMessage msg) {// Process VehicleState to get error count
        Sys sys = systemList.findSysById((Integer) msg.getHeaderValue("src"));
        boolean isFromActive = IMCUtils.isMsgFromActive(msg);

        Log.i(TAG, "Received VehicleState: " + msg.toString());
        int errors = msg.getInteger("error_count");
        if (sys != null) // Meaning it exists on the list
        {
            Log.i(TAG, "" + errors);
            sys.setError(errors > 0);
            systemList.changeList(systemList.getList()); // Update the list
        }

    }
}
