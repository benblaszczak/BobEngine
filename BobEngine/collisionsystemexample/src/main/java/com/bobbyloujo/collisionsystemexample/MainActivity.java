package com.bobbyloujo.collisionsystemexample;

import android.os.Bundle;

import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.extra.BobActivity;
import com.bobbyloujo.bobengine.systems.collision.CollisionSystem;

/**
 * Created by Benjamin on 11/25/2015.
 */
public class MainActivity extends BobActivity {

	static final int NUM_COLLIDABLES = 1;  // Number of Collidables that bounce around but don't handle collision events
	static final int NUM_HANDLERS = 1;     // Number of CollisionHandlers that handle collision events.
	static final int NUM_GO_W_BOX = 1;     // Number of GameObjectWithBoxes.

	// Standard stuff
	BobView view;
	Room room;

	// The system that will handle collisions. AKA the CollisionSystem!
	CollisionSystem collisionSystem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = new BobView(this) {
			@Override protected void onCreateRooms() {
				room = new Room(view);

				collisionSystem = new CollisionSystem();     // Just initialize the system
				room.addComponent(collisionSystem);          // Then add it to the room

				/**
				 * This loop generates objects that handle collisions with other objects.
				 * These objects can collide with other objects in the collision system AND
				 * do something when it happens.
				 */
				for (int i = 0; i < NUM_HANDLERS; i++) {
					new CollisionHandlingObject(room, collisionSystem);
				}

				/**
				 * This loop generates objects that can collide with other objects, but can't
				 * do anything when a collision happens. They can TRIGGER collision events for
				 * collision handling objects, but can't handle collision events themselves.
				 */
				for (int i = 0; i < NUM_COLLIDABLES; i++) {
					new CollisionBoxObject(room, collisionSystem);
				}

				/**
				 * This loop generates some GameObjects that can handle collisions.
				 */
				for (int i = 0; i < NUM_GO_W_BOX; i++) {
					new GameObjectWithBox(room, collisionSystem);
				}

				goToRoom(room);
			}
		};

		setContentView(view);
	}
}
