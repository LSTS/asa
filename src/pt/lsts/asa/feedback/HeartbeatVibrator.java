package pt.lsts.asa.feedback;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.managers.IMCManager;
import pt.lsts.imc.IMCMessage;
import android.content.Context;
import android.os.Vibrator;

/**
 * Simple class that vibrates on receiving an heartbeat message Should be added
 * as a listener to "heartbeat" message
 * 
 * @author jqcorreia
 *
 */
public class HeartbeatVibrator implements IMCSubscriber {
	public static final String[] SUBSCRIBED_MSGS = { "Heartbeat" };

	private Vibrator mVibrator;
	private int mDuration;
	private IMCManager imm;
	public static final int DEFAULT_DURATION = 50;

	public HeartbeatVibrator(Context context, IMCManager imm) {
		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		mDuration = DEFAULT_DURATION;
		this.imm = imm;
		initialize();
	}

	public HeartbeatVibrator(Context context, IMCManager imm, int duration) {
		this(context, imm);
		mDuration = duration;
	}

	private void initialize() {
		imm.addSubscriber(this, SUBSCRIBED_MSGS);
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
			mVibrator.vibrate(mDuration);
	}
}
