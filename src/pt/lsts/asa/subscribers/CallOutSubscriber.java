package pt.lsts.asa.subscribers;

import android.util.Log;
import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.IMCMessage;

public class CallOutSubscriber implements IMCSubscriber{

	private CallOut callOut;
	private Sys activeSys;
	
	public CallOutSubscriber(CallOut callOut) {
		this.callOut = callOut;
	}
	
	@Override
	public void onReceive(IMCMessage msg) {
		
		activeSys = ASA.getInstance().getActiveSys();
		
		Log.v("CallOut", "Received Message");
		
		if (IMCUtils.isMsgFromActive(msg)){
			if (msg.getAbbrev().equalsIgnoreCase("IndicatedSpeed"))
				callOut.setIasValue((Double) msg.getValue("value"));
			if (msg.getAbbrev().equalsIgnoreCase("EstimatedState"))
				callOut.setAltValue((Double) msg.getValue("height"));
		}		
		
	}

}
