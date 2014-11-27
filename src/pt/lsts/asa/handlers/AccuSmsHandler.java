package pt.lsts.asa.handlers;

import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.managers.IMCManager;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.Sms;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class AccuSmsHandler {

	private IMCManager mManager;
	private Context mContext;
	

	public IMCManager getmManager() {
		return mManager;
	}

	public void setmManager(IMCManager mManager) {
		this.mManager = mManager;
	}

	public AccuSmsHandler(Context context) {
		mContext = context;
	}

	public void sendSms(String destination, String text, int timeout) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(destination, null, text, null, null);
		Toast.makeText(mContext, "SMS sent to " + destination,
				Toast.LENGTH_LONG).show();
	}

	public void stop() {

	}
}
