package pt.lsts.asa.settings;

import pt.lsts.asa.util.FileOperations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Profile {

	public static File mainDir = new File(
			"/storage/emulated/0/Android/data/pt.lsts.ASA/");
	public static String defaultSettingsName = "default_settings.csv";
	public static File defaultSettingsFile = new File(mainDir,
			defaultSettingsName);
	public static String firstLineInCsvFile = "type,category_key,value";

	public static void copyAllAssets(Context context) {
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("copyAllAssets", "Failed to get asset file list.", e);
		}
		for (String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(filename);
				FileOperations.initDir(Profile.mainDir);
				File outFile = new File(Profile.mainDir, filename);
				out = new FileOutputStream(outFile);
				FileOperations.copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			}
		}
	}

	public static boolean copySpecificAsset(Context context, String name) {
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		InputStream in = null;
		OutputStream out = null;
		for (String filename : files) {
			if (!filename.equals(name))
				continue;
			try {
				in = assetManager.open(filename);
				FileOperations.initDir(Profile.mainDir);
				File outFile = new File(Profile.mainDir, filename);
				out = new FileOutputStream(outFile);
				FileOperations.copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
				return true;

			} catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			}
		}
		return false;
	}

	public static String restoreDefaults() {
		return load(defaultSettingsName);
	}

	public static String load(String name) {
		File profile = new File(mainDir, name);
		if (!profile.exists())
			return "Profile file:\n" + name + "\nNot Available";
		Vector<String> settings = FileOperations.readLines(profile);
		if (!settings.get(0).equals(firstLineInCsvFile))
			return "Invalid File";
		settings.remove(0);// remove first description line
		Settings.clear();
		for (String setting : settings)
			loadSetting(setting);
		return "Load of file:\n" + name + "\nSucessful!";
	}

	public static void loadSetting(String setting) {
		String parts[] = setting.split(",");
		if (parts.length != 3) {
			Log.e("loadSetting", "parts.length!=3");
			Log.e("loadSetting", "Line not added:" + setting);
			return;
		}
		String type = parts[0];
		String key = parts[1];
		String value = parts[2];
		if (type.equals("class java.lang.String")) {
			Settings.putString(key, value);
			return;
		}
		if (type.equals("class java.lang.Integer")) {
			Settings.putInt(key, Integer.parseInt(value));
			return;
		}
		if (type.equals("class java.lang.Boolean")) {
			Settings.putBoolean(key, Boolean.parseBoolean(value));
			return;
		}
		Log.e("loadSetting", "Line not added:" + setting);

	}

	public static String save(String name) {
		Vector<String> lines = new Vector<String>();
		lines.add(firstLineInCsvFile);
		Map<String, ?> keys = Settings.getAll();
		if (keys.size() == 0) {
			Log.e("save", "Settings.getAll().size()==0");
			return "ERROR: settings empty";
		}
		File file = new File(mainDir, name + ".csv");
		FileOperations.initDir(Profile.mainDir);
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			String type = entry.getValue().getClass().toString();
			String key = entry.getKey();
			String val = entry.getValue().toString();
			String line = type + "," + key + "," + val;
			lines.add(line);
		}
		FileOperations.writeLines(lines, file);
		return "Save of file to:\n" + name + "\nSucessful!";
	}

	public static String[] getProfilesAvailable() {
		String[] filesArray = mainDir.list();
		String extension = "csv";
		String[] result = FileOperations.filterFilesByExtension(filesArray,
				extension);
		return result;
	}

}
