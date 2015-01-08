package pt.lsts.asa.subscribers;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IndicatedSpeed;

import android.util.Log;

public class CallOutSubscriber implements IMCSubscriber{

	private CallOut callOut;
	private Sys activeSys;
    private Thread thread;
	
	public CallOutSubscriber(CallOut callOut) {
		this.callOut = callOut;
	}
	
	@Override
	public void onReceive(final IMCMessage msg) {

        if (thread!=null)//if there is a previous message, finish going through that one
            while (thread.isAlive());

        thread = new Thread() {
            @Override
            public void run() {
		
                activeSys = ASA.getInstance().getActiveSys();

                Log.v("CallOut", "Received Message");

                if (IMCUtils.isMsgFromActive(msg)){
                    final int ID_MSG = msg.getMgid();
                    if (ID_MSG == IndicatedSpeed.ID_STATIC)
                        callOut.setIasValue((Double) msg.getValue("value"));
                    if (ID_MSG == EstimatedState.ID_STATIC)
                        callOut.setAltValue(((Float)msg.getValue("height")));
                    callOut.setLastMsgReceived(msg.getTimestampMillis());
                }

            }
        };
        thread.start();
		
	}

}
