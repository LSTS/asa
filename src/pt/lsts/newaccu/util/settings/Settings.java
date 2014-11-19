package pt.lsts.newaccu.util.settings;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

	private static Settings settings = null;
	private static SharedPreferences sharedPreferences;

	private Settings() {

	}

	public static Settings getSettings() {
		if (settings == null) {
			settings = new Settings();
		}
		return settings;
	}

	public static void initSettings(Context context) {
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	public static SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}

	public static boolean putString(String key, String value) {
		return sharedPreferences.edit().putString(key, value).commit();
	}

	public static boolean putInt(String key, int value) {
		return sharedPreferences.edit().putInt(key, value).commit();
	}

	public static boolean putBoolean(String key, boolean value) {
		return sharedPreferences.edit().putBoolean(key, value).commit();
	}

	public static String getString(String key, String defValue) {
		return sharedPreferences.getString(key, defValue);
	}

	public static int getInt(String key, int defValue) {
		return sharedPreferences.getInt(key, defValue);
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return sharedPreferences.getBoolean(key, defValue);
	}

	public static boolean clear() {
		return sharedPreferences.edit().clear().commit();
	}

	public static Map<String, ?> getAll() {
		return sharedPreferences.getAll();
	}

	public static String getCategory(String key, String defValue) {
		if (sharedPreferences.contains(key)) {// get only Category
			return key.split("_")[0];
		}
		return defValue;
	}

	public static String getKey(String key, String defValue) {
		if (sharedPreferences.contains(key)) {// remove Category
			String parts[] = key.split("_");
			String result = parts[1];
			for (int i = 2; i < parts.length; i++)
				result += " " + parts[i];
			return result;
		}
		return defValue;
	}

}
