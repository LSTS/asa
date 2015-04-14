package pt.lsts.asa.listenners.sysUpdates;

import android.util.Log;
import android.util.Pair;

import com.squareup.otto.Subscribe;

import java.util.Locale;

import pt.lsts.asa.ASA;
import pt.lsts.asa.fragments.LeftRightTopTextViewsFragment;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.imc.AutopilotMode;

/**
 * Created by jloureiro on 2/19/15.
 */
public class LeftRightTopTextViewsSysUpdaterListenner {

    private final String TAG = "LeftRightTopTextViewsSysUpdaterListenner";
    public static final boolean DEBUG = false;

    private LeftRightTopTextViewsFragment leftRightTopTextViewsFragment = null;

    public LeftRightTopTextViewsSysUpdaterListenner(LeftRightTopTextViewsFragment leftRightTopTextViewsFragment){
        this.leftRightTopTextViewsFragment = leftRightTopTextViewsFragment;
    }

    @Subscribe
    public void onAltIASintegerValChanged(Pair<String, Integer> pair) {
        String valChanged = pair.first;
        Integer newVal = pair.second;

        leftRightTopTextViewsFragment.setLastMsgReceived(System.currentTimeMillis());

        String valChangedLowerCase = valChanged.toLowerCase(Locale.getDefault());
        switch(valChangedLowerCase){
            case "ias":
                leftRightTopTextViewsFragment.setLeftTextView(newVal);
                break;
            case "alt":
                leftRightTopTextViewsFragment.setRightTextView(newVal);
                break;
            default:
                if (DEBUG)
                    Log.e(TAG,"String changed unrecognized: "+valChanged);
                break;
        }
    }

    @Subscribe
    public void onModeChanged(AutopilotMode.AUTONOMY autonomy){
        AndroidUtil.showToastLong(ASA.getInstance().getActiveSys().getName()+"'s in "+autonomy.toString()+" Mode");
    }

}
