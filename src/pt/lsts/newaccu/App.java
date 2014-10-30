package pt.lsts.newaccu;

import pt.lsts.imc.IMCDefinition;
import android.app.Application;
import android.util.Log;

/**
 * Class extending application that does a single startup for the application needed to initialize
 * ACCU state object
 * @author sharp
 *
 */
public class App extends Application 
{	
	@Override
	public void onCreate()
	{
		super.onCreate();

		IMCDefinition.getInstance();

		// Sequence of calls needed to properly initialize ACCU
		newAccu.getInstance(this);
		newAccu.getInstance().load();
		newAccu.getInstance().start();
        Log.i("App", "Global ACCU Object Initialized"); 
        
        
        //FIXME jqcorreia
    	// For now theme setting must be done here because it needs an activity restart

		// Do it before setContentView()
   	}
}
