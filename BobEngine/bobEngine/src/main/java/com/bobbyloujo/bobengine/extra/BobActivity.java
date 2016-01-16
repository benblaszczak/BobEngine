/**
 * BobEngine - 2D game engine for Android
 *
 * Copyright (C) 2014, 2015, 2016 Benjamin Blaszczak
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
package com.bobbyloujo.bobengine.extra;

import android.app.Activity;
import android.os.Bundle;

/**
 * This class is an extension of the Android Activity class which adds
 * additional functionality which is useful for games.
 * <br/> <br/>
 * Additional functionality includes easy methods for retrieving the dimensions
 * of the screen and using immersive mode.
 *
 * @author Ben
 */
public abstract class BobActivity extends Activity {

	// Objects
	private BobHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		helper = new BobHelper(this);
	}

	/**
	 * Returns the width of the screen.<br />
	 * <br />
	 * On devices using API level 13 and higher, this function will return the
	 * real width of the screen including things like the navigation bar and
	 * title bar. <br />
	 * <br />
	 * BobView includes a function to get just the width of the view.
	 *
	 * @return - Width of the screen, in pixels.
	 */
	public int getScreenWidth() {
		return helper.getScreenWidth();
	}

	/**
	 * Returns the width of the screen.<br />
	 * <br />
	 * On devices using API level 13 and higher, this function will return the
	 * real height of the screen including things like the navigation bar and
	 * title bar. <br />
	 * <br />
	 * BobView includes a function to get just the height of the view.
	 *
	 * @return - Width of the screen, in pixels.
	 */
	public int getScreenHeight() {
		return helper.getScreenHeight();
	}

	/**
	 * Uses KitKat's immersive mode. Immersive mode only works on Android 4.4.2
	 * and up. There is no need to check for version number when using this
	 * method. This method will handle older versions for you.
	 */
	public void useImmersiveMode() {
		helper.useImmersiveMode();
	}

	public void onResume() {
		super.onResume();
		helper.onResume();
	}

	/**
	 * Save an integer value. It can be retrieved even after the application has quit
	 * by calling getSavedInt();
	 *
	 * @param name  - name of the saved integer. Will be used for retrieval.
	 * @param value - value to save.
	 */
	public void saveInt(String name, int value) {
		helper.saveInt(name, value);
	}

	/**
	 * Save an boolean value. It can be retrieved even after the application has quit
	 * by calling getSavedBool();
	 *
	 * @param name  - name of the saved boolean. Will be used for retrieval.
	 * @param value - value to save.
	 */
	public void saveBool(String name, boolean value) {
		helper.saveBool(name, value);
	}

	/**
	 * Save an float value. It can be retrieved even after the application has quit
	 * by calling getSavedFloat();
	 *
	 * @param name  - name of the saved float. Will be used for retrieval.
	 * @param value - value to save.
	 */
	public void saveFloat(String name, float value) {
		helper.saveFloat(name, value);
	}

	/**
	 * Save an string value. It can be retrieved even after the application has quit
	 * by calling getSavedString();
	 *
	 * @param name  - name of the saved string. Will be used for retrieval.
	 * @param value - value to save.
	 */
	public void saveString(String name, String value) {
		helper.saveString(name, value);
	}

	/**
	 * Get a saved value.
	 *
	 * @param name - name of the saved value.
	 * @return saved value of name. 0 if name doesn't exist.
	 */
	public int getSavedInt(String name) {
		return helper.getSavedInt(name);
	}

	/**
	 * Get a saved value.
	 *
	 * @param name - name of the saved value.
	 * @return saved value of name. 0f if name doesn't exist.
	 */
	public float getSavedFloat(String name) {
		return helper.getSavedFloat(name);
	}

	/**
	 * Get a saved value.
	 *
	 * @param name - name of the saved value.
	 * @return saved value of name. false if name doesn't exist.
	 */
	public boolean getSavedBool(String name) {
		return helper.getSavedBool(name);
	}

	/**
	 * Get a saved value.
	 *
	 * @param name - name of the saved value.
	 * @return saved value of name. "" if name doesn't exist.
	 */
	public String getSavedString(String name) {
		return helper.getSavedString(name);
	}
}
