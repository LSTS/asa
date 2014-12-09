package pt.lsts.asa.comms;

import pt.lsts.asa.ASA;
import pt.lsts.asa.listenners.LocationChangeListener;
import pt.lsts.asa.managers.GPSManager;
import pt.lsts.asa.managers.IMCManager;
import pt.lsts.asa.util.AccuTimer;
import pt.lsts.asa.util.MUtil;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

import java.util.Date;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

public class Announcer {
	public static final String TAG = "Announcer";
	public static final String ANNOUNCE_ADDR = "224.0.75.69"; // FIXME hardcoded
																// value
	public static final int ANNOUNCE_PORT = 30100;
	public static final long ANNOUNCE_DELAY = 10000; // 10 seconds
	public static final boolean DEBUG = false;

	private IMCMessage announce;
	private IMCManager imm;
	private String broadcastAddress;
	private String multicastAddress;
	private AccuTimer at;
	private int gpsAddListenerCounter = 0;
	private GPSManager gpsManager; // = Accu.getInstance().getGpsManager();
	private SensorManager sensorManager;

	private double myHeading = Double.NaN;
	private String services = "";
	private Sensor sensor;

	private Runnable task = new Runnable() {
		@Override
		public void run() {
			boolean announcePosition = ASA.getInstance().getPrefs()
					.getBoolean("announcePosition", true);

			if (DEBUG)
				Log.i(TAG, announce.toString());
			if (imm.getListener() == null) {
				gpsManager.removeListener(locListener);
				gpsAddListenerCounter = 0;
				return;
			}
			if (gpsAddListenerCounter == 0 && announcePosition) {
				gpsManager.addListener(locListener);
			} else if (gpsAddListenerCounter == 2) {
				gpsManager.removeListener(locListener);
			}
			gpsAddListenerCounter = (gpsAddListenerCounter + 1) % 4;

			updateLocationOnAnnounce();
			for (int i = 0; i < 5; i++) {
				imm.send(broadcastAddress, ANNOUNCE_PORT + i, announce);
				imm.send(multicastAddress, ANNOUNCE_PORT + i, announce);
			}
		}
	};
	private LocationChangeListener locListener = new LocationChangeListener() {
		@Override
		public void onLocationChange(Location location) {
			updateLocationOnAnnounce();
		}
	};

	private SensorEventListener orientationListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			myHeading = event.values[0];
			updateHeadingOnAnnounce();
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	public Announcer(IMCManager imm, String broadcast, String multicast) {
		this.imm = imm;
		broadcastAddress = broadcast; // Needed for broadcasting
		multicastAddress = multicast;

		at = new AccuTimer(task, ANNOUNCE_DELAY);
		System.out.println("Broadcast address: " + broadcastAddress);
	}

	public void start() {
		gpsManager = ASA.getInstance().getGpsManager();
		sensorManager = ASA.getInstance().getSensorManager();

		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		generateAnnounce(); // Generate announce message only on start
		at.start();

		boolean announcePosition = ASA.getInstance().getPrefs()
				.getBoolean("announcePosition", true);
		if (announcePosition)
			gpsManager.addListener(locListener);
		boolean announceheading = ASA.getInstance().getPrefs()
				.getBoolean("announceHeading", true);
		if (announceheading)
			sensorManager.registerListener(orientationListener, sensor,
					SensorManager.SENSOR_DELAY_GAME);
	}

	public void stop() {
		at.stop();
		gpsManager.removeListener(locListener);
		sensorManager.unregisterListener(orientationListener);
	}

	public void setBroadcastAddress(String addr) {
		broadcastAddress = addr;
	}

	private void updateLocationOnAnnounce() {
		boolean announcePosition = ASA.getInstance().getPrefs()
				.getBoolean("announcePosition", true);
		if (!announcePosition)
			return;

		Location currentLocation = ASA.getInstance().getGpsManager()
				.getCurrentLocation();
		double lat = currentLocation.getLatitude();
		double lon = currentLocation.getLongitude();
		double height = currentLocation.getAltitude();

		announce.setValue("lat", Math.toRadians(lat));
		announce.setValue("lon", Math.toRadians(lon));
		announce.setValue("height", height);
	}

	private void updateHeadingOnAnnounce() {
		boolean announceHeading = ASA.getInstance().getPrefs()
				.getBoolean("announceHeading", true);
		String headingDegStr = myHeading < 0 || Double.isInfinite(myHeading)
				|| Double.isNaN(myHeading) ? "" : "heading://0.0.0.0/"
				+ Math.round(myHeading);

		if (announceHeading)
			announce.setValue("services", services + ";" + headingDegStr);
		else
			announce.setValue("services", services);
	}

	/**
	 * This function can be called to (re)generate the Announce message sent by
	 * the console
	 */
	public void generateAnnounce() {
		String ipfull = MUtil.getLocalIpAddress();
		// Location currentLocation =
		// Accu.getInstance().getGpsManager().getCurrentLocation();

		String[] ip = new String[4];

		if (ipfull == null) // Means no connection
		{
			ip[0] = ip[1] = ip[2] = ip[3] = "0";
		} else {
			ip = ipfull.split("\\.");
		}

		String sysName = "ASA-" + ip[2] + ip[3];
		String sysType = "CCU";
		// double lat = currentLocation.getLatitude();
		// double lon = currentLocation.getLongitude();
		// double height = currentLocation.getAltitude();

		int owner = 0xFFFF;
		services = "imc+udp://" + MUtil.getLocalIpAddress() + ":6001/" + ";"
				+ "imc+udp://" + MUtil.getLocalIpAddress() + ":6001/sms"; // FIXME
		try {
			announce = IMCDefinition.getInstance().create("Announce",
					"sys_name", sysName, "sys_type", sysType, "owner", owner,
					// "lat", Math.toRadians(lat),
					// "lon", Math.toRadians(lon),
					// "height", height,
					"services", services);
			updateLocationOnAnnounce();
			announce.getHeader().setValue("src", imm.getLocalId());
			announce.getHeader().setValue("src_ent", 255);
			announce.getHeader().setValue("dst_ent", 255);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void teste() {
	// if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	// accelerometerValues = event.values;
	// }
	//
	// if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
	// magneticFieldValues = event.values;
	// }
	//
	// SensorManager.getRotationMatrix(rotate, null, accelerometerValues,
	// magneticFieldValues);
	// SensorManager.remapCoordinateSystem(rotate, SensorManager.AXIS_X,
	// SensorManager.AXIS_MINUS_Z, rotate); //Overwriting rotate matrix with the
	// rotated values
	// SensorManager.getOrientation(rotate, rotationValues);
	//
	// // change radian to degree
	// rotationValues[0] = (float) Math.toDegrees(rotationValues[0]);
	// rotationValues[1] = (float) Math.toDegrees(rotationValues[1]);
	// rotationValues[2] = (float) Math.toDegrees(rotationValues[2]);
	//
	// float course = (float) Math.toDegrees(rotationValues[2]);
	// }
}
