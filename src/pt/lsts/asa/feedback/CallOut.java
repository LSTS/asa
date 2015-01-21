package pt.lsts.asa.feedback;

import pt.lsts.asa.ASA;
import pt.lsts.asa.listenners.sharedPreferences.CallOutPreferencesListenner;
import pt.lsts.asa.managers.SoundManager;
import pt.lsts.asa.settings.Settings;
import pt.lsts.asa.subscribers.CallOutIMCSubscriber;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;

/**
 * Text to Speech
 *
 * @author
 */
public class CallOut {

    private final String TAG = "CallOut";
	private TextToSpeech tts;
	private Context context;
    private CallOutIMCSubscriber callOutIMCSubscriber;

	private ScheduledFuture iasHandle, altHandle, timeoutHandle;
	private ScheduledExecutorService iasScheduler = Executors
			.newScheduledThreadPool(1);
	private ScheduledExecutorService altScheduler = Executors
			.newScheduledThreadPool(1);
	private ScheduledExecutorService timeoutScheduler = Executors
			.newScheduledThreadPool(1);
	Runnable iasRunnable, altRunnable, timeoutRunnable;

	private Float altValue = 0f;
	private Double iasValue = 0d;
	NumberFormat formatter = new DecimalFormat("#0");
	private long lastMsgReceived = 0;

	private int iasInterval = 10000, altInterval = 15000,timeoutInterval = 25000;
	private boolean iasMuteBool = false, altMuteBool = false, timeoutBool = false, globalMuteBool = false;

	public CallOut(Context context) {
		this.context = context;
		SoundManager.getInstance();
        ASA.getInstance().setCallOut(this);
        CallOutPreferencesListenner callOutPreferencesListenner = new CallOutPreferencesListenner(this);
        ASA.getInstance().getBus().register(callOutPreferencesListenner);
        Log.i(TAG, "ASA.getInstance().getBus().register(callOutPreferencesListenner);");
	}

	public void initImcSubscribers() {
        callOutIMCSubscriber = new CallOutIMCSubscriber(this);
		ASA.getInstance().addSubscriber(callOutIMCSubscriber);
	}

    public void initSchedulers(){
        iasScheduler = Executors.newScheduledThreadPool(1);
        altScheduler = Executors.newScheduledThreadPool(1);
        timeoutScheduler = Executors.newScheduledThreadPool(1);
    }

	public void shutdown() {
		shutdownCallOuts();
		tts.shutdown();
	}

	public void initCallOuts() {
        initSchedulers();
        initImcSubscribers();
		initTextToSpeech();
		initAltRunnale();
		initIasRunnable();
		initTimeoutRunnable();
		initBooleans();
		initIntervals();
	}

	public void initTextToSpeech() {
		tts = new TextToSpeech(context, new OnInitListener() {
			@Override
			public void onInit(int status) {

			}
		});
		tts.setLanguage(Locale.UK);
        float speechrate = Settings.getInt("audio_speech_rate",100)/100;
        tts.setSpeechRate(speechrate);
	}

	public void initBooleans() {
		altMuteBool = Settings.getBoolean("audio_altitude_audio",false);
		iasMuteBool = Settings.getBoolean("audio_ias_audio",false);
		timeoutBool = Settings.getBoolean("audio_timeout_audio",false);
		globalMuteBool = Settings.getBoolean("audio_global_audio",false);
	}

	public void initIntervals() {
		int integer = Settings.getInt("audio_altitude_interval_in_seconds", 10) * 1000;
		setAltInterval(integer);
		integer = Settings.getInt("audio_ias_interval_in_seconds", 10) * 1000;
		setIasInterval(integer);
		integer = Settings.getInt("comms_timeout_interval_in_seconds", 60) * 1000;
		setTimeoutInterval(integer);
	}

	public void startCallOuts() {
        initSchedulers();
		startAltHandle();
		startIasHandle();
	}

	public void shutdownCallOuts() {
        shutdownHandlers();
        shutdownSchedulers();
	}

    public void shutdownHandlers(){
        if (iasHandle!=null)
            iasHandle.cancel(true);
        if (altHandle!=null)
            altHandle.cancel(true);
        if (timeoutHandle!=null)
            timeoutHandle.cancel(true);
    }

    public void shutdownSchedulers(){
        if (altScheduler!=null)
            altScheduler.shutdownNow();
        if (iasScheduler!=null)
            iasScheduler.shutdownNow();
        if (timeoutScheduler!=null)
            timeoutScheduler.shutdownNow();
    }

	public void initAltRunnale() {

		altRunnable = new Runnable() {
			@Override
			public void run() {
				if (globalMuteBool == true || isTimeout()
						|| altMuteBool == true)
					return;
				if (altHandle.isCancelled())
					startAltHandle();
                while (tts.isSpeaking());
                String ttsString = "Altitude " + formatter.format(altValue);
                if (iasMuteBool==true)//if ias is mute, speak only the value
                    ttsString = formatter.format(altValue);
				tts.speak(ttsString,TextToSpeech.QUEUE_FLUSH, null);


				Log.i("tts.speak", "alt= " + altValue);
			}
		};
	}

	public void startAltHandle() {
		if (altHandle != null)
			altHandle.cancel(true);
		altHandle = altScheduler.scheduleAtFixedRate(altRunnable, 0,
				altInterval, TimeUnit.MILLISECONDS);
	}

	public void initIasRunnable() {

		iasRunnable = new Runnable() {
			@Override
			public void run() {
				if (globalMuteBool == true || isTimeout()
						|| iasMuteBool == true)
					return;
				if (iasHandle.isCancelled())
					startIasHandle();

                while (tts.isSpeaking());
                String ttsString = "Speed " + formatter.format(iasValue);
                if (altMuteBool==true)//if alt is mute, speak only the value
                    ttsString = formatter.format(iasValue);
				tts.speak(ttsString,TextToSpeech.QUEUE_FLUSH, null);


				Log.i("tts.speak", "ias= " + iasValue);

			}
		};
	}

	public void startIasHandle() {
		if (iasHandle != null)
			iasHandle.cancel(true);
		iasHandle = iasScheduler.scheduleAtFixedRate(iasRunnable, 0,
				iasInterval, TimeUnit.MILLISECONDS);
	}

	public boolean isTimeout() {
		if (timeoutBool == false
				&& System.currentTimeMillis() - timeoutInterval > lastMsgReceived) {
			setTimeoutBool(true);// no message received in timeoutInterval
			startTimeoutHandle();
		}
		return timeoutBool;
	}

	public void initTimeoutRunnable() {
		timeoutRunnable = new Runnable() {
			@Override
			public void run() {
				if (globalMuteBool)
					return;
				if (timeoutBool == false) {
					if (timeoutHandle != null)
						timeoutHandle.cancel(true);
					return;
				}
				long timeSinceLastMessage = ((System.currentTimeMillis() - lastMsgReceived) / 1000);
                while (tts.isSpeaking());
                String ttsString = "Lost Comms";
				tts.speak(ttsString, TextToSpeech.QUEUE_FLUSH, null);

				Log.i("tts.speak", "timeout= " + timeSinceLastMessage
						+ "\ntimeoutInterval= " + timeoutInterval);
			}
		};
	}

	public void startTimeoutHandle() {
		if (timeoutHandle != null)
			timeoutHandle.cancel(true);
		timeoutHandle = timeoutScheduler.scheduleAtFixedRate(timeoutRunnable,
				0, timeoutInterval, TimeUnit.MILLISECONDS);
	}

	public void setIasValue(Double iasValue) {
		this.iasValue = iasValue;
	}

	public void setAltValue(Float altValue) {
		this.altValue = altValue;
	}

	public void setLastMsgReceived(long lastMsgReceived) {
		this.lastMsgReceived = lastMsgReceived;
		setTimeoutBool(false);
	}

	public void setTimeoutBool(boolean timeoutBool) {
		this.timeoutBool = timeoutBool;
    }

	public void setIasInterval(int iasInterval) {
		this.iasInterval = iasInterval;
		startIasHandle();
	}

	public void setAltInterval(int altInterval) {
		this.altInterval = altInterval;
		startAltHandle();
	}

	public void setTimeoutInterval(int timeoutInterval) {
		this.timeoutInterval = timeoutInterval;
	}

	public void setIasMuteBool(boolean iasBool) {
		this.iasMuteBool = iasBool;
        startIasHandle();
	}

	public void setAltMuteBool(boolean altBool) {
		this.altMuteBool = altBool;
        startAltHandle();
	}

	public void setGlobalMuteBool(boolean globalAudioBool) {
		this.globalMuteBool = globalAudioBool;
	}

}
