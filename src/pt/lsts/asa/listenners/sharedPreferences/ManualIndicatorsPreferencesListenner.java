package pt.lsts.asa.listenners.sharedPreferences;

import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.Locale;

import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.fragments.ManualIndicatorsFragment;
import pt.lsts.asa.settings.Settings;

/**
 * Created by jloureiro on 1/21/15.
 */
public class ManualIndicatorsPreferencesListenner {

    private final String TAG = "ManualIndicatorsFragment";
    private ManualIndicatorsFragment manualIndicatorsFragment;

    public ManualIndicatorsPreferencesListenner(ManualIndicatorsFragment manualIndicatorsFragment){
        this.manualIndicatorsFragment= manualIndicatorsFragment;
    }

    @Subscribe
    public void onPreferenceChanged(String key) {
        Log.v(TAG, "Preference with " + key + " changed");
        String keyLowerCase = key.toLowerCase(Locale.getDefault());
        int integer;
        boolean bool;
        switch(keyLowerCase){
            case "comms_timeout_interval_in_seconds":
                integer = Settings.getInt(key, 60)*1000;
                manualIndicatorsFragment.setInterval(integer);
                break;
            default:
                Log.e(TAG,"Setting changed unrecognized: "+key);
                break;
        }
    }

}
