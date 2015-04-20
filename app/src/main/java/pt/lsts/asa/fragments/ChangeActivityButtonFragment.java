package pt.lsts.asa.fragments;

import pt.lsts.asa.R;

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
public class ChangeActivityButtonFragment extends Fragment {

    private Class destinyClass=null;
    private int layoutID=-1;
    private int buttonID=-1;
    private Button changeActivityettingsButton = null;

    public ChangeActivityButtonFragment() {
        // Required empty public constructor
    }
/*
    public ChangeActivityButtonFragment(FragmentActivity fragmentActivityOrigin, Class destinyClass, int layoutID, int buttonID){
        this.fragmentActivity=fragmentActivityOrigin;
        this.destinyClass=destinyClass;
        this.layoutID = layoutID;
        this.buttonID=buttonID;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layoutID = getArguments().getInt("layout");
        this.buttonID=getArguments().getInt("id");
        this.destinyClass = (Class) getArguments().getSerializable("class");
        View v = inflater.inflate(layoutID, container, false);
        changeActivityettingsButton = (Button) v.findViewById(buttonID);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        changeActivityettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity()==null)
                    return;
                Intent i = new Intent(getActivity().getApplicationContext(),
                        destinyClass);
                startActivity(i);
            }
        });
    }
}