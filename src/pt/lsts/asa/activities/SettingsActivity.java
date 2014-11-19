package pt.lsts.asa.activities;

import pt.lsts.asa.settings.SettingsFactory;

import java.util.Vector;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity {

	PreferenceScreen screen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		screen = getPreferenceManager().createPreferenceScreen(this);

		Vector<PreferenceCategory> preferenceCategories = SettingsFactory
				.fetchCategories(this);

		for (PreferenceCategory preferenceCategory : preferenceCategories)
			screen.addPreference(preferenceCategory);

		populateCategories(preferenceCategories);

		setPreferenceScreen(screen);
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

}
