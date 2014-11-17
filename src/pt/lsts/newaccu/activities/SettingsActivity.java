package pt.lsts.newaccu.activities;

import java.util.Map;
import java.util.Vector;

import pt.lsts.newaccu.App;
import pt.lsts.newaccu.R;
import pt.lsts.newaccu.util.settings.Settings;
import pt.lsts.newaccu.util.settings.SettingsFactory;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;

public class SettingsActivity extends PreferenceActivity {

	private Editor sharedPreferencesEditor;

	PreferenceScreen screen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Settings.clear();

		screen = getPreferenceManager().createPreferenceScreen(this);

		Vector<PreferenceCategory> preferenceCategories = SettingsFactory
				.fetchCategories(this);

		for (PreferenceCategory pereferenceCategory : preferenceCategories)
			screen.addPreference(pereferenceCategory);
		for (PreferenceCategory pereferenceCategory : preferenceCategories)
			SettingsFactory.populate(pereferenceCategory, this);

		setPreferenceScreen(screen);
	}

	public void hideCategory(PreferenceCategory preferenceCategory) {
		screen.removePreference(preferenceCategory);
	}

}
