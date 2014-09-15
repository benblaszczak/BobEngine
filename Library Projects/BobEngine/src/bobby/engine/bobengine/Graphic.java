package bobby.engine.bobengine;

/**
 * This class contains information about a graphic sheet.
 * 
 * @author Ben
 * @version alpha
 */
public class Graphic {
	// Data
	public float width;        // Total width of the sheet
	public float height;       // Total height of the sheet
	public int id;             // ID number of the sheet
	
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
	 * @param id     - Must be a unique numerical ID for this sheet.
	 */
	public Graphic (int height, int width, int id) {
		this.width = width;
		this.height = height;
		this.id = id;
	}
}
