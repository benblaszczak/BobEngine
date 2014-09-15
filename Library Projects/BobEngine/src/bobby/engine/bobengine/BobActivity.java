package bobby.engine.bobengine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * This class is an extension of the Android Activity class which adds
 * additional functionality which is useful for games.
 * 
 * Additional functionality includes easy methods for retrieving the dimensions
 * of the screen, using immersive mode, and adding a splash screen.
 * 
 * @author Ben
 * 
 */
public abstract class BobActivity extends Activity {

	// Constants
	@SuppressLint("InlinedApi")
	final int VISIBILITY =                                      // The flags for immersive mode
	View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

	// Data
	public int screenWidth;                                     // Real width of the screen in pixels
	public int screenHeight;                                    // Real height of the screen in pixels
	private boolean useImmersive = false;                       // Flag that determines if Immersive Mode is in use

	// Objects
	private WindowManager wm;
	private Point size;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		size = new Point();

		try {                                                   // Get screen dimensions
			wm.getDefaultDisplay().getRealSize(size);           // New method, might not work on old devices
			screenWidth = size.x;
			screenHeight = size.y;
		} catch (NoSuchMethodError er) {                        // If new method didn't work, use depreciated methods
			screenWidth = wm.getDefaultDisplay().getWidth();
			screenHeight = wm.getDefaultDisplay().getHeight();
		}
	}

	/**
	 * Must be called before super.onCreate in your BobActivity. This will
	 * disable the splash screen so that it doesn't show.
	 * 
	 * <b>This method does nothing! Still experimenting with this...</b>
	 */
	public void disableSplashScreen() {
		//showSplash = false;
	}

	/**
	 * Returns the width of the screen. You may also directly access the
	 * variable "screenWidth" to get the width of the screen but if the
	 * orientation of the device has been changed this function may be called to
	 * update screenWidth. <br />
	 * <br />
	 * On devices using API level 13 and higher, this function will return the
	 * real width of the screen including things like the navigation bar and
	 * title bar. <br />
	 * <br />
	 * BobView includes a function to get just the width of the view.
	 * 
	 * @return - Width of the screen, in pixels.
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public int getScreenWidth() {
		try {                                                   // Get screen dimensions
			wm.getDefaultDisplay().getRealSize(size);           // New method, might not work on old devices
			screenWidth = size.x;
		} catch (NoSuchMethodError er) {                        // If new method didn't work, use depreciated methods
			screenWidth = wm.getDefaultDisplay().getWidth();
		}

		return screenWidth;
	}

	/**
	 * Returns the width of the screen. You may also directly access the
	 * variable "screenWidth" to get the width of the screen but if the
	 * orientation of the device has been changed this function may be called to
	 * update screenWidth. <br />
	 * <br />
	 * On devices using API level 13 and higher, this function will return the
	 * real height of the screen including things like the navigation bar and
	 * title bar. <br />
	 * <br />
	 * BobView includes a function to get just the height of the view.
	 * 
	 * @return - Width of the screen, in pixels.
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public int getScreenHeight() {
		try {                                                   // Get screen dimensions
			wm.getDefaultDisplay().getRealSize(size);           // New method, might not work on old devices
			screenHeight = size.y;
		} catch (NoSuchMethodError er) {                        // If new method didn't work, use depreciated methods
			screenHeight = wm.getDefaultDisplay().getHeight();
		}

		return screenHeight;
	}

	/**
	 * Uses KitKat's immersive mode. Immersive mode only works on Android 4.4.2
	 * and up. There is no need to check for version number when using this
	 * method. This method will handle older versions for you.
	 */
	@SuppressLint("NewApi")
	public void useImmersiveMode() {

		try {                                                                  // Immersive mode (Will not work on versions prior to 4.4.2)
			getWindow().getDecorView().setSystemUiVisibility(VISIBILITY);      // Set the flags for immersive mode
			UIChangeListener();                                                // Add a listener to detect if we have lost immersive mode

			wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			size = new Point();

			wm.getDefaultDisplay().getRealSize(size);                          // Get -REAL- screen size. This excludes the navbar, which isn't visible
			screenWidth = size.x;
			screenHeight = size.y;

			useImmersive = true;
		} catch (NoSuchMethodError e) {                                        // Immersive mode not supported (Android version < 4.4.2)
			// Get KitKat!

			Log.d("BobEngine", "Immersive mode not supported. (Android version < 4.4.2)");
		}
	}

	/**
	 * This method creates a listener that will detect when immersive mode is
	 * lost and get it back. Needs to be called again in onResume.
	 */
	@SuppressLint("NewApi")
	private void UIChangeListener() {
		final View decorView = getWindow().getDecorView();

		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
					decorView.setSystemUiVisibility(VISIBILITY);
				}
			}
		});
	}

	public void onResume() {
		super.onResume();

		if (useImmersive) {
			useImmersiveMode();
		}
	}
}
