package pt.lsts.asa.listenners.sharedPreferences;

import pt.lsts.asa.ASA;
import pt.lsts.asa.feedback.CallOut;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class SoundPreferencesListenner implements OnSharedPreferenceChangeListener{
	CallOut callout = ASA.getInstance().getCallOut();

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		String keyLowerCase = key.toLowerCase();
		switch(keyLowerCase){
				
			default:
				break;
		}
	}
}
