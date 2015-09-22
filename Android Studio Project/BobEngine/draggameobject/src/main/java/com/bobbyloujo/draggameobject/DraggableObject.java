package com.bobbyloujo.draggameobject;

import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Graphic;
import bobby.engine.bobengine.Room;

/**
 * This is a GameObject that can be dragged with a finger.
 *
 * Created by Benjamin on 9/21/2015.
 */
public class DraggableObject extends GameObject {
	private int pointer = -1;  // The ID number of the pointer that is touching this object. This will be -1 if this object is not being touched.

	public DraggableObject(Room room) {
		super(room);

		Graphic graphic = getView().getGraphicsHelper().addGraphic(R.drawable.ic_launcher);
		setGraphic(graphic);

		x = room.getWidth() / 2;
		y = room.getHeight() / 2;
		height = room.getHeight() / 15;
		width = height;
	}

	@Override
	public void step(double dt) {
		if (pointer != -1 && getTouch().held(pointer)) {  // If the pointer touching this object is being held on the screen...
			x = getTouch().getX(pointer);                 // set the x...
			y = getTouch().getY(pointer);                 // and the y of this object to that of the pointer.
		}
	}

	@Override
	public void newpress(int i) {
		if (getTouch().objectTouched(i, this)) {     // If this object is being touched by the pointer that triggered this event...
			pointer = i;                             // set the pointer touching this object to the pointer that triggered this event.
		}
	}

	@Override
	public void released(int i) {
		if (i == pointer) {       // If the pointer that triggered this event is the pointer that is touching this object...
			pointer = -1;         // there is no longer a pointer touching the object so set pointer to -1 to indicate that this is not being touched.
		}
	}
}
