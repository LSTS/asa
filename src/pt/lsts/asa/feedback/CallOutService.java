package pt.lsts.asa.feedback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import pt.lsts.asa.ASA;
import pt.lsts.asa.listenners.sharedPreferences.CallOutPreferencesListenner;
import pt.lsts.asa.listenners.sysUpdates.CallOutSysUpdaterListenner;
import pt.lsts.asa.settings.Settings;

/**
 * Created by jloureiro on 2/11/15.
 */
public class CallOutService extends Service {

    public static final String TAG = "CallOutService";
    private TextToSpeech tts;
    private CallOutPreferencesListenner callOutPreferencesListenner;
    CallOutSysUpdaterListenner callOutSysUpdaterListenner;

    private int iasInt=0;
    private int altInt=0;

    private TimerTask iasTimerTask, altTimerTask, timeoutTimerTask;
    private Timer iasTimer = new Timer();
    private Timer altTimer = new Timer();
    private Timer timeoutTimer = new Timer();
    private Runnable iasRunnable;
    private Runnable altRunnable;
    private Runnable timeoutRunnable;

    private int iasInterval = 10000, altInterval = 15000,timeoutInterval = 25000;
    private boolean iasMuteBool = false, altMuteBool = false, globalMuteBool = false;
    private boolean timeoutBool = false;


    public CallOutService(){
        initListenners();

    }

    public void initListenners() {
        callOutPreferencesListenner = new CallOutPreferencesListenner(this);
        ASA.getInstance().getBus().register(callOutPreferencesListenner);

        callOutSysUpdaterListenner = new CallOutSysUpdaterListenner(this);
        ASA.getInstance().getBus().register(callOutSysUpdaterListenner);
    }

    /**
     *
     * @param intent
     * @param flags 0:Timeout ; 1:Both Active ; 2:Only Alt ; 3:Only IAS
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.v(TAG, "onStartCommand()");

        init();
        Log.i(TAG,"finished init() - flags="+flags);
        switch (flags){
            case 0:
                Log.i(TAG,"0 timeout");
                startTimeout();
                break;
            case 1:
                Log.i(TAG,"1 both");
                startBoth();
                break;
            case 2:
                Log.i(TAG,"2 ias");
                startIasOnly();
                break;
            case 3:
                Log.i(TAG,"3 alt");
                startAltOnly();
                break;
            default:
                Log.e(TAG, "error flag unrecognized");
                stopSelf();
                break;
        }

        return START_NOT_STICKY;//does not resuscitate
    }

    public void startTimeout(){
        startTimeoutHandle();
    }

    public void startBoth(){
        if (altMuteBool==false)
            startAltHandle();
        if (iasMuteBool==false)
            startIasHandle();
    }

    public void startIasOnly(){
        if (iasMuteBool==false)
            startIasHandle();
    }

    public void startAltOnly(){
        if (altMuteBool==false)
            startAltHandle();
    }

    public void init(){
        initSettings();
        initTextToSpeech();
        initRunnables();
    }

    public void initSettings(){
        int integer = Settings.getInt("altitude_interval_in_seconds", 10) * 1000;
        setAltInterval(integer);
        integer = Settings.getInt("ias_interval_in_seconds", 10) * 1000;
        setIasInterval(integer);
        integer = Settings.getInt("timeout_interval_in_seconds", 60) * 1000;
        setTimeoutInterval(integer);

        altMuteBool = Settings.getBoolean("altitude_audio",false);
        iasMuteBool = Settings.getBoolean("ias_audio",false);
        timeoutBool = Settings.getBoolean("timeout_audio",false);
        globalMuteBool = Settings.getBoolean("global_audio",false);
    }

    public void initTextToSpeech() {
        tts = new TextToSpeech(ASA.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
        tts.setLanguage(Locale.UK);
        setSpeechRate(Settings.getInt("speech_rate",100));
    }


    public void initRunnables(){
        initAltRunnale();
        initIasRunnable();
        initTimeoutRunnable();
        Log.i(TAG,"finish initRunnables");
    }

    public void initAltRunnale() {

        altRunnable = new Runnable() {
            @Override
            public void run() {
                if (globalMuteBool == true || isTimeout()
                        || altMuteBool == true)
                    return;

                //int altInt = ASA.getInstance().getActiveSys().getAltInt();

                String ttsString = "Altitude " + altInt;
                if (iasMuteBool==true)//if ias is mute, speak only the value
                    ttsString = ""+altInt;
                tts.speak(ttsString, TextToSpeech.QUEUE_FLUSH, null);

                Log.i("tts.speak", "alt= " + altInt);
            }
        };
        altTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    altRunnable.run();
                } catch (Exception e) {
                    Log.e(TAG,"error in executing: altRunnale. It will no longer be run!");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        };

    }

    public void startAltHandle() {
        long delay = 0;
        if (altTimer!=null)
            altTimer.cancel();
        altTimer=null;
        altTimer = new Timer();

        // schedules the task to be run in an interval
        altTimer.schedule(altTimerTask, delay,
                altInterval);
    }

    public void initIasRunnable() {

        iasRunnable = new Runnable() {
            @Override
            public void run() {
                if (globalMuteBool == true || isTimeout()
                        || iasMuteBool == true)
                    return;

                //int iasInt = ASA.getInstance().getActiveSys().getIasInt();

                String ttsString;
                if (altMuteBool==true)//if alt is muted, speak only the value
                    ttsString = ""+iasInt;
                else
                    ttsString = "Speed " + iasInt;

                tts.speak(ttsString,TextToSpeech.QUEUE_FLUSH, null);

                Log.i("tts.speak", "ias -- "+System.currentTimeMillis());//log timestamp for debugging
            }
        };

        iasTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    iasRunnable.run();
                } catch (Exception e) {
                    String message = "error in executing: iasRunnale.";
                    if (e!=null && e.getMessage()!=null)
                        message += "\ne="+e.getMessage();
                    Log.e(TAG,message);
                    throw e;
                }
            }
        };
    }

    public void startIasHandle() {
        Log.i(TAG,"startIasHandle");
        long delay = 0;
        if (iasTimer!=null)
            iasTimer.cancel();
        iasTimer=null;
        iasTimer = new Timer();
        // schedules the task to be run in an interval
        Log.i(TAG,"before schedule");
        iasTimer.schedule(iasTimerTask, delay,
                iasInterval);
        Log.i(TAG,"after schedule");
    }

    public boolean isTimeout() {
        if (ASA.getInstance().getActiveSys()==null)
            return true;
        long lastMsgReceived = ASA.getInstance().getActiveSys().lastMessageReceived;
        if (timeoutBool == false
                && System.currentTimeMillis() - timeoutInterval > lastMsgReceived) {
            setTimeoutBool(true);// no message received in timeoutInterval
            startTimeoutHandle();
        }
        if (timeoutBool == true
                && System.currentTimeMillis() - timeoutInterval < lastMsgReceived){
            setTimeoutBool(false);
            startBoth();
        }
        return timeoutBool;
    }

    public void initTimeoutRunnable() {
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                Log.v(TAG,"timeoutRunnable");
                if (globalMuteBool)
                    return;
                Log.v(TAG,"TimeoutRunnable after returns");
                long lastMsgReceived = ASA.getInstance().getActiveSys().lastMessageReceived;
                long timeSinceLastMessage = ((System.currentTimeMillis() - lastMsgReceived) / 1000);
                //while (tts.isSpeaking());
                final String ttsString = "Lost Comms";
                tts.speak(ttsString, TextToSpeech.QUEUE_FLUSH, null);

                Log.i("tts.speak", "timeout= " + timeSinceLastMessage
                        + "\ntimeoutInterval= " + timeoutInterval);
            }
        };

        timeoutTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    timeoutRunnable.run();
                } catch (Exception e) {

                    Log.e(TAG,"error in executing: timeoutRunnale. It will no longer be run!");
                    e.printStackTrace();

                    throw new RuntimeException(e);
                }
            }
        };

    }

    public void startTimeoutHandle() {

        long delay = 0;
        if (timeoutTimer!=null)
            timeoutTimer.cancel();
        timeoutTimer=null;
        timeoutTimer = new Timer();

        if (iasTimer!=null)
            iasTimer.cancel();
        iasTimer=null;
        if (altTimer!=null)
            altTimer.cancel();
        altTimer=null;

        // schedules the task to be run in an interval
        timeoutTimer.schedule(iasTimerTask, delay,
                iasInterval);

    }

    public void setSpeechRate(int speechRate){
        float newSpeechRate = (((float)speechRate)/100);
        Log.i(TAG, "speechRateInt= "+speechRate+" | speechRateFloat= "+newSpeechRate);
        tts.setSpeechRate(newSpeechRate);
    }


    public void setIasInt(int iasInt) {
        this.iasInt = iasInt;
    }

    public void setAltInt(int altInt) {
        this.altInt = altInt;
    }

    public void setTimeoutBool(boolean timeoutBool) {
        this.timeoutBool = timeoutBool;
    }

    public int getIasInterval() {
        return iasInterval;
    }

    public void setIasInterval(int iasInterval) {
        this.iasInterval = iasInterval;
    }

    public int getAltInterval() {
        return altInterval;
    }

    public void setAltInterval(int altInterval) {
        this.altInterval = altInterval;
    }

    public int getTimeoutInterval() {
        return timeoutInterval;
    }

    public void setGlobalMuteBool(boolean globalAudioBool) {
        this.globalMuteBool = globalAudioBool;
    }

    public void setIasMuteBool(boolean iasBool) {
        this.iasMuteBool = iasBool;
        startIasHandle();
    }

    public void setAltMuteBool(boolean altBool) {
        this.altMuteBool = altBool;
        startAltHandle();
    }

    public void setTimeoutInterval(int timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(TAG,"onCreate()");
    }

    public void cancelTimers(){
        timeoutTimer.cancel();
        altTimer.cancel();
        iasTimer.cancel();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG,"onDestroy()");
        cancelTimers();
        tts.shutdown();
        stopSelf();
    }
}
