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
package com.bobbyloujo.bobengine.graphics;

/**
 * This class contains information about a graphic sheet.
 * 
 * @author Ben
 * @version alpha
 */
public class Graphic {

	// Data
	public int width;           // Total width of the sheet
	public int height;          // Total height of the sheet
	public int drawable;        // Android drawable
	public int id;              // OpenGL texture ID number of the sheet
	public int magFilter;       // OpenGL upscale filter
	public int minFilter;       // OpenGL downscale filter
	public boolean useMipMaps;  // OpenGL use mipmaps
	public boolean persistent;  // Indicates whether this graphic can be during a cleanup.

	private boolean isLoaded;   // Flag that indicates if this graphic is loaded

	private int cleanupsTilRemoval;  // Number of GraphicsHelper.cleanup() calls that this Graphic should persist through
	private boolean shouldBeLoaded;  // Flag that indicates this Graphic should be loaded.
	private boolean shouldRemove;    // Flag that indicates this Graphic should be removed from the GraphicsHelper.

	/**
	 * Set up a default texture.
	 */
	public Graphic () {
		width = 1;
		height = 1;
		id = 0;
		isLoaded = false;

		cleanupsTilRemoval = GraphicsHelper.DEF_CLEANUPS;
		shouldBeLoaded = false;
		shouldRemove = false;
		persistent = false;
	}
	
	/**
	 * Sets up a texture with specific values.
	 * 
	 * @param width        The total width of the sheet in pixels.
	 * @param height       The total height of the sheet in pixels.
	 * @param minFilter    OpenGL downscale filter. (eg. GL11.GL_NEAREST)
	 * @param magFilter    OpenGL upscale filter. (eg. GL11.GL_NEAREST)
	 * @param useMipMaps   Should this texture use mipmaps. true for better performance.
	 */
	public Graphic (int drawable, int height, int width, int minFilter, int magFilter, boolean useMipMaps) {
		this.drawable = drawable;
		this.width = width;
		this.height = height;
		this.magFilter = magFilter;
		this.minFilter = minFilter;
		this.useMipMaps = useMipMaps;
		isLoaded = false;

		cleanupsTilRemoval = GraphicsHelper.DEF_CLEANUPS;
		shouldBeLoaded = false;
		shouldRemove = false;
		persistent = false;
	}

	/**
	 * Set the dimensions (resolution) of this graphic.  </br></br>
	 *
	 * BobEngine will try to set the dimensions automatically when the graphic is added,
	 * but if you are using more than one drawable folder (for different pixel densities)
	 * you may not know which image BobEngine chooses. If you want to use
	 * setGraphic(Graphic g, int x, int y, int height, int width, int frames) for using
	 * part of a graphic based on pixel coordinates you can set the dimensions of the image
	 * you want to base your coordinates off of with this method.
	 *
	 * @param width
	 * @param height
	 */
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Tell the engine to load this graphic. The graphic is not loaded immediately.
	 * When the graphic has been loaded, isLoaded() will return true.
	 */
	public void load() {
		shouldBeLoaded = true;
	}

	/**
	 * Tell the engine to unload this graphic. The graphic is not unloaded immediately.
	 * When the graphic is unloaded, isLoaded() will return false.
	 */
	public void unload() {
		shouldBeLoaded = false;
	}

	public void indicateUsed(int cleanupsTilRemoval) {
		this.cleanupsTilRemoval = cleanupsTilRemoval;
	}

	/**
	 * If this graphic has not been recently used and cleanup() has been called
	 * enough times since it has been used (# determined by the GraphicsHelper)
	 * this graphic will be unloaded and removed from the GraphicsHelper.
	 */
	public void cleanup() {
		if (cleanupsTilRemoval == 0) {
			remove();
		} else if (!persistent) {
			cleanupsTilRemoval--;
		}
	}

	/**
	 * Forces a cleanup regardless of the number of times this Graphic has been
	 * through a cleanup.
	 */
	public void forceCleanup() {
		cleanupsTilRemoval = 0;
		remove();
	}

	/**
	 * Indicate that this graphic should be unloaded and removed from the GraphicsHelper
	 */
	public void remove() {
		shouldRemove = true;
	}

	/**
	 * @return True if this graphic is loaded and ready to use. False otherwise.
	 */
	public boolean isLoaded() {
		return isLoaded;
	}

	/**
	 * Determine if this graphic should be loaded.
	 * @return true if this graphic should be loaded but hasn't been, false otherwise.
	 */
	public boolean shouldLoad() {
		return shouldBeLoaded && !isLoaded();
	}

	/**
	 * Determine if this graphic should be unloaded.
	 * @return true if this graphic should be unloaded but is still loaded, false otherwise
	 */
	public boolean shouldUnload() {
		return !shouldBeLoaded && isLoaded();
	}

	/**
	 * Determine if this graphic should be removed from the GraphicHelper.
	 * @return true if this graphic should be removed, false otherwise
	 */
	public boolean shouldRemove() {
		return shouldRemove;
	}
	
	/**
	 * Signify that the current command has been finished.
	 */
	public void finished() {
		isLoaded = shouldBeLoaded;
	}

	/**
	 * Signify that this graphic has successfully been loaded.
	 */
	public void loaded() {
		isLoaded = true;
	}

	/**
	 * Signify that this graphic has been successfully unloaded.
	 */
	public void deleted() {
		isLoaded = false;
	}

	/**
	 * Signify that this graphic has been removed from the GraphicsHelper.
	 */
	public void removed() {
		shouldRemove = false;
	}

	/**
	 * This class allows you to predefine parameters for GameObject.setGraphic(...).
	 */
	public static class Parameters {
		public Graphic graphic;
		public int x;
		public int y;
		public int height;
		public int width;
		public int rows;

		public Parameters(Graphic graphic, int x, int y, int height, int width, int frames) {
			setParameters(graphic, x, y, height, width, frames);
		}

		public Parameters(Graphic graphic, int columns, int rows) {
			setParameters(graphic, 0, 0, graphic.height, graphic.width / columns, rows);
		}

		public Parameters(Graphic graphic, int frames) {
			setParameters(graphic, 0, 0, graphic.height, graphic.width, frames);
		}

		private void setParameters(Graphic graphic, int x, int y, int height, int width, int frames) {
			this.graphic = graphic;
			this.x = x;
			this.y = y;
			this.height = height;
			this.width = width;
			this.rows = frames;
		}
	}

	@Override
	public boolean equals(Object o) {
		boolean equal = false;

		if (o instanceof Graphic) {
			if (((Graphic) o).id == id &&
			((Graphic) o).drawable == drawable &&
			((Graphic) o).width == width &&
			((Graphic) o).height == height &&
			((Graphic) o).magFilter == magFilter &&
			((Graphic) o).minFilter == minFilter &&
			((Graphic) o).useMipMaps == useMipMaps) {
				equal = true;
			}
		}

		return equal;
	}
}
