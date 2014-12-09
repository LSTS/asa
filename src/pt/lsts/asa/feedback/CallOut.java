package pt.lsts.asa.feedback;

import pt.lsts.asa.ASA;
import pt.lsts.asa.managers.SoundManager;

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
import android.widget.Toast;

/**
 * Text to Speech
 * 
 * @author
 */
public class CallOut {

	private TextToSpeech tts;
	private Context context;

	private ScheduledFuture iasHandle, altHandle, timeoutHandle;
	private final ScheduledExecutorService iasScheduler = Executors
			.newScheduledThreadPool(1);
	private final ScheduledExecutorService altScheduler = Executors
			.newScheduledThreadPool(1);
	private final ScheduledExecutorService timeoutScheduler = Executors
			.newScheduledThreadPool(1);
	Runnable iasRunnable, altRunnable, timeoutRunnable;

	private Float altValue = 0f;
	private Double iasValue = 0d;
	NumberFormat formatter = new DecimalFormat("#0.00");
	private long lastMsgReceived = 0;

	private int iasInterval, altInterval, timeoutInterval;
	private boolean iasMuteBool, altMuteBool, timeoutBool, globalMuteBool;

	public CallOut(Context context) {
		this.context = context;
		SoundManager.getInstance();
	}

	public void startImcSubscribers() {
		ASA.getInstance().addSubscriber(ASA.getInstance().getCallOutSubscriber());
	}

	public void shutdown() {
		stopCallOuts();
		tts.shutdown();
	}

	public void initCallOuts() {
		startImcSubscribers();
		initBooleans();
		initIntervals();
		initTextToSpeech();
		initAlt();
		initIas();
	}

	public void initTextToSpeech() {
		tts = new TextToSpeech(context, new OnInitListener() {
			@Override
			public void onInit(int status) {

			}
		});
		tts.setLanguage(Locale.UK);
	}

	public void initBooleans() {
		iasMuteBool = false;
		altMuteBool = false;
		timeoutBool = false;
		globalMuteBool = false;
	}
	
	public void initIntervals(){
		iasInterval = 10000;
		altInterval = 15000;
		timeoutInterval = 20000;
	}

	public void startCallOuts() {
		startImcSubscribers();
		initIas();
		initAlt();
	}

	public void stopCallOuts() {
		iasHandle.cancel(true);
		altHandle.cancel(true);
	}

	public void initAlt() {

		altRunnable = new Runnable() {
			@Override
			public void run() {
				if (globalMuteBool==true || isTimeout() || altMuteBool==true)
					return;
				if (altHandle.isCancelled())
					initAlt();
				tts.setSpeechRate(1.25f);
				/*
				tts.speak("Altitude " + formatter.format(altValue),
						TextToSpeech.QUEUE_ADD, null);
						*/

				Log.i("tts.speak", "alt= " + altValue+"\naltInterval= "+altInterval);
			}
		};
		altHandle = altScheduler.scheduleAtFixedRate(altRunnable, 0,
				altInterval, TimeUnit.MILLISECONDS);

	}

	public void initIas() {

		iasRunnable = new Runnable() {
			@Override
			public void run() {
				if (globalMuteBool==true || isTimeout() || iasMuteBool==true)
					return;
				if (iasHandle.isCancelled())
					initIas();
				tts.setSpeechRate(1.25f);
				/*
				tts.speak("Speed " + formatter.format(iasValue),
						TextToSpeech.QUEUE_ADD, null);
						*/

				Log.i("tts.speak", "ias= " + iasValue+"\niasInterval= "+iasInterval);

			}
		};
		iasHandle = iasScheduler.scheduleAtFixedRate(iasRunnable, 0,
				iasInterval, TimeUnit.MILLISECONDS);

	}

	public boolean isTimeout() {
		if (timeoutBool == false
				&& System.currentTimeMillis() - 20000 > lastMsgReceived) {
			timeoutBool = true;// no message received in over a minute
			initTimeout();
		}
		return timeoutBool;
	}

	public void initTimeout() {
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
				tts.setSpeechRate(1f);
				/*
				tts.speak("No message received in " + timeSinceLastMessage
						+ " seconds", TextToSpeech.QUEUE_FLUSH, null);
						*/
				Log.i("tts.speak", "timeout= "+timeSinceLastMessage);

			}
		};
		timeoutHandle = timeoutScheduler.scheduleAtFixedRate(timeoutRunnable,
				0, timeoutInterval, TimeUnit.MILLISECONDS);
		iasHandle.cancel(false);
		altHandle.cancel(false);
	}

	public void setIasValue(Double iasValue) {
		this.iasValue = iasValue;
	}

	public void setAltValue(Float altValue) {
		this.altValue = altValue;
	}

	public void setLastMsgReceived(long lastMsgReceived) {
		this.lastMsgReceived = lastMsgReceived;
		timeoutBool = false;
	}

	public void setTimeoutBool(boolean timeoutBool) {
		this.timeoutBool = timeoutBool;
	}

	public void setIasInterval(int iasInterval) {
		this.iasInterval = iasInterval;
		setIasMuteBool(false);
	}

	public void setAltInterval(int altInterval) {
		this.altInterval = altInterval;
		setAltMuteBool(false);
	}

	public void setTimeoutInterval(int timeoutInterval) {
		this.timeoutInterval = timeoutInterval;
		setTimeoutBool(true);
	}

	public void setIasMuteBool(boolean iasBool) {
		this.iasMuteBool = iasBool;
	}

	public void setAltMuteBool(boolean altBool) {
		this.altMuteBool = altBool;
	}

	public void setGlobalMuteBool(boolean globalAudioBool) {
		this.globalMuteBool = globalAudioBool;
	}

}
