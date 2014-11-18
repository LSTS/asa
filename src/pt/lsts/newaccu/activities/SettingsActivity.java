package pt.lsts.newaccu.activities;

import pt.lsts.newaccu.util.settings.SettingsFactory;

import java.util.Vector;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity {

	private Editor sharedPreferencesEditor;

	PreferenceScreen screen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Settings.clear();

		screen = getPreferenceManager().createPreferenceScreen(this);;

		Vector<PreferenceCategory> preferenceCategories = SettingsFactory
				.fetchCategories(this);

		for (PreferenceCategory pereferenceCategory : preferenceCategories)
			screen.addPreference(pereferenceCategory);
		for (PreferenceCategory pereferenceCategory : preferenceCategories)
			SettingsFactory.populate(pereferenceCategory, this);
		
		Vector<Preference> preferenceButtons = SettingsFactory
				.fetchPreferencesButtons(this);
		
		for (Preference preferenceButton : preferenceButtons)
			screen.addPreference(preferenceButton);

		setPreferenceScreen(screen);
	}

}
