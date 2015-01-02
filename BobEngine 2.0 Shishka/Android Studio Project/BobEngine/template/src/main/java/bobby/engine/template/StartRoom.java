package bobby.engine.template;

import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.Room;

/**
 * This is a room. Right now, it contains only one object but you can create more!
 *
 * Notice that the object is first initialized and then added to the room using the method
 * addObject(object). Calling addObject(object) is how BobEngine will know that when this room
 * is the room that is currently being displayed it will need to display the game object called
 * "object".
 */
public class StartRoom extends Room {
	
	// Game Objects
	private AnObject object;

	public StartRoom(BobView container) {
		super(container);
		
		// TODO Initialization of objects and variables
		object = new AnObject(nextInstance(), this);
		
		
		// TODO Add objects to this room
		addObject(object);
	}

	/**
	 * Set up and reset the room.
	 */
	public void set() {
		object.set();
	}
}
