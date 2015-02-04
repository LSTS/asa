package pt.lsts.asa.fragments;

import pt.lsts.asa.R;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.asa.util.StringUtils;
import pt.lsts.asa.util.mjpeg.MjpegInputStream;
import pt.lsts.asa.util.mjpeg.MjpegView;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class VideoViewFragment extends Fragment {

	private final String TAG = "VideoView";
	private FragmentActivity fragmentActivity;
    private MjpegView mjpegView;
    private View view;

	private ScheduledFuture handle;
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	Runnable runnable;
	private final int timeoutFalse=5000, timeoutTrue=10000;
	private boolean connectedBool =false;
    private AsyncTask task = null;

	public VideoViewFragment() {
		// Required empty public constructor
	}

	public VideoViewFragment(FragmentActivity fragmentActivity) {
		this.fragmentActivity = fragmentActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_video_view, container,false);
        view = v;
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onStart() {
		super.onStart();
		initMjpegView();
	}

	@Override
	public void onResume() {
		super.onResume();
        startVideo();
	}

	public void initMjpegView() {
        if (mjpegView!=null)
            mjpegView=null;
        mjpegView = (MjpegView) view.findViewById(R.id.mjpegVideoView);

        /*
		mjpegView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String url = StringUtils.getCamUrl();
                AndroidUtil.showToastShort(fragmentActivity, "restarting connection to Cam: " + url);
                startVideo();
                return false;
            }
        });
        */
	}


    public void startVideo(){
        initMjpegView();
        //String camUrl = "http://trackfield.webcam.oregonstate.edu/axis-cgi/mjpg/video.cgi?resolution=800x600&amp%3bdummy=1333689998337";//test public ip cam
        String camUrl = StringUtils.getCamUrl();//"http://10.0.20.112/axis-cgi/mjpg/video.cgi?date=0&clock=0&camera=1&resolution=640x480";
        Log.i(TAG,"URL: "+camUrl);

        if (task!=null){
            task.cancel(true);
            task = null;
        }
        task = new getMjpegInputStreamAsyncTask().execute(camUrl);

    }

    public class getMjpegInputStreamAsyncTask extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode()==401){
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
                //Error connecting to camera
            }

            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mjpegView.setSource(result);
            mjpegView.setDisplayMode(MjpegView.SIZE_FULLSCREEN);
            mjpegView.showFps(true);
        }
    }

}
