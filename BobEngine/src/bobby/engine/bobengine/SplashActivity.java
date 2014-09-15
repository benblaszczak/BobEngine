package bobby.engine.bobengine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;


/**
 * This Activity displays the splash screen when the app is opened. The splash
 * screen will always be displayed in immersive mode. The default screen shows
 * the "Powered by BobEngine" logo. You may use a different layout for the splash
 * screen by defining one in a "splash.xml" file and placing it in your
 * res/layout/ folder.
 * <br /><br />
 * Note that if you do not show the BobEngine screen, you must credit BobEngine
 * somewhere else in your app. Thank you for your cooperation!
 * <br /><br />
 * You can display this screen by starting an intent for SplashActivity.
 * <br /><br />
 * If you are having trouble displaying the splash screen, be sure to add the line
 * 'manifestmerger.enabled=true' to your project.properties file. You may also
 * define SplashActivity in your own AndroidManifest.xml file instead.
 * 
 * @author Ben
 *
 */
public class SplashActivity extends Activity {

	private final int SPLASH_TIME = 3000;                                      // Amount of time this screen shows in milliseconds.
	
	@SuppressLint("InlinedApi")
	final int VISIBILITY =                                                     // The flags for immersive mode
	View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {                                                                  // Immersive mode (Will not work on versions prior to 4.4.2)
			getWindow().getDecorView().setSystemUiVisibility(VISIBILITY);      // Set the flags for immersive mode
		} catch (NoSuchMethodError e) {                                        // Immersive mode not supported (Android version < 4.4.2)
			// Get KitKat!
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						finish();
					}

				});
			}

		}, SPLASH_TIME);

		setContentView(R.layout.splash);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		overridePendingTransition(0, android.R.anim.fade_out);
	}
}
