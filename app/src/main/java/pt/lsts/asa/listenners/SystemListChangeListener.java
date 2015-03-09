package pt.lsts.asa.listenners;

import pt.lsts.asa.sys.Sys;

import java.util.ArrayList;

public interface SystemListChangeListener {

	public void onSystemListChange(ArrayList<Sys> list);
}
