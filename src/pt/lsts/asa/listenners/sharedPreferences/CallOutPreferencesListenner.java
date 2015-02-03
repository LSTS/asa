
package pt.lsts.asa.listenners.sharedPreferences;

import pt.lsts.asa.ASA;
import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.settings.Settings;

import java.util.Locale;

import android.util.Log;

import com.squareup.otto.Subscribe;


public class CallOutPreferencesListenner{
    private final String TAG = "CallOutPreferencesListenner";
    private CallOut callOut = null;

    public CallOutPreferencesListenner(CallOut callOut){
        this.callOut=callOut;
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
                callOut.setGlobalMuteBool(bool);
                break;
            case "altitude_audio":
                bool = Settings.getBoolean(key, true);
                callOut.setAltMuteBool(bool);
                break;
            case "ias_audio":
                bool = Settings.getBoolean(key, true);
                callOut.setIasMuteBool(bool);
                break;
            case "timeout_audio":
                bool = Settings.getBoolean(key, true);
                callOut.setTimeoutBool(bool);
                break;
            case "altitude_interval_in_seconds":
                integer = Settings.getInt(key, 10)*1000;
                callOut.setAltInterval(integer);
                break;
            case "ias_interval_in_seconds":
                integer = Settings.getInt(key, 10)*1000;
                callOut.setIasInterval(integer);
                break;
            case "timeout_interval_in_seconds":
                integer = Settings.getInt(key, 60)*1000;
                callOut.setTimeoutInterval(integer);
                break;
            case "speech_rate":
                integer = Settings.getInt(key, 100);
                callOut.setSpeechRate(integer);
                break;
            default:
                Log.e(TAG,"Setting changed unrecognized: "+key);
                break;
        }
    }

}
