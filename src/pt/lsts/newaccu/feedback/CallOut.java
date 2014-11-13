package pt.lsts.newaccu.feedback;

import pt.lsts.newaccu.comms.IMCSubscriber;
import pt.lsts.newaccu.managers.SoundManager;
import pt.lsts.newaccu.util.AccuTimer;
import pt.lsts.imc.IMCMessage;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

/**
 * Text to Speech
 * @author
 */
public class CallOut implements IMCSubscriber{

	private TextToSpeech iasTts, altTts;
	private float iasValue,altValue;
	private AccuTimer iasTimer;
	private AccuTimer altTimer;
	private int iasInterval,altInterval;
	private boolean iasBoolean, altBoolean;
	private Context context;
	private SoundManager soundManager = SoundManager.getInstance();
	
	public CallOut(Context context){
		this.context=context;
		SoundManager.getInstance();
		initCallOuts();
	}
	
	public void shutdown(){
		stopCallOuts();
		iasTts.shutdown();
		altTts.shutdown();
	}
	
	
	@Override
	public void onReceive(IMCMessage msg) {
		// TODO Auto-generated method stub
		
	}
	
	public void initCallOuts(){
		initCallOutIntervals();
		initTimers();
		initTextToSpeech();
		if (!soundManager.checkMute())
			startCallOuts();
	}
	
	public void initTextToSpeech(){
		iasTts = new TextToSpeech(context, new OnInitListener() {
		    @Override
		    public void onInit(int status) {

		    }
		});
		altTts = new TextToSpeech(context, new OnInitListener() {
		    @Override
		    public void onInit(int status) {

		    }
		});
	}
	
	public void initCallOutIntervals(){
		iasValue= 55.6f;
		altValue= 199f;
		iasBoolean=true;
		altBoolean=true;
		iasInterval=10000;
		altInterval=5000;
	}

	public void startCallOuts(){
		if (altBoolean)
			altTimer.start();
		if (iasBoolean)
			iasTimer.start();
		
	}
	
	public void stopCallOuts(){
		iasTimer.stop();
		altTimer.stop();
	}
	
	public void initTimers(){
		
		altTimer = new AccuTimer(new Runnable(){
			@Override
			public void run() {
				String text="Altitude ";
				if (altValue!=-1){
					if (isInt(altValue))
						text+=((int)altValue);
					else
						text+=altValue;
				}else
					text+="NAN";
				altTts.speak(text, altTts.QUEUE_FLUSH, null);
			}
		    }, altInterval);
		
		iasTimer = new AccuTimer(new Runnable(){
			@Override
			public void run() {
				String text="Speed ";
				if (iasValue!=-1){
					if (isInt(iasValue))
						text+=((int)iasValue);
					else
						text+=iasValue;
				}else
					text+="NAN";
				iasTts.speak(text, iasTts.QUEUE_FLUSH, null);
			}
		    }, iasInterval);
		

		    
	}
	
	public boolean isInt(float val){
		if (val==((float)((int)val)))
			return true;
		return false;
	}
	
	
}
