package pt.lsts.asa.listenners.sysUpdates;


import pt.lsts.asa.fragments.BatteryIndicatorFragment;
import pt.lsts.asa.util.BatteryIndicatorPairUtil;

import com.squareup.otto.Subscribe;


/**
 * Created by jloureiro on 3/12/15.
 */
public class BatteryIndicatorSysUpdaterListenner {

    private final String TAG = "BatteryIndicatorSysUpdaterListenner";
    public static final boolean DEBUG = false;

    private BatteryIndicatorFragment batteryIndicatorFragment=null;


    public BatteryIndicatorSysUpdaterListenner(BatteryIndicatorFragment batteryIndicatorFragment){
        this.batteryIndicatorFragment = batteryIndicatorFragment;
    }

    @Subscribe
    public void onValChanged(BatteryIndicatorPairUtil batteryIndicatorPairUtil){
        String idStringLowerCase = batteryIndicatorPairUtil.getId().toLowerCase();
        double val = batteryIndicatorPairUtil.getVal();
        switch (idStringLowerCase){
            case "current":
                batteryIndicatorFragment.setCurrent(val);
                break;
            case "voltage":
                batteryIndicatorFragment.setVoltage(val);
                break;
            case "level":
                batteryIndicatorFragment.setLevel(val);
                break;
            default:
                break;
        }
        batteryIndicatorFragment.updateBatteriesIndicatorTextView();
    }

}
