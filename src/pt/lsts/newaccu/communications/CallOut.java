package pt.lsts.newaccu.communications;

import pt.lsts.newaccu.Accu;
import pt.lsts.newaccu.communications.IMCSubscriber;
import pt.lsts.newaccu.util.AccuTimer;
import pt.lsts.imc.IMCMessage;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

/**
 * Text to Speech
 * @author
 */
public class CallOut implements IMCSubscriber{

    public static final String TAG = "CallOut";

    private TextToSpeech tts;
    private double currentSpeed;
    private final AccuTimer timer = new AccuTimer(new Runnable(){

	@Override
	public void run() {
	    Log.i(TAG, "CurrentSpeed: " + currentSpeed);
	    tts.speak(Double.toString(currentSpeed), 0, null);
	}

    }, 5000);
    private final Context context;
    private boolean started=false;

    public CallOut(Context context)
    {
	this.context = context;
	setDelay(2000);
    }
    @Override
    public void onReceive(IMCMessage msg)
    {
	if(msg.getAbbrev().equalsIgnoreCase("EstimatedState"))
	{
	    double vx = msg.getDouble("vx");
	    double vy = msg.getDouble("vy");
	    double vz = msg.getDouble("vz");
	    currentSpeed = Math.sqrt(Math.pow(vx, 2)+Math.pow(vy, 2)+Math.pow(vz, 2));
	    Log.i(TAG, "CurrentSpeed: " + currentSpeed);
	}
    }

    public void start()
    {
	tts = new TextToSpeech(context, new OnInitListener() {

	    @Override
	    public void onInit(int status) {

	    }
	});
	timer.start();
	Accu.getInstance().getIMCManager().addSubscriber(this, "EstimatedState");
	started = true;
    }

    public void stop()
    {
	Accu.getInstance().getIMCManager().removeSubscriberToAll(this);
	timer.stop();
	tts.shutdown();
	started = false;
    }

    public void toggle()
    {
	if(started) stop();
	else start();

    }
    public void setDelay(int delaymillis)
    {
	timer.setDelay(delaymillis);
    }
}
