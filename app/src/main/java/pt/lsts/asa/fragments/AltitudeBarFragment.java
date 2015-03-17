package pt.lsts.asa.fragments;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.listenners.sysUpdates.AltitudeBarSysUpdaterListenner;


/**
 * A simple {@link Fragment} subclass.
 */
public class AltitudeBarFragment extends Fragment {

    public static final String TAG = "AltitudeBarFragment";
    private FragmentActivity fragmentActivity=null;
    private AltitudeBarSysUpdaterListenner altitudeBarSysUpdaterListenner=null;

    private TextView altitudeFromVehicleTextView=null;
    private TextView altitudeFromPlanTextView=null;

    public AltitudeBarFragment() {
        // Required empty public constructor
    }

    public AltitudeBarFragment(FragmentActivity fragmentActivity){
        this.fragmentActivity=fragmentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_altitude_bar, container, false);

        altitudeFromVehicleTextView = (TextView) v.findViewById(R.id.altitudeFromVehicleTextView);
        altitudeFromPlanTextView = (TextView) v.findViewById(R.id.altitudeFromPlanTextView);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        init();
    }

    public void init(){
        //setVehicleAlt(700);//testing
        //setPlanAlt(450);//testing
        altitudeBarSysUpdaterListenner = new AltitudeBarSysUpdaterListenner(this);
        ASA.getInstance().getBus().register(altitudeBarSysUpdaterListenner);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (altitudeBarSysUpdaterListenner!=null)
            ASA.getInstance().getBus().unregister(altitudeBarSysUpdaterListenner);
    }

    /**
     *
     * @param alt may be bettween 0 and 1000, available range.
     */
    public void setVehicleAlt(int alt){
        altitudeFromVehicleTextView.setText(""+alt);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(altitudeFromVehicleTextView.getLayoutParams());
        frameLayoutParams.setMargins(frameLayoutParams.leftMargin,frameLayoutParams.topMargin,75,(80-(25)) + alt);//80 bottom margin; 25 is the adjustment value for icon positioning
        frameLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        altitudeFromVehicleTextView.setLayoutParams(frameLayoutParams);
    }

    /**
     *
     * @param alt may be bettween 0 and 1000, available range.
     */
    public void setPlanAlt(int alt){
        //altitudeFromPlanTextView.setText(""+alt);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(altitudeFromPlanTextView.getLayoutParams());
        frameLayoutParams.setMargins(frameLayoutParams.leftMargin,frameLayoutParams.topMargin,0,(80-(25)) + alt);//80 bottom margin; 25 is the adjustment value for icon positioning
        frameLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        altitudeFromPlanTextView.setLayoutParams(frameLayoutParams);
    }


}
