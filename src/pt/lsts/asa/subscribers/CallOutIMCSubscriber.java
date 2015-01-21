package pt.lsts.asa.subscribers;

import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.fragments.ManualIndicatorsFragment;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IndicatedSpeed;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.util.Log;
import android.view.View;


public class CallOutIMCSubscriber implements IMCSubscriber{

	private final String TAG = "CallOutIMCSubscriber";
    private Thread thread;
    private CallOut callOut;
	
	public CallOutIMCSubscriber(CallOut callOut) {
		this.callOut = callOut;
	}

	@Override
	public void onReceive(final IMCMessage msg) {

        if (thread!=null)//if there is a previous message, finish going through that one
            while (thread.isAlive());

        thread = new Thread() {
            @Override
            public void run() {

                Log.v(TAG, "Received Message");

                if (IMCUtils.isMsgFromActive(msg)){
                    final int ID_MSG = msg.getMgid();
                    if (ID_MSG == IndicatedSpeed.ID_STATIC){
                        Double ias = (Double) msg.getValue("value");
                        Log.v(TAG,"IndicatedSpedd received: ias="+ias);
                        callOut.setIasValue(ias);
                    }
                    if (ID_MSG == EstimatedState.ID_STATIC){
                        Float alt = (Float) msg.getValue("height");
                        Log.v(TAG,"EstimatedState received: alt="+alt);
                        Log.v("EstimatedState","lat:"+msg.getValue("lat")+" | lon:"+msg.getValue("lon"));
                        callOut.setAltValue(alt);
                    }
                    callOut.setLastMsgReceived(msg.getTimestampMillis());
                }

            }
        };
        thread.start();
		
	}

}
