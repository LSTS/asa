package pt.lsts.asa.pos;

import java.util.ArrayList;

import pt.lsts.asa.ASA;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.listenners.BeaconListChangeListener;
import pt.lsts.asa.managers.IMCManager;
import pt.lsts.imc.IMCMessage;
import android.util.Log;

public class LblBeaconList {
	public static final String[] SUBSCRIBED_MSGS = { "LblConfig" };
	public static final String TAG = "LblBeaconList";
	private ArrayList<Beacon> list = new ArrayList<Beacon>();
	ArrayList<BeaconListChangeListener> listeners = new ArrayList<BeaconListChangeListener>();

	IMCManager imm;

	public LblBeaconList() {
		
	}

	public void notifyListeners() {
		for (BeaconListChangeListener l : listeners) {
			l.onBeaconListChange(list);
		}
	}

	public ArrayList<Beacon> getList() {
		return list;
	}

	public ArrayList<String> getNameList() {
		ArrayList<String> array = new ArrayList<String>();

		for (Beacon b : list) {
			array.add(b.getName());
		}
		return array;
	}

	public Beacon getBeaconByName(String name) {
		for (Beacon b : list) {
			if (b.getName().equalsIgnoreCase(name))
				return b;
		}
		return null;
	}

	public void addBeaconListChangeListener(BeaconListChangeListener l) {
		listeners.add(l);
	}
	
}
