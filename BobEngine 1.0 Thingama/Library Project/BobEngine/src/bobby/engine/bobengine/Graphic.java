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
