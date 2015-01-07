package pt.lsts.asa.feedback;

import java.util.ArrayList;
import java.util.Iterator;

import pt.lsts.asa.ASA;
import pt.lsts.asa.listenners.SystemListChangeListener;
import pt.lsts.asa.managers.IMCManager;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.AccuTimer;
import android.util.Log;

public class Heart implements SystemListChangeListener {
	public static final boolean DEBUG = false;
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
				Log.v(TAG, "Beating...");
			imm.sendToSys(sys, "HeartBeat");
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
