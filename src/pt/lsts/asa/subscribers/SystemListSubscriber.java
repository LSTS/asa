package pt.lsts.asa.subscribers;

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

import android.util.Log;

public class SystemListSubscriber implements IMCSubscriber{

	public static final String TAG = "SystemList";
	public static final int CONNECTED_TIME_LIMIT = 5000;
	public static final boolean DEBUG = false;
	private SystemList systemList;
    private Thread thread;
	
	public SystemListSubscriber(SystemList systemList){
		this.systemList = systemList;
	}
	
	
	@Override
	public void onReceive(final IMCMessage msg) {


        if (thread!=null)//if there is a previous message, finish going through that one
            while (thread.isAlive());

        thread = new Thread() {
            @Override
            public void run() {
                // Process Heartbeat
                // Update lastHeartbeat received on systemList
                if (Heartbeat.ID_STATIC == msg.getMgid()) {

                    return;
                }

                // Process Announce routine
                final int ID_MSG = msg.getMgid();
                if (ID_MSG == Announce.ID_STATIC) {
                    Announce m =  (Announce)msg;

                    Log.v("Announce from ", m.getSysName());

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
                        return;
                    }
                    // If Service IMC+UDP doesnt exist or isnt reachable, return...
                    if (IMCUtils.getAnnounceService(msg, "imc+udp") == null) {
                        Log.e(TAG, m.getSysName()
                                + " node doesn't have IMC protocol or isn't reachable");
                        Log.e(TAG, msg.toString());
                        return;
                    }
                    String[] addrAndPort = IMCUtils.getAnnounceIMCAddressPort(msg);
                    if (addrAndPort == null) {
                        Log.e(TAG, "No Announce Services: " + m.getSysName());
                        return;
                    }

                    // If Not include it
                    Sys s = new Sys(addrAndPort[0], Integer.parseInt(addrAndPort[1]),
                            m.getSysName(),
                            (Integer) msg.getHeaderValue("src"),
                            m.getSysType().name(), true, false);
                    Log.i("New System Added: ", s.getName());
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
                        Log.i("Log", "Received VehicleState: " + msg.toString());
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
        };
        thread.start();

	}

}
