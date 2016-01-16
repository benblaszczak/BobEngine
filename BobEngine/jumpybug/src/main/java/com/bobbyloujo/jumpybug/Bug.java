package com.bobbyloujo.jumpybug;

import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.entities.GameObject;
import com.bobbyloujo.bobengine.systems.collision.CollisionBox;
import com.bobbyloujo.bobengine.systems.collision.CollisionHandler;

/**
 * Created by Ben on 1/6/2015.
 */
public class Bug extends GameObject implements CollisionBox, CollisionHandler {

	// Constants
	private final double ACC = .1;      // The acceleration of gravity. Determines how fast the bug falls.
	private final double JUMP_V = 2.2;  // Velocity to give the bug to make it jump.

	// Variables
	private double vy;                  // The bug's velocity on the y axis.

	/**
	 * Initialization.
	 *
	 * @param parent - Room that this object is in.
	 */
	public Bug(Entity parent) {
		super(parent);

		/**
		 * Assign the bug graphic we created in GameView to this object.
		 * This graphic has only 1 frame. Graphics with many frames must
		 * have those frames arranged vertically and all frames must be the
		 * same size.
		 */
		setGraphic(GameView.bug, 1);
	}

	/**
	 * Set up and reset the bug.
	 */
	public void set() {
		x = getRoom().getWidth() / 2;
		y = getRoom().getHeight() * 3 / 4;
		width = 11;
		height = 11;
		vy = 0;
		layer = 3;
	}

	/**
	 * Step event happens each frame.
	 */
	@Override
	public void step(double dt) {
		vy -= ACC;                 // Acceleration of gravity
		y += vy;                   // y velocity

		angle = vy * getRoom().getGridUnitY() / 2;            // Change the bug's angle based on the bug's y velocity

		if (y < height / 2) {      // Hit the ground, game over!
			GameView.gameOver.set();
			getView().goToRoom(GameView.gameOver);
		}
	}

	/**
	 * This event fires when the screen has been touched.
	 *
	 * @param index a number identifying the pointer that touched the screen
	 */
	@Override
	public void newpress(int index) {
		// Make the bug jump
		if (y + JUMP_V < getRoom().getHeight()) vy = JUMP_V;
	}

	/**
	 * This event fires when this entity collides with another in the same CollisionSystem.
	 *
	 * @param c The CollisionBox object that this CollisionHandler collided with to trigger this event.
	 */
	@Override
	public void onCollision(CollisionBox c) {
		GameView.gameOver.set();
		getView().goToRoom(GameView.gameOver);
	}

	/**
	 * The CollisionSystem needs a Transformation object to use a collision box for detecting
	 * collisions.
	 */
	@Override
	public Transformation getBoxTransformation() {
		return this;
	}

	@Override public CollisionHandler getCollisionHandler() {
		return this;
	}

	@Override public Entity getEntity() {
		return this;
	}
}
