package pt.lsts.asa.listenners.sysUpdates;

import android.util.Log;
import android.util.Pair;

import com.squareup.otto.Subscribe;

import java.util.Locale;

import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.feedback.CallOutService;
import pt.lsts.asa.settings.Settings;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IndicatedSpeed;

/**
 * Created by jloureiro on 2/18/15.
 */
public class CallOutSysUpdaterListenner {

    private final String TAG = "CallOutUpdaterListenner";
    public static final boolean DEBUG = false;

    private CallOutService callOutService = null;

    public CallOutSysUpdaterListenner(CallOutService callOutService){
        this.callOutService=callOutService;
    }

    @Subscribe
    public void onAltIASintegerValChanged(Pair<String,Integer> pair) {
        String valChanged = pair.first;
        Integer newVal = pair.second;

        String valChangedLowerCase = valChanged.toLowerCase(Locale.getDefault());
        switch(valChangedLowerCase){
            case "alt":
                callOutService.setAltInt(newVal);
                break;
            case "ias":
                callOutService.setIasInt(newVal);
                break;
            default:
                if (DEBUG)
                    Log.e(TAG,"String changed unrecognized: "+valChanged);
                break;
        }
    }

    @Subscribe
    public void onLowFuelLevel(String s){
        callOutService.onLowFuelLevel(s);
    }

    @Subscribe
    public void onMsgReceived(Integer IMCMsgID_STATIC){
        switch (IMCMsgID_STATIC){
            case EstimatedState.ID_STATIC:
                callOutService.setLastEstimatedStateMsgReceived();
                break;
            case IndicatedSpeed.ID_STATIC:
                callOutService.setLastIndicatedSpeedMsgReceived();
                break;
            default:
                callOutService.setLastMsgReceived();
                break;
        }

    }

}

