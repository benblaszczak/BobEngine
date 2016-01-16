package com.bobbyloujo.controllerexample;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bobbyloujo.bobengine.extra.BobActivity;
import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.systems.input.gamepad.Gamepad;
import com.bobbyloujo.bobengine.entities.Room;

/**
 * This Activity is a test driver for getting input from a game controller (gamepad).
 *
 * Created by Benjamin Blaszczak on 4/14/15
 *
 * @modified 9/21/15
 */
public class MainActivity extends BobActivity {

	private RelativeLayout rl;          // For holding our TextView and BobView
	private TextView buttonPressed;     // Displays information about the buttons being pressed
	private BobView view;               // BobView for handling and displaying BobEngine content
	private Room room;                  // An empty room

	/**
	 * This just updates the TextView so it will display what we want.
	 *
	 * @param text
	 */
	private void updateTextView(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				buttonPressed.setText(text);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		useImmersiveMode(); // Because why not? :)

		buttonPressed = new TextView(this); // Have to initialize this.

		view = new BobView(this) {          // Set up our BobView...
			@Override
			protected void onCreateGraphics() {

			}

			@Override
			protected void onCreateRooms() {
				room = new Room(this) {     // Create an empty room so we can handle some simple input

					/**
					 * Rooms and GameObjects now have two new events: newpress(int controller, int button)
					 * and released(int controller, int button).
					 *
					 * These events can be used for handling input from a gamepad. They work in similar ways
					 * to the already existing newpress(int index) and released(int index) for touch screen
					 * input.
					 *
					 * When you override these events from a Room, be sure to call super.newpress(controller, button)
					 * and super.released(controller, button) to ensure that the newpress and released events
					 * are also fired for each GameObject in the room.
					 */

					/**
					 * This is the NEWPRESS event for gamepad input. It will be one time each time a button
					 * on the gamepad is pressed. The 'controller' and 'button' parameters tell you which
					 * gamepad the event comes from (1-4) and which button was pressed.
					 *
					 * Buttons are defined as static constants in the Controller class so they can be
					 * accessed like so:
					 *
					 * Controller.A - for gamepad button A
					 * Controller.B - for B...
					 * so on.
					 *
					 * @param player
					 * @param button
					 */
					@Override
					public void newpress(int player, int button) {
						super.newpress(player, button);

						String buttonName;

						switch (button) {             // Determine which button was pressed
							case Gamepad.A:
								buttonName = "A";
								break;
							case Gamepad.B:
								buttonName = "B";
								break;
							case Gamepad.R1:
								buttonName = "R1";
								break;
							case Gamepad.START:
								buttonName = "Start";
								break;
							case Gamepad.SELECT:
								buttonName = "Select";
								break;
							default:
								buttonName = "Some other button.";
						}

						updateTextView("~~NEWPRESS~~   Player: " + Integer.toString(player) + "  Button: " + buttonName);
					}

					/**
					 * The RELEASED event is similar to newpress but it is fired one time each time
					 * a button on the gamepad is released (let go, lifted up, whatever).
					 *
					 * @param player
					 * @param button
					 */
					@Override
					public void released(int player, int button) {
						super.released(player, button);

						String buttonName;

						switch (button) {
							case Gamepad.A:
								buttonName = "A";
								break;
							case Gamepad.B:
								buttonName = "B";
								break;
							case Gamepad.R1:
								buttonName = "R1";
								break;
							case Gamepad.START:
								buttonName = "Start";
								break;
							case Gamepad.SELECT:
								buttonName = "Select";
								break;
							default:
								buttonName = "Some other button.";
						}

						updateTextView("~~RELEASED~~   Player: " + Integer.toString(player) + "  Button: " + buttonName);
					}

					@Override
					public void step(double dt) {

						/**
						 * You can check to see if a button is being held by calling
						 * getGamepad().held(int controller, int button) from either a Room
						 * or a GameObject.
						 *
						 * You can also get axis values for the right joystick, left stick, triggers,
						 * and DPAD (on some gamepads) using getGamepad().getAxisValue(int controller, int axis)
						 * as shown below in the updateTextView() method call.
						 *
						 * PLEASE NOTE: if the BobView containing your Room has not been assigned
						 * a Controller object (see below), getGamepad() will return null.
						 */
						if (getController().held(1, Gamepad.X)) {
							updateTextView("X held, Left stick X axis: " + getController().getAxisValue(1, Gamepad.LS_X));
						}
					}
				};

				goToRoom(room);
			}
		};

		rl = new RelativeLayout(this);
		rl.addView(view);
		rl.addView(buttonPressed);
		setContentView(rl);
	}
}
