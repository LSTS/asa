package pt.lsts.asa.activities;

import pt.lsts.asa.ASA;
import pt.lsts.asa.settings.SettingsFactory;
import pt.lsts.asa.sys.Sys;
import pt.lsts.asa.util.AndroidUtil;

import java.util.ArrayList;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

public class SettingsActivity extends PreferenceActivity {

    public static final String TAG = "SettingsActivity";
	PreferenceScreen preferenceScreen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    protected void onResume(){
        super.onResume();
        ASA.getInstance().setMode(ASA.MODE.SETTINGS);
        preferenceScreen = getPreferenceManager().createPreferenceScreen(this);

        Vector<PreferenceCategory> preferenceCategories = SettingsFactory
                .fetchCategories(this);

        for (PreferenceCategory preferenceCategory : preferenceCategories)
            preferenceScreen.addPreference(preferenceCategory);

        populateCategories(preferenceCategories);

        setPreferenceScreen(preferenceScreen);

        ASA.getInstance().getBus().register(this);
    }

	public void populateCategories(
			Vector<PreferenceCategory> preferenceCategories) {
		for (PreferenceCategory preferenceCategory : preferenceCategories) {
			if (preferenceCategory.getTitle().equals("Profiles")) {
				createProfileCategory(preferenceCategory);
				continue;
			}
			SettingsFactory.populate(preferenceCategory, this);
		}
	}

	public void createProfileCategory(PreferenceCategory preferenceCategory) {
		Vector<Preference> preferenceButtons = SettingsFactory
				.fetchPreferencesButtons(this);

		for (Preference preferenceButton : preferenceButtons)
			preferenceCategory.addPreference(preferenceButton);
	}

    @Override
    protected void onPause(){
        Log.i(TAG, "SettingsActivity.onPause() called");
        //AndroidUtil.removeAllFragments(this);
        super.onPause();
        ASA.getInstance().getBus().unregister(this);
        preferenceScreen.removeAll();
        preferenceScreen=null;
        finish();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startMainActivity();
    }

    public void startMainActivity(){
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(i);
    }

    @Subscribe
    public void showToast(final Toast toast){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast.show();
            }
        });
    }
}
