package pt.lsts.asa.listenners.sysUpdates;

import android.util.Log;
import android.util.Pair;

import com.squareup.otto.Subscribe;

import java.util.Locale;

import pt.lsts.asa.fragments.AltitudeBarFragment;
import pt.lsts.asa.fragments.BatteryIndicatorFragment;
import pt.lsts.asa.util.BatteryIndicatorPairUtil;

/**
 * Created by jloureiro on 3/17/15.
 */
public class AltitudeBarSysUpdaterListenner {

    private final String TAG = "AltBarSysUpdaterList";
    public static final boolean DEBUG = false;

    private AltitudeBarFragment altitudeBarFragment=null;


    public AltitudeBarSysUpdaterListenner(AltitudeBarFragment altitudeBarFragment){
        this.altitudeBarFragment = altitudeBarFragment;
    }

    @Subscribe
    public void onAltIntegerValChanged(Pair<String,Integer> pair) {
        String valChanged = pair.first;
        Integer newVal = pair.second;

        String valChangedLowerCase = valChanged.toLowerCase(Locale.getDefault());
        switch(valChangedLowerCase){
            case "alt":
                Log.d(TAG,"received alt event");
                altitudeBarFragment.setVehicleAlt(newVal);
                break;
            case "altplanned":
                Log.d(TAG,"received altPlanned event");
                altitudeBarFragment.setPlanAlt(newVal);
                break;
            default:
                if (DEBUG)
                    Log.e(TAG,"String changed unrecognized: "+valChanged);
                break;
        }
    }

}
