package pt.lsts.newaccu.listenners;

import java.util.ArrayList;

import pt.lsts.newaccu.pos.Beacon;

public interface BeaconListChangeListener {

	void onBeaconListChange(ArrayList<Beacon> newlist);
}
