package pt.lsts.asa.settings;

import pt.lsts.asa.util.FileOperations;
import pt.lsts.asa.util.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import android.util.Log;

public class Profile {

    public static final String TAG = "Profile";
	public static String defaultSettingsName = "default_settings";
	public static File defaultSettingsFile = new File(FileOperations.getMainDir(),
			defaultSettingsName);
	public static String firstLineInCsvFile = "type,category,key,description,value(s)";
	public static final String extension = "csv";

	public static String restoreDefaults() {
		return load(defaultSettingsName);
	}

	public static String load(String name) {
		File profile = new File(FileOperations.getMainDir(), name+"."+extension);
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
        Settings.putFullString(key,setting);
		//Log.e(TAG, "Line not added:" + setting);

	}

	public static String save(String name) {
		Vector<String> lines = new Vector<String>();
		lines.add(firstLineInCsvFile);
		Map<String, ?> keys = Settings.getAll();

		if (keys.size() == 0) {
			Log.e("save", "Settings.getAll().size()==0");
			return "ERROR: settings empty";
		}
		File file = new File(FileOperations.getMainDir(), name + "." + extension);
		FileOperations.initDir(FileOperations.getMainDir());
        SortedSet<String> keysSorted = new TreeSet<String>(keys.keySet());
        for (String key : keysSorted) {
			String type = Settings.getType(key, "java.lang.String");
            String category = Settings.getCategory(key, "category");
            //String key;
            String description = Settings.getDescription(key, " ");
            String val = "";
            if (Settings.isOptions(key)){
                for (String s : Settings.getStrings(key, new String[0])){
                    val += s +",";
                }
                val = val.substring(0, val.length()-1);
            }else {
                switch (type) {
                    case "java.lang.Integer":
                        val += Settings.getInt(key, -1);
                        break;
                    case "java.lang.Boolean":
                        val += Settings.getBoolean(key, false);
                        break;
                    case "java.lang.String":
                        val = Settings.getString(key, "");
                        break;
                }
            }
			String line = type+","+category+","+key+","+description+","+val;
			lines.add(line);
		}
		FileOperations.writeLines(lines, file);
		return "Save of file to:\n" + name + "\nSucessful!";
	}

	public static String[] getProfilesAvailable() {
		String[] filesArray = FileOperations.getMainDir().list();
		String[] result = FileOperations.filterFilesByExtension(filesArray,extension);
		result = FileOperations.removeExtension(result, extension);

		return result;
	}

}
