/**
 * BobEngine - 2D game engine for Android
 *
 * Copyright (C) 2014, 2015 Benjamin Blaszczak
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

import android.annotation.TargetApi;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

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
 */
public class Controller {

	public static final int MAX_CONTROLLERS = 4;

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

	public static final int RS_X = 10;
	public static final int RS_Y = 11;
	public static final int LS_X = 12;
	public static final int LS_Y = 13;

	public static final int RTRIGGER = 14;
	public static final int LTRIGGER = 15;

	public static final	int AXIS_D_LR = 16;
	public static final int AXIS_D_UD = 17;

	// owner
	BobView myOwner;

	// held values
	private boolean held[][] = new boolean[4][10];

	// Axis values
	private double rsx[] = new double[4];
	private double rsy[] = new double[4];
	private double lsx[] = new double[4];
	private double lsy[] = new double[4];
	private double rt[] = new double[4];
	private double lt[] = new double[4];
	private double dlr[] = new double[4];
	private double dud[] = new double[4];

	// States
	private boolean RSdpad;
	private boolean LSdpad;
	private boolean simpleDPAD;

	// Variables
	private int lastPlayerDown;
	private int lastPlayerUp;

	public Controller(BobView owner) {
		myOwner = owner;
		simpleDPAD = false;
		RSdpad = false;
		LSdpad = false;

		lastPlayerDown = -1;
		lastPlayerUp = -1;

		myOwner.setController(this);
	}

	/**
	 * Set the view for which this controller will fire events.
	 * @param owner
	 */
	public void setView(BobView owner) {
		myOwner = owner;
		myOwner.setController(this);
	}

	/**
	 * For controllers that provide axis values for the directional pad
	 * rather than simple button presses, act as though an axis value above
	 * 0.5 or below -0.5 is a button press.
	 *
	 * @param simpleDPAD
	 */
	public void useSimpleDPAD(boolean simpleDPAD) {
		this.simpleDPAD = simpleDPAD;
	}

	/**
	 * Indicate whether the right stick should fire dpad events. Use the right
	 * stick as a dpad.
	 *
	 * @param RSdpad
	 */
	public void useRSasDPAD(boolean RSdpad) {
		this.RSdpad = RSdpad;
	}

	/**
	 * Indicate whether the left stick should fire dpad events. Use the left
	 * stick as a dpad.
	 *
	 * @param LSdpad
	 */
	public void useLSasDPAD(boolean LSdpad) {
		this.LSdpad = LSdpad;
	}

	/**
	 * Update the held state of the specified button.
	 *
	 * @param controller
	 * @param keyCode
	 * @param state
	 */
	private void updateHeld(int controller, int keyCode, boolean state){
		if (controller >= 0 && controller <= 4 && getButton(keyCode) != -1) {
			held[controller][getButton(keyCode)] = state;
		}
	}

	/**
	 * Cause newpress event.
	 *
	 * @param controller
	 * @param keyCode
	 */
	private void newpress(int controller, int keyCode) {
		if (myOwner != null && myOwner.getCurrentRoom() != null && getButton(keyCode) != -1) {
			myOwner.getCurrentRoom().signifyNewpress(controller, getButton(keyCode));
			updateHeld(controller, keyCode, true);
		}
	}

	/**
	 * Cause released event.
	 *
	 * @param controller
	 * @param keyCode
	 */
	private void released(int controller, int keyCode) {
		if (myOwner != null && myOwner.getCurrentRoom() != null && getButton(keyCode) != -1) {
			myOwner.getCurrentRoom().signifyReleased(controller, getButton(keyCode));
			updateHeld(controller, keyCode, false);
		}
	}

	/**
	 * Determine if a button is being held.
	 *
	 * @param controller
	 * @param button
	 * @return True if the specified button on the specified controller is being held.
	 */
	public boolean held(int controller, int button) {
		if (controller >= 0 && controller <= 4 && button >= 0 && button <= 9)
			return held[controller][button];
		else
			return false;
	}

	/**
	 * Returns the value of the specified axis on the specified controller. Options for
	 * axis are:
	 *
	 * Controller.RS_X - for the right stick X axis
	 * ...RS_Y         - for the right stick Y axis
	 * ...LS_X
	 * ...LS_Y
	 * ...RTRIGGER     - for the right trigger (R2)
	 * ...LTRIGGER     - for the left trigger (L2)
	 * ...AXIS_D_LR    - for the dpad left and right
	 * ...AXIS_D_UD    - for the dpad up and down
	 *
	 * @param controller
	 * @param axis
	 * @return
	 */
	public double getAxisValue(int controller, int axis) {
		switch (axis) {
			case RS_X:
				return rsx[controller];
			case RS_Y:
				return rsy[controller];
			case LS_X:
				return lsx[controller];
			case LS_Y:
				return lsy[controller];
			case RTRIGGER:
				return rt[controller];
			case LTRIGGER:
				return lt[controller];
			case AXIS_D_LR:
				return dlr[controller];
			case AXIS_D_UD:
				return dud[controller];
			default:
				return -2;
		}
	}

	/**
	 * Convert the KeyEvent key code into a Controller key code. Returns -1
	 * if the key code does not have a matching Controller key code.
	 * @param keyCode
	 * @return
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
		}

		return button;
	}

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
				return true;
			}
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
				return true;
			}
		}

		return myOwner.onKeyUp(keyCode, event);
	}

	@TargetApi(19)
	public boolean onGenericMotionEvent(MotionEvent event) {
		int player;      // The player number (controller number) that caused this event

		boolean right = false;
		boolean left = false;
		boolean up = false;
		boolean down = false;

		if (event.getDevice() != null && (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
			try {
				player = event.getDevice().getControllerNumber();
			} catch (NoSuchMethodError e) {
				player = 1;
			}

			rsx[player] = event.getAxisValue(MotionEvent.AXIS_Z);
			rsy[player] = event.getAxisValue(MotionEvent.AXIS_RZ);
			lsx[player] = event.getAxisValue(MotionEvent.AXIS_X);
			lsy[player] = event.getAxisValue(MotionEvent.AXIS_Y);

			rt[player] = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
			lt[player] = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);

			dlr[player] = event.getAxisValue(MotionEvent.AXIS_HAT_X);
			dud[player] = event.getAxisValue(MotionEvent.AXIS_HAT_Y);

			if (simpleDPAD) {
				// DPAD RIGHT
				if (dlr[player] > 0.5) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_RIGHT, true);
					right = true;
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_RIGHT, false);
				}

				// DPAD LEFT
				if (dlr[player] < -0.5) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_LEFT, true);
					left = true;
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_LEFT, false);
				}

				// DPAD DOWN
				if (dud[player] > 0.5) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_DOWN, true);
					down = true;
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_DOWN, false);
				}

				// DPAD UP
				if (dud[player] < -0.5) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_UP, true);
					up = true;
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_UP, false);
				}
			}

			if (RSdpad) {
				// DPAD RIGHT
				if (rsx[player] > 0.5 || right) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_RIGHT, true);
					right = true;
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_RIGHT, false);
				}

				// DPAD LEFT
				if (rsx[player] < -0.5 || left) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_LEFT, true);
					left = true;
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_LEFT, false);
				}

				// DPAD DOWN
				if (rsy[player] > 0.5 || down) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_DOWN, true);
					down = true;
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_DOWN, false);
				}

				// DPAD UP
				if (rsy[player] < -0.5 || up) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_UP, true);
					up = true;
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_UP, false);
				}
			}

			if (LSdpad) {
				// DPAD RIGHT
				if (lsx[player] > 0.5 || right) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_RIGHT, true);
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_RIGHT, false);
				}

				// DPAD LEFT
				if (lsx[player] < -0.5 || left) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_LEFT, true);
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_LEFT, false);
				}

				// DPAD DOWN
				if (lsy[player] > 0.5 || down) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_DOWN, true);
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_DOWN, false);
				}

				// DPAD UP
				if (lsy[player] < -0.5 || up) {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_UP, true);
				} else {
					updateHeld(player, KeyEvent.KEYCODE_DPAD_UP, false);
				}
			}

			return true;
		}


		return myOwner.onGenericMotionEvent(event);
	}
}
