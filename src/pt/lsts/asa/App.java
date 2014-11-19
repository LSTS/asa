package pt.lsts.asa;

import pt.lsts.asa.util.FileOperations;
import pt.lsts.asa.util.settings.Profile;
import pt.lsts.asa.util.settings.Settings;
import pt.lsts.imc.IMCDefinition;
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

		IMCDefinition.getInstance();

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		initSettings();

		ASA.getInstance(this);
		ASA.getInstance().load();
		ASA.getInstance().start();
		Log.i("App", "Global ACCU Object Initialized");

	}

	public void initSettings() {
		Settings.initSettings(getBaseContext());
		Profile.copySpecificAsset(getBaseContext(), "default_settings.csv");
		if (Settings.getAll().isEmpty()) {
			Profile.restoreDefaults();
		}
	}

}
