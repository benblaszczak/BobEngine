/**
 * BobEngine - 2D game engine for Android
 *
 * Copyright (C) 2014, 2015, 2016 Benjamin Blaszczak
 *
 * BobEngine is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser Public License
 * version 2.1 as published by the free software foundation.
 *
 * BobEngine is provided without warranty; without even the implied
 * warranty of merchantability or fitness for a particular
 * purpose. See the GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with BobEngine; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301 USA
 *
 */
package com.bobbyloujo.bobengine.systems.collision;

/**
 * A CollisionBox object that can handle collision events.
 *
 * Created by Benjamin on 11/25/2015.
 */
public interface CollisionHandler {
	/**
	 * This event is fired when this CollisionHandler collides with
	 * a CollisionBox object in the same CollisionSystem.
	 *
	 * @param c The CollisionBox object that this CollisionHandler collided with to trigger this event.
	 */
	void onCollision(CollisionBox c);
}
