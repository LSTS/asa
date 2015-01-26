package pt.lsts.asa.settings;

import pt.lsts.asa.util.FileOperations;
import pt.lsts.asa.util.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import android.util.Log;

public class Profile {

    public static final String TAG = "Profile";
	public static String defaultSettingsName = "default_settings";
	public static File defaultSettingsFile = new File(FileOperations.mainDir,
			defaultSettingsName);
	public static String firstLineInCsvFile = "type,category,key,description,value(s)";
	public static final String extension = "csv";

	public static String restoreDefaults() {
		return load(defaultSettingsName);
	}

	public static String load(String name) {
		File profile = new File(FileOperations.mainDir, name+"."+extension);
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
        if (parts[0].charAt(0) == '#'){
            Log.i(TAG,"setting commented: "+setting);
            return;
        }
		if (StringUtils.validateSetting(setting)==false){
            Log.i(TAG,"setting not valid: "+setting);
            return;
        }
        String key = parts[2];
        /*
		String type = parts[0];
		String category = parts[1];
		String key = parts[2];
        String description=parts[3];
		String value = parts[4];

		if (type.equalsIgnoreCase("java.lang.String")) {
			Settings.putString(key, value);
			return;
		}
		if (type.equalsIgnoreCase("java.lang.Integer")) {
			Settings.putInt(key, Integer.parseInt(value));
			return;
		}
		if (type.equalsIgnoreCase("java.lang.Boolean")) {
			Settings.putBoolean(key, Boolean.parseBoolean(value));
			return;
		}
		*/
        Settings.putString(key,setting);
		Log.e(TAG, "Line not added:" + setting);

	}

	public static String save(String name) {
		Vector<String> lines = new Vector<String>();
		lines.add(firstLineInCsvFile);
		Map<String, ?> keys = Settings.getAll();
		if (keys.size() == 0) {
			Log.e("save", "Settings.getAll().size()==0");
			return "ERROR: settings empty";
		}
		File file = new File(FileOperations.mainDir, name + "." + extension);
		FileOperations.initDir(FileOperations.mainDir);
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			String type = entry.getValue().getClass().getName();
			String key = entry.getKey();
			String val = entry.getValue().toString();
			String line = type + "," + key + "," + val;
			lines.add(line);
		}
		FileOperations.writeLines(lines, file);
		return "Save of file to:\n" + name + "\nSucessful!";
	}

	public static String[] getProfilesAvailable() {
		String[] filesArray = FileOperations.mainDir.list();
		String[] result = FileOperations.filterFilesByExtension(filesArray,extension);
		result = FileOperations.removeExtension(result, extension);

		return result;
	}

}
