package bobby.engine.template;

import android.content.Context;
import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.Graphic;
import bobby.engine.bobengine.R;

/**
 * This is our view that we will use to display BobEngine content. The view will know what content
 * to display by using rooms. Rooms are collections of game objects such as players, chests,
 * balloons, etc.
 *
 * BobViews are a great place to setup your rooms and add your graphics so that they can be used
 * by game objects.
 */
public class GameView extends BobView {

	// Rooms
	public StartRoom start;
	
	// Graphics
	public static Graphic icon;

	public GameView(Context context) {
		super(context);

		// TODO Initialization

		start = new StartRoom(this);
	}

	@Override
	protected void onCreateGraphics() {
		// TODO Add graphics
		
		icon = getGraphicsHelper().addGraphic(R.drawable.ic_launcher);
	}

	@Override
	protected void onCreateRooms() {
		// TODO Set up rooms

		start.set();
		goToRoom(start);
	}

}
