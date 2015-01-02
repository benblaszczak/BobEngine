package bobby.engine.template;

import android.content.Intent;
import android.os.Bundle;
import bobby.engine.bobengine.BobActivity;
import bobby.engine.bobengine.SplashActivity;

/**
 * This project is provided as a template for future projects. It is the most basic BobEngine
 * project:
 * 1 view
 * 1 room
 * 1 game object
 */

public class MainActivity extends BobActivity {
	
	// Data
	private GameView gameView;                                 // The View that shows the GameObjects. Alternatively, you could
	                                                           //     create a GameView in a layout with other views if you want
	                                                           //     to use other views (TextView, WebView, ButtonView... etc)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* 
		 * Show the splash screen 
		 * You can show a different splash screen by creating
		 * a splash.xml layout file and placing it in res/layouts
		 */
		Intent splash = new Intent(this, SplashActivity.class);
		startActivity(splash);
		
		/* Set up the GameView */
		gameView = new GameView(this);
		
		/* Tell Android which view to display */
		setContentView(gameView);
	}
}
