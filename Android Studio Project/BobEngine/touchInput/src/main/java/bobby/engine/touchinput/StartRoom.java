package bobby.engine.touchinput;

import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.Room;

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
