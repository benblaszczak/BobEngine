package com.bobbyloujo.template;

import android.content.Context;
import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.R;

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
	}

	@Override
	protected void onCreateGraphics() {
		// TODO Add graphics
		
		icon = getGraphicsHelper().addGraphic(R.drawable.ic_launcher);
	}

	@Override
	protected void onCreateRooms() {
		// TODO Set up rooms
		start = new StartRoom(this);

		start.set();
		goToRoom(start);
	}

}
