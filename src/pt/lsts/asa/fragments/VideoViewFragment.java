package pt.lsts.asa.fragments;

import pt.lsts.asa.settings.Settings;
import pt.lsts.asa.ui.components.VerticalSeekBar;
import pt.lsts.asa.R;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import java.util.StringTokenizer;

public class VideoViewFragment extends Fragment {

	Context context;
	VideoView videoView;

	public VideoViewFragment() {
		// Required empty public constructor
	}

	public VideoViewFragment(Context context) {
		this.context = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		loadVideo();
	}

	public void setVideoViewClick() {
		videoView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Toast.makeText(context, "videoView Touched\nloadVideo() executed", Toast.LENGTH_SHORT)
						.show();
				if(videoView.isPlaying())
					videoView.stopPlayback();
				else
					loadVideo();
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

		String completeUrl=protocol+"://"+ip+"/"+location+"?videocodec="+codec+"&resolution"+resolution;
		return completeUrl;
	}

	public void loadVideo(){
		String url = getCompleteUrl();
		videoView.setVideoPath(url);
		videoView.start();
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


}
