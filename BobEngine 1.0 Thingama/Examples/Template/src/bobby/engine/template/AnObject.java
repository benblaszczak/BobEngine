package bobby.engine.template;

import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Room;

public class AnObject extends GameObject {

	public AnObject(int id, Room containingRoom) {
		super(id, containingRoom);
		// TODO Initialization
		
		/* Set graphic and number of frames to use for this icon */
		setGraphic(GameView.icon, 1);
	}

	/**
	 * Set up or reset this object.
	 */
	public void set() {
		x = getRoom().getWidth() / 2;
		y = getRoom().getHeight() / 2;
	}
	
	/**
	 * Step event happens every frame.
	 */
	@Override
	public void step(double deltaTime) {
		angle++; // Make it spin!
	}
}
