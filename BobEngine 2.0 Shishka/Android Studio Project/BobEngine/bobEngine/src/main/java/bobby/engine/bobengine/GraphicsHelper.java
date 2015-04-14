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

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * This class helps load graphics. Every BobView has it's own GraphicsHelper.
 * Use getGraphicsHelper() in a BobView to get it's GraphicsHelper.
 * 
 * @author Ben
 * 
 */
public class GraphicsHelper {

	// Constants
	private int START_NUM_TEX = 50;                  // Starting maximum number of textures (graphics)

	// Variables
	private int numGFX;                              // Number of loaded graphics
	private Graphic[] graphics;                      // Textures as drawables
	private boolean useMipMaps;                      // Flag indicates if added graphics should be mip mapped
	private int magFilter;                           // Upscale filter to use
	private int minFilter;                           // Downscale filter to use

	// Object
	private Context context;

	public GraphicsHelper(Context context) {
		this.context = context;

		numGFX = 1;
		graphics = new Graphic[START_NUM_TEX];

		useMipMaps = true;
		magFilter = GL11.GL_LINEAR;
		minFilter = GL11.GL_LINEAR_MIPMAP_LINEAR;
	}

	/**
	 * Specify the texture parameters for all graphics loaded after this method
	 * is called. <br/>
	 * <br/>
	 * 
	 * For 'retro' pixellated graphics, use (false, GL11.GL_NEAREST,
	 * GL11.GL_NEAREST)
	 * 
	 * @param useMipMaps
	 *            - true = better performance. Is already true by default.
	 * @param minFilter
	 *            - Filter to use for downscaling. Must be one of the OpenGL
	 *            filters (eg. GL11.GL_LINEAR_MIPMAP_LINEAR (default))
	 * @param magFilter
	 *            - Filter for upscaling. Must be an OpenGL filter (eg.
	 *            GL11.GL_LINEAR (default))
	 */
	public void setParameters(boolean useMipMaps, int minFilter, int magFilter) {
		this.useMipMaps = useMipMaps;
		this.minFilter = minFilter;
		this.magFilter = magFilter;
	}

	/**
	 * Create a usable graphic from a drawable image.
	 * 
	 * @param drawable
	 *            - Drawable resource in R.drawable.&#42;
     * @param shouldLoad
     *            - Can be set to false if you don't want the graphic to be loaded right away.
	 * @return A Graphic object containing information about the newly created
	 *         graphic. Store this somewhere where it can be accessed by
	 *         GameObjects (Like as a static property in a BobView).
	 */
	public Graphic addGraphic(int drawable, boolean shouldLoad) {
		// Data
		int graphic;

		graphic = numGFX;
		numGFX++;

		if (graphic > graphics.length) {                           // Hit max graphics, need to expand graphics[]
			Graphic[] temp = graphics;
			graphics = new Graphic[graphics.length + START_NUM_TEX];

			for (int i = 0; i < temp.length; i++) {
				graphics[i] = temp[i];
			}
		}

		try {
			// Load the bitmap just to get the height and width
			Bitmap bmp;
			InputStream is = context.getResources().openRawResource(drawable);

			try {
				bmp = BitmapFactory.decodeStream(is);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					Log.e("BobEngine", "Failed to load graphic.");
					e.printStackTrace();
				}
			}
			
			graphics[graphic] = new Graphic(drawable, bmp.getHeight(), bmp.getWidth(), minFilter, magFilter, useMipMaps, graphic);
			bmp.recycle();
		} catch (OutOfMemoryError e) {
			graphics[graphic] = new Graphic(drawable, 100, 100, minFilter, magFilter, useMipMaps, graphic);
			Log.e("BobEngine", "Image too large. Unable to get height and width.");
		}
		
		if (shouldLoad) graphics[graphic].load();

		return graphics[graphic];
	}

    /**
     * Create a usable graphic from a drawable image. This graphic will be loaded when the view is created.
     *
     * @param drawable
     *            - Drawable resource in R.drawable.&#42;
     * @return A Graphic object containing information about the newly created
     *         graphic. Store this somewhere where it can be accessed by
     *         GameObjects (Like as a static property in a BobView).
     */
    public Graphic addGraphic(int drawable) {
        return addGraphic(drawable, true);
    }

	/**
	 * Perform outstanding graphic commands (load, unload).
	 * 
	 * @param gl
	 */
	public void handleGraphics(GL11 gl) {
		int sampleSize = 1;
		boolean success = true;

		do {
			try {
				for (int t = 1; t < numGFX; t++) {
					if (graphics[t].getCommand() == Graphic.Command.LOAD) {                             // Should we load it?
						loadGraphic(gl, t, sampleSize);
					} else if (graphics[t].getCommand() == Graphic.Command.UNLOAD) {                    // Should we unload it?
						unloadGraphic(gl, t);
					}
				}
				
				success = true;
			} catch (OutOfMemoryError e) {  // Not enough memory to load all the graphics. BobEngine will try down sampling them.
				sampleSize++;
				Log.e("BobEngine", "Not enough memory. Retrying in sample size " + Integer.toString(sampleSize));
				success = false;
			}
		} while (!success);  // Try again

		for (int t = 1; t < numGFX; t++) {
			graphics[t].finished();
		}
	}

	/**
	 * Load a particular graphic.
	 * 
	 * @param gl
	 * @param t
	 */
	private void loadGraphic(GL11 gl, int t, int sampleSize) {
		Bitmap bmp;
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inSampleSize = sampleSize;

		InputStream is = context.getResources().openRawResource(graphics[t].drawable);

		try {
			bmp = BitmapFactory.decodeStream(is, null, op);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e("BobEngine", "Failed to load graphic.");
				e.printStackTrace();
			}
		}

		//bmp = BitmapFactory.decodeResource(context.getResources(), graphics[t]); // Old way... does some filtering so no 'retro' style graphics.

		// Tell openGL which texture we are working with
		gl.glBindTexture(GL11.GL_TEXTURE_2D, t);

		// Create mipmaps and set texture parameters.
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, graphics[t].minFilter);                 // Filtering for downscaling
		gl.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, graphics[t].magFilter);                 // Upscale filtering
		if (graphics[t].useMipMaps) gl.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE); // Use mipmapping

		// Texture wrapping
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);

		// This assigns bmp to the texture ID we are working with (t)
		GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bmp, 0);

		// Set the face rotation
		gl.glFrontFace(GL11.GL_CCW);

		bmp.recycle();
	}

	/**
	 * Unload a particular graphic.
	 */
	private void unloadGraphic(GL11 gl, int t) {
		int[] tex = { t };
		gl.glDeleteTextures(1, tex, 0);
	}

	/**
	 * Returns the number of added graphics. Included graphics that aren't loaded.
	 */
	public int getNumGraphics() {
		return numGFX;
	}
}
