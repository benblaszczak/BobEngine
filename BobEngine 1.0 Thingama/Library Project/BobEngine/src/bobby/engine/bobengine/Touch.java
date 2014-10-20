/**
 * BobEngine - 2D game engine for Android
 * 
 * Copyright (C) 2014 Benjamin Blaszczak
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * This class listens for and handles touch input.
 * 
 * @author Ben
 * @version alpha
 */
public class Touch implements OnTouchListener {

	// Constants
	private final static int MAX_FINGERS = 10;       // The max number of fingers that can touch the screen.

	// Variables
	public int numTouches;
	public boolean held[];

	/** X positions of the pointers currently touching the screen. */
	public float X[];                                // Touch X positions
	/**
	 * Y positions of the pointers currently touching the screen. (0 is at the
	 * bottom of the screen)
	 */
	public float Y[];                                // Touch Y positions; NOTE: 0 at the TOP of the screen. That's the opposite of the graphics.

	// Objects
	private BobView myView;                          // BobView that contains this touch listener.

	/**
	 * Create a new BobEngine touch listener.
	 */
	public Touch(BobView container) {
		held = new boolean[MAX_FINGERS];
		X = new float[MAX_FINGERS];
		Y = new float[MAX_FINGERS];
		myView = container;
		numTouches = 0;

		for (int i = 0; i < MAX_FINGERS; i++) {
			X[i] = -1;
			Y[i] = -1;
			held[i] = false;
		}
	}

	/**
	 * Returns true if the are described by x1, y1, x2, and y2 is being touched.
	 * 
	 * @param x1
	 *            - X coord of the top-left corner of the box
	 * @param y1
	 *            - Y coord of the top-left corner
	 * @param x2
	 *            - X coord of the bottom-right corner
	 * @param y2
	 *            - Y coord of teh bottom-right corner
	 * @return true if touched by any finger, false otherwise
	 */
	public boolean areaTouched(double x1, double y1, double x2, double y2) {
		for (int i = 0; i < numTouches; i++) {
			if (X[i] > x1 && X[i] < x2 && X[i] >= 0) {
				if (Y[i] < y1 && Y[i] > y2 && X[i] >= 0) { return true; }
			}
		}

		return false;
	}

	/**
	 * Returns true if GameObject o is touched.
	 * 
	 * @param o
	 *            - GameObject to check if is touched
	 * @return True if o is being touched by any pointer, false otherwise.
	 */
	public boolean objectTouched(GameObject o) {
		if (areaTouched(o.x - o.width / 2, o.y + o.height / 2, o.x + o.width / 2, o.y - o.height / 2)) { return true; }

		return false;
	}
	
	/**
	 * Returns true if any pointers are currently touching the screen.
	 */
	public boolean held() {
		if (held[0]) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if a number of fingers are touching the screen.
	 * 
	 * @param index - the number of fingers to check
	 * @return True if index number of fingers are touching the screen, false otherwise.
	 */
	public boolean held(int index) {
		if (held[index]) {
			return true;
		}
		
		return false;
	}

	/**
	 * Handle touch events. <br />
	 * <br />
	 * This method will... <br />
	 * ...trigger new presses. <br />
	 * ...trigger releases. <br />
	 * ...get touch positions. <br />
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Variables
		numTouches = event.getPointerCount();            // The current number of touchs
		int index = event.getActionIndex();              // The finger that is touching the screen

		switch (event.getActionMasked() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			for (int i = 0; i < MAX_FINGERS; i++) {
				if(i < numTouches) {
					held[i] = true;
				} else {
					held[i] = false;
				}
			}

			X[0] = event.getX();
			Y[0] = myView.getHeight() - event.getY();

			if (myView.getCurrentRoom() != null) myView.getCurrentRoom().newpress(index);
			break;

		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < numTouches; i++) {
				X[i] = event.getX(i);
				Y[i] = myView.getHeight() - event.getY(i);
			}

			break;

		case MotionEvent.ACTION_UP:
			for (int i = 0; i < MAX_FINGERS; i++) {
				if(i < numTouches - 1) {
					held[i] = true;
				} else {
					held[i] = false;
				}
			}

			numTouches = 0;

			if (myView.getCurrentRoom() != null) myView.getCurrentRoom().released(index);
			break;

		case MotionEvent.ACTION_POINTER_DOWN:

			for (int i = 0; i < MAX_FINGERS; i++) {
				if(i < numTouches) {
					held[i] = true;
				} else {
					held[i] = false;
				}
			}

			X[index] = event.getX(index);
			Y[index] = myView.getHeight() - event.getY(index);

			if (myView.getCurrentRoom() != null) myView.getCurrentRoom().newpress(index);
			break;

		case MotionEvent.ACTION_POINTER_UP:
			
			for (int i = 0; i < MAX_FINGERS; i++) {
				if(i < numTouches - 1) {
					held[i] = true;
				} else {
					held[i] = false;
				}
			}
			
			if (myView.getCurrentRoom() != null) myView.getCurrentRoom().released(index);
			break;
		}

		return true;
	}
}
