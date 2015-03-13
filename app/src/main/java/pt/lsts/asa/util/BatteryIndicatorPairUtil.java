package pt.lsts.asa.util;


/**
 * Created by jloureiro on 3/12/15.
 * Utility used for OTTO lib events to avoid conflict with Android Pair<F,S>
 */
public class BatteryIndicatorPairUtil {
    private String id;
    private double val;

    public BatteryIndicatorPairUtil(String id, double val){
        this.id=id;
        this.val=val;
    }

    public String getId() {
        return id;
    }


    public double getVal() {
        return val;
    }

}
