package pt.lsts.asa;

import java.io.IOException;
import java.util.ArrayList;

import pt.lsts.asa.comms.Announcer;
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

	private static Context mContext;
	private static ASA instance;
	private static Sys activeSys;

	private static IMCManager imcManager;
	public SystemList mSysList;
	public SystemListSubscriber systemListSubscriber;
	
	public CallOut callOut;
	public CallOutSubscriber callOutSubscriber;
	
	public static Announcer mAnnouncer;
	public static AccuSmsHandler mSmsHandler;
	public static AccuSmsHandlerSubscriber accuSmsHandlerSubscriber;
	public static GPSManager mGpsManager;
	public static HeartbeatVibrator mHBVibrator;
	public static HeartbeatVibratorSubscriber heartbeatVibratorSubscriber;
	public static Heart mHeart;
	public static LblBeaconList mBeaconList;
	public static LblBeaconListSubscriber lblBeaconListSubscriber;
	public static SensorManager mSensorManager;

	private static ArrayList<MainSysChangeListener> mMainSysChangeListeners;
	public String broadcastAddress;
	public boolean started = false;
	public SharedPreferences mPrefs;

	private static Integer requestId = 0xFFFF; // Request ID for quick plan

	// sending

	private ASA(Context context) {
		Log.i(TAG, ASA.class.getSimpleName()
				+ ": Initializing Global ACCU Object");
		mContext = context;
		imcManager = new IMCManager();
		imcManager.startComms(); // Start comms here upfront

		mSysList = new SystemList(imcManager);
		systemListSubscriber = new SystemListSubscriber(mSysList);
		imcManager.addSubscriberToAllMessages(systemListSubscriber);
		
		callOut = new CallOut(context, "");
		callOutSubscriber = new CallOutSubscriber(callOut);

		try {
			broadcastAddress = MUtil.getBroadcastAddress(mContext);
		} catch (IOException e) {
			Log.e(TAG, ASA.class.getSimpleName()
					+ ": Couldn't get Brodcast address", e);
		}

		mGpsManager = new GPSManager(mContext);
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		mMainSysChangeListeners = new ArrayList<MainSysChangeListener>();
		mAnnouncer = new Announcer(imcManager, broadcastAddress, "224.0.75.69");
		
		mSmsHandler = new AccuSmsHandler(mContext);
		accuSmsHandlerSubscriber = new AccuSmsHandlerSubscriber(mSmsHandler);
		imcManager.addSubscriber(accuSmsHandlerSubscriber, accuSmsHandlerSubscriber.SUBSCRIBED_MSGS);
		
		mHBVibrator = new HeartbeatVibrator(mContext, imcManager);
		heartbeatVibratorSubscriber = new HeartbeatVibratorSubscriber(mHBVibrator);
		imcManager.addSubscriber(heartbeatVibratorSubscriber, mHBVibrator.SUBSCRIBED_MSGS);
	}

	public void load() {
		Log.i(TAG, ASA.class.getSimpleName() + ": load");
		mHeart = new Heart();
		mBeaconList = new LblBeaconList();
		lblBeaconListSubscriber = new LblBeaconListSubscriber(mBeaconList);
		imcManager.addSubscriber(lblBeaconListSubscriber, lblBeaconListSubscriber.SUBSCRIBED_MSGS);
	}

	public void start() {
		Log.i(TAG, ASA.class.getSimpleName() + ": start");
		if (!started) {
			imcManager.startComms();
			mAnnouncer.start();
			mSysList.start();
			mHeart.start();
			started = true;
		} else
			Log.e(TAG, ASA.class.getSimpleName()
					+ ": ACCU ERROR: Already Started ACCU Global");
	}

	public void pause() {
		Log.i(TAG, ASA.class.getSimpleName() + ": pause");
		if (started) {
			imcManager.killComms();
			mAnnouncer.stop();
			mSysList.stop();
			mHeart.stop();
			mSmsHandler.stop();
			started = false;
		} else
			Log.e(TAG, ASA.class.getSimpleName()
					+ ": ACCU ERROR: ACCU Global already stopped");
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

	// public static void killInstance()
	// {
	// instance = null;
	// mSysList.timer.cancel(); //FIXME For now the timer cancelling goes here..
	// mAnnouncer.timer.cancel(); //FIXME same as above
	// }

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
		return mSysList;
	}

	public GPSManager getGpsManager() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getGpsManager");
		return mGpsManager;
	}

	public SensorManager getSensorManager() {
		return mSensorManager;
	}

	public LblBeaconList getLblBeaconList() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getLblBeaconList");
		return mBeaconList;
	}

	// Main System listeners list related code
	public void addMainSysChangeListener(MainSysChangeListener listener) {
		Log.i(TAG, ASA.class.getSimpleName() + ": addMainSysChangeListener");
		mMainSysChangeListeners.add(listener);
	}

	public void removeMainSysChangeListener(MainSysChangeListener listener) {
		Log.i(TAG, ASA.class.getSimpleName() + ": removeMainSysChangeListener");
		mMainSysChangeListeners.remove(listener);
	}

	private static void notifyMainSysChange() {
		Log.i(TAG, ASA.class.getSimpleName() + ": notifyMainSysChange");
		for (MainSysChangeListener l : mMainSysChangeListeners) {
			l.onMainSysChange(activeSys);
		}
	}

	public SharedPreferences getPrefs() {
		Log.i(TAG, ASA.class.getSimpleName() + ": getPrefs");
		return mPrefs;
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
