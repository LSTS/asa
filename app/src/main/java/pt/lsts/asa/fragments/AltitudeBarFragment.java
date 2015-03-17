package pt.lsts.asa.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import pt.lsts.asa.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AltitudeBarFragment extends Fragment {

    public static final String TAG = "AltitudeBarFragment";
    private FragmentActivity fragmentActivity=null;
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
        setVehicleAlt(700);//testing
        setPlanAlt(250);//testing
    }

    /**
     *
     * @param alt may be bettween 0 and 1000, available range.
     */
    public void setVehicleAlt(int alt){
        //FrameLayout.LayoutParams frameLayoutParams = (FrameLayout.LayoutParams) altitudeBarDroneIconimageView.getLayoutParams();
        //frameLayoutParams.setMargins(frameLayoutParams.leftMargin,frameLayoutParams.topMargin,frameLayoutParams.rightMargin,(80-(35)) + alt);//80 bottom margin; 35 is the adjustment value for icon positioning

        altitudeFromVehicleTextView.setText(""+alt);
        FrameLayout.LayoutParams frameLayoutParams = (FrameLayout.LayoutParams) altitudeFromVehicleTextView.getLayoutParams();
        frameLayoutParams.setMargins(frameLayoutParams.leftMargin,frameLayoutParams.topMargin,frameLayoutParams.rightMargin,(80-(25)) + alt);//80 bottom margin; 35 is the adjustment value for icon positioning
    }

    /**
     *
     * @param alt may be bettween 0 and 1000, available range.
     */
    public void setPlanAlt(int alt){
        altitudeFromPlanTextView.setText(""+alt);
        FrameLayout.LayoutParams frameLayoutParams = (FrameLayout.LayoutParams) altitudeFromPlanTextView.getLayoutParams();
        frameLayoutParams.setMargins(frameLayoutParams.leftMargin,frameLayoutParams.topMargin,frameLayoutParams.rightMargin,(80-(25)) + alt);//80 bottom margin; 35 is the adjustment value for icon positioning
    }


}
