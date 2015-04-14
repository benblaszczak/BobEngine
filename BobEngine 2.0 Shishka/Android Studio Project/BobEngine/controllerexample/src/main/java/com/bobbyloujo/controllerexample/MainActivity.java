package com.bobbyloujo.controllerexample;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bobby.engine.bobengine.BobActivity;
import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.Controller;
import bobby.engine.bobengine.Room;

/**
 * This Activity is a test driver for getting input from a game controller (gamepad).
 *
 * Created by Benjamin Blaszczak on 4/14/15
 */
public class MainActivity extends BobActivity {

	private RelativeLayout rl;          // For holding our TextView and BobView
	private TextView buttonPressed;     // Displays information about the buttons being pressed
	private BobView view;               // BobView for handling and displaying BobEngine content
	private Room room;                  // An empty room

	private Controller controller;      // This Controller object will help us get input from the controller (gamepad)

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
					 * @param controller
					 * @param button
					 */
					@Override
					public void newpress(int controller, int button) {
						super.newpress(controller, button);

						String buttonName;

						switch (button) {             // Determine which button was pressed
							case Controller.A:
								buttonName = "A";
								break;
							case Controller.B:
								buttonName = "B";
								break;
							case Controller.R1:
								buttonName = "R1";
								break;
							default:
								buttonName = "Some other button.";
						}

						updateTextView("~~NEWPRESS~~   Controller: " + Integer.toString(controller) + "  Button: " + buttonName);
					}

					/**
					 * The RELEASED event is similar to newpress but it is fired one time each time
					 * a button on the gamepad is released (let go, lifted up, whatever).
					 *
					 * @param controller
					 * @param button
					 */
					@Override
					public void released(int controller, int button) {
						super.released(controller, button);

						String buttonName;

						switch (button) {
							case Controller.A:
								buttonName = "A";
								break;
							case Controller.B:
								buttonName = "B";
								break;
							case Controller.R1:
								buttonName = "R1";
								break;
							default:
								buttonName = "Some other button.";
						}

						updateTextView("~~RELEASED~~   Controller: " + Integer.toString(controller) + "  Button: " + buttonName);
					}

					@Override
					public void step(double dt) {

						/**
						 * You can check to see if a button is being held by calling
						 * getController().held(int controller, int button) from either a Room
						 * or a GameObject.
						 *
						 * You can also get axis values for the right joystick, left stick, triggers,
						 * and DPAD (on some gamepads) using getController().getAxisValue(int controller, int axis)
						 * as shown below in the updateTextView() method call.
						 *
						 * PLEASE NOTE: if the BobView containing your Room has not been assigned
						 * a Controller object (see below), getController() will return null.
						 */
						if (getController().held(1, Controller.X)) {
							updateTextView("X held, Left stick X axis: " + getController().getAxisValue(1, Controller.LS_X));
						}
					}
				};

				goToRoom(room);
			}
		};


		/**
		 * IMPORTANT - ASSIGNING A CONTROLLER TO A BOBVIEW
		 *
		 * The Controller object must be told which view it will fire events for.
		 * This can be changed by using the method setView(BobView view) if you have
		 * more than one BobView that you want to switch between.
		 */
		controller = new Controller(view);

		rl = new RelativeLayout(this);
		rl.addView(view);
		rl.addView(buttonPressed);
		setContentView(rl);
	}

	/* IMPORTANT EVENT HANDLING */

	/**
	 * Below are the events that Android fires when it receives input
	 * from a gamepad or keyboard. You must pass these events on to your
	 * Controller object so that it can interpret them.
	 */

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return controller.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return controller.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		return controller.onGenericMotionEvent(event);
	}
}
