package com.bobbyloujo.touchinput;

import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.entities.Room;

public class StartRoom extends Room {
	
	// Game Objects
	private AnObject object;

	public StartRoom(BobView container) {
		super(container);

		object = new AnObject(this);
	}

	/**
	 * Set up and reset the room.
	 */
	public void set() {
		object.set();
	}
}
