package pt.lsts.asa.subscribers;

import android.util.Log;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.handlers.AccuSmsHandler;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.Sms;

public class AccuSmsHandlerSubscriber implements IMCSubscriber{

	public static final String[] SUBSCRIBED_MSGS = { "Sms" };
	public static final String TAG = "AccuSmsHandler";
	private AccuSmsHandler accuSmsHandler;
	
	public AccuSmsHandlerSubscriber(AccuSmsHandler accuSmsHandler){
		this.accuSmsHandler = accuSmsHandler;
	}
	
	@Override
	public void onReceive(IMCMessage msg) {

		if (msg.getMgid() == Sms.ID_STATIC
				&& msg.getDst() == accuSmsHandler.getmManager().getLocalId()) {
			Log.i("SmsManager", "Sending an SMS to " + msg.getString("number"));
			accuSmsHandler.sendSms(msg.getString("number"), msg.getString("contents"),
					msg.getInteger("timeout"));
		} else {
			Log.w("SmsManager", "Ignoring Sms request");
			System.out.println(accuSmsHandler.getmManager().getLocalId());
			System.out.println(msg.toString());
		}
	}
	
}
