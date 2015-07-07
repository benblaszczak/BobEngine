package com.bobbyloujo.jumpybug;

import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Room;
import bobby.engine.bobengine.SoundPlayer;

/**
 * Created by Ben on 1/6/2015.
 */
public class Flower extends GameObject {

	// Constants
	private final int SPEED = 8;   // Speed at which the flower moves

	// Variables
	private boolean passed;        // Indicates when the bug has passed the flower

	// Sounds
	private SoundPlayer player;    // Plays sound effects
	private int ding;              // Score sound

	/**
	 * Initialize.
	 *
	 * @param room - Room that this object is in.
	 */
	public Flower(Room room) {
		super(room);

		player = new SoundPlayer(getActivity().getApplicationContext()); // Initialize the sound player
		ding = player.newSound(R.raw.ding);                              // Add new sound.

		setGraphic(GameView.flower, 1);

		/**
		 * Collision detection.
		 *
		 * You can give any object up to 10 collision boxes. Boxes are defined with 2 points.
		 * (0,0) is the top left corner of the object, and (1,1) is the bottom right.
		 *
		 * Note: collision boxes do not rotate with the object.
		 */
		giveCollisionBox(0, 0, 1, 1);     // Top, the flower pedals
	}

	public void set(boolean isTop) {
		height = getRoom().getHeight() / 2;
		width = height / 4;
		x = -width;

		passed = true;

		if (isTop) {
			y = getRoom().getHeight();
			angle = 180;
		} else {
			y = 0;
			angle = 0;
		}
	}

	@Override
	public void step(double dt) {
		x -= SPEED;

		// Passed the bug, increment score.
		if (x < getRoom().getWidth() / 2 && !passed && angle == 0) {
			((GameRoom) getRoom()).incrementScore();
			passed = true;

			player.play(ding); // Play the sound effect.
		}

		// Back on the right, can be passed again.
		if (x > getRoom().getWidth()) {
			passed = false;
		}
	}
}
