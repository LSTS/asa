package pt.lsts.asa;

import java.io.IOException;
import java.util.ArrayList;

import pt.lsts.asa.comms.Announcer;
import pt.lsts.asa.comms.IMCSubscriber;
import pt.lsts.asa.feedback.CallOut;
import pt.lsts.asa.feedback.Heart;
import pt.lsts.asa.feedback.HeartbeatVibrator;
import pt.lsts.asa.handlers.AccuSmsHandler;
import pt.lsts.asa.listenners.MainSysChangeListener;
import pt.lsts.asa.managers.GPSManager;
import pt.lsts.asa.managers.IMCManager;
import pt.lsts.asa.pos.LblBeaconList;
import pt.lsts.asa.subscribers.AccuSmsHandlerSubscriber;
import pt.lsts.asa.subscribers.CallOutSubscriber;
import pt.lsts.asa.subscribers.HeartbeatVibratorSubscriber;
import pt.lsts.asa.subscribers.LblBeaconListSubscriber;
import pt.lsts.asa.subscribers.SystemListSubscriber;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.MUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Global Singleton that actually acts as the workhorse of ACCU. Contains all
 * the structures that need to be global and persistent across all
 * activities/panels
 * 
 * @author jqcorreia
 * 
 */

public class ASA {

	private static final String TAG = "ASA";

	private static Context context;
	private static ASA instance;
	private static Sys activeSys;

	private static IMCManager imcManager;
	public SystemList sysList;
	public SystemListSubscriber systemListSubscriber;
	
	public CallOut callOut;
	public CallOutSubscriber callOutSubscriber;
	
	public static Announcer announcer;
	public static AccuSmsHandler smsHandler;
	public static AccuSmsHandlerSubscriber accuSmsHandlerSubscriber;
	public static GPSManager gpsManager;
	public static HeartbeatVibrator hearbeatVibrator;
	public static HeartbeatVibratorSubscriber heartbeatVibratorSubscriber;
	public static Heart heart;
	public static LblBeaconList lblBeaconList;
	public static LblBeaconListSubscriber lblBeaconListSubscriber;
	public static SensorManager sensorManager;

	private static ArrayList<MainSysChangeListener> mainSysChangeListeners;
	public String broadcastAddress;
	public boolean started = false;
	public SharedPreferences sharedPreferences;

	private static Integer requestId = 0xFFFF; // Request ID for quick plan

	private ASA(Context context) {
		this.context = context;

		initIMCManager();
		initBroadcast();
		initGPSManager(context);
		initPreferences(context);
		initSystemsList();
		initAnnouncer();
		initSubscribers(context);
	}
	
	public void addSubscriber(IMCSubscriber sub){
		imcManager.addSubscriberToAllMessages(sub);
	}
	public void addSubscriber(IMCSubscriber sub, String[] abbrevNameList){
		imcManager.addSubscriber(sub, abbrevNameList);
	}
	
	public void initBroadcast(){
		try {
			broadcastAddress = MUtil.getBroadcastAddress(context);
		} catch (IOException e) {
			Log.e(TAG, ASA.class.getSimpleName()
					+ ": Couldn't get Brodcast address", e);
		}
	}
	
	public void initSystemsList(){
		mainSysChangeListeners = new ArrayList<MainSysChangeListener>();
		sysList = new SystemList(imcManager);
		systemListSubscriber = new SystemListSubscriber(sysList);
	}
	
	public void initAnnouncer(){
		announcer = new Announcer(imcManager, broadcastAddress, "224.0.75.69");
	}
	
	public void initGPSManager(Context context){
		gpsManager = new GPSManager(context);
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
	}
	
	public void initPreferences(Context context){
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void initIMCManager(){
		imcManager = new IMCManager();
		imcManager.startComms();
	}
	
	public void initSubscribers(Context context){		
		callOut = new CallOut(context);
		callOutSubscriber = new CallOutSubscriber(callOut);
		
		smsHandler = new AccuSmsHandler(context);
		accuSmsHandlerSubscriber = new AccuSmsHandlerSubscriber(smsHandler);
		
		hearbeatVibrator = new HeartbeatVibrator(context, imcManager);
		heartbeatVibratorSubscriber = new HeartbeatVibratorSubscriber(hearbeatVibrator);
		
		addInitialSubscribers();
	}
	
	public void addInitialSubscribers(){
		addSubscriber(heartbeatVibratorSubscriber, heartbeatVibratorSubscriber.SUBSCRIBED_MSGS);
		addSubscriber(accuSmsHandlerSubscriber, accuSmsHandlerSubscriber.SUBSCRIBED_MSGS);
		addSubscriber(systemListSubscriber);
		addSubscriber(lblBeaconListSubscriber, lblBeaconListSubscriber.SUBSCRIBED_MSGS);
	}	

	public void load() {
		Log.i(TAG, ASA.class.getSimpleName() + ": load");
		heart = new Heart();
		addLblSubscriber();
	}
	
	public void addLblSubscriber(){
		lblBeaconList = new LblBeaconList();
		lblBeaconListSubscriber = new LblBeaconListSubscriber(lblBeaconList);
	}

	public void start() {
		Log.i(TAG, ASA.class.getSimpleName() + ": start");
		if (!started) {
			imcManager.startComms();
			announcer.start();
			sysList.start();
			heart.start();
			started = true;
		} else
			Log.e(TAG, ASA.class.getSimpleName()
					+ ": ASA ERROR: Already Started ASA Global");
	}

	public void pause() {
		Log.i(TAG, ASA.class.getSimpleName() + ": pause");
		if (started) {
			imcManager.killComms();
			announcer.stop();
			sysList.stop();
			heart.stop();
			smsHandler.stop();
			started = false;
		} else
			Log.e(TAG, ASA.class.getSimpleName()
					+ ": ASA ERROR: ASA Global already stopped");
	}

	public static ASA getInstance(Context context) {
		Log.i(TAG, ASA.class.getSimpleName() + ": getInstance(context)");
		if (instance == null) {
			instance = new ASA(context);
		}
		return instance;
	}

	public static ASA getInstance() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getInstance");
		return instance;
	}

	public Sys getActiveSys() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getActiveSys");
		return activeSys;
	}

	public void setActiveSys(Sys activeS) {
		Log.i(TAG, ASA.class.getSimpleName() + ": setActiveSys");
		activeSys = activeS;
		notifyMainSysChange();
	}

	public IMCManager getIMCManager() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getIMCManager");
		return imcManager;
	}

	public SystemList getSystemList() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getSystemList");
		return sysList;
	}

	public GPSManager getGpsManager() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getGpsManager");
		return gpsManager;
	}

	public SensorManager getSensorManager() {
		return sensorManager;
	}

	public LblBeaconList getLblBeaconList() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getLblBeaconList");
		return lblBeaconList;
	}

	// Main System listeners list related code
	public void addMainSysChangeListener(MainSysChangeListener listener) {
		Log.i(TAG, ASA.class.getSimpleName() + ": addMainSysChangeListener");
		mainSysChangeListeners.add(listener);
	}

	public void removeMainSysChangeListener(MainSysChangeListener listener) {
		Log.i(TAG, ASA.class.getSimpleName() + ": removeMainSysChangeListener");
		mainSysChangeListeners.remove(listener);
	}

	private static void notifyMainSysChange() {
		Log.i(TAG, ASA.class.getSimpleName() + ": notifyMainSysChange");
		for (MainSysChangeListener l : mainSysChangeListeners) {
			l.onMainSysChange(activeSys);
		}
	}

	public SharedPreferences getPrefs() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getPrefs");
		return sharedPreferences;
	}

	public boolean isStarted() {
		Log.i(TAG, ASA.class.getSimpleName() + ": isStarted");
		return started;
	}

	/**
	 * @return the next requestId
	 */
	public int getNextRequestId() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getNextRequestId");
		synchronized (requestId) {
			++requestId;
			if (requestId > 0xFFFF)
				requestId = 0;
			if (requestId < 0)
				requestId = 0;
			return requestId;
		}
	}
}
