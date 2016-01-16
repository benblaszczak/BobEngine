package com.bobbyloujo.touchinput;

import android.os.Bundle;

import com.bobbyloujo.bobengine.extra.BobActivity;

public class MainActivity extends BobActivity {
	
	// Data
	private GameView gameView;                                 // The View that shows the GameObjects. Alternatively, you could
	                                                           //     create a GameView in a layout with other views if you want
	                                                           //     to use other views (TextView, WebView, ButtonView... etc)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Set up the GameView */
		gameView = new GameView(this);
		
		/* Tell Android which view to display */
		setContentView(gameView);
	}
}
