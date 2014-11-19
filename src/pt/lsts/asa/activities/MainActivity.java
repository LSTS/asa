package pt.lsts.asa.activities;

import pt.lsts.asa.ASA;
import pt.lsts.asa.util.settings.Settings;
import pt.lsts.asa.R;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button buttonManualStabilized;
	Button buttonPFD;
	Button buttonSettingsCheckList;
	Button buttonTest;
	Button buttonTest2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonManualStabilized = (Button) findViewById(R.id.buttonManualStabilized);
		setButtonManualStabilized();
		buttonPFD = (Button) findViewById(R.id.buttonPfd);
		setButtonPfd();
		buttonSettingsCheckList = (Button) findViewById(R.id.buttonSettingsCheckList);
		setButtonSettingsCheckList();
		buttonTest = (Button) findViewById(R.id.buttonTest);
		setButtonTest();
		buttonTest2 = (Button) findViewById(R.id.buttonTest2);
		setButtonTest2();

	}

	public void test() {
		// showToast(sharedPreferences.getString("username", "NA"));

		Map<String, ?> keys = Settings.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			String type = entry.getValue().getClass().toString();
			String key = entry.getKey();
			String val = entry.getValue().toString();
			showToast(type + ";" + key + ";" + val);
		}

	}

	public void test2() {

		Settings.clear();
		Settings.putBoolean("audio_global_audio", true);
		Settings.putBoolean("audio_Altitude_audio", false);
		Settings.putInt("audio_Altitude_Interval_in_seconds", 15);
		Settings.putBoolean("audio_IAS_audio", false);
		Settings.putInt("audio_IAS_Interval_in_seconds", 5);

	}

	public void setButtonTest() {
		buttonTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				test();
			}
		});
	}

	public void setButtonTest2() {
		buttonTest2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				test2();
			}
		});
	}

	public void setButtonSettingsCheckList() {
		buttonSettingsCheckList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						SettingsActivity.class);
				startActivity(i);
			}
		});
	}

	public void setButtonManualStabilized() {
		buttonManualStabilized.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						ManualStabilizedModeActivity.class);
				startActivity(i);
			}
		});
	}

	public void setButtonPfd() {
		buttonPFD.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						AutoModeActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first
		pauseASA();
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first
		resumeASA();
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Exit")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton(android.R.string.no, null)
				.setNegativeButton(android.R.string.yes, new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						back();
					}
				}).create().show();

	}

	public void back() {
		super.onBackPressed();
		onPause();
	}

	public void pauseASA() {
		if (ASA.getInstance() != null)
			if (ASA.getInstance().started)
				ASA.getInstance().pause();
	}

	public void resumeASA() {
		if (ASA.getInstance() != null)
			if (!ASA.getInstance().started)
				ASA.getInstance().start();
	}

	public void showToast(String text) {
		Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT)
				.show();
	}

}
