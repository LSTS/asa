package pt.lsts.asa.fragments;

import pt.lsts.asa.settings.Settings;
import pt.lsts.asa.R;
import pt.lsts.asa.util.StringUtils;

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
import android.widget.Toast;
import android.widget.VideoView;
import android.support.v4.app.Fragment;

public class VideoViewFragment extends Fragment {

	private final String TAG = "CamConnection";
	private FragmentActivity fragmentActivity;
	private VideoView videoView;

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
		videoView = (VideoView) v.findViewById(R.id.videoViewFullscreen);

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
		setVideoViewClick();
	}

	@Override
	public void onResume() {
		super.onResume();
		startVideo();
		setConnectionChecker();
	}

	public void setVideoViewClick() {
		videoView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				String url = StringUtils.getCamUrl(videoView);
				showToastShort("restarting connection to Cam: "+url);
				if (videoView.isPlaying())
					videoView.stopPlayback();
				else
					restartVideo();
				return false;
			}
		});
	}

	public void startVideo(){
		String url = StringUtils.getCamUrl(videoView);
		videoView.setVideoPath(url);
		videoView.start();
		Log.i(TAG, "Connecting: "+url);
	}

	public void restartVideo(){
		Log.i(TAG, "restartVideo()");
		videoView.stopPlayback();
		startVideo();
	}

	public void setConnectionChecker(){
		setRunnable();
		startHandle(timeoutFalse);
	}

	public void setRunnable(){
		runnable = new Runnable() {
			@Override
			public void run() {
				if (videoView.isPlaying()) {
					if (connectedBool==false){
						connectedBool = true;
						startHandle(timeoutTrue);
						Log.w(TAG, "Connection Established");
						showToastShort("Connection Established");
					}
					Log.i(TAG, "Connection OK, retrying connection in: "+timeoutTrue);
				}else{
					if (connectedBool==true){
						connectedBool=false;
						startHandle(timeoutFalse);
						Log.w(TAG, "Connection Lost");
						showToastLong("Connection Lost");
					}
					Log.i(TAG, "Connection failed, retrying connection in: "+timeoutFalse);
				}
			}};
	}

	public void startHandle(int timeout){
		if (handle != null)
			handle.cancel(true);
		handle = scheduler.scheduleAtFixedRate(runnable, timeout,
				timeout, TimeUnit.MILLISECONDS);
	}

	public void showToastLong(final String msg){
		fragmentActivity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(fragmentActivity, msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	public void showToastShort(final String msg){
		fragmentActivity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(fragmentActivity, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
