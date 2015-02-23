package pt.lsts.asa.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import pt.lsts.asa.util.mjpeg.MjpegInputStream;

/**
 * Created by jloureiro on 2/23/15.
 */
class DownloaFileAsyncTask extends AsyncTask<String, Void, Void> {
    protected Void doInBackground(String params[]) {
        try {
            String urlString = params[0];
            String destinationPath = params[1];
            String filename = params[2];

            File dir = new File(destinationPath);
            dir.mkdirs();
            URL u = new URL(urlString);
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(new File(destinationPath,filename));
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }

        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }

        return null;
    }

    protected void onPostExecute(MjpegInputStream result) {
    }
}