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


public class CallOutSubscriber implements IMCSubscriber{

	private CallOut callOut;
    private ManualIndicatorsFragment manualIndicatorsfragment = null;
    private Thread thread;

    NumberFormat formatter = new DecimalFormat("#0.00");
	
	public CallOutSubscriber(CallOut callOut) {
		this.callOut = callOut;
	}

    public void setManualIndicatorsFragment(ManualIndicatorsFragment manualIndicatorsfragment){
        this.manualIndicatorsfragment = manualIndicatorsfragment;
    }

	@Override
	public void onReceive(final IMCMessage msg) {

        if (thread!=null)//if there is a previous message, finish going through that one
            while (thread.isAlive());

        thread = new Thread() {
            @Override
            public void run() {

                Log.v("CallOut", "Received Message");

                if (IMCUtils.isMsgFromActive(msg)){
                    final int ID_MSG = msg.getMgid();
                    if (ID_MSG == IndicatedSpeed.ID_STATIC){
                        Double ias = (Double) msg.getValue("value");
                        callOut.setIasValue(ias);
                        if (manualIndicatorsfragment!=null)
                            manualIndicatorsfragment.setLeftTextView("IAS: "+formatter.format(ias));
                    }
                    if (ID_MSG == EstimatedState.ID_STATIC){
                        Float alt = (Float) msg.getValue("height");
                        callOut.setAltValue(alt);
                        if (manualIndicatorsfragment!=null)
                            manualIndicatorsfragment.setRightTextView("Alt: " + formatter.format(alt));
                    }
                    callOut.setLastMsgReceived(msg.getTimestampMillis());
                }

            }
        };
        thread.start();
		
	}

}
