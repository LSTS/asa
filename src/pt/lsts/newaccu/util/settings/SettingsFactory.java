package pt.lsts.newaccu.util.settings;

import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;

public class SettingsFactory {

	public static void populate(PreferenceCategory category, Context context) {
		Map<String, ?> keys = Settings.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			if (!Settings.getCategory(entry.getKey(), "ERROR").equals(
					category.getTitle()))
				continue;// not in this category
			if (entry.getValue().getClass().equals(String.class))
				createEntry(category, entry.getKey(),
						(String) entry.getValue(), context);
			if (entry.getValue().getClass().equals(Integer.class)) {
				String valString = ((Integer) entry.getValue()).toString();
				createEntry(category, entry.getKey(), valString, context)
						.getEditText()
						.setInputType(InputType.TYPE_CLASS_NUMBER);
			}
			if (entry.getValue().getClass().equals(Boolean.class))
				createEntry(category, entry.getKey(),
						(Boolean) entry.getValue(), context);
		}
	}

	public static EditTextPreference createEntry(PreferenceCategory category,
			String key, String valString, Context context) {
		EditTextPreference editTextPreference = new EditTextPreference(context);
		editTextPreference.setTitle(Settings.getKey(key, "ERROR"));
		editTextPreference.setSummary("Val: " + valString);
		editTextPreference.setDefaultValue(valString);
		setOnChangeListener(editTextPreference, key);
		category.addPreference(editTextPreference);
		return editTextPreference;
	}

	public static void setOnChangeListener(
			final EditTextPreference editTextPreference, final String key) {
		editTextPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						boolean result = changeValue(key, newValue);
						preference.setDefaultValue(newValue);
						preference.setSummary("Val: " + ((String) newValue));

						return result;
					}
				});
	}

	public static void createEntry(PreferenceCategory category,
			final String key, Boolean val, Context context) {

		CheckBoxPreference checkBoxPref = new CheckBoxPreference(context);
		checkBoxPref.setTitle(Settings.getKey(key, "ERROR"));
		// checkBoxPref.setSummary("Val: "+val);
		checkBoxPref.setChecked(val);

		checkBoxPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						boolean result = changeValue(key, newValue);
						preference.setDefaultValue(newValue);

						return result;
					}
				});

		category.addPreference(checkBoxPref);
	}

	public static boolean changeValue(String key, Object newValue) {

		if (newValue.getClass().equals(String.class)) {
			return Settings.putString(key, (String) newValue);
		}
		if (newValue.getClass().equals(Integer.class)) {
			return Settings.putInt(key, (Integer) newValue);
		}
		if (newValue.getClass().equals(Boolean.class)) {
			return Settings.putBoolean(key, (Boolean) newValue);
		}

		return false;
	}

	public static PreferenceCategory createCategory(String name, Context context) {
		PreferenceCategory category = new PreferenceCategory(context);
		category.setTitle(name);
		return category;
	}

	public static Vector<PreferenceCategory> fetchCategories(Context context) {
		Vector<PreferenceCategory> preferenceCategories = new Vector<PreferenceCategory>();
		Vector<String> categoriesStrings = new Vector<String>();
		Map<String, ?> keys = Settings.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			String categoryString = Settings.getCategory(entry.getKey(),
					"ERROR");
			if (!categoriesStrings.contains(categoryString))
				categoriesStrings.add(categoryString);
		}
		for (String entry : categoriesStrings)
			preferenceCategories.add(createCategory(entry, context));
		return preferenceCategories;
	}

}
