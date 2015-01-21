
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
        Log.v("CallOutPreferencesListenner", "Preference with "+key+" changed");

        String keyLowerCase = key.toLowerCase(Locale.getDefault());
        int integer;
        boolean bool;
        switch(keyLowerCase){
            case "audio_global_audio":
                bool = Settings.getBoolean(key, true);
                callOut.setGlobalMuteBool(bool);
                break;
            case "audio_altitude_audio":
                bool = Settings.getBoolean(key, true);
                callOut.setAltMuteBool(bool);
                break;
            case "audio_ias_audio":
                bool = Settings.getBoolean(key, true);
                callOut.setIasMuteBool(bool);
                break;
            case "audio_altitude_interval_in_seconds":
                integer = Settings.getInt(key, 10)*1000;
                callOut.setAltInterval(integer);
                break;
            case "audio_ias_interval_in_seconds":
                integer = Settings.getInt(key, 10)*1000;
                callOut.setIasInterval(integer);
                break;
            case "comms_timeout_interval_in_seconds":
                integer = Settings.getInt(key, 60)*1000;
                callOut.setTimeoutInterval(integer);
                break;

            default:
                Log.e(TAG,"Setting changed unrecognized: "+key);
                break;
        }
    }

}
