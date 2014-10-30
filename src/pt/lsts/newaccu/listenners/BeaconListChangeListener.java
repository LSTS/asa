package pt.lsts.newaccu.listenners;

import java.util.ArrayList;

import pt.lsts.newaccu.positioning.Beacon;

public interface BeaconListChangeListener {

	void onBeaconListChange(ArrayList<Beacon> newlist);
}
