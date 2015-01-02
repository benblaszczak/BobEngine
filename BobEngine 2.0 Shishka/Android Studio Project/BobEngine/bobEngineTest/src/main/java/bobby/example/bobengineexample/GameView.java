package bobby.example.bobengineexample;

import android.content.Context;
import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.Graphic;

public class GameView extends BobView {

	// Textures
	public static Graphic android;
	
	// Rooms
	private StartRoom start;
	
	public GameView(Context context) {
		super(context);
	}
	
	public void onCreateGraphics() {
		android = getGraphicsHelper().addGraphic(R.drawable.ic_launcher);
	}
	
	public void onCreateRooms() {
		start = new StartRoom(this);
		
		goToRoom(start);
	}
}
