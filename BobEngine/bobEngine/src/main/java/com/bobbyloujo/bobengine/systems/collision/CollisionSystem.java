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

import com.bobbyloujo.bobengine.components.Transform;
import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.systems.quadrenderer.AnimatedGraphicAreaTransform;
import com.bobbyloujo.bobengine.systems.quadrenderer.GraphicAreaTransformation;
import com.bobbyloujo.bobengine.systems.quadrenderer.Quad;
import com.bobbyloujo.bobengine.systems.quadrenderer.QuadRenderSystem;

import java.util.ArrayList;

/**
 * A system for detecting collisions between objects. Transformables are used to
 * define hit boxes.
 *
 * Created by Benjamin on 11/24/2015.
 */
public class CollisionSystem implements Updatable {

	private static final int DEF_CELL_W = 1; // Default cellW
	private static final int DEF_CELL_H = 1; // Default cellH

	private int cellW;                       // Width of a cell on the cell grid.
	private int cellH;                       // Height of a cell on the cell grid.

	private ArrayList<HitBox> hitBoxes;      // All of the hit boxes in this system.
	private Entity parent;

	/**
	 * Create a new collision system.
	 */
	public CollisionSystem() {
		init();
	}

	/**
	 * Create a new collision system.
	 */
	public CollisionSystem(Entity parent) {
		parent.addComponent(this);
		this.parent = parent;
		init();
	}

	private void init() {
		hitBoxes = new ArrayList<HitBox>();
		cellW = DEF_CELL_W;
		cellH = DEF_CELL_H;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}

	public void showBoxes(boolean show) {
		if (parent != null) {
			QuadRenderSystem renderSystem = parent.getRoom().getQuadRenderSystem(new Graphic());
			renderSystem.setLayerColor(2, 1, 0, 0, 0.5f);

			if (show) {
				for (final HitBox h : hitBoxes) {
					final GraphicAreaTransformation graphic = new AnimatedGraphicAreaTransform();

					Quad q = new Quad() {
						@Override
						public Transformation getTransformation() {
							return h.c.getBoxTransformation();
						}

						@Override
						public GraphicAreaTransformation getGraphicAreaTransformation() {
							return graphic;
						}
					};

					renderSystem.addQuad(q);
				}
			} else {
				renderSystem.removeAllQuads();
			}
		}
	}

	/**
	 * Set the dimensions of a single cell on the cell grid. Larger cells means
	 * larger hit boxes. The smallest value is 1 which will cause cells to be
	 * one pixel. Default value is 1.
	 *
	 * @param cellW Width of a cell.
	 * @param cellH Height of a cell.
	 */
	public void setCellDim(int cellW, int cellH) {
		if (cellW < 1) cellW = 1;
		if (cellH < 1) cellH = 1;
		this.cellW = cellW;
		this.cellH = cellH;
	}

	/**
	 * Add each CollisionBox component of Entity e to this system.
	 * @param e an Entity with CollisionBox components.
	 */
	public void addEntity(Entity e) {
		ArrayList<CollisionBox> collisionBoxes = e.getComponentsOfType(CollisionBox.class);

		if (e instanceof CollisionBox) {
			addCollidable((CollisionBox) e);
		}

		for (CollisionBox c: collisionBoxes) {
			addCollidable(c);
		}
	}

	/**
	 * Add a new CollisionBox to this collision system.
	 * @param c The CollisionBox to add to this collision system.
	 */
	public void addCollidable(CollisionBox c) {
		hitBoxes.add(new HitBox(c));
	}

	@Override
	public void update(double deltaTime) {
		for (int i = 0; i < hitBoxes.size(); i++) {           // Update all the hit boxes.
			hitBoxes.get(i).update();
		}

		for (int i = 0; i < hitBoxes.size(); i++) {           // Find all the CollisionHandlers.
			HitBox h1 = hitBoxes.get(i);

			if (h1.c.getCollisionHandler() != null) {         // Is this hit box for a CollisionHandler?
				for (int j = 0; j < hitBoxes.size(); j++) {   // Compare hit box h1 to all other hit boxes.
					HitBox h2 = hitBoxes.get(j);

					if (h1.c != h2.c) {                       // Make sure it isn't the same CollisionBox!
						boolean collided = ((h2.y <= h1.y + h2.h) && (h2.y >= h1.y - h1.h)) && ((h2.x >= h1.x - h2.w) && (h2.x <= h1.x + h1.w));

						if (collided) {
							h1.c.getCollisionHandler().onCollision(h2.c);
						}
					}
				}
			}
		}
	}

	/**
	 * Generates a CollisionBox object (collision box, hit box) using a parent Transformation and a box defined by two points within the bounds
	 * of the parent Transformation. The left edge of the parent Transformation will 0 on the x axis, while the right edge will be 1. The
	 * bottom of the parent Transformation will be 0 on the y axis and the top will be 1. Therefor, a box defined by points (0,0) and (1,1)
	 * will be the full bounds of the parent Transformation.
	 *
	 * @param x1 The left edge of the collision box
	 * @param y1 The bottom edge of the collision box
	 * @param x2 The right edge of the collision box
	 * @param y2 The top edge of the collision box
	 * @param owner The Entity that this collision box belongs to.
	 * @param parent The parent Transformation that this box is based on.
	 * @param collisionHandler An optional CollisionHandler to handle collision events with this box. Can be null.
	 *
	 * @return A new CollisionBox.
	 */
	public static CollisionBox generateCollisionBox(final double x1, final double y1, final double x2, final double y2, final Entity owner, final Transformation parent, final CollisionHandler collisionHandler) {
		return new CollisionBox() {
			Transform t;

			{
				t = new Transform();
				t.parent = parent;
				t.x = (((x1 + x2) / 2) - .5) * parent.getWidth();
				t.y = (((1-y1 + 1-y2) / 2) - .5) * parent.getHeight();
				t.width = (x2 - x1) * parent.getWidth();
				t.height = (y2 - y1) * parent.getHeight();
			}

			@Override public Transformation getBoxTransformation() {
				double pw = parent.getWidth();
				double ph = parent.getHeight();

				t.x = (((x1 + x2) / 2) - .5) * pw;
				t.y = (((1-y1 + 1-y2) / 2) - .5) * ph;
				t.width = (x2 - x1) * pw;
				t.height = (y2 - y1) * ph;
				return t;
			}

			@Override public CollisionHandler getCollisionHandler() {
				return collisionHandler;
			}

			@Override public Entity getEntity() {
				return owner;
			}
		};
	}

	/**
	 * Generates a new CollisionBox using the provided Transformation for the bounds.
	 *
	 * @param transformation A Transformation defining the bounds of the CollisionBox
	 * @param collisionHandler A CollisionHandler, optional.
	 * @param owner The Entity that this box belongs to.
	 *
	 * @return A new CollisionBox
	 */
	public static CollisionBox generateCollisionBox(final Transformation transformation, final CollisionHandler collisionHandler, final Entity owner) {
		return new CollisionBox() {
			@Override public Transformation getBoxTransformation() {
				return transformation;
			}

			@Override public CollisionHandler getCollisionHandler() {
				return collisionHandler;
			}

			@Override public Entity getEntity() {
				return owner;
			}
		};
	}

	public static boolean checkPosition(CollisionBox c, double x, double y) {
		Transformation t = c.getBoxTransformation();

		int x1 = (int) (Transform.getRealX(t) - Math.abs(t.getWidth()) * Transform.getRealScale(t) / 2);
		int y1 = (int) (Transform.getRealY(t) + Math.abs(t.getHeight()) * Transform.getRealScale(t) / 2);

		int x2 = (int) (Transform.getRealX(t) + Math.abs(t.getWidth()) * Transform.getRealScale(t) / 2);
		int y2 = (int) (Transform.getRealY(t) - Math.abs(t.getHeight()) * Transform.getRealScale(t) / 2);

		return x >= x1 && x <= x2 && y <= y1 && y >= y2;
	}

	/**
	 * An instance of this class is generated for each CollisionBox in this system each frame.
	 * The information held in the class is used to determine if two Collidables have collided
	 * with each other.
	 */
	private class HitBox {
		CollisionBox c;    // The collidable from which this hit box was generated.

		int x; // The x position on the cell grid of the upper left corner of this box.
		int y; // The y position on the cell grid of the upper left corner of this box.
		int w; // The width in cells on the cell grid of this box.
		int h; // The width in cells on the cell grid of this box.

		/**
		 * Generates a new hit box.
		 * @param c
		 */
		public HitBox(CollisionBox c) {
			this.c = c;
		}

		/**
		 * Refreshes this hit box to reflect changes in c's position, dimensions, and scale.
		 */
		public void update() {
			Transformation t = c.getBoxTransformation();

			int x1 = (int) (Transform.getRealX(t) - Math.abs(t.getWidth()) * Transform.getRealScale(t) / 2) / cellW;
			int y1 = (int) (Transform.getRealY(t) + Math.abs(t.getHeight()) * Transform.getRealScale(t) / 2) / cellH;

			int x2 = (int) (Transform.getRealX(t) + Math.abs(t.getWidth()) * Transform.getRealScale(t) / 2) / cellW;
			int y2 = (int) (Transform.getRealY(t) - Math.abs(t.getHeight()) * Transform.getRealScale(t) / 2) / cellH;

			x = x1;
			y = y1;
			w = x2 - x1;
			h = y1 - y2;
		}
	}
}
