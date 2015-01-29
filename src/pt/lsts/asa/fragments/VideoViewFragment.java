package pt.lsts.asa.fragments;

import pt.lsts.asa.R;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.asa.util.StringUtils;
import pt.lsts.asa.util.mjpeg.MjpegInputStream;
import pt.lsts.asa.util.mjpeg.MjpegView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

public class VideoViewFragment extends Fragment {

	private final String TAG = "VideoView";
	private FragmentActivity fragmentActivity;
    private MjpegView mv;
    private View view;

	private ScheduledFuture handle;
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	Runnable runnable;
	private final int timeoutFalse=5000, timeoutTrue=10000;
	private boolean connectedBool =false;

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
		setVideoViewListeners();
	}

	@Override
	public void onResume() {
		super.onResume();
        startMjpegVideo();
		setConnectionChecker();
	}

	public void setVideoViewListeners() {
        mv = (MjpegView) view.findViewById(R.id.mjpegVideoView);
		mv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String url = StringUtils.getCamUrl();
                AndroidUtil.showToastShort(fragmentActivity, "restarting connection to Cam: " + url);
                restartVideo();
                return false;
            }
        });
	}


	public void restartVideo(){
		Log.i(TAG, "restartVideo()");
		if (mv!=null) {
            mv.stopPlayback();
            mv=null;
        }
		startMjpegVideo();
	}

	public void setConnectionChecker(){
		setRunnable();
		startHandle(timeoutFalse);
	}

	public void setRunnable(){
		runnable = new Runnable() {
			@Override
			public void run() {
				if (mv.isActivated()) {
					if (connectedBool==false){
						connectedBool = true;
						startHandle(timeoutTrue);
						Log.w(TAG, "Connection to Cam Established");
                        AndroidUtil.showToastShort(fragmentActivity,"Connection to Cam Established");
					}
					Log.i(TAG, "Connection to Cam OK, retrying connection in: "+timeoutTrue);
				}else{
					if (connectedBool==true){
						connectedBool=false;
						startHandle(timeoutFalse);
						Log.w(TAG, "Connection to Cam Lost");
						AndroidUtil.showToastLong(fragmentActivity,"Connection to Cam Lost");
					}
					Log.i(TAG, "Connection to Cam failed, retrying connection in: "+timeoutFalse);
                    restartVideo();
				}
			}};
	}


	public void startHandle(int timeout){
		if (handle != null)
			handle.cancel(true);
		handle = scheduler.scheduleAtFixedRate(runnable, timeout,
				timeout, TimeUnit.MILLISECONDS);
	}

    public void startMjpegVideo(){
        setVideoViewListeners();
        String URL = StringUtils.getCamUrl();//"http://10.0.20.112/axis-cgi/mjpg/video.cgi?date=0&clock=0&camera=1&resolution=640x480";
        Log.i(TAG,"URL: "+URL);

        mv.setSource(MjpegInputStream.read(URL));
        mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        mv.showFps(true);

    }

}
