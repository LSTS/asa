package pt.lsts.asa;

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
import pt.lsts.asa.subscribers.AccuSmsHandlerIMCSubscriber;
import pt.lsts.asa.subscribers.HeartbeatVibratorIMCSubscriber;
import pt.lsts.asa.subscribers.LblBeaconListIMCSubscriber;
import pt.lsts.asa.subscribers.SystemListIMCSubscriber;
import pt.lsts.asa.subscribers.SystemsUpdaterServiceIMCSubscriber;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.util.MUtil;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Global Singleton of ASA and necessary components
 * initiated in App extension of Application.
 * (Fork from ACCU)
 * 
 */

public class ASA {

	private static final String TAG = "ASA";

    public boolean UIThread = false;

	private static Context context;
	private static ASA instance;
	private static Sys activeSys;

	private static IMCManager imcManager;
	public SystemList sysList;
	public SystemListIMCSubscriber systemListIMCSubscriber;
	
	public static Announcer announcer;
	public static AccuSmsHandler smsHandler;
	public static AccuSmsHandlerIMCSubscriber accuSmsHandlerIMCSubscriber;
	public static GPSManager gpsManager;
	public static HeartbeatVibrator hearbeatVibrator;
	public static HeartbeatVibratorIMCSubscriber heartbeatVibratorIMCSubscriber;
	public static Heart heart;
	public static LblBeaconList lblBeaconList;
	public static LblBeaconListIMCSubscriber lblBeaconListIMCSubscriber;
	public static SensorManager sensorManager;
    private CallOut callOut;

	private static ArrayList<MainSysChangeListener> mainSysChangeListeners;
	public String broadcastAddress;
	public boolean started = false;
	public SharedPreferences sharedPreferences;
    private Bus bus;

    private SystemsUpdaterServiceIMCSubscriber systemsUpdaterServiceIMCSubscriber;

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
    public void removeSubscriber(IMCSubscriber sub){
        imcManager.removeSubscriberToAll(sub);
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
		//systemListIMCSubscriber = new SystemListIMCSubscriber(sysList);
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
        bus = new Bus(ThreadEnforcer.ANY);
	}
	
	public void initIMCManager(){
		imcManager = new IMCManager();
		imcManager.startComms();
	}
	
	public void initSubscribers(Context context){
		smsHandler = new AccuSmsHandler(context);
		accuSmsHandlerIMCSubscriber = new AccuSmsHandlerIMCSubscriber(smsHandler);
		
		hearbeatVibrator = new HeartbeatVibrator(context, imcManager);
		heartbeatVibratorIMCSubscriber = new HeartbeatVibratorIMCSubscriber(hearbeatVibrator);
		
		addInitialSubscribers();
	}
	
	public void addInitialSubscribers(){
		addSubscriber(heartbeatVibratorIMCSubscriber, heartbeatVibratorIMCSubscriber.SUBSCRIBED_MSGS);
		addSubscriber(accuSmsHandlerIMCSubscriber, accuSmsHandlerIMCSubscriber.SUBSCRIBED_MSGS);
		//addSubscriber(systemListIMCSubscriber);
		addSubscriber(lblBeaconListIMCSubscriber, lblBeaconListIMCSubscriber.SUBSCRIBED_MSGS);
	}	

	public void load() {
		Log.i(TAG, ASA.class.getSimpleName() + ": load");
		heart = new Heart();
		addLblSubscriber();
	}
	
	public void addLblSubscriber(){
		lblBeaconList = new LblBeaconList();
		lblBeaconListIMCSubscriber = new LblBeaconListIMCSubscriber(lblBeaconList);
	}

	public void start() {
		Log.i(TAG, ASA.class.getSimpleName() + ": start");
		if (!started) {
			imcManager.startComms();
			announcer.start();
			sysList.start();
			heart.start();
			started = true;
            startAndBindSystemsUpdaterIMCSubscriber();
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
		Log.v(TAG, ASA.class.getSimpleName() + ": getInstance(context)");
		if (instance == null) {
			instance = new ASA(context);
		}
		return instance;
	}

	public static ASA getInstance() {
		Log.v(TAG, ASA.class.getSimpleName() + ": getInstance");
		return instance;
	}

	public Sys getActiveSys() {
		Log.v(TAG, ASA.class.getSimpleName() + ": getActiveSys");
		return activeSys;
	}

	public void setActiveSys(Sys activeS) {
		Log.v(TAG, ASA.class.getSimpleName() + ": setActiveSys");
		activeSys = activeS;
		notifyMainSysChange();
	}

	public IMCManager getIMCManager() {
		Log.v(TAG, ASA.class.getSimpleName() + ": getIMCManager");
		return imcManager;
	}

	public SystemList getSystemList() {
		Log.v(TAG, ASA.class.getSimpleName() + ": getSystemList");
		return sysList;
	}

	public GPSManager getGpsManager() {
		Log.v(TAG, ASA.class.getSimpleName() + ": getGpsManager");
		return gpsManager;
	}

	public SensorManager getSensorManager() {
		return sensorManager;
	}

	public LblBeaconList getLblBeaconList() {
		Log.v(TAG, ASA.class.getSimpleName() + ": getLblBeaconList");
		return lblBeaconList;
	}

    public SystemsUpdaterServiceIMCSubscriber getSystemsUpdaterServiceIMCSubscriber() {
        return systemsUpdaterServiceIMCSubscriber;
    }

    public void setSystemsUpdaterServiceIMCSubscriber(SystemsUpdaterServiceIMCSubscriber systemsUpdaterServiceIMCSubscriber) {
        this.systemsUpdaterServiceIMCSubscriber = systemsUpdaterServiceIMCSubscriber;
    }

    public CallOut getCallOut() {
        return callOut;
    }

    public Bus getBus() {
        return bus;
    }

    public void setCallOut(CallOut callOut) {
        this.callOut = callOut;
    }

	// Main System listeners list related code
	public void addMainSysChangeListener(MainSysChangeListener listener) {
		Log.v(TAG, ASA.class.getSimpleName() + ": addMainSysChangeListener");
		mainSysChangeListeners.add(listener);
	}

	public void removeMainSysChangeListener(MainSysChangeListener listener) {
		Log.v(TAG, ASA.class.getSimpleName() + ": removeMainSysChangeListener");
		mainSysChangeListeners.remove(listener);
	}

	private static void notifyMainSysChange() {
		Log.v(TAG, ASA.class.getSimpleName() + ": notifyMainSysChange");
		for (MainSysChangeListener l : mainSysChangeListeners) {
			l.onMainSysChange(activeSys);
		}
	}

	public SharedPreferences getPrefs() {
		Log.v(TAG, ASA.class.getSimpleName() + ": getPrefs");
		return sharedPreferences;
	}

	public boolean isStarted() {
		Log.v(TAG, ASA.class.getSimpleName() + ": isStarted");
		return started;
	}

	/**
	 * @return the next requestId
	 */
	public int getNextRequestId() {
		Log.v(TAG, ASA.class.getSimpleName() + ": getNextRequestId");
		synchronized (requestId) {
			++requestId;
			if (requestId > 0xFFFF)
				requestId = 0;
			if (requestId < 0)
				requestId = 0;
			return requestId;
		}
	}
	
	public static Context getContext() {
		return context;
	}
	
	public void addPreferencesListenner(OnSharedPreferenceChangeListener listener){
		ASA.getInstance().sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        Log.i(TAG,"ASA.getInstance().sharedPreferences.registerOnSharedPreferenceChangeListener(listener);");
	}

    public void startAndBindSystemsUpdaterIMCSubscriber(){
        SystemsUpdaterServiceIMCSubscriber systemsUpdaterServiceIMCSubscriber = new SystemsUpdaterServiceIMCSubscriber();
        Intent intent = new Intent(getContext(),SystemsUpdaterServiceIMCSubscriber.class);
        systemsUpdaterServiceIMCSubscriber.onStartCommand(intent,0,0);
        systemsUpdaterServiceIMCSubscriber.onBind(intent);
    }
	
}
