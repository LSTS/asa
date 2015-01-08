package pt.lsts.asa.feedback;

import pt.lsts.asa.managers.IMCManager;

import android.content.Context;
import android.os.Vibrator;

/**
 * Simple class that vibrates on receiving an heartbeat message Should be added
 * as a listener to "heartbeat" message
 * 
 * @author jqcorreia
 *
 */
public class HeartbeatVibrator {

	private Vibrator mVibrator;
	private int mDuration;
	private IMCManager imm;
	public static final int DEFAULT_DURATION = 50;

	public HeartbeatVibrator(Context context, IMCManager imm) {
		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		setmDuration(DEFAULT_DURATION);
		this.imm = imm;
	}

	public HeartbeatVibrator(Context context, IMCManager imm, int duration) {
		this(context, imm);
		setmDuration(duration);
	}

	public int getmDuration() {
		return mDuration;
	}

	public void setmDuration(int mDuration) {
		this.mDuration = mDuration;
	}

	public void setmVibrator(Vibrator mVibrator) {
		this.mVibrator = mVibrator;
	}

	public Vibrator getmVibrator() {
		return this.mVibrator;
	}

}
