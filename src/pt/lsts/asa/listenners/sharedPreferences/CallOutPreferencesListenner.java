
package pt.lsts.asa.listenners.sharedPreferences;

import pt.lsts.asa.ASA;
import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.feedback.CallOutService;
import pt.lsts.asa.settings.Settings;

import java.util.Locale;

import android.util.Log;

import com.squareup.otto.Subscribe;


public class CallOutPreferencesListenner{
    private final String TAG = "CallOutPreferencesListenner";
    private CallOut callOut = null;
    private CallOutService callOutService = null;

    public CallOutPreferencesListenner(CallOut callOut){
        this.callOut=callOut;
    }

    public CallOutPreferencesListenner(CallOutService callOutService){
        this.callOutService=callOutService;
    }

    @Subscribe
    public void onPreferenceChanged(String key) {
        Log.v(TAG, "Preference with "+key+" changed");

        String keyLowerCase = key.toLowerCase(Locale.getDefault());
        int integer;
        boolean bool;
        switch(keyLowerCase){
            case "global_audio":
                bool = Settings.getBoolean(key, true);
                if (callOut!=null)
                    callOut.setGlobalMuteBool(bool);
                if (callOutService!=null)
                    //callOutService.setGlobalMuteBool(bool);
                break;
            case "altitude_audio":
                bool = Settings.getBoolean(key, true);
                if (callOut!=null)
                    callOut.setAltMuteBool(bool);
                if (callOutService!=null)
                    //callOutService.setAltMuteBool(bool);
                break;
            case "ias_audio":
                bool = Settings.getBoolean(key, true);
                if (callOut!=null)
                    callOut.setIasMuteBool(bool);
                if (callOutService!=null)
                    //callOutService.setIasMuteBool(bool);
                break;
            case "timeout_audio":
                bool = Settings.getBoolean(key, true);
                if (callOut!=null)
                    callOut.setTimeoutBool(bool);
                if (callOutService!=null)
                    //callOutService.setTimeoutBool(bool);
                break;
            case "altitude_interval_in_seconds":
                integer = Settings.getInt(key, 10)*1000;
                if (callOut!=null)
                    callOut.setAltInterval(integer);
                if (callOutService!=null)
                    //callOutService.setAltInterval(integer);
                break;
            case "ias_interval_in_seconds":
                integer = Settings.getInt(key, 10)*1000;
                if (callOut!=null)
                    callOut.setIasInterval(integer);
                if (callOutService!=null)
                    //callOutService.setIasInterval(integer);
                break;
            case "timeout_interval_in_seconds":
                integer = Settings.getInt(key, 60)*1000;
                if (callOut!=null)
                    callOut.setTimeoutInterval(integer);
                if (callOutService!=null)
                    //callOutService.setTimeoutInterval(integer);
                break;
            case "speech_rate":
                integer = Settings.getInt(key, 100);
                if (callOut!=null)
                    callOut.setSpeechRate(integer);
                if (callOutService!=null)
                    //callOutService.setSpeechRate(integer);
                break;
            default:
                Log.e(TAG,"Setting changed unrecognized: "+key);
                break;
        }
    }

}
