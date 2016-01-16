package com.bobbyloujo.jumpybug;

import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.entities.SimpleGameObject;
import com.bobbyloujo.bobengine.systems.collision.CollisionBox;
import com.bobbyloujo.bobengine.systems.collision.CollisionHandler;

/**
 * Created by Ben on 1/6/2015.
 */
public class Flower extends SimpleGameObject implements CollisionBox {

	/**
	 * Initialize.
	 *
	 * @param parent - Room that this object is in.
	 */
	public Flower(Entity parent) {
		super(parent);

		setGraphic(GameView.flower);

		transform.height = 80;
		transform.width = 16;
	}

	/* COLLIDABLE METHODS */

	@Override
	public Transformation getBoxTransformation() {
		return transform;
	}

	@Override public CollisionHandler getCollisionHandler() {
		return null;
	}

	@Override public Entity getEntity() {
		return this;
	}
}
