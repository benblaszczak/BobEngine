package bobby.example.bobengineexample;

import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Room;
import bobby.engine.bobengine.SoundPlayer;

public class Android extends GameObject {
	// Constants
	private final int FRAMES = 1;

	// variables
	private int dX;
	private int dY;
	private int speed = 10;

	private SoundPlayer sounds;
	private int POP;

	// On create
	public Android(int id, Room containingRoom) {
		super(id, containingRoom);

		sounds = new SoundPlayer(getView().getContext());
		POP = sounds.newSound(R.raw.pop);

		setGraphic(GameView.android, FRAMES);
	}

	// Set/Reset
	public void set(double x, double y, double size, int layer) {
		this.x = x;
		this.y = y;
		this.layer = layer;

		height = myRoom.getHeight() / size;
		width = height;

        //getRoom().setCameraX(0);
        //getRoom().setCameraY(0);
        //getRoom().setCameraZoom(2);
        //getRoom().setCameraAnchor(getRoom().getWidth() / 2, getRoom().getHeight() / 2);

		giveCollisionBox(0.1, 0.1, .9, .9);

		dX = 1;
		dY = -1;

		frame = 0;
	}

	// Step
	@Override
	public void step(double deltaTime) {
		x += dX * speed * deltaTime;
		y += dY * speed * deltaTime;

		if (x > myRoom.getWidth() - width / 2) {
			dX = -dX;
			x = myRoom.getWidth() - width / 2;
		}

		if (x < -width) {
			dX = -dX;
			x = -width;
		}

		if (y > myRoom.getHeight() - height / 2) {
			dY = -dY;
			y = myRoom.getHeight() - height / 2;
		}

		if (y < height / 2) {
			dY = -dY;
			y = height / 2;
		}

		super.step(deltaTime);
	}

	@Override
	public void newpress(int index) {

		if (getTouch().objectTouched(this)) {
			sounds.play(POP);
		}
	}

	/**
	 * The following 3 method overrides show how you can intercept the methods that pass information about
	 * this object to OpenGL and use concatenate() to display this game object using more than one quad.
	 *
	 * It would help to already have an understanding of how OpenGL works before trying to understand what
	 * is happening here.
	 */
	/*@Override
	public float[] getVertices() {

		// The first quad will be shifted to the left by the width of the object.
		updatePosition(x - width, y);                 // This updates this objects vertices at the specified position.
		float[] first = super.getVertices().clone();  // Get the updated vertices.

		// The second quad is at (x, y)
		updatePosition(x, y);
		float[] second = super.getVertices().clone();

		// Combine them!
		float[] both = concatenate(first, second);

		// Return the combined vertices of both quads.
		return both;
	}

	@Override
	public float[] getGraphic() {
		// In getVertices() above, we generated vertices for two quads. We need to match
		// the number of position vertices with the number of texture (graphic) vertices.

		// NOTE: You can use different parts of the same texture (graphic) for both quads,
		// but you can't use different textures for each quad. You'll have to use separate
		// game objects for separate textures.

		// Here we just use two of the same sets of graphics vertices.
		float[] two = concatenate(super.getGraphic(), super.getGraphic());

		return two;
	}

	@Override
	public int getIndices() {
		// Each quad has 6 indices and we have 2 quads so we need to return 12 indices.
		return 12;
	}*/
}
