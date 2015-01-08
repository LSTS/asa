package pt.lsts.asa.fragments;

import pt.lsts.asa.R;
import pt.lsts.asa.activities.SettingsActivity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by jloureiro on 1/6/15.
 */
public class SettingsButtonFragment extends Fragment {

    private FragmentActivity fragmentActivity = null;
    private Button settingsButton = null;

    public SettingsButtonFragment() {
        // Required empty public constructor
    }

    public SettingsButtonFragment(FragmentActivity fragmentActivity){
        this.fragmentActivity=fragmentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_button, container, false);
        settingsButton = (Button) v.findViewById(R.id.settingsButton);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(fragmentActivity.getApplicationContext(),
                        SettingsActivity.class);
                startActivity(i);
            }
        });
    }
}