package pt.lsts.asa.feedback;

import pt.lsts.asa.ASA;
import pt.lsts.asa.listenners.SystemListChangeListener;
import pt.lsts.asa.managers.IMCManager;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.AccuTimer;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.PlanDB;

import java.util.ArrayList;

import android.util.Log;

public class Heart implements SystemListChangeListener {
	public static final boolean DEBUG = true;
	public static final String TAG = "Heart";
	AccuTimer timer;
	ArrayList<Sys> vehicleList = new ArrayList<Sys>();
	SystemList sysList = ASA.getInstance().getSystemList();
	IMCManager imm = ASA.getInstance().getIMCManager();

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			sendHeartbeat();
		}
	};

	public Heart() {
		sysList.addSystemListChangeListener(this);
		timer = new AccuTimer(runnable, 1000);
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public void sendHeartbeat() {
        ArrayList<Sys> arrayListSys = (ArrayList<Sys>) vehicleList.clone();
		for (Sys sys : arrayListSys) {
			if (DEBUG)
				Log.v(TAG, "Beating... to sys:"+sys.getName());
			try {
                //imm.sendToSys(sys, "HeartBeat");//accu old version
                Heartbeat heartbeat = new Heartbeat();
                ASA.getInstance().getIMCManager().sendToSys(sys,heartbeat);
            }catch(Exception e){
                Log.e(TAG,"sendHeartBeat exception: "+e.getMessage(),e);
                e.printStackTrace();
            }
		}
	}

	public void updateVehicleList(ArrayList<Sys> list) {
		vehicleList.clear();
		for (Sys s : list) {
			// if(!s.getType().equalsIgnoreCase("CCU"))
			vehicleList.add(s);
		}
	}

	@Override
	public void onSystemListChange(ArrayList<Sys> list) {
		updateVehicleList(list);
	}
}
