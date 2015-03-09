package pt.lsts.asa.activities;

import pt.lsts.asa.ASA;
import pt.lsts.asa.fragments.GmapFragment;
import pt.lsts.asa.R;
import pt.lsts.asa.util.AndroidUtil;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class AutoActivity extends FragmentActivity {

	GmapFragment gmapFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_auto);

		//loadFragments(savedInstanceState);

	}

    @Override
    protected void onPause(){
        AndroidUtil.removeAllFragments(this);
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        ASA.getInstance().setMode(ASA.MODE.AUTO);
        loadFragments(null);//always load fragments from null
    }

	public void loadFragments(Bundle savedInstanceState) {
		if (findViewById(R.id.fragment_container_auto) != null) {
			if (savedInstanceState != null) {
				return;// restoring state
			}

            gmapFragment = new GmapFragment(this);
            AndroidUtil.loadFragment(this, gmapFragment, R.id.fragment_container_auto);

		}
	}

}
