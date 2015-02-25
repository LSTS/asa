package pt.lsts.asa.subscribers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCConsumers.AnnounceIMCConsumer;
import pt.lsts.asa.comms.IMCConsumers.EstimatedStateIMCConsumer;
import pt.lsts.asa.comms.IMCConsumers.IndicatedSpeedIMCConsumer;
import pt.lsts.asa.comms.IMCConsumers.PlanControlStateIMCConsumer;
import pt.lsts.asa.comms.IMCConsumers.VehicleStateIMCConsumer;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
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

    public SystemsUpdaterServiceIMCSubscriber(){}

    @Override
    public void onReceive(final IMCMessage msg) {

        final int ID_MSG = msg.getMgid();
        switch (ID_MSG) {

                case Announce.ID_STATIC:
                    AnnounceIMCConsumer announceIMCConsumer = new AnnounceIMCConsumer();
                    announceIMCConsumer.consume(msg);
                    break;

                case VehicleState.ID_STATIC:
                    VehicleStateIMCConsumer vehicleStateIMCConsumer = new VehicleStateIMCConsumer();
                    vehicleStateIMCConsumer.consume(msg);
                    break;

                case EstimatedState.ID_STATIC:
                    EstimatedStateIMCConsumer estimatedStateIMCConsumer = new EstimatedStateIMCConsumer();
                    estimatedStateIMCConsumer.consume(msg);
                    break;

                case IndicatedSpeed.ID_STATIC:
                    IndicatedSpeedIMCConsumer indicatedSpeedIMCConsumer= new IndicatedSpeedIMCConsumer();
                    indicatedSpeedIMCConsumer.consume(msg);
                    break;

                case PlanControlState.ID_STATIC://STATE = EXECUTING, READY, INITIALIZING, BLOCKED
                    PlanControlStateIMCConsumer planControlStateIMCConsumer = new PlanControlStateIMCConsumer();
                    planControlStateIMCConsumer.consume(msg);
                    break;

                case PlanDB.ID_STATIC://interaction with PlanDB, request and reply with plan spec
                    Log.i(TAG,"PlanDB: \n"+msg.toString());
                    break;

                // Nothing to do on other messages
                default:
                    Log.i(TAG,"other - "+msg.getAbbrev());
                    break;

        }
        IMCUtils.updateSysLastMsgReceived(msg);
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
