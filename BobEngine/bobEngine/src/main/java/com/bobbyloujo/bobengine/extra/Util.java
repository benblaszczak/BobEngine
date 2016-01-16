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
package com.bobbyloujo.bobengine.extra;

import com.bobbyloujo.bobengine.components.Transform;
import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.GameObject;
import com.bobbyloujo.bobengine.entities.SimpleGameObject;

/**
 * This Utility class provides many useful functions for games.
 *
 * Created by Benjamin on 11/25/2015.
 */
public class Util {

	/* FINDING DISTANCE */

	/**
	 * Gets the distance between two SimpleGameObjects.
	 *
	 * @param ob1 GameObject 1
	 * @param ob2 GameObject 2
	 * @return Distance between ob1 and ob2, in pixels.
	 */
	public static double getDistanceBetween(SimpleGameObject ob1, SimpleGameObject ob2) {
		return getDistanceBetween(ob1.transform, ob2.transform);
	}

	/**
	 * Gets the distance between two SimpleGameObjects, squared. If you need to find the
	 * distance often, sometimes the distance squared can be used instead which
	 * saves computation time.
	 *
	 * @param ob1 GameObject 1
	 * @param ob2 GameObject 2
	 * @return Distance between ob1 and ob2, squared, in pixels.
	 */
	public static double getDistanceBetweenSquared(SimpleGameObject ob1, SimpleGameObject ob2) {
		return getDistanceBetweenSquared(ob1.transform, ob2.transform);
	}

	/**
	 * Gets the distance between two Transformables.
	 *
	 * @param t1 Transformation 1
	 * @param t2 Transformation 2
	 * @return Distance between t1 and t2, in pixels.
	 */
	public static double getDistanceBetween(Transformation t1, Transformation t2) {
		return Math.sqrt(getDistanceBetweenSquared(t1, t2));
	}

	/**
	 * Gets the distance between two Transformables, squared. If you need to find the
	 * distance often, sometimes the distance squared can be used instead which
	 * saves computation time.
	 *
	 * @param t1 Transformation 1
	 * @param t2 Transformation 2
	 * @return Distance between t1 and t2, squared, in pixels.
	 */
	public static double getDistanceBetweenSquared(Transformation t1, Transformation t2) {
		return Math.pow(Transform.getRealX(t1) - Transform.getRealX(t2), 2) + Math.pow(Transform.getRealY(t1) - Transform.getRealY(t2), 2);
	}

	/**
	 * Gets the distance between two points.
	 *
	 * @return Distance between (x1, y1) and (x2, y2)
	 */
	public static double getDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * Gets the distance between two points *squared*. If you need to find the
	 * distance often, sometimes the distance squared can be used instead which
	 * saves computation time.
	 *
	 * @return Distance between (x1, y1) and (x2, y2)
	 */
	public static double getDistanceSquared(int x1, int y1, int x2, int y2) {
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
	}

	/* FINDING ANGLES */

	/**
	 * Find the angle between two game objects.
	 *
	 * @param ob1 first object
	 * @param ob2 second object
	 * @return The angle between ob1 and ob2
	 */
	public static double getAngleBetween(GameObject ob1, GameObject ob2) {
		return getAngle(ob1.x, ob1.y, ob2.x, ob2.y);
	}

	/**
	 * Find the angle between two simple game objects.
	 *
	 * @param ob1 first object
	 * @param ob2 second object
	 * @return The angle between ob1 and ob2
	 */
	public static double getAngleBetween(SimpleGameObject ob1, SimpleGameObject ob2) {
		return getAngle(ob1.transform.x, ob1.transform.y, ob2.transform.x, ob2.transform.y);
	}

	/**
	 * Find the angle between two transforms.
	 *
	 * @param t1 first object
	 * @param t2 second object
	 * @return The angle between t1 and t2
	 */
	public static double getAngleBetween(Transform t1, Transform t2) {
		return getAngle(t1.x, t1.y, t2.x, t2.y);
	}

	/**
	 * Find the angle between two points.
	 *
	 * @return The angle between (x1, y1) and (x2, y2) in radians
	 */
	public static double getAngle(double x1, double y1, double x2, double y2) {
		if (x1 < x2) return Math.atan((y1 - y2) / (x1 - x2));
		else return Math.atan((y1 - y2) / (x1 - x2)) + Math.PI;
	}
}
