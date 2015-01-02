/**
 * BobEngine - 2D game engine for Android
 * 
 * Copyright (C) 2014, 2015 Benjamin Blaszczak
 * 
 * BobEngine is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser Public License
 * version 2.1 as published by the free software foundation.
 * 
 * BobEngine is provided without warranty; without even the implied
 * warranty of merchantability or fitness for a particular 
 * purpose. See the GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with BobEngine; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301 USA
 * 
 */

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

	public static final String TIME = "time";                                  // Use this to send a custom amount of time
	private final int SPLASH_TIME = 3000;                                      // Default amount of time this screen shows in milliseconds.
	private int time;                                                          // Amount of time this screen shows
	
	@SuppressLint("InlinedApi")
	final int VISIBILITY =                                                     // The flags for immersive mode
	View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		time = getIntent().getIntExtra(TIME, SPLASH_TIME);
		
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

		}, time);

		setContentView(R.layout.splash);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		overridePendingTransition(0, android.R.anim.fade_out);
	}
}
