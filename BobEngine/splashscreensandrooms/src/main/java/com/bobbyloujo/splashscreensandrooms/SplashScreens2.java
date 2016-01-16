package com.bobbyloujo.splashscreensandrooms;

import com.bobbyloujo.bobengine.extra.SplashActivity;

/**
 * Created by Benjamin on 5/11/2015.
 */
public class SplashScreens2 extends SplashActivity {
	@Override
	protected void setup() {
		addSplash(R.layout.splashscreens2, 2000);
	}

	@Override
	protected void end() {
		finish();
	}
}
