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

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


/**
 * This activity allows you to show multiple splash screens. Splash screens should be
 * created as XML layouts and placed in res\layouts.
 * 
 * @author Ben
 */
public abstract class SplashActivity extends BobActivity {

    private final int MAX_LAYOUTS = 10;    // The maximum number of splash screens.

    private int[] layouts;                 // Holds the resource IDs for all the splash screen layouts
    private int[] times;                   // Holds the times for all the splash screens
    private int numLays;                   // The number of splash screens to show
    private int curLay;                    // The current screen being shown

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        useImmersiveMode();

        numLays = 0;
        curLay = 0;
        layouts = new int[MAX_LAYOUTS];
        times = new int[MAX_LAYOUTS];

        setup();
        nextScreen();
	}

    /**
     * Add a splash screen to the queue of splash screens. Call this in setup().
     *
     * @param layout - The resource ID of the splash screen layout
     * @param time - The amount of time in milliseconds to show the screen
     */
    public void addSplash(int layout, int time) {
        if (numLays < MAX_LAYOUTS) {
            layouts[numLays] = layout;
            times[numLays] = time;
            numLays++;
        } else {
            Log.i("BobEngine", "Too many splash screens. Increase MAX_LAYOUTS.");
        }
    }

    /**
     * Go to the next splash screen or call end() when all screens
     * have been shown.
     */
    private void nextScreen() {
        if (curLay < numLays) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    setContentView(layouts[curLay]);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            curLay++;
                            nextScreen();
                        }
                    }, times[curLay]);
                }
            });
        } else {
            end();
        }
    }

    /**
     * Use this method to add your layouts to the queue of splash screens.
     */
    protected abstract void setup();

    /**
     * This method is called after all the splash screens have been shown. Use it
     * to go to the next activity.
     */
    protected abstract void end();
}
