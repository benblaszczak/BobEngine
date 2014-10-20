/**
 * BobEngine - 2D game engine for Android
 * 
 * Copyright (C) 2014 Benjamin Blaszczak
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

package bobby.engine.bobengine;

/**
 * This class contains information about a graphic sheet.
 * 
 * @author Ben
 * @version alpha
 */
public class Graphic {
	// Data
	public float width;         // Total width of the sheet
	public float height;        // Total height of the sheet
	public int drawable;        // Android drawable
	public int id;              // OpenGL ID number of the sheet
	public int magFilter;       // OpenGL upscale filter
	public int minFilter;       // OpenGL downscale filter
	public boolean useMipMaps;  // OpenGL use mipmaps
	
	/**
	 * Set up a default texture.
	 */
	public Graphic () {
		width = 1;
		height = 1;
		id = 0;
	}
	
	/**
	 * Sets up a texture with specific values.
	 * 
	 * @param width  - The total width of the sheet in pixels.
	 * @param height - The total height of the sheet in pixels.
	 * @param minFilter - OpenGL downscale filter. (eg. GL11.GL_NEAREST)
	 * @param magFilter - OpenGL upscale filter. (eg. GL11.GL_NEAREST)
	 * @param useMipMaps - Should this texture use mipmaps. true for better performance.
	 * @param id     - Must be a unique numerical ID for this sheet.
	 */
	public Graphic (int drawable, int height, int width, int minFilter, int magFilter, boolean useMipMaps, int id) {
		this.drawable = drawable;
		this.width = width;
		this.height = height;
		this.id = id;
		this.magFilter = magFilter;
		this.minFilter = minFilter;
		this.useMipMaps = useMipMaps;
	}
}
