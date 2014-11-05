package pt.lsts.newaccu;

import java.io.IOException;
import java.util.ArrayList;

import pt.lsts.newaccu.comms.Announcer;
import pt.lsts.newaccu.comms.CallOut;
import pt.lsts.newaccu.feedback.Heart;
import pt.lsts.newaccu.feedback.HeartbeatVibrator;
import pt.lsts.newaccu.handlers.AccuSmsHandler;
import pt.lsts.newaccu.listenners.MainSysChangeListener;
import pt.lsts.newaccu.managers.GPSManager;
import pt.lsts.newaccu.managers.IMCManager;
import pt.lsts.newaccu.pos.LblBeaconList;
import pt.lsts.newaccu.sys.Sys;
import pt.lsts.newaccu.sys.SystemList;
import pt.lsts.newaccu.util.MUtil;
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
public class newAccu {

    private static final String TAG = "ACCU";

    private static Context mContext;
    private static newAccu instance;
    private static Sys activeSys;

    private static IMCManager imcManager;
    public SystemList mSysList;
    public static Announcer mAnnouncer;
    public static AccuSmsHandler mSmsHandler;
    public static GPSManager mGpsManager;
    public static HeartbeatVibrator mHBVibrator;
    public static Heart mHeart;
    public static LblBeaconList mBeaconList;
    public static CallOut callOut;
    public static SensorManager mSensorManager;

    private static ArrayList<MainSysChangeListener> mMainSysChangeListeners;
    public String broadcastAddress;
    public boolean started = false;
    public SharedPreferences mPrefs;

    private static Integer requestId = 0xFFFF; // Request ID for quick plan
    // sending

    private newAccu(Context context) {
	Log.i(TAG, newAccu.class.getSimpleName()
		+ ": Initializing Global ACCU Object");
	mContext = context;
	imcManager = new IMCManager();
	imcManager.startComms(); // Start comms here upfront

	mSysList = new SystemList(imcManager);

	try {
	    broadcastAddress = MUtil.getBroadcastAddress(mContext);
	} catch (IOException e) {
	    Log.e(TAG, newAccu.class.getSimpleName()
		    + ": Couldn't get Brodcast address", e);
	}

	mGpsManager = new GPSManager(mContext);
	mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	mMainSysChangeListeners = new ArrayList<MainSysChangeListener>();
	mAnnouncer = new Announcer(imcManager, broadcastAddress, "224.0.75.69");
	mSmsHandler = new AccuSmsHandler(mContext, imcManager);
	mHBVibrator = new HeartbeatVibrator(mContext, imcManager);
	callOut = new CallOut(mContext);
    }

    public void load() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": load");
	mHeart = new Heart();
	mBeaconList = new LblBeaconList();
    }

    public void start() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": start");
	if (!started) {
	    imcManager.startComms();
	    mAnnouncer.start();
	    mSysList.start();
	    mHeart.start();
	    started = true;
	} else
	    Log.e(TAG, newAccu.class.getSimpleName()
		    + ": ACCU ERROR: Already Started ACCU Global");
    }

    public void pause() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": pause");
	if (started) {
	    imcManager.killComms();
	    mAnnouncer.stop();
	    mSysList.stop();
	    mHeart.stop();
	    mSmsHandler.stop();
	    started = false;
	} else
	    Log.e(TAG, newAccu.class.getSimpleName()
		    + ": ACCU ERROR: ACCU Global already stopped");
    }

    public static newAccu getInstance(Context context) {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getInstance(context)");
	if (instance == null) {
	    instance = new newAccu(context);
	}
	return instance;
    }

    public static newAccu getInstance() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getInstance");
	return instance;
    }

    // public static void killInstance()
    // {
    // instance = null;
    // mSysList.timer.cancel(); //FIXME For now the timer cancelling goes here..
    // mAnnouncer.timer.cancel(); //FIXME same as above
    // }

    public Sys getActiveSys() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getActiveSys");
	return activeSys;
    }

    public void setActiveSys(Sys activeS) {
	Log.i(TAG, newAccu.class.getSimpleName() + ": setActiveSys");
	activeSys = activeS;
	notifyMainSysChange();
    }

    public IMCManager getIMCManager() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getIMCManager");
	return imcManager;
    }

    public SystemList getSystemList() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getSystemList");
	return mSysList;
    }

    public GPSManager getGpsManager() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getGpsManager");
	return mGpsManager;
    }

    public SensorManager getSensorManager() {
		return mSensorManager;
	}
    
    public LblBeaconList getLblBeaconList() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getLblBeaconList");
	return mBeaconList;
    }

    public CallOut getCallOut() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getCallOut");
	return callOut;
    }

    // Main System listeners list related code
    public void addMainSysChangeListener(MainSysChangeListener listener) {
	Log.i(TAG, newAccu.class.getSimpleName() + ": addMainSysChangeListener");
	mMainSysChangeListeners.add(listener);
    }

    public void removeMainSysChangeListener(MainSysChangeListener listener) {
	Log.i(TAG, newAccu.class.getSimpleName() + ": removeMainSysChangeListener");
	mMainSysChangeListeners.remove(listener);
    }

    private static void notifyMainSysChange() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": notifyMainSysChange");
	for (MainSysChangeListener l : mMainSysChangeListeners) {
	    l.onMainSysChange(activeSys);
	}
    }

    public SharedPreferences getPrefs() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getPrefs");
	return mPrefs;
    }

    public boolean isStarted() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": isStarted");
	return started;
    }

    /**
     * @return the next requestId
     */
    public int getNextRequestId() {
	Log.i(TAG, newAccu.class.getSimpleName() + ": getNextRequestId");
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
