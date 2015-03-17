package pt.lsts.asa.listenners.sysUpdates;

import android.util.Log;
import android.util.Pair;

import com.squareup.otto.Subscribe;

import java.util.Locale;

import pt.lsts.asa.fragments.AltBarFragment;

/**
 * Created by jloureiro on 3/17/15.
 */
public class AltBarSysUpdaterListenner {

    private final String TAG = "AltBarSysUpdaterListnr";
    public static final boolean DEBUG = false;

    private AltBarFragment altBarFragment =null;


    public AltBarSysUpdaterListenner(AltBarFragment altBarFragment){
        this.altBarFragment = altBarFragment;
    }

    @Subscribe
    public void onAltIntegerValChanged(Pair<String,Integer> pair) {
        String valChanged = pair.first;
        Integer newVal = pair.second;

        String valChangedLowerCase = valChanged.toLowerCase(Locale.getDefault());
        switch(valChangedLowerCase){
            case "alt":
                Log.d(TAG,"received alt event");
                altBarFragment.setVehicleAlt(newVal);
                break;
            case "altplanned":
                Log.d(TAG,"received altPlanned event");
                altBarFragment.setPlanAlt(newVal);
                break;
            default:
                if (DEBUG)
                    Log.e(TAG,"String changed unrecognized: "+valChanged);
                break;
        }
    }

}
