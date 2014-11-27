package pt.lsts.asa.sys;

import java.util.ArrayList;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.listenners.SystemListChangeListener;
import pt.lsts.asa.managers.IMCManager;
import pt.lsts.asa.util.AccuTimer;
import pt.lsts.asa.util.IMCUtils;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import android.util.Log;

public class SystemList {

	public static final String TAG = "SystemList";
	public static final int CONNECTED_TIME_LIMIT = 5000;
	public static final boolean DEBUG = false;

	ArrayList<Sys> sysList = new ArrayList<Sys>();
	ArrayList<SystemListChangeListener> listeners = new ArrayList<SystemListChangeListener>();

	AccuTimer timer;
	Runnable task = new Runnable() {
		public void run() {
			checkConnection();
		}
	};

	public SystemList(IMCManager imm) {
		timer = new AccuTimer(task, 1000);
	}

	// Timed action, in this case checking connection state trough Heartbeat
	// FIXME Heartbeat is not that good of a metric used simply like that
	private void checkConnection() {
		long currentTime = System.currentTimeMillis();

		if (DEBUG)
			Log.v("SystemList", "Checking Connections");

		for (Sys s : sysList) {
			if (DEBUG)
				Log.i("Log", s.getName() + " - "
						+ (currentTime - s.lastMessageReceived));
			if ((currentTime - s.lastMessageReceived) > CONNECTED_TIME_LIMIT
					&& s.isConnected()) {
				s.setConnected(false);
				changeList(sysList);
			} else if ((currentTime - s.lastMessageReceived) < CONNECTED_TIME_LIMIT
					&& !s.isConnected()) {
				s.setConnected(false);
				changeList(sysList);
			}
		}
	}

	public void addSystemListChangeListener(SystemListChangeListener l) {
		listeners.add(l);
	}

	public void changeList(ArrayList<Sys> list) {
		// Pass the new list to the listeners
		for (SystemListChangeListener l : listeners)
			l.onSystemListChange(list);
	}

	public boolean containsSysName(String name) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public boolean containsSysId(int id) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getId() == id)
				return true;
		}
		return false;
	}

	public Sys findSysByName(String name) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getName().equalsIgnoreCase(name))
				return sysList.get(c);
		}
		return null;
	}

	public Sys findSysById(int id) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getId() == id)
				return sysList.get(c);
		}
		return null;
	}

	public ArrayList<Sys> getList() {
		return sysList;
	}

	public ArrayList<String> getNameList() {
		ArrayList<String> list = new ArrayList<String>();
		for (Sys s : sysList) {
			list.add(s.getName());
		}
		return list;
	}

	public ArrayList<String> getNameListByType(String type) {
		ArrayList<String> list = new ArrayList<String>();
		for (Sys s : sysList) {
			if (s.getType().equals(type))
				list.add(s.getName());
		}
		return list;
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

}
