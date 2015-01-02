/**
 * BobEngine - 2D game engine for Android
 * 
 * Copyright (C) 2014, 2015 Benjamin Blaszczak
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
	private boolean isLoaded;   // Flag that indicates if this graphic is loaded
	public Command c;           // Tells the graphic helper what to do next
	
	public enum Command {
		DO_NOTHING,
		LOAD,
		UNLOAD
	}
	
	/**
	 * Set up a default texture.
	 */
	public Graphic () {
		width = 1;
		height = 1;
		id = 0;
		isLoaded = false;
		c = Command.DO_NOTHING;
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
		isLoaded = false;
		c = Command.DO_NOTHING;
	}
	
	/**
	 * @return True if this graphic is loaded and ready to use. False otherwise.
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	
	/**
	 * Commands can be DO_NOTHING, LOAD, or UNLOAD.
	 * 
	 * @return The command that this graphic is waiting to be executed.
	 */
	public Command getCommand() {
		return c;
	}
	
	/**
	 * Tell the engine to load this graphic. The graphic is not loaded immediately.
	 * When the graphic has been loaded, isLoaded() will return true.
	 */
	public void load() {
		c = Command.LOAD;
	}
	
	/**
	 * Tell the engine to unload this graphic. The graphic is not unloaded immediately.
	 * When the graphic is unloaded, isLoaded() will return false.
	 */
	public void unload() {
		c = Command.UNLOAD;
	}
	
	/**
	 * Signify that the current command has been finished.
	 */
	public void finished() {
		if (c == Command.LOAD) isLoaded = true;
		else if (c == Command.UNLOAD) isLoaded = false;
		
		c = Command.DO_NOTHING;
	}
}
