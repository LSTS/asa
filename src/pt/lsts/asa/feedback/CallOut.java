package pt.lsts.asa.feedback;

import pt.lsts.asa.ASA;
import pt.lsts.asa.managers.SoundManager;
import pt.lsts.asa.settings.Settings;

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

	private int iasInterval = 10000, altInterval = 15000,
			timeoutInterval = 25000;
	private boolean iasMuteBool, altMuteBool, timeoutBool, globalMuteBool;

	public CallOut(Context context) {
		this.context = context;
		SoundManager.getInstance();
	}

	public void initImcSubscribers() {
		ASA.getInstance().addSubscriber(
				ASA.getInstance().getCallOutSubscriber());
	}

	public void shutdown() {
		stopCallOuts();
		tts.shutdown();
	}

	public void initCallOuts() {
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
	}

	public void initBooleans() {
		iasMuteBool = false;
		altMuteBool = false;
		timeoutBool = false;
		globalMuteBool = false;
	}

	public void initIntervals() {
		int integer = Settings.getInt("audio_altitude_interval_in_seconds", 10) * 1000;
		setAltInterval(integer);
		integer = Settings.getInt("audio_ias_interval_in_seconds", 10) * 1000;
		setIasInterval(integer);
		integer = Settings.getInt("audio_timeout_interval_in_seconds", 60) * 1000;
		setTimeoutInterval(integer);
	}

	public void startCallOuts() {
		startAltHandle();
		startIasHandle();
	}

	public void stopCallOuts() {
		iasHandle.cancel(true);
		altHandle.cancel(true);
		timeoutHandle.cancel(true);
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
				tts.setSpeechRate(1.25f);
				/*
				 * tts.speak("Altitude " + formatter.format(altValue),
				 * TextToSpeech.QUEUE_ADD, null);
				 */

				Log.i("tts.speak", "alt= " + altValue + "\naltInterval= "
						+ altInterval);
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
				tts.setSpeechRate(1.25f);
				/*
				 * tts.speak("Speed " + formatter.format(iasValue),
				 * TextToSpeech.QUEUE_ADD, null);
				 */

				Log.i("tts.speak", "ias= " + iasValue + "\niasInterval= "
						+ iasInterval);

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
			timeoutBool = true;// no message received in over a minute
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
				tts.setSpeechRate(1f);
				/*
				 * tts.speak("No message received in " + timeSinceLastMessage +
				 * " seconds", TextToSpeech.QUEUE_FLUSH, null);
				 */
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
	}

	public void setAltMuteBool(boolean altBool) {
		this.altMuteBool = altBool;
	}

	public void setGlobalMuteBool(boolean globalAudioBool) {
		this.globalMuteBool = globalAudioBool;
	}

}
