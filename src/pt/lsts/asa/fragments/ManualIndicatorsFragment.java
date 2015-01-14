package pt.lsts.asa.fragments;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import pt.lsts.asa.ASA;
import pt.lsts.asa.R;

/**
 * Created by jloureiro on 1/14/15.
 */
public class ManualIndicatorsFragment extends Fragment {

    private FragmentActivity fragmentActivity = null;
    private TextView leftTextView = null;
    private TextView rightTextView = null;

    public ManualIndicatorsFragment() {
        // Required empty public constructor
    }

    public ManualIndicatorsFragment(FragmentActivity fragmentActivity){
        this.fragmentActivity=fragmentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.manual_indicators_fragment, container, false);
        leftTextView = (TextView) v.findViewById(R.id.leftTextView);
        rightTextView = (TextView) v.findViewById(R.id.rightTextView);
        leftTextView.setBackgroundColor(Color.parseColor("#000000"));//Black
        leftTextView.setTextColor(Color.parseColor("#FFFFFF"));//White
        rightTextView.setBackgroundColor(Color.parseColor("#000000"));//Black
        rightTextView.setTextColor(Color.parseColor("#FFFFFF"));//White
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        setListener();
    }

    public void setListener(){
        ASA.getInstance().getCallOutSubscriber().setManualIndicatorsFragment(this);
    }

    public void setLeftTextView(final String text){
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftTextView.setText(text);
            }
        });
    }

    public void setRightTextView(final String text){
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rightTextView.setText(text);
            }
        });
    }

}