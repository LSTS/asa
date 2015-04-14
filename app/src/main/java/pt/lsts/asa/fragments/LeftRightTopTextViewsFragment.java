package pt.lsts.asa.fragments;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.listenners.sysUpdates.LeftRightTopTextViewsSysUpdaterListenner;
import pt.lsts.asa.settings.Settings;

/**
 * Created by jloureiro on 1/14/15.
 */
public class LeftRightTopTextViewsFragment extends Fragment {

    public static final String TAG = "LeftRightTopTextViewsFrag";

    private TextView leftTextView = null;
    private TextView rightTextView = null;

    private TimerTask timeoutTimerTask;
    private Timer timeoutTimer;
    private int timeoutInterval = (Settings.getInt("timeout_interval_in_seconds", 60) * 1000);
    private long lastMsgReceived;
    private LeftRightTopTextViewsSysUpdaterListenner leftRightTopTextViewsSysUpdaterListenner;

    public LeftRightTopTextViewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manual_indicators, container, false);

        findViews(v);
        setTextViewsColors();
        init();

        return v;
    }

    public void init(){
        leftRightTopTextViewsSysUpdaterListenner = new LeftRightTopTextViewsSysUpdaterListenner(this);
        ASA.getInstance().getBus().register(leftRightTopTextViewsSysUpdaterListenner);
        if (ASA.getInstance().getActiveSys()!=null){
            setLeftTextView(ASA.getInstance().getActiveSys().getIasInt());
            setRightTextView(ASA.getInstance().getActiveSys().getAltInt());
        }
    }

    public void findViews(View v){
        leftTextView = (TextView) v.findViewById(R.id.leftTextView);
        rightTextView = (TextView) v.findViewById(R.id.rightTextView);
    }

    public void setTextViewsColors(){
        setBackgroundColors();
        setTextColors();
    }

    public void setBackgroundColors(){
        leftTextView.setBackgroundColor(Color.parseColor("#000000"));//Black
        rightTextView.setBackgroundColor(Color.parseColor("#000000"));//Black
    }

    public void setTextColors(){
        leftTextView.setTextColor(Color.parseColor("#FFFFFF"));//White
        rightTextView.setTextColor(Color.parseColor("#FFFFFF"));//White
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void setLeftTextView(final int val){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftTextView.setText(" " + "IAS:" + " " + val + " ");
            }
        });
    }

    public void setRightTextView(final int val){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rightTextView.setText(" " + "Alt:" + " " + val + " ");
            }
        });
    }


    public void setLastMsgReceived(long lastMsgReceived) {
        this.lastMsgReceived = lastMsgReceived;
    }


}