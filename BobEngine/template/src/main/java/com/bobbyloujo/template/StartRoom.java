package com.bobbyloujo.template;

import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.entities.Room;

/**
 * This is a room. Right now, it contains only one object but you can create more!
 */
public class StartRoom extends Room {
	
	// Game Objects
	private AnObject object;

	public StartRoom(BobView container) {
		super(container);
		
		// TODO Initialization of objects and variables
		object = new AnObject(this); // This object will appear in this room thanks to passing this room as the argument in the object's constructor.
	}

	/**
	 * Set up and reset the room. This is not something that is built in to BobEngine
	 * but I encourage using it or something similar so you can easily set up and reset
	 * your rooms.
	 */
	public void set() {
		// TODO Set up objects.
		object.set();
	}
}
