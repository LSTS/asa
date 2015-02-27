package pt.lsts.asa.subscribers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.GmapsUtil;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.Announce;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IndicatedSpeed;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanDB;
import pt.lsts.imc.VehicleState;

/**
 * Created by jloureiro on 2/10/15.
 */
public class SystemsUpdaterServiceIMCSubscriber extends Service implements IMCSubscriber {

    public static final String TAG = "SysUpdaterServiceIMCSub";
    public static final boolean DEBUG = false;
    private SystemList systemList = ASA.getInstance().getSystemList();

    public SystemsUpdaterServiceIMCSubscriber(){}

    @Override
    public void onReceive(final IMCMessage msg) {

        final int ID_MSG = msg.getMgid();
        if (ID_MSG==Announce.ID_STATIC){
            Log.v(TAG, "Announce:\n"+msg.toString());
            processAnnounce(msg,systemList);
        }else {
            Sys sys = systemList.findSysById((Integer) msg.getHeaderValue("src"));
            switch (ID_MSG) {

                // Process VehicleState to get error count
                case VehicleState.ID_STATIC:
                    Log.v(TAG, "Received VehicleState:\n"+msg.toString());
                    processVehicleState(msg, sys);
                    break;

                case EstimatedState.ID_STATIC:
                    Log.v(TAG, "EstimatedState:\n"+msg.toString());
                    processEstimatedState(msg,sys);
                    break;
                case IndicatedSpeed.ID_STATIC:
                    Log.v(TAG, "IndicatedSpeed:\n"+msg.toString());
                    processIndicatedSpeed(msg,sys);
                    break;

                case PlanDB.ID_STATIC://interaction with PlanDB, request and reply with plan spec
                    Log.v(TAG,"PlanDB:\n"+msg.toString());

                    break;
                case PlanControlState.ID_STATIC://STATE = EXECUTING, READY, INITIALIZING, BLOCKED
                    Log.v(TAG,"PlanControlState:\n"+msg.toString());
                    processPlanControlState(msg,sys);
                    break;

                // Nothing to do on other messages
                default:
                    Log.i(TAG,"other - "+msg.getAbbrev());
                    break;
            }
            sys.lastMessageReceived = System.currentTimeMillis();//always update lastMessageReceived
        }

    }

    public void processAnnounce(IMCMessage msg, SystemList systemList){
        Announce m = (Announce) msg;

        Log.v(TAG, "announce from: "+m.getSysName());

        // If System already exists in host list
        if (systemList.containsSysName(m.getSysName())) {
            Log.i(TAG,"alreadyOnList");
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
        Log.i(TAG,"notOnList");
        // If Service IMC+UDP doesnt exist or isnt reachable, return...
        if (IMCUtils.getAnnounceService(msg, "imc+udp") == null) {
            Log.e(TAG, m.getSysName()
                    + " node doesn't have IMC protocol or isn't reachable");
            Log.e(TAG, msg.toString());
            return;
        }
        Log.i(TAG,"addrAndPort: "+IMCUtils.getAnnounceIMCAddressPort(msg));
        String[] addrAndPort = IMCUtils.getAnnounceIMCAddressPort(msg);
        if (addrAndPort == null) {
            Log.e(TAG, "No Announce Services: " + m.getSysName());
            return;
        }

        // If Not include it
        Sys sys = new Sys(addrAndPort[0], Integer.parseInt(addrAndPort[1]),
                m.getSysName(),
                (Integer) msg.getHeaderValue("src"),
                m.getSysType().name(), true, false);
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

    public void processVehicleState(IMCMessage msg, Sys sys){
        int errors = msg.getInteger("error_count");
        if (sys != null) // Meaning it exists on the list
        {
            Log.i(TAG, "" + errors);
            sys.setError(errors > 0);
            systemList.changeList(systemList.getList()); // Update the list
        }
    }

    public void processEstimatedState(IMCMessage msg, Sys sys){
        Float alt = - ((Float) msg.getValue("z"));
        sys.setAlt(alt);
        int altInt = Math.round(alt);
        Log.i(TAG,"altDouble= "+alt+" | altInt="+altInt+" | sys.getAltInt()="+sys.getAltInt());
        if (altInt!=sys.getAltInt()){
            sys.setAltInt(altInt);
            if (ASA.getInstance().getActiveSys().equals(sys)){
                Log.i(TAG,"alt: getActiveSys().equals(sys)");
                ASA.getInstance().getBus().post(new Pair<String,Integer>("alt",altInt));
            }

        }
        Double latRad = msg.getDouble("lat");
        Double lonRad = msg.getDouble("lon");
        Double latDeg = (Double) Math.toDegrees(latRad);
        Double lonDeg = (Double) Math.toDegrees(lonRad);

        float offsetX = msg.getFloat("x");//offset north
        float offsetY = msg.getFloat("y");//offset east

        LatLng latLng = GmapsUtil.translateCoordinates(new LatLng(latDeg,lonDeg), offsetX, offsetY);

        Log.i(TAG,"latLng="+latLng.toString());
        //gmapfragment.setActiveSysLatLng(latLng);
        sys.setLatLng(latLng);

        float psi = msg.getFloat("psi");
        double psiDouble = psi;
        sys.setPsi((float) Math.toDegrees(psiDouble));
        //gmapfragment.updateSysMarker(sys);
        //call OTTO
    }

    public void processIndicatedSpeed(IMCMessage msg, Sys sys){
        Double ias = (Double) msg.getValue("value");
        Log.v(TAG,"IndicatedSpedd received: ias="+ias);
        sys.setIas(ias);
        int iasInt = (int) Math.round(ias);
        Log.i(TAG,"iasDouble= "+ias+" | iasInt="+iasInt+" | sys.getIasInt()="+sys.getIasInt());
        if (iasInt!=sys.getIasInt()){
            sys.setIasInt(iasInt);
            if (ASA.getInstance().getActiveSys().equals(sys)){
                Log.i(TAG,"getActiveSys().equals(sys)");
                ASA.getInstance().getBus().post(new Pair<String,Integer>("ias",iasInt));
            }
        }
        //call OTTO
    }

    public void processPlanControlState(IMCMessage msg,Sys sys){
        if (IMCUtils.isMsgFromActive(msg)) {
            Log.i(TAG, "PlanControlState: \n" + msg.toString());
            PlanControlState planControlState = (PlanControlState) msg;
            if (planControlState.getState() == PlanControlState.STATE.EXECUTING){
                boolean changed = ASA.getInstance().getActiveSys().setPlanID(planControlState.getPlanId());
                if (changed==true){
                    Log.i(TAG,"PlanControlState: \n"+"Changed Plan to: "+planControlState.getPlanId());
                    //notification to user
                    //planDB.request(planID) -> planDB.reply(arg=Plan Spec)
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.v(TAG,"onStartCommand()");
        //int result = super.onStartCommand(intent, flags, startId);
        ASA.getInstance().addSubscriber(this);
        return START_STICKY;//prevents from dying
    }

    @Override
    public void onCreate() {
        Log.v(TAG,"onCreate()");
    }

    @Override
    public void onDestroy() {
        Log.v(TAG,"onDestroy()");
        ASA.getInstance().removeSubscriber(this);
        stopSelf();
    }

}
