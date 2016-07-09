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
package com.bobbyloujo.bobengine.entities;

import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.systems.quadrenderer.AnimatedGraphicAreaTransform;
import com.bobbyloujo.bobengine.components.Transform;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.systems.quadrenderer.GraphicAreaTransformation;
import com.bobbyloujo.bobengine.systems.quadrenderer.Quad;
import com.bobbyloujo.bobengine.systems.quadrenderer.QuadRenderSystem;

/**
 * This lightweight Entity only has what is needed to output a graphic to the screen.
 *
 * Created by Benjamin on 11/20/2015.
 */
public class SimpleGameObject extends Entity implements Quad {
	/** Transform data for this object*/
	public Transform transform;
	/** Graphic sheet transform data for this object */
	public AnimatedGraphicAreaTransform graphic;

	// Private data
	private Graphic g;
	private QuadRenderSystem renderSystem;

	public SimpleGameObject() {
		super();

		transform = new Transform();
		graphic = new AnimatedGraphicAreaTransform();

		addComponent(transform);
		addComponent(graphic);

		// todo setGraphic(getView().getGraphicsHelper().getDefaultGraphic(), false);
	}

	public SimpleGameObject(Entity parent) {
		super(parent);

		transform = new Transform();
		graphic = new AnimatedGraphicAreaTransform();

		addComponent(transform);
		addComponent(graphic);

		setGraphic(getView().getGraphicsHelper().getDefaultGraphic(), false);
	}

	@Override
	public void onParentAssigned() {
		if (renderSystem == null || renderSystem.getGraphic() != g) {
			removeFromRenderer();
			renderSystem = getRoom().getQuadRenderSystem(g);
			renderSystem.addQuad(this);
		}
	}

	/**
	 * Get the QuadRenderSystem that is rendering this SimpleGameObject.
	 * @return The QuadRenderSystem that is rendering this SimpleGameObject if it exists, null otherwise.
	 */
	public QuadRenderSystem getRenderSystem() {
		return renderSystem;
	}

	/**
	 * Tell this SimpleGameObject which Graphic to use.
	 * @param graphic The Graphic!
	 */
	public void setGraphic(Graphic graphic) {
		setPreciseGraphic(graphic, 0, 0, 1, 1, 1, true);
	}

	/**
	 * Tell this SimpleGameObject which Graphic to use.
	 * @param graphic The Graphic!
	 * @param addToRenderer True if this object should be automatically added to a render. False if
	 *                      this object is going to be added to a renderer manually.
	 */
	public void setGraphic(Graphic graphic, boolean addToRenderer) {
		setPreciseGraphic(graphic, 0, 0, 1, 1, 1, addToRenderer);
	}

	/**
	 * Set the graphic for this object with a specific number of frames.
	 *
	 * @param graphic The Graphic object to use. This should be added to your GraphicsHelper.
	 * @param frames The number of frames this Graphic has.
	 */
	public void setGraphic(Graphic graphic, int frames) {
		setPreciseGraphic(graphic, 0, 0, 1, 1, frames, true);
	}

	/**
	 * Set the graphic for this object with a specific number of frames.
	 *
	 * @param graphic The Graphic object to use. This should be added to your GraphicsHelper.
	 * @param frames The number of frames this Graphic has.
	 * @param addToRenderer True if this object should be automatically added to a render. False if
	 *                      this object is going to be added to a renderer manually.
	 */
	public void setGraphic(Graphic graphic, int frames, boolean addToRenderer) {
		setPreciseGraphic(graphic, 0, 0, 1, 1, frames, addToRenderer);
	}

	/**
	 * Set the graphic for this object with a specific number of columns of frames.
	 *
	 * @param graphic The Graphic object to use. This should be added to your GraphicsHelper.
	 * @param columns The number of columns of frames
	 * @param rows The number of frames in a column
	 */
	public void setGraphic(Graphic graphic, int columns, int rows) {
		setPreciseGraphic(graphic, 0, 0, 1, 1f / (float) columns, rows, true);
	}

	/**
	 * Set the graphic for this object with a specific number of columns of frames.
	 *
	 * @param graphic The Graphic object to use. This should be added to your GraphicsHelper.
	 * @param columns The number of columns of frames
	 * @param rows The number of frames in a column
	 * @param addToRenderer True if this object should be automatically added to a render. False if
	 *                      this object is going to be added to a renderer manually.
	 */
	public void setGraphic(Graphic graphic, int columns, int rows, boolean addToRenderer) {
		setPreciseGraphic(graphic, 0, 0, 1, 1f / (float) columns, rows, addToRenderer);
	}

	/**
	 * Set the graphic for this object using a Parameters object.
	 * @param params Predefined parameter object
	 */
	public void setGraphic(Graphic.Parameters params) {
		setGraphic(params.graphic, params.x, params.y, params.height, params.width, params.rows, true);
	}

	/**
	 * Set the graphic for this object using a Parameters object.
	 * @param params Predefined parameter object
	 * @param addToRenderer True if this object should be automatically added to a render. False if
	 *                      this object is going to be added to a renderer manually.
	 */
	public void setGraphic(Graphic.Parameters params, boolean addToRenderer) {
		setGraphic(params.graphic, params.x, params.y, params.height, params.width, params.rows, addToRenderer);
	}

	/**
	 * Set graphic information for this object using pixel coordinates. Should only be used
	 * when there is only one drawable folder.
	 * <br/>
	 * <br/>
	 * NOTE: if a graphic has more than one frame, they should all be the same size and
	 * be stacked vertically in the same image file.
	 *
	 * @param graphic The graphic sheet to use.
	 * @param x The x coordinate of the graphic on the sheet, in pixels.
	 * @param y The y coordinate of the graphic on the sheet, in pixels.
	 * @param height The height of a single frame of the graphic on the sheet, in pixels.
	 * @param width The width of a single frame of the graphic on the sheet, in pixels.
	 * @param frameRows The number of rows of frames the graphic has.
	 * @param addToRenderer True if this object should automatically be given a render system.
	 */
	public void setGraphic(Graphic graphic, int x, int y, int height, int width, int frameRows, boolean addToRenderer) {
		removeComponent(this.graphic);
		this.graphic = new AnimatedGraphicAreaTransform(x, y, width, height, frameRows, graphic.width, graphic.height);
		addComponent(this.graphic);

		g = graphic;

		removeFromRenderer();

		if (getRoom() != null && addToRenderer) {
			renderSystem = getRoom().getQuadRenderSystem(graphic);
			renderSystem.addQuad(this);
		}
	}

	/**
	 * Set graphic information for this object with precise values for x, y,
	 * width, height. Use this when there is more than one drawable folder.
	 *
	 * @param graphic The graphic sheet to use.
	 * @param x The x coordinate of the graphic on the sheet, from 0 to 1.
	 * @param y The y coordinate of the graphic on the sheet, from 0 to 1.
	 * @param height The height of a single frame of the graphic on the sheet, from 0 to 1.
	 * @param width The width of a single frame of the graphic on the sheet, from 0 to 1.
	 * @param frameRows The number of frameRows the graphic has.
	 */
	public void setPreciseGraphic(Graphic graphic, float x, float y, float height, float width, int frameRows, boolean addToRenderer) {

		removeComponent(this.graphic);
		this.graphic = new AnimatedGraphicAreaTransform(x, y, width, height, frameRows);
		addComponent(this.graphic);

		g = graphic;

		removeFromRenderer();

		if (getRoom() != null && addToRenderer) {
			renderSystem = getRoom().getQuadRenderSystem(g);
			renderSystem.addQuad(this);
		}
	}

	/**
	 * Remove this game object from it's current render system. You will want to do this if you are deleting this
	 * object.
	 */
	public void removeFromRenderer() {
		if (renderSystem != null) {
			renderSystem.removeQuad(this);
		}
	}

	/**
	 * Determine if this SimpleGameObject is on the screen.
	 * @return True if this SimpleGameObject appears on screen, false otherwise.
	 */
	public boolean onScreen() {
		return transform.onScreen(getRoom());
	}

	@Override
	public Transformation getTransformation() {
		return transform;
	}

	@Override
	public GraphicAreaTransformation getGraphicAreaTransformation() {
		return graphic;
	}
}
