package pt.lsts.asa.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;
import pt.lsts.asa.listenners.sysUpdates.AltBarSysUpdaterListenner;


/**
 * A simple {@link Fragment} subclass.
 */
public class AltBarFragment extends Fragment {

    public static final String TAG = "AltBarFragment";
    private FragmentActivity fragmentActivity=null;
    private AltBarSysUpdaterListenner altBarSysUpdaterListenner =null;

    private TextView altitudeFromVehicleTextView=null;
    private TextView altitudeFromPlanTextView=null;
    private ImageView altitudeFromPlanImageView=null;

    public AltBarFragment() {
        // Required empty public constructor
    }

    public AltBarFragment(FragmentActivity fragmentActivity){
        this.fragmentActivity=fragmentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alt_bar, container, false);

        altitudeFromVehicleTextView = (TextView) v.findViewById(R.id.altFromVehicleTextView);
        altitudeFromPlanTextView = (TextView) v.findViewById(R.id.altFromPlanTextView);
        altitudeFromPlanImageView = (ImageView) v.findViewById(R.id.altFromPlanImageView);

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
        altBarSysUpdaterListenner = new AltBarSysUpdaterListenner(this);
        ASA.getInstance().getBus().register(altBarSysUpdaterListenner);
        if (ASA.getInstance().getActiveSys()!=null){
            setVehicleAlt(ASA.getInstance().getActiveSys().getAltInt());
            setPlanAlt(ASA.getInstance().getActiveSys().getPlannedAlt());
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        if (altBarSysUpdaterListenner !=null)
            ASA.getInstance().getBus().unregister(altBarSysUpdaterListenner);
    }

    /**
     *
     * @param alt may be bettween 0 and 1000, available range.
     */
    public void setVehicleAlt(int alt){
        altitudeFromVehicleTextView.setText(""+alt);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(altitudeFromVehicleTextView.getLayoutParams());
        frameLayoutParams.setMargins(frameLayoutParams.leftMargin,frameLayoutParams.topMargin,50,(80-(25)) + alt);//80 bottom margin; 25 is the adjustment value for icon positioning
        frameLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        altitudeFromVehicleTextView.setLayoutParams(frameLayoutParams);
    }

    /**
     *
     * @param alt may be bettween 0 and 1000, available range.
     */
    public void setPlanAlt(int alt){
        altitudeFromPlanTextView.setText(""+alt);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(altitudeFromPlanImageView.getLayoutParams());
        frameLayoutParams.setMargins(frameLayoutParams.leftMargin,frameLayoutParams.topMargin,0,(80-(25)) + alt);//80 bottom margin; 25 is the adjustment value for icon positioning
        frameLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        altitudeFromPlanImageView.setLayoutParams(frameLayoutParams);


    }


}
