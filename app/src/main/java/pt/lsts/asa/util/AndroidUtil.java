package pt.lsts.asa.util;

import pt.lsts.asa.ASA;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by jloureiro on 1/8/15.
 */
public class AndroidUtil {

    public static final String TAG = "AndroidUtil";

    public static void showToastShort(String msg){
        Toast toast = Toast.makeText(ASA.getContext(), msg, Toast.LENGTH_SHORT);
        ASA.getInstance().getBus().post(toast);
    }

    public static void showToastLong(String msg){
        Toast toast = Toast.makeText(ASA.getContext(), msg, Toast.LENGTH_LONG);
        ASA.getInstance().getBus().post(toast);
    }
    public static void loadFragment(FragmentActivity fragmentActivity, Fragment fragment, int fragmentContainerID){
        fragmentActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(fragmentContainerID,fragment).commit();
        fragmentActivity.getSupportFragmentManager().executePendingTransactions();
    }

    public static void removeFragment(FragmentActivity fragmentActivity, Fragment fragment){
        fragmentActivity.getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment).commit();
        fragmentActivity.getSupportFragmentManager().executePendingTransactions();
    }

    public static void removeAllFragments(FragmentActivity fragmentActivity){
        for ( Fragment fragment : fragmentActivity.getSupportFragmentManager().getFragments()){
            removeFragment(fragmentActivity, fragment);
        }
    }

    public static float calcRotation(float cameraBearing, float vehicleBearing){
        return (((cameraBearing + vehicleBearing)%(360)));
    }

}
