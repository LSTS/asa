package pt.lsts.asa;

import pt.lsts.asa.managers.IMCManager;
import pt.lsts.asa.sys.SystemList;
import pt.lsts.asa.settings.Profile;
import pt.lsts.asa.settings.Settings;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.net.IMCProtocol;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Class extending application that does a single startup for the application
 * needed to initialize ACCU state object
 * 
 * @author sharp
 *
 */
public class App extends Application {

	private static AudioManager audioManager;

	public static AudioManager getAudioManager() {
		return audioManager;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		initSettings();

		
		IMCDefinition.getInstance();
		ASA.getInstance(this);
		ASA.getInstance().load();
		ASA.getInstance().start();
		Log.i("App", "Global ASA Object Initialized");
		
		
	}

	public void initSettings() {
		Settings.initSettings(getBaseContext());
		Profile.copySpecificAsset(getBaseContext(), "default_settings.csv");
		if (Settings.getAll().isEmpty()) {//if no previous settings, set the defaults
			Profile.restoreDefaults();
		}
	}

}
