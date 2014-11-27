package pt.lsts.asa.subscribers;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.feedback.HeartbeatVibrator;
import pt.lsts.imc.IMCMessage;

public class HeartbeatVibratorSubscriber implements IMCSubscriber{

	private HeartbeatVibrator heartbeatVibrator;
	
	public HeartbeatVibratorSubscriber(HeartbeatVibrator heartbeatVibrator){
		this.heartbeatVibrator = heartbeatVibrator;
	}
	
	@Override
	public void onReceive(IMCMessage msg) {
		// If active system doesnt exist or isnt a message from active system
		if (ASA.getInstance().getActiveSys() == null)
			return;
		if (ASA.getInstance().getActiveSys().getId() != (Integer) msg
				.getHeaderValue("src"))
			return;

		if (ASA.getInstance().getPrefs().getBoolean("vibrate", true))
			heartbeatVibrator.getmVibrator().vibrate(heartbeatVibrator.getmDuration());
	}
	
}
