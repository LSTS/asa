package pt.lsts.asa.listenners;

import java.util.ArrayList;

import pt.lsts.asa.sys.Sys;

public interface SystemListChangeListener {

	public void onSystemListChange(ArrayList<Sys> list);
}
