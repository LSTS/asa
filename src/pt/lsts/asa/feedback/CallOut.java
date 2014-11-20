package pt.lsts.asa.feedback;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.comms.ImcSystem;
import pt.lsts.asa.fragments.DataFragment;
import pt.lsts.asa.managers.SoundManager;
import pt.lsts.asa.util.AccuTimer;
import pt.lsts.imc.IMCMessage;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

/**
 * Text to Speech
 * 
 * @author
 */
public class CallOut implements IMCSubscriber {

	private TextToSpeech iasTts, altTts;
	private ScheduledFuture iasCalloutHandle, altCalloutHandle;
	private final ScheduledExecutorService iasScheduler = Executors
			.newScheduledThreadPool(1);
	private final ScheduledExecutorService altScheduler = Executors
			.newScheduledThreadPool(1);
	Runnable iasCalloutRunnable;
	Runnable altCalloutRunnable;
	private ImcSystem sys;

	private float iasValue, altValue;
	private int iasInterval, altInterval;
	private boolean iasBoolean, altBoolean;
	private DataFragment dataFrag;
	private Context context;
	private SoundManager soundManager = SoundManager.getInstance();

	public CallOut(Context context, String selectedSys) {
		//this.sys = dataFrag.getSystem(selectedSys);
		this.context = context;
		SoundManager.getInstance();
		initCallOuts();
	}

	public void setSys(String selectedSys) {
		this.sys = dataFrag.getSystem(selectedSys);
	}

	public void shutdown() {
		stopCallOuts();
		iasTts.shutdown();
		altTts.shutdown();
	}

	@Override
	public void onReceive(IMCMessage msg) {
		// TODO Auto-generated method stub

	}

	public void initCallOuts() {
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
		iasValue = 55.6f;
		altValue = 199f;
		iasBoolean = false;
		altBoolean = true;
		iasInterval = 10000;
		altInterval = 2500;
	}

	public void startCallOuts() {
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
				Log.i("CallOut", "Start Runnable Alt");
				altTts.speak("Altitude " + altValue,
						TextToSpeech.QUEUE_FLUSH, null);
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
				Log.i("CallOut", "Start Runnable IAS");
				iasTts.speak("Speed " + iasValue,
						TextToSpeech.QUEUE_FLUSH, null);
			}
		};

	}

	public boolean isInt(float val) {
		if (val == ((float) ((int) val)))
			return true;
		return false;
	}

}
