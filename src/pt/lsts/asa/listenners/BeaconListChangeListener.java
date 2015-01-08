package pt.lsts.asa.listenners;

import pt.lsts.asa.pos.Beacon;

import java.util.ArrayList;

public interface BeaconListChangeListener {

	void onBeaconListChange(ArrayList<Beacon> newlist);
}
