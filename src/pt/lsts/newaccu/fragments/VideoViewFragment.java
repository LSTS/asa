package pt.lsts.newaccu.fragments;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.ui.components.VerticalSeekBar;

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
import android.widget.Toast;
import android.widget.VideoView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

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

	public void setVideoViewClick() {
		videoView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Toast.makeText(context, "videoView Touched", Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
	}

}
