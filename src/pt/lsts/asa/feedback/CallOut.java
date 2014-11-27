package pt.lsts.asa.feedback;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.comms.ImcSystem;
import pt.lsts.asa.fragments.DataFragment;
import pt.lsts.asa.managers.SoundManager;
import pt.lsts.asa.util.AccuTimer;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

import java.nio.charset.UnmappableCharacterException;
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

	private TextToSpeech iasTts, altTts;
	private ScheduledFuture iasCalloutHandle, altCalloutHandle;
	private final ScheduledExecutorService iasScheduler = Executors
			.newScheduledThreadPool(1);
	private final ScheduledExecutorService altScheduler = Executors
			.newScheduledThreadPool(1);
	Runnable iasCalloutRunnable;
	Runnable altCalloutRunnable;
	private ImcSystem sys;
	private Float iasValue=0f, altValue=0f;
	private int iasInterval, altInterval;
	private boolean iasBoolean, altBoolean;
	private DataFragment dataFrag;
	private Context context;
	private SoundManager soundManager = SoundManager.getInstance();

	public CallOut(Context context) {
		this.context = context;
		SoundManager.getInstance();
	}

	public void	startImcSubscribers(){
		ASA.getInstance().addSubscriber(ASA.getInstance().callOutSubscriber);
	}
	
	public void setSys(String selectedSys) {
		this.sys = dataFrag.getSystem(selectedSys);
	}

	public void shutdown() {
		stopCallOuts();
		iasTts.shutdown();
		altTts.shutdown();
	}

	public void initCallOuts() {
		startImcSubscribers();
		initCallOutIntervals();
		initTimers();
		initTextToSpeech();
		if (!soundManager.checkMute())
			startCallOuts();
	}

	public void initTextToSpeech() {
		iasTts = new TextToSpeech(context, new OnInitListener() {
			@Override
			public void onInit(int status) {

			}
		});
		iasTts.setLanguage(Locale.UK);
		altTts = new TextToSpeech(context, new OnInitListener() {
			@Override
			public void onInit(int status) {

			}
		});
		altTts.setLanguage(Locale.UK);
	}

	public void initCallOutIntervals() {
		//iasValue = 55.6f;
		//altValue = 199f;
		iasBoolean = false;
		altBoolean = true;
		iasInterval = 10000;
		altInterval = 2500;
	}

	public void startCallOuts() {
		startImcSubscribers();
		iasCalloutHandle = iasScheduler.scheduleAtFixedRate(iasCalloutRunnable,
				0, iasInterval, TimeUnit.MILLISECONDS);

		altCalloutHandle = altScheduler.scheduleAtFixedRate(altCalloutRunnable,
				0, altInterval, TimeUnit.MILLISECONDS);
	}

	public void stopCallOuts() {
		iasCalloutHandle.cancel(true);
		altCalloutHandle.cancel(true);
	}

	public void initTimers() {
		initAltTimer();
		initIasTimer();
	}

	public void initAltTimer() {
/*
		altCalloutRunnable = new Runnable() {
			@Override
			public void run() {
				Log.i("CallOut", "Start Runnable Alt");
				Log.i("CallOut", "Adding Altitude" + sys.getHeight());
				altTts.speak("Altitude " + sys.getHeight(),
						TextToSpeech.QUEUE_FLUSH, null);
			}
		};
		*/
		altCalloutRunnable = new Runnable() {
			@Override
			public void run() {
				/*
				altTts.speak("Altitude " + altValue,
						TextToSpeech.QUEUE_FLUSH, null);
						*/
				Log.i("Altitude","alt= "+altValue);
			}
		};

	}

	public void initIasTimer() {
/*
		iasCalloutRunnable = new Runnable() {
			@Override
			public void run() {
				Log.i("CallOut", "Start Runnable IAS");
				Log.i("CallOut", "Adding Speed" + sys.getSpeed());
				iasTts.speak("Speed " + sys.getSpeed(),
						TextToSpeech.QUEUE_FLUSH, null);
			}
		};
		*/
		
		iasCalloutRunnable = new Runnable() {
			@Override
			public void run() {
				/*
				iasTts.speak("Speed " + iasValue,
						TextToSpeech.QUEUE_FLUSH, null);
						*/
				Log.i("IAS","ias= "+iasValue);
			}
		};

	}

	public boolean isInt(float val) {
		if (val == ((float) ((int) val)))
			return true;
		return false;
	}
	
	public void setIasValue(Float iasValue) {
		this.iasValue = iasValue;
	}

	public void setAltValue(Float altValue) {
		this.altValue = altValue;
	}

}
