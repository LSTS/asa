package pt.lsts.asa.listenners.sysUpdates;

import android.util.Log;
import android.util.Pair;

import com.squareup.otto.Subscribe;

import java.util.Locale;

import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.feedback.CallOutService;
import pt.lsts.asa.settings.Settings;

/**
 * Created by jloureiro on 2/18/15.
 */
public class CallOutSysUpdaterListenner {

    private final String TAG = "CallOutPreferencesListenner";
    public static final boolean DEBUG = false;

    private CallOutService callOutService = null;

    public CallOutSysUpdaterListenner(CallOutService callOutService){
        this.callOutService=callOutService;
    }

    @Subscribe
    public void onPreferenceChanged(Pair<String,Integer> pair) {
        String valChanged = pair.first;
        Integer newVal = pair.second;

        if (DEBUG)
            Log.v(TAG, "Preference with " + valChanged + " changed");

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
                    Log.e(TAG,"Setting changed unrecognized: "+valChanged);
                break;
        }
    }

}

