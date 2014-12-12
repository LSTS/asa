package pt.lsts.asa.fragments;

import pt.lsts.asa.settings.Settings;
import pt.lsts.asa.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;
import android.support.v4.app.Fragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class VideoViewFragment extends Fragment {

	private Context context;
	private VideoView videoView;

	private ScheduledFuture handle;
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	Runnable runnable;
	private final int timeoutFalse=5000, timeoutTrue=120000;
	private boolean connectedBool =false;

	public VideoViewFragment() {
		// Required empty public constructor
	}

	public VideoViewFragment(Context context) {
		this.context = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = super.getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_video_view, container,
				false);
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
				Toast.makeText(context, "videoView Touched\nstartVideo() executed", Toast.LENGTH_SHORT)
						.show();
				if(videoView.isPlaying())
					videoView.stopPlayback();
				else
					restartVideo();
				return false;
			}
		});
	}

	public String getCompleteUrl(){
		String protocol = Settings.getString("cam_protocol","rtsp");
		String ip= Settings.getString("cam_ip","10.0.20.199");
		String location = "axis-media/media.amp";
		String codec = Settings.getString("cam_codec","h264");
		String resolution = Settings.getString("cam_resolution", "0x0");

		resolution = validateResolution(resolution);
		//Toast.makeText(context, "resolution: "+resolution, Toast.LENGTH_SHORT).show();

		String completeUrl=protocol+"://"+ip+"/"+location+"?videocodec="+codec+"&resolution="+resolution;
		return completeUrl;
	}

	public void startVideo(){
		String url = getCompleteUrl();
		videoView.setVideoPath(url);
		videoView.start();
		Log.i("CamConnection", "Connecting: "+url);
	}

	public void restartVideo(){
		videoView.stopPlayback();
		startVideo();
	}

	public String getVideoViewResolution(){
		int width = videoView.getWidth();
		int height = videoView.getHeight();
		String resolution=width+"x"+height;
		return resolution;
	}

	public String validateResolution(String resolution){
		String[] res = resolution.split("x");
		if (res.length!=2){
			resolution = getVideoViewResolution();
			//Toast.makeText(context, "used original - 1st one: "+resolution, Toast.LENGTH_SHORT).show();
		}else {
			try {
				Integer.parseInt(res[0]);
				Integer.parseInt(res[1]);
			} catch (Exception e) {
				resolution = getVideoViewResolution();
				//Toast.makeText(context, "used original - 2nd one: "+resolution, Toast.LENGTH_SHORT).show();
			}
		}
		return resolution;
	}

	public void setConnectionChecker(){
		setRunnable(this.context);
		startHandle(timeoutFalse);
	}

	public void setRunnable(final Context context){
		runnable = new Runnable() {
			@Override
			public void run() {
			if (videoView.isPlaying()) {
				if (connectedBool==false){
					connectedBool = true;
					startHandle(timeoutTrue);
					Log.w("CamConnection", "Connection Established");
					Toast.makeText(context, "Connection Established", Toast.LENGTH_SHORT).show();
				}
				Log.i("CamConnection", "Connection OK, retrying connection in: "+timeoutTrue);
				Toast.makeText(context, "Connection OK, retrying connection in: "+timeoutTrue, Toast.LENGTH_SHORT).show();
			}else{
				if (connectedBool==true){
					connectedBool=false;
					startHandle(timeoutFalse);
					Log.w("CamConnection", "Connection Dropped");
					Toast.makeText(context,"Connection Dropped", Toast.LENGTH_SHORT).show();
				}
				Log.i("CamConnection", "Connection failed, retrying connection in: "+timeoutFalse);
				Toast.makeText(context,"Connection failed, retrying connection in: "+timeoutFalse, Toast.LENGTH_SHORT).show();
				restartVideo();
			}
		}};
	}

	public void startHandle(int timeout){
		if (handle != null)
			handle.cancel(true);
		handle = scheduler.scheduleAtFixedRate(runnable, 0,
				timeout, TimeUnit.MILLISECONDS);
	}

}
