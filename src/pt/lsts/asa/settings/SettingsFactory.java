package pt.lsts.asa.settings;

import pt.lsts.asa.activities.SettingsActivity;

import java.util.Map;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsFactory {

	public static void populate(PreferenceCategory category, Context context) {
		createHideEntry(category, context);
		Map<String, ?> keys = Settings.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String key = entry.getKey();
			if (!Settings.getCategory(key, "ERROR").equals(
					category.getTitle()))
				continue;// not in this category
            if (Settings.isOptions(key))
                continue;//do not display options
            if (Settings.hasOptions(key)) {
                createEntry(category, key, Settings.getString(key, "null"), Settings.getOptions(key), context);
                continue;
            }
			if (Settings.getType(key,"ERROR").equalsIgnoreCase(String.class.getName())) {
                createEntry(category, key, Settings.getString(key,"null"), context);
                continue;
            }
            if (Settings.getType(key,"ERROR").equalsIgnoreCase(Integer.class.getName())){
				int valInt = Settings.getInt(key,-1);
				createEntry(category, key,
						valInt, context).getEditText()
						.setInputType(InputType.TYPE_CLASS_NUMBER);
                continue;
			}
            if (Settings.getType(key,"ERROR").equalsIgnoreCase(Boolean.class.getName())){
                boolean valBoolean = Settings.getBoolean(key,false);
                createEntry(category, key,
						valBoolean, context);
                continue;
            }
		}
	}

	public static void createHideEntry(final PreferenceCategory category,
			final Context context) {
		CheckBoxPreference checkBoxPref = new CheckBoxPreference(context);
		checkBoxPref.setTitle("Hide Category");
		checkBoxPref.setChecked(false);
		checkBoxPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						hideCategory(category, context);
						return true;
					}
				});
		category.addPreference(checkBoxPref);
	}

	public static void hideCategory(final PreferenceCategory category,
			final Context context) {
		category.removeAll();
		CheckBoxPreference checkBoxPref = new CheckBoxPreference(context);
		checkBoxPref.setTitle("Hide Category");
		checkBoxPref.setChecked(true);
		checkBoxPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						category.removeAll();
						populate(category, context);

						return true;
					}
				});
		category.addPreference(checkBoxPref);
	}

	public static EditTextPreference createEntry(PreferenceCategory category,
			String key, String valString, Context context) {
		EditTextPreference editTextPreference = new EditTextPreference(context);
		editTextPreference.setTitle(Settings.getKey(key, "ERROR")+": "+valString);
		editTextPreference.setSummary(Settings.getDescription(key,"null"));
		editTextPreference.setDefaultValue(valString);
		setOnChangeListener(editTextPreference, key);
		category.addPreference(editTextPreference);
		return editTextPreference;
	}

	public static EditTextPreference createEntry(PreferenceCategory category,
			String key, Integer valInteger, Context context) {

        EditTextPreference editTextPreference = new EditTextPreference(context);
        editTextPreference.setTitle(Settings.getKey(key, "ERROR")+": "+valInteger);
        editTextPreference.setSummary(Settings.getDescription(key,"null"));
        editTextPreference.getEditText().setInputType(
                InputType.TYPE_CLASS_NUMBER);
        editTextPreference.setDefaultValue(""+valInteger);
        setOnChangeListener(editTextPreference, key);
        category.addPreference(editTextPreference);
        return editTextPreference;
	}

    public static ListPreference createEntry(PreferenceCategory category, String key, String defValue, String[] list, Context context){
        ListPreference listPreference = new ListPreference(context);
        listPreference.setTitle(Settings.getKey(key,"ERROR")+": "+defValue);
        listPreference.setSummary(Settings.getDescription(key,"null"));
        listPreference.setDefaultValue(defValue);
        listPreference.setEntries(list);
        listPreference.setEntryValues(list);
        setOnChangeListenerList(listPreference,key);
        category.addPreference(listPreference);
        return listPreference;
    }

    public static void setOnChangeListenerList(final ListPreference listPreference, final String key){
        listPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean result = changeValue(key, newValue);
                preference.setTitle(key+": "+newValue);
                preference.setDefaultValue(newValue);
                preference.setSummary(Settings.getDescription(key,"null"));

                return result;
            }
        });
    }

	public static void setOnChangeListener(
			final EditTextPreference editTextPreference, final String key) {
		editTextPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						boolean result = changeValue(key, newValue);
                        preference.setTitle(key + ": " + newValue);
						preference.setDefaultValue(newValue);
						preference.setSummary(Settings.getDescription(key,"null"));

						return result;
					}
				});
	}

	public static void createEntry(PreferenceCategory category,
			final String key, Boolean val, Context context) {
		CheckBoxPreference checkBoxPref = new CheckBoxPreference(context);
		checkBoxPref.setTitle(Settings.getKey(key, "ERROR"));
        checkBoxPref.setSummary(Settings.getDescription(key,"null"));
		checkBoxPref.setChecked(val);
		checkBoxPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						boolean result = changeValue(key, newValue);
						preference.setDefaultValue(newValue);
                        preference.setSummary(Settings.getDescription(key,"null"));

						return result;
					}
				});
		category.addPreference(checkBoxPref);
	}

	public static boolean changeValue(String key, Object newValue) {
		String type = Settings.getType(key,"ERROR");

		if (type.equalsIgnoreCase(String.class.getName())) {
			return Settings.putString(key, (String) newValue);
		}
		if (type.equalsIgnoreCase(Integer.class.getName())) {
			return Settings.putInt(key, Integer.parseInt((String) newValue));
		}
		if (type.equalsIgnoreCase(Boolean.class.getName())) {
			return Settings.putBoolean(key, (Boolean) newValue);
		}
		return false;
	}

	public static PreferenceCategory createCategory(String name,
			final Context context) {
		final PreferenceCategory category = new PreferenceCategory(context);
		category.setTitle(name);
		category.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (category.getPreferenceCount() > 0)
					category.removeAll();
				else
					populate(category, context);
				return false;
			}
		});
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
		preferenceCategories.add(createCategory("Profiles", context));
		return preferenceCategories;
	}

	public static Vector<Preference> fetchPreferencesButtons(
			final Context context) {
		Vector<Preference> preferenceButtons = new Vector<Preference>();
		preferenceButtons.add(createSaveButton(context));
		preferenceButtons.add(createLoadButton(context));
		preferenceButtons.add(createRestoreButton(context));
		return preferenceButtons;

	}

	public static Preference createSaveButton(final Context context) {
		Preference preference = new Preference(context);
		preference.setTitle("Save Profile");
		preference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						save(context);
						return false;
					}
				});
		return preference;
	}

	public static Preference createLoadButton(final Context context) {
		Preference preference = new Preference(context);
		preference.setTitle("Load Profile");
		preference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						load(context);
						return false;
					}
				});
		return preference;
	}

	public static Preference createRestoreButton(final Context context) {
		Preference preference = new Preference(context);
		preference.setTitle("RESTORE DEFAULTS");
		preference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						restoreDefaults(context);
						return false;
					}
				});
		return preference;
	}

	public static void restoreDefaults(final Context context) {
		new AlertDialog.Builder(context)
				.setTitle("Restore Settings Default")
				.setMessage(
						"Are you sure you want to restore settings default?\nWARNING: this action is permenant")
				.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String result = Profile.restoreDefaults();
								Toast.makeText(context, result,
										Toast.LENGTH_SHORT).show();
								regenerateActivity(context);
							}
						})
				.setNegativeButton("CANCEL",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						}).create().show();
	}

	public static void load(final Context context) {
		final String[] array = Profile.getProfilesAvailable();
		new AlertDialog.Builder(context).setTitle("Choose a Profile:")
				.setItems(array, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String result = Profile.load(array[which]);
						Toast.makeText(context, result, Toast.LENGTH_SHORT)
								.show();
						regenerateActivity(context);
					}
				}).create().show();
	}

	public static void save(final Context context) {
		final EditText input = new EditText(context);
		new AlertDialog.Builder(context)
				.setTitle("Save Profile")
				.setMessage("Select a name for Profile:")
				.setView(input)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Editable value = input.getText();
						String result = Profile.save(value.toString());
						Toast.makeText(context, result, Toast.LENGTH_SHORT)
								.show();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// canceled
							}
						}).create().show();
	}

	public static void regenerateActivity(Context context) {
		Intent i = new Intent(context, SettingsActivity.class);
		context.startActivity(i);
	}

}
