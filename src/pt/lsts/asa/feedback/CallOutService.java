package pt.lsts.asa.feedback;

import android.app.Service;
import android.content.Context;
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
import pt.lsts.asa.util.TextToSpeechUtilService;


/**
 * Created by jloureiro on 2/11/15.
 */
public class CallOutService extends Service implements
        TextToSpeech.OnInitListener{

    public static final String TAG = "CallOutService";

    private CallOutPreferencesListenner callOutPreferencesListenner;
    private CallOutSysUpdaterListenner callOutSysUpdaterListenner;
    private Context context;
    private TextToSpeech tts;

    private int iasInt=0;
    private int altInt=0;

    private TimerTask iasTimerTask;
    private TimerTask altTimerTask;
    private TimerTask timeoutTimerTask;
    private Timer iasTimer;
    private Timer altTimer;
    private Timer timeoutTimer;
    private boolean iasTimerTaskRunning =false;//true if iasTimerTask is running
    private boolean altTimerTaskRunning =false;//true if altTimerTask is running

    private int iasInterval;
    private int altInterval;
    private int timeoutInterval;

    private boolean altMuteBool=false;

    private boolean iasMuteBool=false;
    private boolean timeoutBool=false;
    private boolean globalMuteBool=false;

    private long lastMsgReceived;

    public CallOutService(Context context){
        this.context=context;
    }

    /**
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        initTts();
        initPreferences();
        if (globalMuteBool==true) {
            this.onDestroy();
            return START_NOT_STICKY;//does not resuscitate
        }
        initListeners();
        initTimerTasks();

        startTimers();
        return START_NOT_STICKY;//does not resuscitate
    }

    public void startTimers(){
        startTimeoutTimer();
        if (altMuteBool==false)
            startAltTimer();
        if (iasMuteBool==false)
            startIasTimer();
    }

    public void initPreferences(){
        boolean globalMuteBool = Settings.getBoolean("global_audio",false);
        setGlobalMuteBool(globalMuteBool);

        boolean altMuteBool = Settings.getBoolean("altitude_audio",false);
        setAltMuteBool(altMuteBool);
        boolean iasMuteBool = Settings.getBoolean("ias_audio",false);
        setIasMuteBool(iasMuteBool);
        boolean timeoutBool = Settings.getBoolean("timeout_audio",false);
        setTimeoutBool(timeoutBool);

        int altInterval = Settings.getInt("altitude_interval_in_seconds", 10) * 1000;
        setAltInterval(altInterval);
        int iasInterval = Settings.getInt("ias_interval_in_seconds", 10) * 1000;
        setIasInterval(iasInterval);
        int timeoutInterval = Settings.getInt("timeout_interval_in_seconds", 60) * 1000;
        setTimeoutInterval(timeoutInterval);



    }

    public void initListeners(){

        callOutSysUpdaterListenner = new CallOutSysUpdaterListenner(this);
        ASA.getInstance().getBus().register(callOutSysUpdaterListenner);
    }

    public void initTimerTasks(){
        initIasTimerTask();
        initAltTimerTask();
        initTimeoutTask();
    }

    public void initIasTimerTask(){
        iasTimerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG,"ias= "+iasInt+ " -- "+System.currentTimeMillis());
                if (tts==null)
                    initTts();
                tts.speak(""+iasInt,TextToSpeech.QUEUE_FLUSH, null);
            }
        };
    }

    public void initAltTimerTask(){
        altTimerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG,"alt= "+altInt+ " -- "+System.currentTimeMillis());
                if (tts==null)
                    initTts();
                tts.speak(""+altInt,TextToSpeech.QUEUE_FLUSH, null);
            }
        };
    }

    public void initTimeoutTask(){
        timeoutTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > lastMsgReceived+timeoutInterval
                        && System.currentTimeMillis() > ASA.getInstance().getActiveSys().lastMessageReceived+timeoutInterval) {
                    Log.i(TAG, "Lost Comms");
                    tts.speak("Lost Comms", TextToSpeech.QUEUE_FLUSH, null);
                    if (iasTimer!=null) {
                        iasTimer.cancel();
                        iasTimer=null;
                        setIasTimerTaskRunning(false);
                    }
                    if (altTimer!=null) {
                        altTimer.cancel();
                        altTimer=null;
                        setAltTimerTaskRunning(false);
                    }
                }
            }
        };
    }

    public void startIasTimer(){
        cancelIas();

        iasTimer = new Timer();
        initIasTimerTask();

        long delay = iasInterval;//initialDelay

        // schedules the task to be run in an interval
        iasTimer.scheduleAtFixedRate(iasTimerTask, delay, iasInterval);
        setIasTimerTaskRunning(true);
    }

    public void cancelIas(){
        if (iasTimer!=null){
            iasTimer.cancel();
            iasTimer=null;
            iasTimerTask.cancel();
            iasTimerTask=null;
        }
    }

    public void startAltTimer(){


        altTimer = new Timer();
        initAltTimerTask();

        long delay = altInterval;//initialDelay

        // schedules the task to be run in an interval
        altTimer.scheduleAtFixedRate(altTimerTask, delay, altInterval);
        setAltTimerTaskRunning(true);
    }

    public void cancelAlt(){
        if (altTimer!=null){
            altTimer.cancel();
            altTimer=null;
            altTimerTask.cancel();
            altTimerTask=null;
        }
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

    public void initTts(){

        tts = new TextToSpeech(context, this, "com.ivona.tts");

        int speechRate = (Settings.getInt("speech_rate",100));
        float newSpeechRate = (((float)speechRate)/100);
        tts.setSpeechRate(newSpeechRate);
        Log.v(TAG,"speechRate="+speechRate+"| newSpeechRate="+newSpeechRate);

    }

    @Override
    public void onInit(int status) {//tts.onInitOverride
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.UK);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "This Language is not supported, result="+result);
            } else {
                Log.i(TAG, "tts initialized with sucess: Lang=" + tts.getLanguage().toString());
                //downloadAndAssociateAudioNumbers();
            }

        } else {
            Log.e(TAG, "Initilization Failed. TTS.Status="+status);
        }
    }

    public void downloadAudioNumbers(){
        TextToSpeechUtilService textToSpeechUtilService  = new TextToSpeechUtilService(tts);
        Intent intent = new Intent(context,TextToSpeechUtilService.class);
        textToSpeechUtilService.onStartCommand(intent,1,0);//1 only downloadFiles
        textToSpeechUtilService.onBind(intent);
    }

    public void associateAudioNumbers(){
        Log.i(TAG,"associateAudioNumbers()");
        TextToSpeechUtilService textToSpeechUtilService  = new TextToSpeechUtilService(tts);
        Intent intent = new Intent(context,TextToSpeechUtilService.class);
        textToSpeechUtilService.onStartCommand(intent,2,0);//2: associate DownloadedFiles with tts
        textToSpeechUtilService.onBind(intent);
    }

    public void downloadAndAssociateAudioNumbers(){
        downloadAudioNumbers();
        associateAudioNumbers();
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
        cancelIas();
        cancelAlt();
        cancelTimeout();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG,"onDestroy()");
        cancelTimers();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts=null;
        }
        stopSelf();
        super.onDestroy();
    }


    public int getAltInt() {
        return altInt;
    }

    public void setAltInt(int altInt) {
        this.altInt = altInt;
        setLastMsgReceived(System.currentTimeMillis());
    }

    public int getIasInt() {
        return iasInt;
    }

    public void setIasInt(int iasInt) {
        this.iasInt = iasInt;
        setLastMsgReceived(System.currentTimeMillis());
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

    public void setTimeoutInterval(int timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }

    public boolean isAltMuteBool() {
        return altMuteBool;
    }

    public void setAltMuteBool(boolean altMuteBool) {
        this.altMuteBool = altMuteBool;
    }

    public boolean isIasMuteBool() {
        return iasMuteBool;
    }

    public void setIasMuteBool(boolean iasMuteBool) {
        this.iasMuteBool = iasMuteBool;
    }

    public boolean isTimeoutBool() {
        return timeoutBool;
    }

    public void setTimeoutBool(boolean timeoutBool) {
        this.timeoutBool = timeoutBool;
    }

    public boolean isGlobalMuteBool() {
        return globalMuteBool;
    }

    public void setGlobalMuteBool(boolean globalMuteBool) {
        this.globalMuteBool = globalMuteBool;
    }

    public long getLastMsgReceived() {
        return lastMsgReceived;
    }

    public void setLastMsgReceived(long lastMsgReceived) {
        this.lastMsgReceived = lastMsgReceived;
        if (isAltTimerTaskRunning()==false
                && altMuteBool==false) {
            startAltTimer();
        }
        if (isIasTimerTaskRunning()==false
                && iasMuteBool==false) {
            startIasTimer();
        }
    }

    public boolean isIasTimerTaskRunning() {
        return iasTimerTaskRunning;
    }

    public void setIasTimerTaskRunning(boolean iasTimerTaskRunning) {
        this.iasTimerTaskRunning = iasTimerTaskRunning;
    }

    public boolean isAltTimerTaskRunning() {
        return altTimerTaskRunning;
    }

    public void setAltTimerTaskRunning(boolean altTimerTaskRunning) {
        this.altTimerTaskRunning = altTimerTaskRunning;
    }

}
