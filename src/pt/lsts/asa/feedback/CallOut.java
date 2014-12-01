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
	private ScheduledFuture iasHandle, altHandle, timeoutHandle;
	private final ScheduledExecutorService iasScheduler = Executors
			.newScheduledThreadPool(1);
	private final ScheduledExecutorService altScheduler = Executors
			.newScheduledThreadPool(1);
	private final ScheduledExecutorService timeoutScheduler = Executors
			.newScheduledThreadPool(1);
	Runnable iasRunnable, altRunnable, timeoutRunnable;
	private ImcSystem sys;
	private Float altValue=0f;
	private Double iasValue=0d;
	private long lastMsgReceived = 0;
	private boolean timeoutBool = false;
	private int iasInterval, altInterval, timeoutInterval;
	private boolean iasBoolean, altBoolean;
	private DataFragment dataFrag;
	private Context context;
	private SoundManager soundManager = SoundManager.getInstance();
	NumberFormat formatter = new DecimalFormat("#0.00");

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
		tts.shutdown();
	}

	public void initCallOuts() {
		startImcSubscribers();
		initCallOutIntervals();
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

	public void initCallOutIntervals() {
		//iasValue = 55.6f;
		//altValue = 199f;
		
		iasBoolean = false;
		altBoolean = true;
		timeoutBool = false;
		
		iasInterval = 10000;
		altInterval = 15000;
		timeoutInterval=20000;
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
				if (isTimeout())
					return;
				if (altHandle.isCancelled())
					initAlt();
				tts.setPitch(1.15f);
				tts.speak("Altitude " + formatter.format(altValue),
						TextToSpeech.QUEUE_ADD, null);
						
				Log.i("Altitude","alt= "+altValue);
			}
		};
		altHandle = altScheduler.scheduleAtFixedRate(altRunnable,
				0, altInterval, TimeUnit.MILLISECONDS);

	}

	public void initIas() {
		
		iasRunnable = new Runnable() {
			@Override
			public void run() {
				if (isTimeout())
					return;	
				if (iasHandle.isCancelled())
					initIas();
				tts.setPitch(1.15f);
				tts.speak("Speed " + formatter.format(iasValue),
						TextToSpeech.QUEUE_ADD, null);
						
				Log.i("IAS","ias= "+iasValue);

			}
		};
		iasHandle = iasScheduler.scheduleAtFixedRate(iasRunnable,
				0, iasInterval, TimeUnit.MILLISECONDS);

	}
	
	public boolean isTimeout(){
		if (timeoutBool==false && System.currentTimeMillis()-20000>lastMsgReceived){
			timeoutBool=true;//no message received in over a minute
			initTimeout();
		}
		return timeoutBool;
	}
	
	public void initTimeout() {
		timeoutRunnable = new Runnable() {
			@Override
			public void run() {
				if (timeoutBool==false){
					if (timeoutHandle!=null)
						timeoutHandle.cancel(true);
					return;
				}
				long timeSinceLastMessage = ((System.currentTimeMillis()-lastMsgReceived)/1000);
				tts.setPitch(1f);
				tts.speak("No message received in " + timeSinceLastMessage + " seconds",
						TextToSpeech.QUEUE_FLUSH, null);
				
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
		timeoutBool=false;
	}

}
