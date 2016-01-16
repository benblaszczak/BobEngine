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

import com.bobbyloujo.bobengine.components.Component;
import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.Entity;

/**
 * Labels an object as collidable, thus having a hit box defined
 * by a Transformation and can be added to a CollisionSystem to trigger
 * collision events.
 *
 * Created by Benjamin on 11/25/2015.
 */
public interface CollisionBox extends Component {
	/**
	 * Should return the Transformation that is used to define the hit
	 * box of this CollisionBox object.
	 * @return A Transformation defining a hit box.
	 */
	Transformation getBoxTransformation();

	/**
	 * If this CollisionBox has a CollisionHandler to handle collision events
	 * with other Collidables, it should be returned. Otherwise, return null.
	 * @return This CollisionBox's CollisionHandler or null.
	 */
	CollisionHandler getCollisionHandler();

	/**
	 * May be used to return the Entity that this CollisionBox component belongs
	 * to if needed. Otherwise, can return null.
	 * @return This CollisionBox's parent Entity or null.
	 */
	Entity getEntity();
}
