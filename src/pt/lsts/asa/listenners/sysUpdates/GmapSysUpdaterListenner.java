package pt.lsts.asa.listenners.sysUpdates;

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Locale;

import pt.lsts.asa.feedback.CallOutService;
import pt.lsts.asa.fragments.GmapFragment;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.util.PlanUtilities;

/**
 * Created by jloureiro on 3/2/15.
 */
public class GmapSysUpdaterListenner {

    private final String TAG = "GmapSysUpdaterListenner";
    public static final boolean DEBUG = false;

    private GmapFragment gmapFragment = null;

    public GmapSysUpdaterListenner(GmapFragment gmapFragment){
        this.gmapFragment = gmapFragment;
    }

    @Subscribe
    public void onPosChanged(Sys sys) {//called from EstimatedState
        Log.v(TAG,"onPosChanged");
        gmapFragment.updateSysMarker(sys);
    }

    @Subscribe
    public void onPlanChanged(PlanSpecification planSpecification){//called from PlanDB
        Log.v(TAG,"onPlanChanged");
        List<PlanUtilities.Waypoint> waypointList = PlanUtilities.computeWaypoints(planSpecification);
        for (PlanUtilities.Waypoint waypoint : waypointList){
            Log.i(TAG,"Waypoint:\n"+"alt: "+waypoint.getAltitude()
                    +"\n"+"height: "+waypoint.getHeight()
                    +"\n"+"depth: "+waypoint.getDepth()
                    +"\n"+"lat/lon: "+waypoint.getLatitude()+" , "+waypoint.getLongitude()
                    +"\n"+"Radius: "+waypoint.getRadius());
        }
        gmapFragment.updateCurrentPlanMarkers(waypointList);
        AndroidUtil.showToastLong(gmapFragment.getFragmentActivity(), "Plan changed to: "+planSpecification.getPlanId());
    }

}


