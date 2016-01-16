package com.bobbyloujo.collisionsystemexample;

import java.util.Random;

import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.entities.SimpleGameObject;
import com.bobbyloujo.bobengine.systems.collision.CollisionBox;
import com.bobbyloujo.bobengine.systems.collision.CollisionHandler;
import com.bobbyloujo.bobengine.systems.collision.CollisionSystem;

/**
 * This is an object that other objects can collide into. It is able to trigger collision events
 * for CollisionHandlers in the same CollisionSystem. This object uses its own transformation as its
 * collision box!
 *
 * Created by Benjamin on 11/25/2015.
 */
public class CollisionBoxObject extends SimpleGameObject implements CollisionBox, Updatable {

	static Random rand;        // This is just for picking random starting position

	private int dX;            // X direction
	private int dY;            // Y direction
	private int speed = 10;    // Speed in both X and Y direction

	public CollisionBoxObject(Room room, CollisionSystem collisionSystem) {
		super(room);

		/* Usual GameObject initialization stuff */
		setGraphic(getView().getGraphicsHelper().addGraphic(R.drawable.logo));

		transform.width = 110;
		transform.height = 60;
		transform.layer = 1;

		transform.scale = 2;

		if (rand == null) rand = new Random();

		transform.x = rand.nextInt((int) getRoom().getWidth());
		transform.y = rand.nextInt((int) getRoom().getHeight());

		dX = rand.nextInt() % 2 == 0 ? 1 : -1;
		dY = rand.nextInt() % 2 == 0 ? 1 : -1;

		/**
		 * Since this object implements the CollisionBox interface, we add it
		 * to the CollisionSystem.
		 *
		 *    A CollisionBox object is a Component, so an Entity could also have many
		 *    CollisionBox components and thus have many collision boxes. All of an
		 *    Entity's CollisionBox components can be added to a CollisionSystem using
		 *    the addEntity() method. You will see this in GameObjectWithBox.java
		 *
		 * See the implemented methods at the bottom of this file to see what
		 * information the CollisionSystem needs from each CollisionBox.
		 */
		collisionSystem.addEntity(this);
	}

	/**
	 * All of this just makes the object bounce around the screen.
	 */
	@Override public void update(double deltaTime) {
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

	/**
	 * The Transformation object that is returned by this method will be used
	 * by the CollisionSystem to determine the bounds of this CollisionBox's
	 * collision box.
	 *
	 * Here, we just return this SimpleGameObject's transform component. So,
	 * the collision box will just be the bounds of this SimpleGameObject.
	 *
	 * @return A Transformation object defining the bounds of this CollisionBox's
	 * collision box. <b>This should never be null!</b>
	 */
	@Override public Transformation getBoxTransformation() {
		return transform;
	}

	/**
	 * Here, we could return a CollisionHandler to handle collision events any
	 * time this CollisionBox collides with another CollisionBox. For this CollisionBox
	 * I have decided not to assign a CollisionHandler by returning null. If
	 * another CollisionBox which has a CollisionHandler collides with this
	 * CollisionBox, that other CollisionBox's CollisionHandler will handle the
	 * collision event.
	 *
	 * Collisions between two Collidables that do not have CollisionHandlers will
	 * not be checked because neither can handle collision events anyway. It is best
	 * to use as few CollisionHandlers as possible to improve efficiency.
	 *
	 * @return A CollisionHandler if there is one, null otherwise.
	 */
	@Override public CollisionHandler getCollisionHandler() {
		return null;
	}

	/**
	 * A CollisionBox (which is really just a collision box) will usually represent
	 * a collidable (ie. solid) portion of an Entity. This method is meant to
	 * return that Entity so that CollisionHandlers can determine which Entity
	 * they collided with. This is helpful when an Entity has many Collidables!
	 *
	 * In this case, the CollisionBox is its own Entity so it just returns itself.
	 *
	 * @return The Entity to which this CollisionBox belongs.
	 */
	@Override public Entity getEntity() {
		return this;
	}
}
