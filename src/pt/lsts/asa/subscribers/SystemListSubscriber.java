package pt.lsts.asa.subscribers;

import android.util.Log;
import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

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
		if (msg.getAbbrev().equalsIgnoreCase("heartbeat")) {

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
		if (msg.getAbbrev().equalsIgnoreCase("Announce")) {
			// If System already exists in host list
			if (systemList.containsSysName(msg.getString("sys_name"))) {
				Sys s = systemList.findSysByName(msg.getString("sys_name"));

				if (DEBUG)
					Log.i("Log",
							"Repeated announce from: "
									+ msg.getString("sys_name"));

				if (!s.isConnected()) {
					systemList.findSysByName(msg.getString("sys_name")).lastMessageReceived = System
							.currentTimeMillis();
					systemList.findSysByName(msg.getString("sys_name")).setConnected(true);
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

				return;
			}
			// If Service IMC+UDP doesnt exist or isnt reachable, return...
			if (IMCUtils.getAnnounceService(msg, "imc+udp") == null) {
				Log.e(TAG, msg.getString("sys_name")
						+ " node doesn't have IMC protocol or isn't reachable");
				Log.e(TAG, msg.toString());
				return;
			}
			String[] addrAndPort = IMCUtils.getAnnounceIMCAddressPort(msg);
			if (addrAndPort == null) {
				Log.e(TAG, "Unreachable System - " + msg.getString("sys_name"));
				return;
			}
			// If Not include it
			Log.i("Log", "Adding new System");
			Sys s = new Sys(addrAndPort[0], Integer.parseInt(addrAndPort[1]),
					msg.getString("sys_name"),
					(Integer) msg.getHeaderValue("src"),
					msg.getString("sys_type"), true, false);

			systemList.getList().add(s);

			// Update the list of available Vehicles
			systemList.changeList(systemList.getList());

			// Send an Heartbeat to register as a node in the vehicle (maybe
			// EntityList?)
			try {
				IMCMessage m = IMCDefinition.getInstance().create("Heartbeat");
				m.getHeader().setValue("src", 0x4100);
				ASA.getInstance().getIMCManager()
						.send(s.getAddress(), s.getPort(), m);
				ASA.getInstance().getIMCManager().getComm()
						.sendMessage(s.getAddress(), s.getPort(), m);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		// Process VehicleState to get error count
		else if (msg.getAbbrev().equalsIgnoreCase("VehicleState")) {
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
			if (sys == null)
				return;
			sys.lastMessageReceived = System.currentTimeMillis();
		}
		
	}

}
