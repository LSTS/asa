package pt.lsts.asa.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pt.lsts.asa.R;

/**
 * Created by jloureiro on 1/5/15.
 */
public class SystemListFragment extends Fragment {

    private FragmentActivity fragmentActivity = null;
    private ListView systemListView = null;

    public SystemListFragment(){
        // Required empty public constructor
    }

    public SystemListFragment(FragmentActivity fragmentActivity){
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_system_list_view, container, false);
        systemListView = (ListView) v.findViewById(R.id.systemListView);
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        populateSystemListView();

    }

    public void populateSystemListView(){
        List<String> your_array_list = new ArrayList<String>();
        your_array_list.add("item1");
        your_array_list.add("item2");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                fragmentActivity,
                android.R.layout.simple_list_item_1,
                your_array_list );

        systemListView.setAdapter(arrayAdapter);
    }

}