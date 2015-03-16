package pt.lsts.asa.fragments;


import pt.lsts.asa.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class AltitudeBarFragment extends Fragment {

    public static final String TAG = "AltitudeBarFragment";
    private FragmentActivity fragmentActivity=null;

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
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
    }


}
