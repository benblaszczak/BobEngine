package com.bobbyloujo.bobengine.systems.quadrenderer;

import com.bobbyloujo.bobengine.components.Component;

/**
 * An interface for a Component that should contain data defining a
 * rectangular area on an image.
 *
 * Created by Benjamin on 11/14/2015.
 */
public interface GraphicAreaTransformation extends Component {
	/**
	 * Should return the X position of the left side of the area
	 * defined by this GraphicTransformable as a portion of the
	 * total width of the image (from 0 to 1).
	 * @return The x position of the area.
	 */
	float getGraphicX();

	/**
	 * Should return the Y position of the top side of the area
	 * defined by this GraphicTransformable as a portion of the
	 * total height of the image (from 0 to 1).
	 * @return The y position of the area.
	 */
	float getGraphicY();

	/**
	 * Should return the width of the area defined by this
	 * GraphicTransformable as a portion of the total width of
	 * the image (from 0 to 1).
	 * @return The width of the area.
	 */
	float getGraphicWidth();

	/**
	 * Should return the height of the area defined by this
	 * GraphicTransformable as a portion of the total height of
	 * the image (from 0 to 1).
	 * @return The height of the area.
	 */
	float getGraphicHeight();
}
