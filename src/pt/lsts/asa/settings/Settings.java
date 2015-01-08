package pt.lsts.asa.settings;

import pt.lsts.asa.ASA;

import java.util.Map;

import android.content.SharedPreferences;

public class Settings {

	private static Settings settings = null;

	private Settings() {

	}

	public static Settings getSettings() {
		if (settings == null) {
			settings = new Settings();
		}
		return settings;
	}

	public static SharedPreferences getSharedPreferences() {
		return ASA.getInstance().sharedPreferences;
	}

	public static boolean putString(String key, String value) {
		return ASA.getInstance().sharedPreferences.edit().putString(key, value).commit();
	}

	public static boolean putInt(String key, int value) {
		return ASA.getInstance().sharedPreferences.edit().putInt(key, value).commit();
	}

	public static boolean putBoolean(String key, boolean value) {
		return ASA.getInstance().sharedPreferences.edit().putBoolean(key, value).commit();
	}

	public static String getString(String key, String defValue) {
		return ASA.getInstance().sharedPreferences.getString(key, defValue);
	}

	public static int getInt(String key, int defValue) {
		return ASA.getInstance().sharedPreferences.getInt(key, defValue);
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return ASA.getInstance().sharedPreferences.getBoolean(key, defValue);
	}

	public static boolean clear() {
		return ASA.getInstance().sharedPreferences.edit().clear().commit();
	}

	public static Map<String, ?> getAll() {
		return ASA.getInstance().sharedPreferences.getAll();
	}

	public static String getCategory(String key, String defValue) {
		if (ASA.getInstance().sharedPreferences.contains(key)) {// get only Category
			return key.split("_")[0];
		}
		return defValue;
	}

	public static String getKey(String key, String defValue) {
		if (ASA.getInstance().sharedPreferences.contains(key)) {// remove Category
			String parts[] = key.split("_");
			String result = parts[1];
			for (int i = 2; i < parts.length; i++)
				result += " " + parts[i];
			return result;
		}
		return defValue;
	}

}
