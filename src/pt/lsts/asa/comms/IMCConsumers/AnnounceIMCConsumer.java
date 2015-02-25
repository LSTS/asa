package pt.lsts.asa.comms.IMCConsumers;

import android.util.Log;

import pt.lsts.asa.ASA;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.Announce;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

/**
 * Created by jloureiro on 2/25/15.
 */
public class AnnounceIMCConsumer implements IMCConsumer{

    public static final String TAG = "Announce";
    private SystemList systemList;

    public AnnounceIMCConsumer(){
        this.systemList= ASA.getInstance().getSystemList();
    }

    public AnnounceIMCConsumer(SystemList systemList){
        this.systemList=systemList;
    }

    @Override
    public void consume(IMCMessage msg) {
        //Sys sys = systemList.findSysById((Integer) msg.getHeaderValue("src"));
        //boolean isFromActive = IMCUtils.isMsgFromActive(msg);

        Announce announce = (Announce) msg;

        Log.v(TAG, "announce from: " + announce.getSysName());

        // If System already exists in host list
        if (systemList.containsSysName(announce.getSysName())) {
            Log.i(TAG,"alreadyOnList");
            Sys s = systemList.findSysByName(announce.getSysName());

            Log.i("Log","Repeated announce from: "+ announce.getSysName());

            if (!s.isConnected()) {
                systemList.findSysByName(announce.getSysName()).lastMessageReceived = System
                        .currentTimeMillis();
                systemList.findSysByName(announce.getSysName()).setConnected(true);
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
        Log.i(TAG,"notOnList");
        // If Service IMC+UDP doesnt exist or isnt reachable, return...
        if (IMCUtils.getAnnounceService(msg, "imc+udp") == null) {
            Log.e(TAG, announce.getSysName()
                    + " node doesn't have IMC protocol or isn't reachable");
            Log.e(TAG, msg.toString());
            return;
        }
        Log.i(TAG,"addrAndPort: "+IMCUtils.getAnnounceIMCAddressPort(msg));
        String[] addrAndPort = IMCUtils.getAnnounceIMCAddressPort(msg);
        if (addrAndPort == null) {
            Log.e(TAG, "No Announce Services: " + announce.getSysName());
            return;
        }

        // If Not include it
        Sys sys = new Sys(addrAndPort[0], Integer.parseInt(addrAndPort[1]),
                announce.getSysName(),
                (Integer) msg.getHeaderValue("src"),
                announce.getSysType().name(), true, false);
        sys.lastMessageReceived=System.currentTimeMillis();
        Log.i("New System Added: ", sys.getName());
        systemList.getList().add(sys);

        // Update the list of available Vehicles
        systemList.changeList(systemList.getList());
        ASA.getInstance().sysList = systemList;

        // Send an Heartbeat to register as a node in the vehicle (maybe
        // EntityList?)
        try {
            Heartbeat mm = new Heartbeat();
            mm.setSrc(0x4100);
            ASA.getInstance().getIMCManager()
                    .send(sys.getAddress(), sys.getPort(), mm);
            ASA.getInstance().getIMCManager().getComm()
                    .sendMessage(sys.getAddress(), sys.getPort(), mm);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
