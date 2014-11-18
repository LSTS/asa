package pt.lsts.newaccu.util.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import pt.lsts.newaccu.util.FileOperations;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Profile {
	
	public static File dir = new File("/storage/emulated/0/data/newACCUdata/");
	public static File defaultSettingsFile = new File(dir, "default_settings.csv");
	
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
				FileOperations.initDir(Profile.dir);
				File outFile = new File(Profile.dir, filename);
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
				FileOperations.initDir(Profile.dir);
				File outFile = new File(Profile.dir, filename);
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

	public static void load(String name){
		
	}
	
	public static void save(String name){
		
	}
	
	public static Vector<String> listProfilesAvailable(){
		Vector<String> list = new Vector<String>();
		return list;
	}
}
