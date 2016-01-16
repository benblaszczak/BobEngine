package com.bobbyloujo.draggameobject;

import android.app.Activity;
import android.os.Bundle;

import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.entities.Room;

/**
 * This example shows how you can make a GameObject that can be dragged with
 * a finger.
 *
 * Created by Benjamin on 9/21/2015.
 */
public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BobView view = new BobView(this) {
			@Override protected void onCreateGraphics() {

			}

			@Override protected void onCreateRooms() {
				Room room = new Room(this);
				DraggableObject objectToDrag = new DraggableObject(room);

				goToRoom(room);
			}
		};

		setContentView(view);
	}
}
