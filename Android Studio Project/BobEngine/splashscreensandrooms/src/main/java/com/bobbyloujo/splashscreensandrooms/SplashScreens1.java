package com.bobbyloujo.splashscreensandrooms;

import android.content.Intent;

import bobby.engine.bobengine.SplashActivity;

/**
 * Created by Benjamin on 5/11/2015.
 */
public class SplashScreens1 extends SplashActivity {
	@Override
	protected void setup() {
		addSplash(R.layout.splash, 2000);
		addSplash(R.layout.splashscreen1, 2000);
	}

	@Override
	protected void end() {
		Intent main = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(main);
		finish();
	}
}
