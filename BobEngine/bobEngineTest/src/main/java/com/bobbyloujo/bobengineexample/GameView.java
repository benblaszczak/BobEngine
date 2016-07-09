package com.bobbyloujo.bobengineexample;

import android.content.Context;

import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.graphics.Graphic;

public class GameView extends BobView {

	// Textures
	public static Graphic icon;
	
	// Rooms
	private StartRoom start;
	
	public GameView(Context context) {
		super(context);
	}
	
	public void onCreateGraphics() {
		icon = getGraphicsHelper().getGraphic(R.drawable.ic_launcher);

		for (int i = 0; i < 10000; i++) {
			getGraphicsHelper().getGraphic(R.drawable.characters);
		}
	}
	
	public void onCreateRooms() {
		start = new StartRoom(this);
		start.set();
		goToRoom(start);
	}
}
