package pt.lsts.asa.subscribers;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.fragments.ManualIndicatorsFragment;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IndicatedSpeed;

/**
 * Created by jloureiro on 1/21/15.
 */
public class ManualIndicatorsFragmentIMCSubscriber implements IMCSubscriber{

    private final String TAG = "ManualIndicatorsFragmentIMCSubscriber";
    private ManualIndicatorsFragment manualIndicatorsfragment = null;
    private Thread thread;

    private NumberFormat formatter = new DecimalFormat("#0");

    public ManualIndicatorsFragmentIMCSubscriber(ManualIndicatorsFragment manualIndicatorsfragment){
        this.manualIndicatorsfragment=manualIndicatorsfragment;
    }
    
    @Override
    public void onReceive(final IMCMessage msg) {
        if (thread!=null)//if there is a previous message, finish going through that one
            while (thread.isAlive());

        thread = new Thread() {
            @Override
            public void run() {
/*
                Log.v(TAG, "Received Message");

                if (IMCUtils.isMsgFromActive(msg)){
                    //manualIndicatorsfragment.startScheduler(false);
                    Log.v(TAG,"Message from active:"+msg.getAbbrev());
                    final int ID_MSG = msg.getMgid();
                    if (ID_MSG == IndicatedSpeed.ID_STATIC){
                        Double ias = (Double) msg.getValue("value");
                        Log.v(TAG,"received speed="+ias);
                        long previousIasLong = Math.round(manualIndicatorsfragment.getIas());
                        long newIasLong = Math.round(ias);
                        if (previousIasLong!=newIasLong) {
                            manualIndicatorsfragment.setLeftTextView(" IAS: " + formatter.format(ias) + " ");
                            //manualIndicatorsfragment.setIas(ias);
                        }
                    }
                    if (ID_MSG == EstimatedState.ID_STATIC) {
                        Float alt = -((Float) msg.getValue("z"));
                        Log.v(TAG, "received alt=" + alt);
                        //long previousAltLong = Math.round(manualIndicatorsfragment.getAlt());
                        long newAltLong = Math.round(alt);
                        if (previousAltLong!=newAltLong) {
                            manualIndicatorsfragment.setRightTextView(" Alt: " + formatter.format(alt)+" ");
                            manualIndicatorsfragment.setAlt(alt);
                        }
                    }
                }
*/
            }

        };
        //thread.start();
    }
}
