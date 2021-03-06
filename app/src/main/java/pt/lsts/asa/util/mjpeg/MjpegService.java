package pt.lsts.asa.util.mjpeg;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.VideoView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import pt.lsts.asa.feedback.CallOutService;
import pt.lsts.asa.fragments.VideoViewFragment;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.asa.util.StringUtils;


/**
 * Created by jloureiro on 3/5/15.
 */
public class MjpegService extends Service {


    public static final String TAG = "MjpegService";
    private MjpegView mjpegView=null;
    private MjpegInputStream mjpegInputStream=null;
    private AsyncTask task = null;

    public MjpegService(MjpegView mjpegView){
        this.mjpegView=mjpegView;
    }

    /**
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        startAsyncTask();
        return START_NOT_STICKY;//does not resuscitate
    }

    public void startAsyncTask(){
        if (task!=null){
            task.cancel(true);
            task = null;
        }
        //String camUrl = "http://trackfield.webcam.oregonstate.edu/axis-cgi/mjpg/video.cgi?resolution=800x600&amp%3bdummy=1333689998337";//test public ip cam
        //String camUrl = "http://10.0.2.153:8080/";
        String camUrl = StringUtils.getCamUrl();

        task = new getMjpegInputStreamAsyncTask().execute(camUrl);
        this.stopSelf();
    }

    public void reconnect(){
        startAsyncTask();
    }

    public class getMjpegInputStreamAsyncTask extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            HttpResponse res = null;
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 500;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 20000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
            try {
                Log.d(TAG, "1. Sending http request");
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode()==401){
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                mjpegInputStream = new MjpegInputStream(res.getEntity().getContent());
                return mjpegInputStream;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            } catch (ConnectException e){
                Log.d(TAG, "ConnectException: "+e,e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            if (result==null)
                onDestroy();
            mjpegView.setSource(result);
            mjpegView.setDisplayMode(MjpegView.SIZE_FULLSCREEN);
            mjpegView.showFps(true);
            mjpegView.startPlayback();
            Log.d(TAG,"startPlayBack()");
        }
    }

    @Override
    public void onCreate() {
        Log.v(TAG,"onCreate()");
    }

    @Override
    public void onDestroy() {
        task.cancel(true);
        this.stopSelf();
        Log.d(TAG,"onDestroy() with task.cancel & this.stopSelf()");

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
