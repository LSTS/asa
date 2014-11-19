package pt.lsts.asa.listenners;

import java.util.ArrayList;

import pt.lsts.asa.pos.Beacon;

public interface BeaconListChangeListener {

	void onBeaconListChange(ArrayList<Beacon> newlist);
}
