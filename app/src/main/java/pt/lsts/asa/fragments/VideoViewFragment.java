package pt.lsts.asa.fragments;


import pt.lsts.asa.R;
import pt.lsts.asa.feedback.CallOutService;
import pt.lsts.asa.util.AndroidUtil;
import pt.lsts.asa.util.StringUtils;
import pt.lsts.asa.util.mjpeg.MjpegService;
import pt.lsts.asa.util.mjpeg.MjpegView;

import android.app.Activity;
import android.content.Intent;
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
    private MjpegView mjpegView;
    private MjpegService mjpegService=null;
    private View view;

	public VideoViewFragment() {
		// Required empty public constructor
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
        Log.d("MjpegService","videoViewFragment.onDetach()");
        mjpegService.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
        initMjpegView();
        startVideo();
	}

	public void initMjpegView() {
        if (mjpegView!=null)
            mjpegView=null;
        mjpegView = (MjpegView) view.findViewById(R.id.mjpegVideoView);

		mjpegView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String url = StringUtils.getCamUrl();
                AndroidUtil.showToastShort("restarting connection to Cam:\n" + url);
                reset();
                return false;
            }
        });

	}

    public void reset(){
        this.onPause();
        this.onResume();
    }

    public void startVideo(){
        initMjpegView();
        startMjpegService();
    }

    public void startMjpegService(){
        if (mjpegService!=null){
            mjpegService.reconnect();
        }else{
            mjpegService = new MjpegService(mjpegView);

            if (getActivity()==null)
                return;
            Intent intent = new Intent(getActivity(),MjpegService.class);
            mjpegService.onStartCommand(intent,1,0);
            mjpegService.onBind(intent);
        }
    }

}
