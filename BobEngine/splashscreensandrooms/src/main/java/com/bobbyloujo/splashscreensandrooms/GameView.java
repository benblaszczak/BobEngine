package com.bobbyloujo.splashscreensandrooms;

import android.content.Context;

import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.graphics.Graphic;

/**
 * Created by Benjamin on 5/11/2015.
 */
public class GameView extends BobView {

	// rooms
	public static Room1 room1;
	public static Room2 room2;

	// graphics
	public static Graphic doorForRoom1;
	public static Graphic doorForRoom2;

	public GameView(Context context) {
		super(context);
	}

	@Override
	protected void onCreateGraphics() {
		doorForRoom1 = getGraphicsHelper().addGraphic(R.drawable.doorforroom1);
		doorForRoom2 = getGraphicsHelper().addGraphic(R.drawable.doorforroom2);
	}

	@Override
	protected void onCreateRooms() {
		room1 = new Room1(this);
		room2 = new Room2(this);

		goToRoom(room1);
	}
}
