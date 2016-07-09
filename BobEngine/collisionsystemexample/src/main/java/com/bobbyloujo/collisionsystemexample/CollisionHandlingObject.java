package com.bobbyloujo.collisionsystemexample;

import java.util.Random;

import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.entities.SimpleGameObject;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.systems.collision.CollisionBox;
import com.bobbyloujo.bobengine.systems.collision.CollisionHandler;
import com.bobbyloujo.bobengine.systems.collision.CollisionSystem;

import javax.microedition.khronos.opengles.GL10;

/**
 * This is an object that other objects can collide into AND can handle collision events with other
 * objects. It uses its own transformation as its collision box.
 *
 * Created by Benjamin on 11/25/2015.
 */
public class CollisionHandlingObject extends SimpleGameObject implements CollisionBox, CollisionHandler, Updatable {

	private static final boolean FOLLOW_POINTER = true;    // Make this true if the object should follow the pointer on the touch screen. False if this object should bounce around.

	private static Random rand; // Just used for initial position

	private int dX;         // X direction
	private int dY;         // Y direction
	private int speed = 10; // Speed in X and Y directions

	public CollisionHandlingObject(Room room, CollisionSystem collisionSystem) {
		super(room);

		/* Your average, everyday initialization stuff */
		getView().getGraphicsHelper().setParameters(false, GL10.GL_NEAREST, GL10.GL_NEAREST, false);
		Graphic g = getView().getGraphicsHelper().addGraphic(R.drawable.collisionsquare);

		setGraphic(g);

		room.getQuadRenderSystem(g).setLayerColor(transform.layer, 1, 0, 0, 1);

		transform.width = 100;
		transform.height = 100;
		transform.scale = 2;

		if (rand == null) rand = new Random();

		transform.x = rand.nextInt((int) getRoom().getWidth());
		transform.y = rand.nextInt((int) getRoom().getHeight());

		dX = rand.nextInt() % 2 == 0 ? 1 : -1;
		dY = rand.nextInt() % 2 == 0 ? 1 : -1;

		/**
		 * Add this Entity to the collision system!
		 */
		collisionSystem.addEntity(this);
	}

	/**
	 * All this code just moves the object around the screen.
	 */
	@Override public void update(double deltaTime) {

		/* Follow pointer */
		if (FOLLOW_POINTER) {
			transform.x = getRoom().getTouch().getX();
			transform.y = getRoom().getTouch().getY();
		}

		/* Bounce around */
		if (!FOLLOW_POINTER) {
			transform.x += dX * speed * deltaTime;
			transform.y += dY * speed * deltaTime;

			if (transform.x > getRoom().getWidth() - transform.width / 2 * transform.scale) {
				dX = -dX;
				transform.x = getRoom().getWidth() - transform.width / 2 * transform.scale;
			}

			if (transform.x < transform.width / 2 * transform.scale) {
				dX = -dX;
				transform.x = transform.width / 2 * transform.scale;
			}

			if (transform.y > getRoom().getHeight() - transform.height / 2 * transform.scale) {
				dY = -dY;
				transform.y = getRoom().getHeight() - transform.height / 2 * transform.scale;
			}

			if (transform.y < transform.height / 2 * transform.scale) {
				dY = -dY;
				transform.y = transform.height / 2 * transform.scale;
			}
		}
	}

	/**
	 * This is the collision event that is fired when a CollisionBox with this CollisionHandler
	 * collides with another CollisionBox in the same CollisionSystem. CollisionBox c is the box
	 * that was collided with and can be used to get information about the Entity being collided
	 * with.
	 *
	 * @param c The CollisionBox object that this CollisionHandler collided with to trigger this event.
	 */
	@Override public void onCollision(CollisionBox c) {
		transform.angle += 2;
	}

	/* CollisionBox methods. Look in CollisionBoxObject for explanations */

	@Override public Transformation getBoxTransformation() {
		return transform;
	}

	@Override public CollisionHandler getCollisionHandler() {
		return this;
	}

	@Override public Entity getEntity() {
		return this;
	}
}
