package com.bobbyloujo.collisionsystemexample;

import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.entities.SimpleGameObject;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.systems.collision.CollisionBox;
import com.bobbyloujo.bobengine.systems.collision.CollisionHandler;
import com.bobbyloujo.bobengine.systems.collision.CollisionSystem;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Benjamin on 12/8/2015.
 */
public class GameObjectWithBox extends SimpleGameObject implements Updatable, CollisionHandler {

	private static final boolean BOUNCE = false; // Set this to true if you want these objects to bounce around the screen

	private static Random rand;

	private int dX;
	private int dY;
	private int speed = 10;

	public GameObjectWithBox(Room room, CollisionSystem collisionSystem) {
		super(room);

		getView().getGraphicsHelper().setParameters(false, GL10.GL_NEAREST, GL10.GL_NEAREST);
		Graphic g = getView().getGraphicsHelper().addGraphic(R.drawable.collisionsquare);

		setGraphic(g);

		room.getQuadRenderSystem(g).setLayerColor(transform.layer, 1, 0, 0, 1);

		transform.width = 100;
		transform.height = 100;
		transform.layer = 1;
		transform.scale = 4;

		if (rand == null) rand = new Random();

		transform.x = rand.nextInt((int) getRoom().getWidth());
		transform.y = rand.nextInt((int) getRoom().getHeight());

		dX = rand.nextInt() % 2 == 0 ? 1 : -1;
		dY = rand.nextInt() % 2 == 0 ? 1 : -1;

		addComponent(CollisionSystem.generateCollisionBox(0, 0, .25, .25, this, transform, this));

		collisionSystem.addEntity(this);
	}

	@Override public void update(double deltaTime) {
		//transform.x += dX * speed * deltaTime;
		//transform.y += dY * speed * deltaTime;

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

	@Override public void onCollision(CollisionBox c) {
		transform.angle += 2;
	}
}
