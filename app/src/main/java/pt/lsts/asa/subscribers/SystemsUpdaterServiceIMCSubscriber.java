package pt.lsts.asa.subscribers;


import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.BatteryIndicatorPairUtil;
import pt.lsts.asa.util.GmapsUtil;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.Announce;
import pt.lsts.imc.AutopilotMode;
import pt.lsts.imc.Current;
import pt.lsts.imc.EntityList;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.FuelLevel;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IndicatedSpeed;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanDB;
import pt.lsts.imc.PlanManeuver;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.Voltage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by jloureiro on 2/10/15.
 */
public class SystemsUpdaterServiceIMCSubscriber extends Service implements IMCSubscriber {

    public static final String TAG = "SysUpdaterServiceIMCSub";
    public static final boolean DEBUG = false;
    private SystemList systemList = ASA.getInstance().getSystemList();
    private long lastMsgSentTimeMills = -1;//time in milliseconds since last Message Sent

    public SystemsUpdaterServiceIMCSubscriber(){}

    @Override
    public void onReceive(final IMCMessage msg) {

        final int ID_MSG = msg.getMgid();
        final ASA.MODE mode = ASA.getInstance().getMode();
        Sys sys = systemList.findSysById((Integer) msg.getHeaderValue("src"));

        //system does not exist, only interesting message is announce
        if (sys==null){//system does not exist
            if (ID_MSG==Announce.ID_STATIC){//only interesting message is announce
                Log.v(TAG, "Announce:\n"+msg.toString());
                processAnnounce(msg,systemList);
            }
            return;
        }
        //system already exists
        if (ID_MSG==EntityList.ID_STATIC){
            Log.v(TAG, "EntityList:\n"+msg.toString());
            processEntityList(msg,sys);
        }
        switch (mode){
            case CHECKLIST:
                //only parse messages from active Sys
                //see what messages are important
                break;
            case MANUAL:
                //only parse EstimatedState and IndicatedSpeed from active Sys
                switch (ID_MSG) {
                    case EstimatedState.ID_STATIC:
                        Log.v(TAG, "EstimatedState:\n"+msg.toString());
                        processEstimatedState(msg,sys);
                        break;
                    case IndicatedSpeed.ID_STATIC:
                        Log.v(TAG, "IndicatedSpeed:\n"+msg.toString());
                        processIndicatedSpeed(msg,sys);
                        break;
                    case AutopilotMode.ID_STATIC:
                        Log.v(TAG, "AutoPilotMode:\n"+msg.toString());
                        processAutoPilotMode(msg, sys);
                        break;
                    case FuelLevel.ID_STATIC:
                        Log.v(TAG, "FuelLevel:\n"+msg.toString());
                        processFuelLevel(msg,sys);
                        break;
                    case Current.ID_STATIC:
                        Log.v(TAG, "Current:\n" + msg.toString());
                        processCurrent(msg,sys);
                        break;
                    case Voltage.ID_STATIC:
                        Log.v(TAG, "Voltage:\n" + msg.toString());
                        processVoltage(msg,sys);
                        break;
                }
                break;
            case AUTO:
                switch (ID_MSG) {
                    case EstimatedState.ID_STATIC:
                        Log.v(TAG, "EstimatedState:\n"+msg.toString());
                        processEstimatedState(msg,sys);
                        break;
                    case IndicatedSpeed.ID_STATIC:
                        Log.v(TAG, "IndicatedSpeed:\n"+msg.toString());
                        processIndicatedSpeed(msg,sys);
                        break;
                    case PlanControlState.ID_STATIC://STATE = EXECUTING, READY, INITIALIZING, BLOCKED
                        Log.v(TAG,"PlanControlState:\n"+msg.toString());
                        processPlanControlState(msg,sys);
                        break;
                    case PlanDB.ID_STATIC://interaction with PlanDB, request and reply with plan spec
                        Log.v(TAG,"PlanDB:\n"+msg.toString());
                        processPlanDB(msg, sys);
                        break;
                    case AutopilotMode.ID_STATIC:
                        Log.v(TAG, "AutoPilotMode:\n"+msg.toString());
                        processAutoPilotMode(msg, sys);
                        break;
                    case FuelLevel.ID_STATIC:
                        Log.v(TAG, "FuelLevel:\n"+msg.toString());
                        processFuelLevel(msg,sys);
                        break;
                    case Current.ID_STATIC:
                        Log.v(TAG, "Current:\n"+msg.toString());
                        processCurrent(msg,sys);
                        break;
                    case Voltage.ID_STATIC:
                        Log.v(TAG, "Voltage:\n"+msg.toString());
                        processVoltage(msg,sys);
                        break;
                }
                break;
            case SYSTEMLIST:
                Log.v(TAG,"ASA.mode==SYSTEMLIST, message ignored:\n"+msg.toString());
                break;
            case NONE:
                Log.v(TAG,"ASA.mode==NONE, message ignored:\n"+msg.toString());
                break;
            default:
                Log.e(TAG,"Unreacognized mode: "+mode.toString());
                break;
        }
        //Log.v(TAG,"MsgType:"+msg.getAbbrev());
        //Log.v(TAG,msg.getAbbrev()+":\n"+msg.toString());
        if (sys!=null)
            sys.lastMessageReceived = System.currentTimeMillis();//always update lastMessageReceived

    }

    public void processAnnounce(IMCMessage msg, SystemList systemList){
        Announce m = (Announce) msg;

        Log.v(TAG, "announce from: " + m.getSysName());

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
        sendEntityListQuery(sys);//query sys for EntityList
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

    public void processEntityList(IMCMessage msg, Sys sys){
        EntityList entityList = (EntityList) msg;
        if (entityList.getOp().equals(EntityList.OP.REPORT)){
            sys.setEntityList(entityList.getList());
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
        Float alt = ((Float) msg.getValue("height")) - ((Float) msg.getValue("z"));
        sys.setHeight((Float)msg.getValue("height"));
        sys.setAlt(alt);
        int altInt = Math.round(alt);
        Log.i(TAG,"altDouble= "+alt+" | altInt="+altInt+" | sys.getAltInt()="+sys.getAltInt());
        if (altInt!=sys.getAltInt()){
            sys.setAltInt(altInt);
            if (sys.equals(ASA.getInstance().getActiveSys())){
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
        ASA.getInstance().getBus().post(sys);
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
            if (sys.equals(ASA.getInstance().getActiveSys())){
                Log.i(TAG,"getActiveSys().equals(sys)");
                ASA.getInstance().getBus().post(new Pair<String,Integer>("ias",iasInt));
            }
        }
        //call OTTO
    }

    public void processAutoPilotMode(IMCMessage msg,Sys sys){
        if (!sys.equals(ASA.getInstance().getActiveSys()))
            return;
        AutopilotMode autopilotMode = (AutopilotMode) msg;
        AutopilotMode.AUTONOMY autonomy = autopilotMode.getAutonomy();
        if (autonomy.equals(sys.getAutonomy()))
            return;
        sys.setAutonomy(autonomy);
        Log.i(TAG,"AutoPilotMode.autonomy changed to: "+autonomy.toString());
        ASA.getInstance().getBus().post(autonomy);
    }

    public void processFuelLevel(IMCMessage msg, Sys sys){
        if (!sys.equals(ASA.getInstance().getActiveSys()))
            return;
        FuelLevel fuelLevel = (FuelLevel) msg;
        double fuelLevelValue = fuelLevel.getValue();
        Log.i(TAG,"fuelLevel.getValue(): "+fuelLevelValue);
        ASA.getInstance().getBus().post(new BatteryIndicatorPairUtil("level",fuelLevelValue));
        if (fuelLevelValue<20 && sys.getFuelLevelValue()>=20)
            ASA.getInstance().getBus().post("LOW FUEL on: "+sys.getName());
        sys.setFuelLevelValue(fuelLevelValue);
    }

    public void processPlanControlState(IMCMessage msg,Sys sys){
        if (IMCUtils.isMsgFromActive(msg)) {
            Log.i(TAG, "PlanControlState: \n" + msg.toString());
            PlanControlState planControlState = (PlanControlState) msg;
            if (planControlState.getState() == PlanControlState.STATE.EXECUTING){
                String planID = planControlState.getPlanId();
                if (ASA.getInstance().getActiveSys()==null)
                    return;
                String paintedPlanID = ASA.getInstance().getActiveSys().getPaintedPlanID();
                boolean changed = ASA.getInstance().getActiveSys().setPlanID(planID);
                if (!ASA.getInstance().getActiveSys().isPaintedPlanUpdated()
                        && lastMsgSentTimeMills+500<System.currentTimeMillis()){//only send message every 500ms
                    //Log.i(TAG,"PlanControlState: \n"+"Changed Plan to: "+planID);
                    //planDB.request(planID) -> planDB.reply(arg=Plan Spec)
                    sendPlanDBrequestPlanID(planID);
                    this.lastMsgSentTimeMills=System.currentTimeMillis();
                }
                boolean maneuverChanged = sys.setManeuverID(planControlState.getManId());//Maneuver ID
                if (maneuverChanged==true){
                    for (PlanManeuver planManeuver : sys.getPlanSpecification().getManeuvers()) {
                        if (planManeuver.getManeuverId().equalsIgnoreCase(ASA.getInstance().getActiveSys().getManeuverID())) {
                            Float altPlanned = (sys.getHeight()) + ((Float) planManeuver.getData().getValue("z"));
                            int altPlannedInt = Math.round(altPlanned);
                            Log.d(TAG, "planManeuver:\n" + planManeuver.getData().toString() + "\n-------------------------------\n" + altPlanned);
                            ASA.getInstance().getBus().post(new Pair<String, Integer>("altPlanned", altPlannedInt));
                        }
                    }
                }
            }
        }
    }

    public void processCurrent(IMCMessage msg,Sys sys){
        if (!sys.equals(ASA.getInstance().getActiveSys()))
            return;
        if (sys.getEntityList().isEmpty())//EntityList is empty, requery
            sendEntityListQuery(sys);//query sys for EntityList

        String entityName = "Autopilot";
        String src_ent = ""+msg.getSrcEnt();

        if(src_ent.equalsIgnoreCase(sys.resolveEntity(entityName))){
            Current current = (Current) msg;
            double val = current.getValue();
            Log.d(TAG,"Current update: "+val);
            ASA.getInstance().getBus().post(new BatteryIndicatorPairUtil("current",val));
        }
    }

    public void processVoltage(IMCMessage msg,Sys sys){
        if (!sys.equals(ASA.getInstance().getActiveSys()))
            return;
        if (sys.getEntityList().isEmpty())//EntityList is empty, requery
            sendEntityListQuery(sys);//query sys for EntityList

        String entityName = "Autopilot";
        String src_ent = ""+msg.getSrcEnt();

        if(src_ent.equalsIgnoreCase(sys.resolveEntity(entityName))){
            Voltage voltage = (Voltage) msg;
            double val = voltage.getValue();
            Log.d(TAG,"Voltage update: "+val);
            ASA.getInstance().getBus().post(new BatteryIndicatorPairUtil("voltage",val));
        }
    }

    public static void sendPlanDBrequestPlanID(String planID){
        Log.i(TAG,"sendPlanDBrequestPlanID");
        PlanDB planDB = new PlanDB();
        planDB.setType(PlanDB.TYPE.REQUEST);
        planDB.setOp(PlanDB.OP.GET);
        planDB.setPlanId(planID);
        ASA.getInstance().getIMCManager().sendToActiveSys(planDB);
    }

    public static void sendEntityListQuery(Sys sys){
        EntityList entityList = new EntityList();
        entityList.setOp(EntityList.OP.QUERY);
        ASA.getInstance().getIMCManager().sendToSys(sys,entityList);
        Log.i(TAG,"sent EntityList QUERY to "+sys.getName());
    }

    public void processPlanDB(IMCMessage msg, Sys sys){
        PlanDB planDB = (PlanDB) msg;
        String planID = planDB.getPlanId();
        if (ASA.getInstance().getActiveSys()!=null
                && ASA.getInstance().getActiveSys().getPaintedPlanID().equals(planID)){
            Log.i(TAG,"processPlanDB: no update needed");
            return;//no update needed
        }
        if (planDB.getType().equals(PlanDB.TYPE.SUCCESS)
                && IMCUtils.isMsgFromActive(msg)
                && planDB.getArg().getAbbrev().equals("PlanSpecification")){
            Log.i(TAG,"PlanDB:\n"+"plan switched in sys:"+sys.getName()+" to plan:"+planID);
            sys.setPlanID(planID);
            PlanSpecification planSpecification = (PlanSpecification) planDB.getArg();
            sys.setPlanSpecification(planSpecification);
            ASA.getInstance().getBus().post(planSpecification);
            for (PlanManeuver planManeuver : sys.getPlanSpecification().getManeuvers()) {
                if (planManeuver.getManeuverId().equalsIgnoreCase(ASA.getInstance().getActiveSys().getManeuverID())) {
                    Float altPlanned = (sys.getHeight()) + ((Float) planManeuver.getData().getValue("z"));
                    int altPlannedInt = Math.round(altPlanned);
                    Log.d(TAG, "planManeuver:\n" + planManeuver.getData().toString() + "\n-------------------------------\n" + altPlanned);
                    ASA.getInstance().getBus().post(new Pair<String, Integer>("altPlanned", altPlannedInt));
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
