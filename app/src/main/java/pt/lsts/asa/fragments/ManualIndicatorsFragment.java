package pt.lsts.asa.fragments;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.listenners.sysUpdates.ManualIndicatorsSysUpdaterListenner;
import pt.lsts.asa.settings.Settings;

/**
 * Created by jloureiro on 1/14/15.
 */
public class ManualIndicatorsFragment extends Fragment {

    public static final String TAG = "ManualIndicatorsFrag";

    private TextView leftTextView = null;
    private TextView rightTextView = null;
    private TextView centerTextView = null;

    private String lost_comms=" LOST COMMS ";
    private TimerTask timeoutTimerTask;
    private Timer timeoutTimer;
    private int timeoutInterval = (Settings.getInt("timeout_interval_in_seconds", 60) * 1000);
    private long lastMsgReceived;
    private ManualIndicatorsSysUpdaterListenner manualIndicatorsSysUpdaterListenner;

    public ManualIndicatorsFragment() {
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
        initTimeoutTimerTask();
        manualIndicatorsSysUpdaterListenner = new ManualIndicatorsSysUpdaterListenner(this);
        ASA.getInstance().getBus().register(manualIndicatorsSysUpdaterListenner);
        if (ASA.getInstance().getActiveSys()!=null){
            setLeftTextView(ASA.getInstance().getActiveSys().getIasInt());
            setRightTextView(ASA.getInstance().getActiveSys().getAltInt());
        }
    }

    public void findViews(View v){
        leftTextView = (TextView) v.findViewById(R.id.leftTextView);
        rightTextView = (TextView) v.findViewById(R.id.rightTextView);
        centerTextView = (TextView) v.findViewById(R.id.centerTextView);
    }

    public void setTextViewsColors(){
        setBackgroundColors();
        setTextColors();
    }

    public void setBackgroundColors(){
        leftTextView.setBackgroundColor(Color.parseColor("#000000"));//Black
        rightTextView.setBackgroundColor(Color.parseColor("#000000"));//Black
        centerTextView.setBackgroundColor(Color.parseColor("#000000"));//Black
    }

    public void setTextColors(){
        leftTextView.setTextColor(Color.parseColor("#FFFFFF"));//White
        rightTextView.setTextColor(Color.parseColor("#FFFFFF"));//White
        centerTextView.setTextColor(Color.parseColor("#FFFFFF"));//White
    }

    @Override
    public void onResume(){
        startTimeoutTimer();
        super.onResume();
    }

    @Override
    public void onPause(){
        cancelTimeout();
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

    public void setCenterTextView(final String s){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                centerTextView.setTextSize(35f);
                centerTextView.setText(s);
                centerTextView.setVisibility(View.VISIBLE);
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


    public void setCenterTextViewVisibility(final boolean visibility){//View.INVISIBLE View.VISIBLE
        Log.e(TAG,"getActivity()==null : "+(getActivity()==null));
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visibility==true){
                    centerTextView.setVisibility(View.VISIBLE);
                    leftTextView.setText(" " + "IAS:" + " " + "---" + " ");
                    rightTextView.setText(" " + "Alt:" + " " + "---" + " ");
                }
                if (visibility==false){
                    Log.i("TAG", "centerTextView.getText(): "+centerTextView.getText());
                    if (centerTextView.getText().equals(lost_comms))
                        centerTextView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void initTimeoutTimerTask(){
        timeoutTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > lastMsgReceived+timeoutInterval
                        && ASA.getInstance().getActiveSys()!=null
                        && System.currentTimeMillis() > ASA.getInstance().getActiveSys().lastMessageReceived+timeoutInterval) {
                    setCenterTextViewVisibility(true);
                }
            }
        };
    }
    public void startTimeoutTimer(){
        timeoutTimer = new Timer();
        long delay = timeoutInterval;//initialDelay

        // schedules the task to be run in an interval
        timeoutTimer.scheduleAtFixedRate(timeoutTimerTask, delay, timeoutInterval);
    }

    public void cancelTimeout(){
        if (timeoutTimer!=null){
            timeoutTimer.cancel();
            timeoutTimer=null;
            timeoutTimerTask.cancel();
            timeoutTimerTask=null;
        }
    }


    public void setLastMsgReceived(long lastMsgReceived) {
        this.lastMsgReceived = lastMsgReceived;
        setCenterTextViewVisibility(false);
    }


}