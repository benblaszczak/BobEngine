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
package com.bobbyloujo.bobengine.components;

import com.bobbyloujo.bobengine.entities.Room;

/**
 * A simple implementation of Transformation. Provides some extra
 * functionality such as a method that determines if this Transform
 * is in a position that would appear on the screen.
 * <br />
 * <br />
 * This class also contains some static utility methods for finding
 * information about Transformations.
 *
 * Created by Benjamin on 9/25/2015.
 */
public class Transform implements Transformation {

	public double x = 0;
	public double y = 0;
	public double width = 0;
	public double height = 0;
	public double angle = 0;
	public double scale = 1;
	public int layer = 2;
	public boolean visible = true;
	public boolean followCamera = false;

	public Transformation parent = null;

	public Transform() {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
		angle = 0;
		scale = 1;
		layer = 2;
		visible = true;
		followCamera = false;
	}

	/**
	 * Determine if the area defined by this Transform would be visible on the
	 * screen in the specified Room.
	 *
	 * @param room The Room to use to determine if this Transform is on the screen.
	 * @return true if this Transform appears on the screen, false otherwise.
	 */
	public boolean onScreen(Room room) {
		double width = Math.abs(this.width);

		double screenLeft = room.getCameraLeftEdge();
		double screenRight = room.getCameraRightEdge();
		double screenTop = room.getCameraTopEdge();
		double screenBottom = room.getCameraBottomEdge();

		if (followCamera) {
			screenLeft = 0;
			screenRight = room.getViewWidth();
			screenBottom = 0;
			screenTop = room.getViewHeight();
		}

		if (x > -width / 2 + screenLeft && x < width / 2 + screenRight) {
			if (y > -height / 2 + screenBottom && y < height / 2 + screenTop) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the real X position after all parent transformations have been
	 * applied.
	 *
	 * @return The real X position after all parent transformations have been
	 * applied.
	 */
	public double getRealX() {
		return getRealX(this);
	}

	/**
	 * Returns the real Y position after all parent transformations have been
	 * applied.
	 *
	 * @return The real Y position after all parent transformations have been
	 * applied.
	 */
	public double getRealY() {
		return getRealY(this);
	}

	/**
	 * Returns the real angle after all parent transformations have been
	 * applied.
	 *
	 * @return The real angle after all parent transformations have been
	 * applied.
	 */
	public double getRealAngle() {
		return getRealAngle(this);
	}

	/**
	 * Returns the real scale after all parent transformations have been
	 * applied.
	 *
	 * @return The real scale after all parent transformations have been
	 * applied.
	 */
	public double getRealScale() {
		return getRealScale(this);
	}

	/**
	 * Returns the real visibility after all parent transformations have been
	 * applied.
	 *
	 * @return The real visibility after all parent transformations have been
	 * applied.
	 */
	public boolean getRealVisibility() {
		return getRealVisibility(this);
	}

	/* TRANSFORMATION METHODS */

	@Override public Transformation getParent() {
		return parent;
	}

	@Override public double getX() {
		return x;
	}

	@Override public double getY() {
		return y;
	}

	@Override public double getAngle() {
		return angle;
	}

	@Override public double getWidth() {
		return width;
	}

	@Override public double getHeight() {
		return height;
	}

	@Override public double getScale() {
		return scale;
	}

	@Override public int getLayer() {
		return layer;
	}

	@Override public boolean getVisibility() {
		return visible;
	}

	@Override public boolean shouldFollowCamera() {
		return followCamera;
	}

	/* STATIC UTILITY METHODS */

	/**
	 * Returns the real X position of t after all parent transformations have been
	 * applied.
	 *
	 * @param t The Transformation for which to find the real X position
	 * @return The real X position of t after all parent transformations have been
	 * applied.
	 */
	public static double getRealX(Transformation t) {
		Transformation parent = t.getParent();
		double x = t.getX();
		double y = t.getY();

		while (parent != null) {
			double cos = Math.cos(Math.toRadians(parent.getAngle()));
			double sin = Math.sin(Math.toRadians(parent.getAngle()));

			x *= parent.getScale();
			y *= parent.getScale();

			double oX = x;
			double oY = y;

			x = oX * cos - oY * sin;

			x += parent.getX();
			y += parent.getY();
			parent = parent.getParent();
		}

		return x;
	}

	/**
	 * Returns the real Y position of t after all parent transformations have been
	 * applied.
	 *
	 * @param t The Transformation for which to find the real Y position
	 * @return The real Y position of t after all parent transformations have been
	 * applied.
	 */
	public static double getRealY(Transformation t) {
		Transformation parent = t.getParent();
		double x = t.getX();
		double y = t.getY();

		while (parent != null) {
			double cos = Math.cos(Math.toRadians(parent.getAngle()));
			double sin = Math.sin(Math.toRadians(parent.getAngle()));

			x *= parent.getScale();
			y *= parent.getScale();

			double oX = x;
			double oY = y;

			y = oX * sin + oY * cos;

			x += parent.getX();
			y += parent.getY();
			parent = parent.getParent();
		}

		return y;
	}

	/**
	 * Returns the real angle of t after all parent transformations have been
	 * applied.
	 *
	 * @param t The Transformation for which to find the real angle
	 * @return The real angle of t after all parent transformations have been
	 * applied.
	 */
	public static double getRealAngle(Transformation t) {
		Transformation parent = t.getParent();
		double angle = t.getAngle();

		while (parent != null) {
			angle += parent.getAngle();
			parent = parent.getParent();
		}

		return angle;
	}

	/**
	 * Returns the real scale of t after all parent transformations have been
	 * applied.
	 *
	 * @param t The Transformation for which to find the real scale
	 * @return The real scale of t after all parent transformations have been
	 * applied.
	 */
	public static double getRealScale(Transformation t) {
		Transformation parent = t.getParent();
		double scale = t.getScale();

		while (parent != null) {
			scale *= parent.getScale();
			parent = parent.getParent();
		}

		return scale;
	}

	/**
	 * Returns the real visibility of t after all parent transformations have been
	 * applied.
	 *
	 * @param t The Transformation for which to find the real visibility
	 * @return The real visibility of t after all parent transformations have been
	 * applied.
	 */
	public static boolean getRealVisibility(Transformation t) {
		Transformation parent = t.getParent();

		while (parent != null) {
			if (!parent.getVisibility()) {
				return false;
			}

			parent = parent.getParent();
		}

		return t.getVisibility();
	}
}
