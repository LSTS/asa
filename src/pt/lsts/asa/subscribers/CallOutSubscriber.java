package pt.lsts.asa.subscribers;

import android.util.Log;
import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.sys.Sys;
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
		
		if (activeSys.getName().equals(msg.getString("sys_name"))){
			if (msg.getAbbrev().equalsIgnoreCase("IndicatedSpeed"))
				callOut.setIasValue((Float) msg.getValue("value"));
			if (msg.getAbbrev().equalsIgnoreCase("EstimatedState"))
				callOut.setAltValue((Float) msg.getValue("height"));
		}
		
		Log.i("CallOutSource",msg.getSourceName());
		Log.i("CallOutType",msg.getAbbrev());
		Log.i("CallOutEverything",msg.toString());
		
	}

}
