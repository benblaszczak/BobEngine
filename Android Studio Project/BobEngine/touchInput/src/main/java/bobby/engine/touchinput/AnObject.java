package bobby.engine.touchinput;

import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Room;

public class AnObject extends GameObject {

	public AnObject(Room containingRoom) {
		super(containingRoom);
		
		/* Set graphic and number of frames the graphic has */
		setGraphic(GameView.icon, 1);
	}

	/**
	 * Set up or reset this object.
	 */
	public void set() {
		x = getRoom().getWidth() / 2;
		y = getRoom().getHeight() / 2;
		
		width = height = getRoom().getWidth() / 5;
	}
	
	/**
	 * Step event happens every frame.
	 */
	@Override
	public void step(double deltaTime) {
		
		
		/**
		 * Rather than having an event like newpress and released,
		 * the "held" state of a touch is determined by a function
		 * call.
		 */
		if (getTouch().held()) {                        // ANY finger is held
			angle++;                                    // Make it spin!
		}
		
		if (getTouch().held(1)) {                       // 2 fingers are held (index IDs 0 and 1)
			width = height = getRoom().getWidth() / 3;  // Make it bigger!
		} else {                                        // Reset size
			width = height = getRoom().getWidth() / 5;
		}
	}
	
	/**
	 * NEWPRESS: Fires once when the screen is touched.
	 * <p/>
	 * index is the ID of the finger that touched the screen.
	 * 0 - is the first finger
	 * 1 - is the second... so on
	 */
	@Override
	public void newpress(int index) {
		x = getTouch().getX();         // Use "getTouch()" to get lots of information about the input
		y = getTouch().getY();         // These are the coords of the first index (the "oldest" finger on the screen)
		
		if (index == 1) {            // A second finger!
			getView().setBackgroundColor(1, 0, 0, 1);  // Red background. (RGBA)
		}
	}
	
	/**
	 * RELEASED: Fires once when a finger stops touching the screen.
	 * <p/>
	 * Note that when this happens, the index ID numbers for all the
	 * fingers about this one move down to fill in the gap left.
	 * <p/>
	 * ex.
	 * if there are 3 fingers, their IDs are 0, 1, and 2
	 * when finger 1 is removed, 2 becomes one. So now the IDs are 0, and 1
	 * but 1 is the finger that was previously 2.
	 * <p/>
	 * Test this out:
	 * The code in newpress() will turn the background red when a second finger is touched down.
	 * <p/>
	 * While still holding down the first finger, release the second one. This code
	 * should turn the background white.
	 * <p/>
	 * Now, turn background red again. This time, release the FIRST finger while still holding
	 * down the second. The bg is still red.
	 * <p/>
	 * Release the other finger, the bg stays red.
	 * <p/>
	 * When you released the first finger, "index" was 0. Then, the second finger took the
	 * first finger's place as 0. So when it was released "index" was still 0.
	 */
	@Override
	public void released(int index) {
		if (index == 1) {                             // Second finger was released
			getView().setBackgroundColor(1, 1, 1, 1); // White
		}
	}
}
