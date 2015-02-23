package pt.lsts.asa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;


public class FileOperations {


    public static final String TAG = "FileOperations";
    public static File mainDir = new File(
            "/storage/emulated/0/Android/data/pt.lsts.ASA/");
    public static final String mainDirString = "/storage/emulated/0/Android/data/pt.lsts.ASA/";

	public static void initDir(File dir) {
		dir.mkdirs();
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		writeLines(readLines(in), out);
	}

	public static void copyFile(File fileIn, File fileOut) throws IOException {
		writeLines(readLines(fileIn), fileOut);
	}

	public static Vector<String> readLines(File file) {
		try {
			return readLines(new FileInputStream(file));
		} catch (Exception e) {
			Log.e(TAG,"readLines: "+e.getMessage(),e);
		}
		return null;
	}

	public static Vector<String> readLines(InputStream in) {
		Vector<String> lines = new Vector<String>();
		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		} catch (Exception e) {
			Log.e(TAG, "readLines: "+e.getMessage(),e);
		}
		return lines;
	}

	public static void writeLine(String line, OutputStream out) {
		byte[] buffer = line.getBytes();
		try {
			out.write(buffer);
			out.write("\n".getBytes());
		} catch (Exception e) {
			Log.e(TAG,"writeLine: "+e.getMessage(),e);
		}
	}

	public static void writeLines(Vector<String> lines, OutputStream out) {
		for (String line : lines)
			writeLine(line, out);
	}

	public static void writeLine(String line, File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			writeLine(line, out);
		} catch (Exception e) {
			Log.e(TAG,"writeLine: "+e.getMessage(),e);
		}
	}

	public static void writeLines(Vector<String> lines, File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			writeLines(lines, out);
		} catch (Exception e) {
			Log.e(TAG,"writeLine: "+e.getMessage(),e);
		}
	}

	public static String[] filterFilesByExtension(String[] filesArray,
			String extension) {
		Vector<String> filesVector = new Vector<String>();
		for (String file : filesArray) {
			if (file.endsWith("." + extension))
				filesVector.add(file);
		}
		String[] resultArray = new String[filesVector.size()];
		for (int i = 0; i < resultArray.length && i < filesVector.size(); i++)
			resultArray[i] = filesVector.get(i);
		return resultArray;
	}

	public static String[] removeExtension(String[] filesArray, String extension){
		String[] result = new String[filesArray.length];
		for (int i=0;i<filesArray.length;i++) {
			if (filesArray[i].endsWith("."+extension)) {
				result[i] = filesArray[i].substring(0, filesArray[i].length() - (extension.length()+1));
			}
		}
		return result;
	}

    public static void copyAssetsFolder(Context context, String listPath) {
        File dir = new File(mainDir+"listPath/");
        dir.mkdirs();//initialize folder
        Log.i(TAG, dir.getAbsolutePath() + ".mkdirs()");
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(listPath);
            for (String filename : files) {
                if (assetManager.list(filename).length==0){
                    if (listPath.equalsIgnoreCase("")) {
                        Log.i(TAG,"copySpecificAsset(context, "+filename+");");
                        copySpecificAsset(context, filename);
                    }else{
                        Log.i(TAG,"copySpecificAsset(context, "+listPath+"/"+filename+");");
                        copySpecificAsset(context, listPath+"/"+filename);
                    }
                }else{
                    Log.i(TAG,filename+".isFolder");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list.", e);
        }
    }

    public static boolean copySpecificAsset(Context context, String name) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
            try {
                in = assetManager.open(name);
                FileOperations.initDir(mainDir);
                File outFile = new File(mainDir, name);
                out = new FileOutputStream(outFile);
                FileOperations.copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                return true;

            } catch (IOException e) {
                Log.e(TAG, "Failed to copy asset file: " + name, e);
            }
        return false;
    }

    public static void downloadFile(String urlString, String destinationPath, String filename){
        new DownloaFileAsyncTask().execute(urlString, destinationPath, filename);
    }

}