package com.bobbyloujo.jumpybug;

import com.bobbyloujo.bobengine.components.Transform;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.sound.SoundPlayer;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.systems.collision.CollisionSystem;

import java.util.Random;

/**
 * Created by Benjamin on 11/27/2015.
 */
public class FlowerPair extends Entity implements Updatable {
	// Constants
	public static final double SPEED = 1;  // Speed at which the flower moves (in grid units)

	private static final int Y_RANGE = 40;  // Range of random y values in grid units
	private static final int GAP = 40;      // Size of the gap between top and bottom flowers in grid units

	// Variables
	private boolean passed;          // Indicates when the bug has passed the flower

	// Sounds
	private SoundPlayer player;      // Plays sound effects
	private int ding;                // Score sound

	private Flower top;
	private Flower bottom;

	private static Random rand;

	public Transform transform;

	public FlowerPair(Entity parent, CollisionSystem collisionSystem) {
		super(parent);

		transform = new Transform();
		top = new Flower(this);
		bottom = new Flower(this);

		top.transform.parent = transform;
		bottom.transform.parent = transform;

		top.transform.y = GAP / 2 + top.transform.height / 2;
		top.transform.angle = 180;

		bottom.transform.y = -GAP / 2 - bottom.transform.height / 2;
		bottom.transform.angle = 0;

		collisionSystem.addCollidable(top);
		collisionSystem.addCollidable(bottom);

		player = new SoundPlayer(getActivity().getApplicationContext()); // Initialize the sound player
		ding = player.newSound(R.raw.ding);                              // Add new sound.

		passed = true;

		if (rand == null) rand = new Random();
	}

	public void sendToRightEdge() {
		transform.x = getRoom().getWidth() + top.transform.width;
		passed = false;
	}

	public void sendToLeftEdge() {
		transform.x = -top.transform.width;
		passed = true;
	}

	public void randomizeY() {
		int offset = rand.nextInt(Y_RANGE);
		transform.y = (getRoom().getHeight() - Y_RANGE) / 2 + offset;
	}

	@Override public void update(double deltaTime) {
		transform.x -= SPEED;

		// Passed the bug, increment score.
		if (transform.x < getRoom().getWidth() / 2 && !passed && transform.angle == 0) {
			((GameRoom) getRoom()).incrementScore();
			passed = true;

			player.play(ding); // Play the sound effect.
		}
	}
}
