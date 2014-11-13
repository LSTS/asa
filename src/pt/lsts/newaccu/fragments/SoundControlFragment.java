package pt.lsts.newaccu.fragments;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.R.layout;
import pt.lsts.newaccu.feedback.CallOut;
import pt.lsts.newaccu.managers.SoundManager;
import pt.lsts.newaccu.ui.components.VerticalSeekBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link SoundControlFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link SoundControlFragment#newInstance} factory method to create an instance
 * of this fragment.
 *
 */
public class SoundControlFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;
	
	Context context;
	AudioManager audioManager;
	ImageButton imageButtonMute;
	CallOut callOut;
	SoundManager soundManager = SoundManager.getInstance();
	VerticalSeekBar volControl;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 *
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment SoundControlFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static SoundControlFragment newInstance(String param1, String param2) {
		SoundControlFragment fragment = new SoundControlFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public SoundControlFragment() {
		// Required empty public constructor
	}
	
	public SoundControlFragment(Context context) {
		this.context=context;
		callOut = new CallOut(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_sound_control, container,
				false);
		volControl = (VerticalSeekBar) v.findViewById(R.id.seekBarVolume);
		imageButtonMute = (ImageButton) v.findViewById(R.id.imageButtonMute);
		return v;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;			
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		setVolumeControl();
		callOut.startCallOuts();
		
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	public void setVolumeControl(){
		volControl = (VerticalSeekBar) getView().findViewById(R.id.seekBarVolume);
	    volControl.setMax(soundManager.getMaxVolume());
	    volControl.setProgress(soundManager.getCurrentVolume());
	    setVolumeControlChanger(volControl, audioManager);
	    setImageButtonMute();

	}
	
	public void setImageButtonMute(){
		
		setImageButtonMuteIcon();
		
		imageButtonMute.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	
	            if (soundManager.checkMute())
	            	unmute();
	            else
	            	mute();
	            //showToastWithVolume();
	             
	        }
	    });
	    
	}
	
	public void setImageButtonMuteIcon(){
		if (soundManager.checkMute())
			imageButtonMute.setImageResource(R.drawable.sound_off);
		else
			imageButtonMute.setImageResource(R.drawable.sound_on);
	}
	
	public void setVolumeControlChanger(VerticalSeekBar volControl, final AudioManager audioManager){
		volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		    @Override
		    public void onStopTrackingTouch(SeekBar arg0) {
		    }
		
		    @Override
		    public void onStartTrackingTouch(SeekBar arg0) {
		    }
		
		    @Override
		    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		        unmute();
		    	soundManager.setCurrentVolume(arg1);
		    	adjustVolumeBarAndIcon();
		    	//showToastWithVolume();
		    }
		});
	}
	
	private void adjustVolumeBarAndIcon(){
		int curVolume = soundManager.getCurrentVolume();
	    volControl.setProgress(curVolume);
		setImageButtonMuteIcon();
	}
	
	public void mute() {
	    soundManager.mute();
	    setImageButtonMuteIcon();
	}

	public void unmute() {
		soundManager.unmute();
		setImageButtonMuteIcon();
		callOut.startCallOuts();
	}
	
	public void shutdown(){
		unmute();
		callOut.shutdown();
	}
	
	public void showToastWithVolume(){
		int vol = soundManager.getCurrentVolume();
	    Toast.makeText(context, "vol="+vol, Toast.LENGTH_LONG).show();
	}
	
}
