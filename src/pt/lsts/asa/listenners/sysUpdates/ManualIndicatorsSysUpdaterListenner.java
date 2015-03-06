package pt.lsts.asa.listenners.sysUpdates;

import android.util.Log;
import android.util.Pair;

import com.squareup.otto.Subscribe;

import java.util.Locale;

import pt.lsts.asa.ASA;
import pt.lsts.asa.feedback.CallOutService;
import pt.lsts.asa.fragments.ManualIndicatorsFragment;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.imc.AutopilotMode;

/**
 * Created by jloureiro on 2/19/15.
 */
public class ManualIndicatorsSysUpdaterListenner {

    private final String TAG = "CallOutPreferencesListenner";
    public static final boolean DEBUG = false;

    private ManualIndicatorsFragment manualIndicatorsFragment = null;

    public ManualIndicatorsSysUpdaterListenner(ManualIndicatorsFragment manualIndicatorsFragment){
        this.manualIndicatorsFragment = manualIndicatorsFragment;
    }

    @Subscribe
    public void onAltIASintegerValChanged(Pair<String, Integer> pair) {
        String valChanged = pair.first;
        Integer newVal = pair.second;

        manualIndicatorsFragment.setLastMsgReceived(System.currentTimeMillis());

        String valChangedLowerCase = valChanged.toLowerCase(Locale.getDefault());
        switch(valChangedLowerCase){
            case "ias":
                manualIndicatorsFragment.setLeftTextView(newVal);
                break;
            case "alt":
                manualIndicatorsFragment.setRightTextView(newVal);
                break;
            default:
                if (DEBUG)
                    Log.e(TAG,"String changed unrecognized: "+valChanged);
                break;
        }
    }

    @Subscribe
    public void onModeChanged(AutopilotMode.AUTONOMY autonomy){
        AndroidUtil.showToastLong(manualIndicatorsFragment.getFragmentActivity(), ASA.getInstance().getActiveSys().getName()+"'s in "+autonomy.toString()+" Mode");
    }

}
