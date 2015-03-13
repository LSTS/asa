package pt.lsts.asa.fragments;


import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.listenners.sysUpdates.BatteryIndicatorSysUpdaterListenner;
import pt.lsts.asa.settings.Settings;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BatteryIndicatorFragment extends Fragment {

    private FragmentActivity fragmentActivity = null;
    private TextView batteriesTextView = null;

    private String lost_comms="Batteries: -%, -V, -A";
    private double level,voltage,current;
    public NumberFormat numberFormat = new DecimalFormat(".##");
    private TimerTask timeoutTimerTask;
    private Timer timeoutTimer;
    private int timeoutInterval = (Settings.getInt("timeout_interval_in_seconds", 60) * 1000);
    private long lastMsgReceived;
    private BatteryIndicatorSysUpdaterListenner batteryIndicatorSysUpdaterListenner;

    public BatteryIndicatorFragment() {
        // Required empty public constructor
    }

    public BatteryIndicatorFragment(FragmentActivity fragmentActivity){
        this.fragmentActivity=fragmentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_battery_indicator, container, false);

        findViews(v);
        setTextViewsColors();
        init();

        return v;
    }

    public void init(){
        initTimeoutTimerTask();
        batteryIndicatorSysUpdaterListenner = new BatteryIndicatorSysUpdaterListenner(this);
        ASA.getInstance().getBus().register(batteryIndicatorSysUpdaterListenner);
        setBatteriesTextView(lost_comms);
    }

    public void findViews(View v){
        batteriesTextView = (TextView) v.findViewById(R.id.batteriesTextView);
    }

    public void setTextViewsColors(){
        setBackgroundColors();
        setTextColors();
    }

    public void setBackgroundColors(){
        batteriesTextView.setBackgroundColor(Color.parseColor("#000000"));//Black
    }

    public void setTextColors(){
        batteriesTextView.setTextColor(Color.parseColor("#FFFFFF"));//White
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

    public void setBatteriesTextView(final String text){
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                batteriesTextView.setText(text);
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
                    setBatteriesTextView(lost_comms);
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

    public void updateBatteriesIndicatorTextView(){
        final String text = buildBatteriesIndicatorTextView();
        setBatteriesTextView(text);
    }

    public String buildBatteriesIndicatorTextView(){
        //"Batteries: -%, -V, -A"
        String result= "Batteries: "
                +numberFormat.format(level)+"%, "
                +numberFormat.format(voltage)+"V, "
                +numberFormat.format(current)+"A";
        return result;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public void setLastMsgReceived(long lastMsgReceived) {
        this.lastMsgReceived = lastMsgReceived;
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentActivity;
    }

    public void setFragmentActivity(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

}
