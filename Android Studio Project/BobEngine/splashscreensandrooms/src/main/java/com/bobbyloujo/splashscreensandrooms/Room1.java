package com.bobbyloujo.splashscreensandrooms;

import android.content.Intent;

import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Room;

/**
 * Created by Benjamin on 5/11/2015.
 */
public class Room1 extends Room {

	GameObject door;

	public Room1(BobView container) {
		super(container);

		door = new GameObject(nextInstance(), this);
		door.setGraphic(GameView.doorForRoom1);
		door.x = getWidth() / 2;
		door.y = getHeight() / 2;
		door.width = getWidth() / 2;
		door.height = door.width * 2;

		addObject(door);
	}

	@Override
	public void newpress(int index) {
		super.newpress(index);

		if (getTouch().objectTouched(door)) {
			Intent switching = new Intent(getActivity().getApplicationContext(), SplashScreens2.class);
			getActivity().startActivity(switching);

			getView().goToRoom(GameView.room2);
		}
	}
}
