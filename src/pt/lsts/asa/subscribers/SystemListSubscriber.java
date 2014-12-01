package pt.lsts.asa.subscribers;

import android.util.Log;
import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.Announce;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.VehicleState;

public class SystemListSubscriber implements IMCSubscriber{

	public static final String TAG = "SystemList";
	public static final int CONNECTED_TIME_LIMIT = 5000;
	public static final boolean DEBUG = false;
	private SystemList systemList;
	
	public SystemListSubscriber(SystemList systemList){
		this.systemList = systemList;
	}
	
	
	@Override
	public void onReceive(IMCMessage msg) {
		
		// Process Heartbeat
		// Update lastHeartbeat received on systemList
		if (Heartbeat.ID_STATIC == msg.getMgid()) {

			return;
		}

		// Process Estimated State
		// Store Position Information
		// COMMENTED FOR NOW, WE DONT NEED THIS INFO TO BE GLOBAL

		// if(msg.getAbbrevName().equalsIgnoreCase("EstimatedState"))
		// {
		// Sys sys = findSysById((Integer)msg.getHeaderValue("src"));
		//
		// if(sys!=null) // Safeguard some rogue message of a system that doesnt
		// exist
		// {
		// String ref = msg.getString("ref");
		// sys.setRefMode(ref);
		// double[] rpy =
		// {msg.getDouble("phi"),msg.getDouble("theta"),msg.getDouble("psi")};
		// sys.setRPY(rpy);
		// if(ref.equalsIgnoreCase("LLD_ONLY"))
		// {
		// double[] ned = {0.0,0.0,0.0};
		// double[] lld =
		// {msg.getDouble("lat"),msg.getDouble("lon"),msg.getDouble("depth")};
		// sys.setLLD(lld);
		// sys.setNED(ned);
		// }
		// if(ref.equalsIgnoreCase("NED_ONLY"))
		// {
		// double[] ned =
		// {msg.getDouble("x"),msg.getDouble("y"),msg.getDouble("z")};
		// double[] lld = {0.0,0.0,0.0};
		// sys.setLLD(lld);
		// sys.setNED(ned);
		// }
		// if(ref.equalsIgnoreCase("NED_LLD"))
		// {
		// double[] ned =
		// {msg.getDouble("x"),msg.getDouble("y"),msg.getDouble("z")};
		// double[] lld =
		// {msg.getDouble("lat"),msg.getDouble("lon"),msg.getDouble("depth")};
		// sys.setLLD(lld);
		// sys.setNED(ned);
		// }
		// if(DEBUG)Log.i("Log","Name : " + sys.getName() + " lat " +
		// sys.getLLD()[1] + " x " + sys.getNED()[1]); // Simple debug message
		// }
		// }

		// Process Announce routine
		final int ID_MSG = msg.getMgid();
		if (ID_MSG == Announce.ID_STATIC) {
			Announce m =  (Announce)msg;
			
			Log.v("Announce from", "1 "+m.getSysName());
			
			// If System already exists in host list
			if (systemList.containsSysName(m.getSysName())) {
				Sys s = systemList.findSysByName(m.getSysName());

				if (DEBUG)
					Log.i("Log",
							"Repeated announce from: "
									+ m.getSysName());

				if (!s.isConnected()) {
					systemList.findSysByName(m.getSysName()).lastMessageReceived = System
							.currentTimeMillis();
					systemList.findSysByName(m.getSysName()).setConnected(true);
					systemList.changeList(systemList.getList());
					// Send an Heartbeat to resume communications in case of
					// system prior crash
					try {
						ASA.getInstance()
								.getIMCManager()
								.send(s.getAddress(),
										s.getPort(),
										IMCDefinition.getInstance().create(
												"Heartbeat"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				Log.v("Announce from", "ret 1 "+m.getSysName());
				return;
			}
			// If Service IMC+UDP doesnt exist or isnt reachable, return...
			if (IMCUtils.getAnnounceService(msg, "imc+udp") == null) {
				Log.e(TAG, m.getSysName()
						+ " node doesn't have IMC protocol or isn't reachable");
				Log.e(TAG, msg.toString());
				Log.v("Announce from", "ret 2 "+m.getSysName());
				return;
			}
			String[] addrAndPort = IMCUtils.getAnnounceIMCAddressPort(msg);
			if (addrAndPort == null) {
				Log.e(TAG, "No Announce Services - " + m.getSysName());
				return;
			}
			
			// If Not include it
			Sys s = new Sys(addrAndPort[0], Integer.parseInt(addrAndPort[1]),
					m.getSysName(),
					(Integer) msg.getHeaderValue("src"),
					m.getSysType().name(), true, false);
			Log.i("New System Added", s.getName());
			systemList.getList().add(s);

			// Update the list of available Vehicles
			systemList.changeList(systemList.getList());

			// Send an Heartbeat to register as a node in the vehicle (maybe
			// EntityList?)
			try {
				Heartbeat mm = new Heartbeat();
				mm.setSrc(0x4100);
				ASA.getInstance().getIMCManager()
						.send(s.getAddress(), s.getPort(), mm);
				ASA.getInstance().getIMCManager().getComm()
						.sendMessage(s.getAddress(), s.getPort(), mm);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		// Process VehicleState to get error count
		else if (ID_MSG == VehicleState.ID_STATIC) {
			if (DEBUG)
				Log.i("Log", "Received VehicleState" + msg.toString());
			Sys s = systemList.findSysById((Integer) msg.getHeaderValue("src"));
			int errors = msg.getInteger("error_count");
			if (s != null) // Meaning it exists on the list
			{
				if (DEBUG)
					Log.i("Log", "" + errors);
				s.setError(errors > 0);
				systemList.changeList(systemList.getList()); // Update the list
			}
		}
		// Update last messageReceived
		else {
			Sys sys = systemList.findSysById((Integer) msg.getHeaderValue("src"));

			// Returning from a ACCU crash this will prevent from listening to
			// messages with nothing on the list
			if (sys == null) {
				return;
			}
			sys.lastMessageReceived = System.currentTimeMillis();
		}
		
	}

}
