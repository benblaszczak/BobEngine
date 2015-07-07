package bobby.engine.touchinput;

import android.content.Context;
import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.Graphic;
import bobby.engine.bobengine.R;

public class GameView extends BobView {

	// Rooms
	public StartRoom start;
	
	// Graphics
	public static Graphic icon;
	
	public GameView(Context context) {
		super(context);

		start = new StartRoom(this);
	}

	@Override
	protected void onCreateGraphics() {
		icon = getGraphicsHelper().addGraphic(R.drawable.ic_launcher);
	}

	@Override
	protected void onCreateRooms() {
		start.set();
		goToRoom(start);
	}

}
