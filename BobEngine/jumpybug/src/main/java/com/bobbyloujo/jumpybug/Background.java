package com.bobbyloujo.jumpybug;

import com.bobbyloujo.bobengine.components.Transform;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.entities.SimpleGameObject;
import com.bobbyloujo.bobengine.systems.Updatable;

/**
 * Created by Ben on 1/9/2015.
 */
public class Background extends Entity implements Updatable {

	// Constants
	private static final double SPEED = .5;

	private static final double HEIGHT = 160;
	private static final double WIDTH = 90;

	// Objects
	public Transform transform;
	private SimpleGameObject[] panels;

	// Variables
	private double speed;
	private double nextX;

	/**
	 * Initialization.
	 *
	 * @param parent - Room that this object is in.
	 */
	public Background(Entity parent) {
		super(parent);

		transform = new Transform();
		transform.y = HEIGHT / 2;

		speed = SPEED;

		int numNeeded = (int) getRoom().getWidth() / (int) WIDTH + 2;
		panels = new SimpleGameObject[numNeeded];

		for (int i = 0; i < panels.length; i++) {
			panels[i] = new SimpleGameObject(this);
			panels[i].transform.parent = transform;
			panels[i].transform.layer = 0;
			panels[i].transform.width = WIDTH;
			panels[i].transform.height = HEIGHT;
			panels[i].setGraphic(GameView.bg);
		}
	}

	/**
	 * Set up and reset the background
	 */
	public void set() {
		nextX = WIDTH / 2;

		for (SimpleGameObject panel : panels) {
			panel.transform.x = nextX;
			nextX += panel.transform.width;
		}
	}

	@Override
	public void update(double dt) {
		nextX -= speed;

		for (SimpleGameObject panel : panels) {
			panel.transform.x -= speed;

			if (panel.transform.x <= -panel.transform.width / 2) {
				panel.transform.x = nextX;
				nextX = panel.transform.x + panel.transform.width;
			}
		}
	}
}
