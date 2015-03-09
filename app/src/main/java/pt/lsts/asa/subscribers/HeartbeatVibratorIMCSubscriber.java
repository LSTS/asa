package pt.lsts.asa.subscribers;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.feedback.HeartbeatVibrator;
import pt.lsts.imc.IMCMessage;

public class HeartbeatVibratorIMCSubscriber implements IMCSubscriber{

	public static final String[] SUBSCRIBED_MSGS = { "Heartbeat" };
	public static final String TAG = "HeartbeatVibrator";
	private HeartbeatVibrator heartbeatVibrator;
    private Thread thread;
	
	public HeartbeatVibratorIMCSubscriber(HeartbeatVibrator heartbeatVibrator){
		this.heartbeatVibrator = heartbeatVibrator;
	}
	
	@Override
	public void onReceive(final IMCMessage msg) {

        if (thread!=null)//if there is a previous message, finish going through that one
            while (thread.isAlive());

        thread = new Thread() {
            @Override
            public void run() {

                // If active system doesnt exist or isnt a message from active system
                if (ASA.getInstance().getActiveSys() == null)
                    return;
                if (ASA.getInstance().getActiveSys().getId() != (Integer) msg
                        .getHeaderValue("src"))
                    return;

                if (ASA.getInstance().getPrefs().getBoolean("vibrate", true))
                    heartbeatVibrator.getmVibrator().vibrate(heartbeatVibrator.getmDuration());
            }
        };
        thread.start();

	}
}
