package pt.lsts.newaccu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

public class FileOperations {

	public static File dir = new File("/storage/emulated/0/data/newACCUdata/");
	public static File defaultSettingsFile = new File(dir, "default_settings.csv");
	
	public static void initDefaultDir() {
		dir.mkdirs();
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		initDefaultDir();
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		initDefaultDir();
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public static void copyAllAssets(Context context) {
		initDefaultDir();
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		for (String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(filename);
				File outFile = new File(dir, filename);
				out = new FileOutputStream(outFile);
				copyFile(in, out);
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
		initDefaultDir();
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
				File outFile = new File(dir, filename);
				out = new FileOutputStream(outFile);
				copyFile(in, out);
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

	public static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
	
	public static Vector<String> readLines(File file){
		try{
			return readLines(new FileInputStream(file));
		}catch(Exception e){
			Log.e("readLines",e.getMessage());
		}
		return null;
	}
	
	public static Vector<String> readLines(InputStream in){
		Vector<String> lines = new Vector<String>();
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
	 
		String line = null;
		try{
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		}catch(Exception e){
			Log.e("readLines",e.getMessage());
		}		
		return lines;
	}
	
	public static void writeLine(String line, OutputStream out){
		byte[] buffer = line.getBytes();
		try {
			out.write(buffer);
			out.write("\n".getBytes());
		}catch(Exception e){
			Log.e("writeLine",e.getMessage());
		}	
	}
	
	public static void writeLines(Vector<String> lines, OutputStream out){
		for (String line : lines)
			writeLine(line,out);
	}
	
	public static void writeLine(String line, File file){
		try {
			FileOutputStream out = new FileOutputStream(file);
			writeLine(line, out);
		}catch(Exception e){
			Log.e("writeLine",e.getMessage());
		}	
	}
	
	public static void writeLines(Vector<String> lines, File file){
		try {
			FileOutputStream out = new FileOutputStream(file);
			writeLines(lines, out);
		}catch(Exception e){
			Log.e("writeLine",e.getMessage());
		}	
	}

}
