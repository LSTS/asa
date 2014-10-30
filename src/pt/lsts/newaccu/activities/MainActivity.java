package pt.lsts.newaccu.activities;

import pt.lsts.newaccu.R;
import pt.lsts.newaccu.newAccu;
import pt.lsts.newaccu.R.id;
import pt.lsts.newaccu.R.layout;
import pt.lsts.newaccu.R.menu;
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

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button=(Button)findViewById(R.id.buttonManualStabilized);
        button.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            Intent i = new Intent(getApplicationContext(),ManualStabilizedActivity.class);
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
	    super.onPause();  // Always call the superclass method first
	    pauseNewAccu();
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    resumeNewAccu();
	}
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
        .setTitle("Exit")
        .setMessage("Are you sure you want to exit?")
        .setNegativeButton(android.R.string.no, null)
        .setNegativeButton(android.R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            	back();
            }
        }).create().show();
		
	}
	
	public void back(){
		super.onBackPressed();
		onPause();
	}
	
	public void pauseNewAccu(){
		if (newAccu.getInstance()!=null)
			if (newAccu.getInstance().started)
		    	newAccu.getInstance().pause();
	}
	
	public void resumeNewAccu(){
		if (newAccu.getInstance()!=null)
			if (!newAccu.getInstance().started)
		    	newAccu.getInstance().start();
	}
	
}
