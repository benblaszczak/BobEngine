package com.bobbyloujo.touchinput;

import android.content.Context;
import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.R;

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
		icon = getGraphicsHelper().addGraphic(R.drawable.ic_launcher);
	}

	@Override
	protected void onCreateRooms() {
		start = new StartRoom(this);
		start.set();
		goToRoom(start);
	}

}
