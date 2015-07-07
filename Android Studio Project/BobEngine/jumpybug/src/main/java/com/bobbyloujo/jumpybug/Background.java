package com.bobbyloujo.jumpybug;

import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Room;

/**
 * Created by Ben on 1/9/2015.
 */
public class Background extends GameObject {

	// Variables
	private int speed;

	/**
	 * Initialization.
	 *
	 * @param room - Room that this object is in.
	 */
	public Background(Room room) {
		super(room);

		layer = 0; // By default, the layer is 2. Lower layers are drawn first, so they will appear behind higher layers

		setGraphic(GameView.bg, 1);
	}

	/**
	 * Set up and reset the background
	 */
	public void set(int x, int speed) {
		this.x = x;
		this.speed = speed;

		width = getRoom().getWidth();
		height = getRoom().getHeight();
		y = height / 2;
	}

	@Override
	public void step(double dt) {
		x -= speed;

		if (x <= -width / 2) {
			x = getRoom().getWidth() + width / 2 - (x + width / 2);
		}
	}
}
