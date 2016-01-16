package com.bobbyloujo.template;

import com.bobbyloujo.bobengine.entities.GameObject;
import com.bobbyloujo.bobengine.entities.Room;

/**
 * This is a game object! Game objects are where most of the fun happens. Game objects have
 * graphics associated with them, attributes such as x, y, width, and height, and a step
 * event that happens every frame. There are other events too, such as:
 *
 * newpress(int) - called when there is a new touch on the screen.
 * released(int) - called when a pointer is released from the screen.
 */
public class AnObject extends GameObject {

	public AnObject(Room room) {
		super(room);
		// TODO Initialization
		
		/* Set graphic and number of frames to use for this icon */
		setGraphic(GameView.icon, 1);
	}

	/**
	 * Set up or reset this object.
	 */
	public void set() {
		x = getRoom().getWidth() / 2;
		y = getRoom().getHeight() / 2;
	}
	
	/**
	 * Step event happens every frame.
	 */
	@Override
	public void step(double deltaTime) {
		angle++; // Make it spin!
	}
}
