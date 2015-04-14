package pt.lsts.asa.listenners.sysUpdates;

import com.squareup.otto.Subscribe;

import pt.lsts.asa.fragments.CentralTextViewFragment;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.IMCMessage;

/**
 * Created by jloureiro on 4/14/15.
 */
public class CentralTextViewUpdaterListenner {

    public static final String TAG = "CentralTextViewSysUpdaterListenner";

    public static final boolean DEBUG = false;

    private CentralTextViewFragment centralTextViewFragment = null;

    public CentralTextViewUpdaterListenner(CentralTextViewFragment centralTextViewFragment){
        this.centralTextViewFragment = centralTextViewFragment;
    }

    @Subscribe
    public void newMessage(IMCMessage msg){
        if (IMCUtils.isMsgFromActive(msg))
            centralTextViewFragment.setLastMsgReceived();
    }

    @Subscribe
    public void onLowFuelLevel(String s){
        AndroidUtil.showToastLong(s);
        centralTextViewFragment.setCenterTextView(s);
    }

}
