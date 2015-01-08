package pt.lsts.asa.util;

import pt.lsts.asa.ASA;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by jloureiro on 1/8/15.
 */
public class AndroidUtil {


    public static void showToastShort(FragmentActivity fragmentActivity, final String msg){
        fragmentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ASA.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToastLong(FragmentActivity fragmentActivity, final String msg){
        fragmentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ASA.getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void loadFragment(FragmentActivity fragmentActivity, Fragment fragment, int fragmentContainerID){
        fragmentActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(fragmentContainerID,fragment).commit();
        fragmentActivity.getSupportFragmentManager().executePendingTransactions();
    }

}
