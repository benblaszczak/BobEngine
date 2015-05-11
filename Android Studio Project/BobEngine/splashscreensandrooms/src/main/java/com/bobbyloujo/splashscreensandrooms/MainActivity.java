package com.bobbyloujo.splashscreensandrooms;

import android.os.Bundle;

import bobby.engine.bobengine.BobActivity;

/**
 * Created by Benjamin on 5/11/2015.
 */
public class MainActivity extends BobActivity {

	private GameView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = new GameView(this);

		setContentView(view);
	}
}
