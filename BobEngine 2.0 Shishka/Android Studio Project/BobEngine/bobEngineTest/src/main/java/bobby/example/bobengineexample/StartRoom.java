package bobby.example.bobengineexample;

import java.util.Random;

import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.NumberDisplay;
import bobby.engine.bobengine.Room;

public class StartRoom extends Room {

	// Game objects
	private Android[] droid;
	private NumberDisplay num;

	// Other objects
	private Random rand;

	// Constants
	private final int NUM_DROIDS = 5;   // Number of bouncing icons (not actually droids anymore, whatever)
	private final double DROID_SIZE = 10; // Size... sort of. Bigger number = smaller bouncing icons

	StartRoom(BobView container) {
		super(container);

		rand = new Random();
		droid = new Android[NUM_DROIDS];
		num = new NumberDisplay(nextInstance(), this);

		for (int i = 0; i < NUM_DROIDS; i++) {
			droid[i] = new Android(nextInstance(), this);
		}

		set();

		for (int i = 0; i < NUM_DROIDS; i++) {
			addObject(droid[i]);
		}
		
		addObject(num);
	}

	public void set() {
		for (int i = 0; i < NUM_DROIDS; i++) {
			droid[i].set(rand.nextInt(getWidth()), rand.nextInt(getHeight()), DROID_SIZE, 1);
		}
		
		num.set(500, 1000, 1f / 6f, 1);
		num.setAfterKerning(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		num.setBeforeKerning(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Override
	public void step(double deltaTime) {
		getView().setBackgroundColor(1, 1, 1, 0);
		
		for (int x = 0; x < NUM_DROIDS; x++) {
			for (int y = 0; y < NUM_DROIDS; y++) {
				// Uncomment these lines for collision detection
				//if (x != y && checkCollision(droid[x], droid[y])) {
				//	getView().setBackgroundColor(0, 0, 1, 1);
				//}
			}
		}
		
		num.setNumber((int) ((60.0 / deltaTime) * 10));
		num.x = (getWidth() / 2) + num.getWidth() / 2;

		super.step(deltaTime);
	}
}
