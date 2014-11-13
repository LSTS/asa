package pt.lsts.newaccu.listenners;

import java.util.ArrayList;

import pt.lsts.newaccu.sys.Sys;

public interface SystemListChangeListener {

	public void onSystemListChange(ArrayList<Sys> list);
}
