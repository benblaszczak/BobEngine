package bobby.engine.bobengine;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * This class helps load graphics. Every BobView has it's own GraphicsHelper.
 * Use getGraphicsHelper() in a BobView to get it's GraphicsHelper.
 * 
 * @author Ben
 *
 */
public class GraphicsHelper {

	// Constants
	private int START_NUM_TEX = 20;                  // Starting maximum number of textures (graphics)

	// Variables
	private int numGFX;                              // Number of loaded graphics
	private int[] graphics;                          // Textures as drawables
	private boolean newGraphics;                     // Flag for when there are new graphics to load.

	// Object
	private Context context;

	public GraphicsHelper(Context context) {
		this.context = context;

		numGFX = 0;
		graphics = new int[START_NUM_TEX];
	}

	/**
	 * Add a texture to the list of usuable textures.
	 * 
	 * @param drawable
	 *            - Drawable resource in R.drawable.&#42;
	 * @return A Texture object containing information about the newly created
	 *         texture. Store this somewhere where it can be accessed by
	 *         GameObjects (Like as a static property in a BobView).
	 */
	public Graphic addGraphic(int drawable) {
		// Data
		int graphic;

		graphic = numGFX;
		numGFX++;

		if (graphic > graphics.length) {                           // Hit max graphics, need to expand graphics[]
			int[] temp = graphics;
			graphics = new int[graphics.length + START_NUM_TEX];

			for (int i = 0; i < temp.length; i++) {
				graphics[i] = temp[i];
			}
		}

		graphics[graphic] = drawable;

		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), drawable);
		
		newGraphics = true;

		return new Graphic(bmp.getHeight(), bmp.getWidth(), graphic);
	}

	/**
	 * Load all of the added textures into openGL.
	 * 
	 * @param gl
	 */
	public void loadAllTextures(GL11 gl) {
		// Data
		Bitmap bmp;     // The bitmap to be bound to thisTexture

		for (int t = 0; t < numGFX; t++) {
			// Get resource
			InputStream is = context.getResources().openRawResource(graphics[t]);
			try {
				bmp = BitmapFactory.decodeStream(is);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					// Hmm
				}
			}
			
			//bmp = BitmapFactory.decodeResource(context.getResources(), graphics[t]); // Old way... does some filtering so no 'retro' style graphics.

			// Tell openGL which texture we are working with
			gl.glBindTexture(GL11.GL_TEXTURE_2D, t);

			// Create mipmaps and set texture parameters. Some day I will provide a way to choose these parameters through BobEngine.
			//gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR); // Linear filtering for downscaling
			//gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR); // Linear filtering for upscaling
			gl.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); // Nearest neighbor filtering for downscaling
			gl.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); // Nearest neighbor filtering for upscaling ('retro' pixelated graphics)
			//gl.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE); // Use mipmapping (better performance, can cause strange glitches I must look into...)

			// Texture wrapping
			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);

			// This assigns bmp to the texture ID we are working with (t)
			GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bmp, 0);

			// Set the face rotation
			gl.glFrontFace(GL11.GL_CCW);

			bmp.recycle();
		}
		
		newGraphics = false; // All graphics loaded.
	}
	
	/**
	 * Returns the number of loaded graphics.
	 */
	public int getNumGraphics() {
		return numGFX;
	}
	
	/**
	 * Returns true if new graphics have been added but have not been loaded.
	 */
	public boolean newGraphics() {
		return newGraphics;
	}
}
