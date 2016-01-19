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
package com.bobbyloujo.bobengine.systems.input.gamepad;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bobbyloujo.bobengine.view.BobView;

/**
 * This class handles input from a gamepad. It will call newpress and release events
 * in the Rooms and GameObjects. Newpress and released events pass along two pieces of
 * information: the controller number that caused the event and the button that caused
 * the event. Buttons are as follows: <br /><br />
 *
 * Controller.A         <br />
 * ...B                 <br />
 * ...X                 <br />
 * ...Y                 <br />
 *                      <br />
 * ...R1                <br />
 * ...L1                <br /><br />
 *
 * ...D_LEFT, D_RIGHT, D_UP, D_DOWN         <br /><br />
 *
 * <b>NOTE: you must pass the onKeyDown, onKeyUp, and onGenericMotionEvent events
 * from your main activity to this.</b>
 *
 * Created by Benjamin on 4/9/2015.
 *
 * @modified 9/21/15
 */
public class Gamepad {

	public static final int MAX_CONTROLLERS = 4;
	public static final int NUM_BUTTONS = 12;

	// Button and Axis Constants
	public static final int A = 0;
	public static final int B = 1;
	public static final int X = 2;
	public static final int Y = 3;

	public static final int R1 = 4;
	public static final int L1 = 5;

	public static final int D_LEFT = 6;
	public static final int D_RIGHT = 7;
	public static final int D_UP = 8;
	public static final int D_DOWN = 9;

	public static final int START = 10;
	public static final int SELECT = 11;

	public static final int RS_X = 10;
	public static final int RS_Y = 11;
	public static final int LS_X = 12;
	public static final int LS_Y = 13;

	public static final int RTRIGGER = 14;
	public static final int LTRIGGER = 15;

	public static final	int AXIS_D_LR = 16;
	public static final int AXIS_D_UD = 17;

	// owner
	BobView view;

	// held values
	private boolean held[][] = new boolean[MAX_CONTROLLERS][NUM_BUTTONS];

	// Axis values
	private double rsx[] = new double[MAX_CONTROLLERS];
	private double rsy[] = new double[MAX_CONTROLLERS];
	private double lsx[] = new double[MAX_CONTROLLERS];
	private double lsy[] = new double[MAX_CONTROLLERS];
	private double rt[] = new double[MAX_CONTROLLERS];
	private double lt[] = new double[MAX_CONTROLLERS];
	private double dlr[] = new double[MAX_CONTROLLERS];
	private double dud[] = new double[MAX_CONTROLLERS];

	// States
	private boolean RSdpad;
	private boolean LSdpad;
	private boolean simpleDPAD;

	// Variables
	private int lastPlayerDown;
	private int lastPlayerUp;

	public Gamepad(BobView owner) {
		view = owner;
		simpleDPAD = true;
		RSdpad = false;
		LSdpad = false;

		lastPlayerDown = -1;
		lastPlayerUp = -1;

		view.setGamepad(this);
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}

	/**
	 * Set the view for which this controller will fire events.
	 * @param owner
	 */
	public void setView(BobView owner) {
		view = owner;
		view.setGamepad(this);
	}

	/* UTILITY METHODS */

	/**
	 * For controllers that provide axis values for the directional pad
	 * rather than simple button presses, act as though an axis value above
	 * 0.5 or below -0.5 is a button press.
	 *
	 * @param simpleDPAD true if DPAD should register as button presses, false otherwise
	 */
	public void useSimpleDPAD(boolean simpleDPAD) {
		this.simpleDPAD = simpleDPAD;
	}

	/**
	 * Indicate whether the right stick should fire dpad events. Use the right
	 * stick as a dpad.
	 *
	 * @param RSdpad true if the right stick should be used as a DPAD, false otherwise
	 */
	public void useRSasDPAD(boolean RSdpad) {
		this.RSdpad = RSdpad;
	}

	/**
	 * Indicate whether the left stick should fire dpad events. Use the left
	 * stick as a dpad.
	 *
	 * @param LSdpad true if the left stick should be used as a DPAD, false otherwise
	 */
	public void useLSasDPAD(boolean LSdpad) {
		this.LSdpad = LSdpad;
	}

	/**
	 * Determine if a button is being held.
	 *
	 * @param gamepad Gamepad number
	 * @param button Gamepad button
	 * @return True if the specified button on the specified gamepad is being held.
	 */
	public boolean held(int gamepad, int button) {
		if (gamepad >= 1 && gamepad <= MAX_CONTROLLERS && button >= 0 && button <= 9) {
			return held[gamepad - 1][button];
		} else {
			return false;
		}
	}

	/**
	 * Returns the value of the specified axis on the specified gamepad. Options for
	 * axis are:
	 * <br /><br />
	 * Gamepad.RS_X    - for the right stick X axis  <br />
	 * ...RS_Y         - for the right stick Y axis  <br />
	 * ...LS_X         - for the left stick X axis   <br />
	 * ...LS_Y         - for the left stick Y axis   <br />
	 * ...RTRIGGER     - for the right trigger (R2)  <br />
	 * ...LTRIGGER     - for the left trigger (L2)   <br />
	 * ...AXIS_D_LR    - for the dpad left and right <br />
	 * ...AXIS_D_UD    - for the dpad up and down    <br />
	 *
	 * @param gamepad Gamepad number
	 * @param axis Axis ID
	 * @return Value of the specified axis on the specified gamepad.
	 */
	public double getAxisValue(int gamepad, int axis) {

		if (gamepad < 1 || gamepad > MAX_CONTROLLERS) {
			throw new IllegalArgumentException("Invalid gamepad number.");
		}

		switch (axis) {
			case RS_X:
				return rsx[gamepad - 1];
			case RS_Y:
				return rsy[gamepad - 1];
			case LS_X:
				return lsx[gamepad - 1];
			case LS_Y:
				return lsy[gamepad - 1];
			case RTRIGGER:
				return rt[gamepad - 1];
			case LTRIGGER:
				return lt[gamepad - 1];
			case AXIS_D_LR:
				return dlr[gamepad - 1];
			case AXIS_D_UD:
				return dud[gamepad - 1];
			default:
				throw new IllegalArgumentException("Invalid axis identifier.");
		}
	}

	/* INTERNAL METHODS */

	/**
	 * Update the held state of the specified button.
	 *
	 * @param gamepad Gamepad number
	 * @param keyCode KeyEvent key code
	 * @param state true if the button is held, false otherwise
	 */
	private void updateHeld(int gamepad, int keyCode, boolean state){
		if (gamepad >= 1 && gamepad <= MAX_CONTROLLERS && getButton(keyCode) != -1) {
			held[gamepad - 1][getButton(keyCode)] = state;
		}
	}

	/**
	 * Cause newpress event.
	 *
	 * @param gamepad Gamepad number
	 * @param keyCode KeyEvent key code.
	 */
	private void newpress(int gamepad, int keyCode) {
		if (view != null && view.getCurrentRoom() != null && getButton(keyCode) != -1) {
			view.getCurrentRoom().signifyNewpress(gamepad, getButton(keyCode));
			updateHeld(gamepad, keyCode, true);
		}
	}

	/**
	 * Cause released event.
	 *
	 * @param gamepad gamepad number
	 * @param keyCode KeyEvent key code
	 */
	private void released(int gamepad, int keyCode) {
		if (view != null && view.getCurrentRoom() != null && getButton(keyCode) != -1) {
			view.getCurrentRoom().signifyReleased(gamepad, getButton(keyCode));
			updateHeld(gamepad, keyCode, false);
		}
	}

	/**
	 * Convert the KeyEvent key code into a Gamepad key code. Returns -1
	 * if the key code does not have a matching Gamepad key code.
	 * @param keyCode a KeyEvent key code
	 * @return Gamepad key code
	 */
	public int getButton(int keyCode) {
		int button = -1;

		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				button = D_RIGHT;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				button = D_LEFT;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				button = D_UP;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				button = D_DOWN;
				break;
			case KeyEvent.KEYCODE_BUTTON_A:
				button = A;
				break;
			case KeyEvent.KEYCODE_BUTTON_B:
				button = B;
				break;
			case KeyEvent.KEYCODE_BUTTON_X:
				button = X;
				break;
			case KeyEvent.KEYCODE_BUTTON_Y:
				button = Y;
				break;
			case KeyEvent.KEYCODE_BUTTON_R1:
				button = R1;
				break;
			case KeyEvent.KEYCODE_BUTTON_L1:
				button = L1;
				break;
			case KeyEvent.KEYCODE_BUTTON_START:
				button = START;
				break;
			case KeyEvent.KEYCODE_BUTTON_SELECT:
				button = SELECT;
				break;
		}

		return button;
	}

	/* INPUT EVENTS */

	@TargetApi(19)
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int player;      // The player number (controller number) that caused this event

		if (event.getDevice() != null && (event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
			try {
				player = event.getDevice().getControllerNumber();
			} catch (NoSuchMethodError e) {
				player = 1;
			}

			if (event.getRepeatCount() == 0 || player != lastPlayerDown) {
				newpress(player, keyCode);
				lastPlayerDown = player;
			}

			return true;
		}

		return false;
	}

	@TargetApi(19)
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		int player;      // The player number (controller number) that caused this event

		if (event.getDevice() != null && (event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
			try {
				player = event.getDevice().getControllerNumber();
			} catch (NoSuchMethodError e) {
				player = 1;
			}

			if (event.getRepeatCount() == 0 || player != lastPlayerUp) {
				released(player, keyCode);
				lastPlayerUp = player;
			}

			return true;
		}

		return false;
	}

	@TargetApi(19)
	public boolean onGenericMotionEvent(MotionEvent event) {
		int player;              // The player number (controller number) that caused this event

		boolean right = false;   // DPAD flags
		boolean left = false;
		boolean up = false;
		boolean down = false;

		if (event.getDevice() != null && (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
			try {
				player = event.getDevice().getControllerNumber();
			} catch (NoSuchMethodError e) {
				player = 1;
			}

			rsx[player - 1] = event.getAxisValue(MotionEvent.AXIS_Z);
			rsy[player - 1] = event.getAxisValue(MotionEvent.AXIS_RZ);
			lsx[player - 1] = event.getAxisValue(MotionEvent.AXIS_X);
			lsy[player - 1] = event.getAxisValue(MotionEvent.AXIS_Y);

			rt[player - 1] = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
			lt[player - 1] = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);

			dlr[player - 1] = event.getAxisValue(MotionEvent.AXIS_HAT_X);
			dud[player - 1] = event.getAxisValue(MotionEvent.AXIS_HAT_Y);

			if (simpleDPAD) {                  // Use the DPAD to fire button press events even if the DPAD is the weird axis nonsense.
				if (dlr[player - 1] > 0.5) {   // DPAD RIGHT
					right = true;
				}

				if (dlr[player - 1] < -0.5) {  // DPAD LEFT
					left = true;
				}

				if (dud[player - 1] > 0.5) {   // DPAD DOWN
					down = true;
				}

				if (dud[player - 1] < -0.5) {  // DPAD UP
					up = true;
				}
			}

			if (RSdpad) {                  // Use the right analog stick as a DPAD
				if (rsx[player - 1] > 0.5) {   // DPAD RIGHT
					right = true;
				}

				if (rsx[player - 1] < -0.5) {  // DPAD LEFT
					left = true;
				}

				if (rsy[player - 1] > 0.5) {   // DPAD DOWN
					down = true;
				}

				if (rsy[player - 1] < -0.5) {  // DPAD UP
					up = true;
				}
			}

			if (LSdpad) {                  // Use the left analog stick as a DPAD
				if (lsx[player - 1] > 0.5) {   // DPAD RIGHT
					right = true;
				}

				if (lsx[player - 1] < -0.5) {  // DPAD LEFT
					left = true;
				}

				if (lsy[player - 1] > 0.5) {   // DPAD DOWN
					down = true;
				}

				if (lsy[player - 1] < -0.5) {  // DPAD UP
					up = true;
				}
			}

			// Fire right dpad events
			if (right) {
				if (!held(player, Gamepad.D_RIGHT)) {
					newpress(player, KeyEvent.KEYCODE_DPAD_RIGHT);
				}

				updateHeld(player, KeyEvent.KEYCODE_DPAD_RIGHT, true);
			} else {
				if (held(player, Gamepad.D_RIGHT)) {
					released(player, KeyEvent.KEYCODE_DPAD_RIGHT);
				}

				updateHeld(player, KeyEvent.KEYCODE_DPAD_RIGHT, false);
			}

			// Fire left dpad events
			if (left) {
				if (!held(player, Gamepad.D_LEFT)) {                     // If left isn't already being held, fire newpress
					newpress(player, KeyEvent.KEYCODE_DPAD_LEFT);
				}

				updateHeld(player, KeyEvent.KEYCODE_DPAD_LEFT, true);    // Left is held.
			} else {                                                     // Left not being pressed.
				if (held(player, Gamepad.D_LEFT)) {                      // If left was previously being held, fire release
					released(player, KeyEvent.KEYCODE_DPAD_LEFT);
				}

				updateHeld(player, KeyEvent.KEYCODE_DPAD_LEFT, false);   // Left is not held.
			}

			// Fire down dpad events
			if (down) {
				if (!held(player, Gamepad.D_DOWN)) {
					newpress(player, KeyEvent.KEYCODE_DPAD_DOWN);
				}

				updateHeld(player, KeyEvent.KEYCODE_DPAD_DOWN, true);
			} else {
				if (held(player, Gamepad.D_DOWN)) {
					released(player, KeyEvent.KEYCODE_DPAD_DOWN);
				}

				updateHeld(player, KeyEvent.KEYCODE_DPAD_DOWN, false);
			}

			// Fire up dpad events
			if (up) {
				if (!held(player, Gamepad.D_UP)) {
					newpress(player, KeyEvent.KEYCODE_DPAD_UP);
				}

				updateHeld(player, KeyEvent.KEYCODE_DPAD_UP, true);
			} else {
				if (held(player, Gamepad.D_UP)) {
					released(player, KeyEvent.KEYCODE_DPAD_UP);
				}

				updateHeld(player, KeyEvent.KEYCODE_DPAD_UP, false);
			}

			return true;
		}


		return false;
	}
}
