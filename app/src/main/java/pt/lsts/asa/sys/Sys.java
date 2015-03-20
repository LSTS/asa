package pt.lsts.asa.sys;


import pt.lsts.imc.AutopilotMode;
import pt.lsts.imc.PlanManeuver;
import pt.lsts.imc.PlanSpecification;

import java.util.LinkedHashMap;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class Sys {

    public static final String TAG = "Sys";
	private String mAddress;
	private int mPort;
	private String mName;
	private int mId;
	public long lastMessageReceived;

    /**
     * CCU, STATICSENSOR, HUMANSENSOR, MOBILESENSOR, WSN, UUV, USV, UAV, UGV,
     *
     */
    private String mType;

	private double[] LLD = { 0.0, 0.0, 0.0 }; // Lat Lon depth in radians and
												// meters
	private double[] NED = { 0.0, 0.0, 0.0 }; // North East Down in meters
	private double[] RPY = { 0.0, 0.0, 0.0 }; // Roll Pitch Yaw in radians

    private String refMode; // Reference Mode name

    // This 2 Booleans are used to compute the color of
    // each sys in system list and serve as the actual state
    boolean mConnected;
    boolean mError;

    private LinkedHashMap<String,String> entityList = new LinkedHashMap<String,String>();//EntityList, used for Voltage/Current

    private AutopilotMode.AUTONOMY autonomy = null;//UAV mode: ASSISTED, AUTO, MANUAL
    private double fuelLevelValue = 100.0;

    private float height=0;//fixed variable representing ground level, altitude is height-z; only z varies

    //variables for auto mode
    private LatLng latLng = new LatLng(0,0);//Last known lat and lon including meters offset
    private float psi = 0.0f;//last known orientation from EstimatedState.psi
    private Marker maker=null;//googleMap marker
    private String planID = "";//plan executing
    private String paintedPlanID = "";//painted plan in GoogleMaps
    private String maneuverID = "";//current maneuverID executing
    private PlanSpecification planSpecification=null;//current PlanSpecification


    //variables for manual mode
    private float alt;
    private double ias;
    private int iasInt, altInt;

    public String getPlanID() {
        return planID;
    }

    /**
     *
     * @param planID new PlanID from PlanControlState.getPlanID()
     * @return true if changed
     */
    public boolean setPlanID(String planID) {
        if (this.planID.equals(planID))
            return false;
        this.planID = planID;
        return true;
    }


    public String getPaintedPlanID() {
        return paintedPlanID;
    }

    public void setPaintedPlanID(String paintedPlanID) {
        this.paintedPlanID = paintedPlanID;
    }

    public boolean isPaintedPlanUpdated(){
        return this.paintedPlanID.equals(this.planID);

    }

    public int getAltInt() {
        return altInt;
    }

    public void setAltInt(int altInt) {
        this.altInt = altInt;
    }

    public double getIas() {
        return ias;
    }

    public void setIas(double ias) {
        this.ias = ias;
    }

    public float getAlt() {
        return alt;
    }

    public void setAlt(float alt) {
        this.alt = alt;
    }

    public int getIasInt() {
        return iasInt;
    }

    public void setIasInt(int iasInt) {
        this.iasInt = iasInt;
    }

	public boolean isError() {
		return mError;
	}

	public void setError(boolean error) {
		this.mError = error;
	}

	public boolean isConnected() {
		return mConnected;
	}

	public void setConnected(boolean mConnected) {
		this.mConnected = mConnected;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		this.mAddress = address;
	}

	public int getPort() {
		return mPort;
	}

	public void setPort(int port) {
		this.mPort = port;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getType() {
		return mType;
	}

	public double[] getLLD() {
		return LLD;
	}

	public void setLLD(double[] lLD) {
		LLD = lLD;
	}

	public double[] getNED() {
		return NED;
	}

	public void setNED(double[] nED) {
		NED = nED;
	}

	public double[] getRPY() {
		return RPY;
	}

	public void setRPY(double[] rPY) {
		RPY = rPY;
	}

	public String getRefMode() {
		return refMode;
	}

	public void setRefMode(String refMode) {
		this.refMode = refMode;
	}

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getPsi() {
        return psi;
    }

    public void setPsi(float psi) {
        this.psi = psi;
    }

    public Marker getMaker() {
        return maker;
    }

    public void setMaker(Marker maker) {
        this.maker = maker;
    }

    public AutopilotMode.AUTONOMY getAutonomy() {
        return autonomy;
    }

    public void setAutonomy(AutopilotMode.AUTONOMY autonomy) {
        this.autonomy = autonomy;
    }

    public double getFuelLevelValue() {
        return fuelLevelValue;
    }

    public void setFuelLevelValue(double fuelLevelValue) {
        this.fuelLevelValue = fuelLevelValue;
    }

    public String getManeuverID() {
        return maneuverID;
    }

    public boolean setManeuverID(String maneuverID) {
        if (maneuverID.equals(this.maneuverID))
            return false;
        this.maneuverID = maneuverID;
        return true;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public PlanSpecification getPlanSpecification() {
        return planSpecification;
    }

    public void setPlanSpecification(PlanSpecification planSpecification) {
        this.planSpecification = planSpecification;
    }

    public LinkedHashMap<String, String> getEntityList() {
        return entityList;
    }

    public void setEntityList(LinkedHashMap<String, String> entityList) {
        this.entityList = entityList;
        Log.v(TAG, "Sys: "+getName()+"\nsetEntityList:\n" + entityList.toString());
    }

    public String resolveEntity(String id){
        //Log.v(TAG,"resolveEntity - "+getName()+". EntityList:\n"+entityList.toString());
        if (entityList.containsKey(id)) {
            //Log.v(TAG,"resolveEntity returning: "+entityList.get(id));
            return entityList.get(id);
        }
        return null;
    }

    public int getPlannedAlt(){
        if (getPlanSpecification()!=null) {
            for (PlanManeuver planManeuver : getPlanSpecification().getManeuvers()) {
                if (planManeuver.getManeuverId().equalsIgnoreCase(getManeuverID())) {
                    Float altPlanned = (getHeight()) + ((Float) planManeuver.getData().getValue("z"));
                    return Math.round(altPlanned);
                }
            }
        }
        return -1;
    }

    public void resetVisualizations(){
        setMaker(null);
        setPlanID("");
        setPaintedPlanID("");
    }

    public boolean isOnMap(){
        if (getMaker()==null)
            return false;
        return true;
    }

	public Sys(String address, int port, String name, int id, String type,
			boolean connected, boolean error) {
		super();
		this.mAddress = address;
		this.mPort = port;
		this.mName = name;
		this.mId = id;
		this.mConnected = connected;
		this.mError = error;
		this.mType = type;
		lastMessageReceived = System.currentTimeMillis();
	}

}
