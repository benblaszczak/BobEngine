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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;
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
	private final static int START_NUM_TEX = 50;     // Starting maximum number of textures (graphics)
	public final static int DEF_CLEANUPS = 2;        // Default number of cleanups until a graphic is removed.

	// Variables
	private int numGFX;                              // Number of added graphics
	private Graphic[] graphics;                      // Textures as drawables
	private boolean useMipMaps;                      // Flag indicates if added graphics should be mip mapped
	private int magFilter;                           // Upscale filter to use
	private int minFilter;                           // Downscale filter to use
	private int	cleanupsTilRemoval;                  // Number of cleanups until a graphic is removed.

	// Object
	private Context context;
	private Graphic defGraphic;

	public GraphicsHelper(Context context) {
		this.context = context;
		defGraphic = new Graphic();

		numGFX = 0;
		graphics = new Graphic[START_NUM_TEX];

		useMipMaps = true;
		magFilter = GL11.GL_LINEAR;
		minFilter = GL11.GL_LINEAR_MIPMAP_LINEAR;
		cleanupsTilRemoval = DEF_CLEANUPS;
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
	 * Returns a default graphic.
	 * @return
	 */
	public Graphic getDefaultGraphic() {
		return defGraphic;
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
		int graphic = 1;

		Graphic alreadyAdded = findGraphic(drawable, useMipMaps, minFilter, magFilter);
		if (alreadyAdded != null) {
			return alreadyAdded;
		}

		numGFX++;

		if (numGFX >= graphics.length) {                           // Hit max graphics
			cleanUp();                                             // Try to get rid of some that haven't been used recently

			if (numGFX >= graphics.length) {                       // Still too many, increase size of graphics
				Graphic[] temp = graphics;
				graphics = new Graphic[graphics.length + START_NUM_TEX];

				for (int i = 0; i < temp.length; i++) {
					graphics[i] = temp[i];
				}
			}
		}

		for (int i = 1; i < graphics.length; i++) {
			if (graphics[i] == null) {
				graphic = i;
				break;
			}
		}

		try {
			// Load the bitmap just to get the height and width
			Bitmap bmp = null;
			InputStream is = context.getResources().openRawResource(drawable);

			try {
				bmp = BitmapFactory.decodeStream(is);
			} catch (Exception e) {
				Log.e("BobEngine", "Failed to load graphic.");
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (bmp != null){
				graphics[graphic] = new Graphic(drawable, bmp.getHeight(), bmp.getWidth(), minFilter, magFilter, useMipMaps);
				bmp.recycle();
			}
		} catch (OutOfMemoryError e) {
			graphics[graphic] = new Graphic(drawable, 100, 100, minFilter, magFilter, useMipMaps);
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
	 * Add a graphic object. graphic may be assigned a new ID.
	 * @param graphic
	 */
	public void addGraphic(Graphic graphic) {
		// Data
		int g = 1;

		Graphic alreadyAdded = findGraphic(graphic.drawable, graphic.useMipMaps, graphic.minFilter, graphic.magFilter);
		if (alreadyAdded != null) {
			graphic.id = alreadyAdded.id;
			graphic.indicateUsed(cleanupsTilRemoval);
			return;
		}

		numGFX++;

		if (numGFX >= graphics.length) {                           // Hit max graphics
			cleanUp();                                             // Try to get rid of some that haven't been used recently

			if (numGFX >= graphics.length) {                       // Still too many, increase size of graphics
				Graphic[] temp = graphics;
				graphics = new Graphic[graphics.length + START_NUM_TEX];

				for (int i = 0; i < temp.length; i++) {
					graphics[i] = temp[i];
				}
			}
		}

		for (int i = 1; i < graphics.length; i++) {
			if (graphics[i] == null) {
				g = i;
				break;
			}
		}

		graphic.id = g;
		graphic.indicateUsed(cleanupsTilRemoval);
		graphics[g] = graphic;
	}

	/**
	 * Signify that a graphic should be removed from the list.
	 * @param graphic
	 */
	public void removeGraphic(Graphic graphic) {
		if (graphics[graphic.id] == graphic) graphics[graphic.id].remove();
	}

	/**
	 * Get the graphic created from a drawable that has already been added.
	 *
	 * @param drawable The drawable to find
	 * @return A graphic object created from the drawable or null if the drawable has not been added.
	 */
	public Graphic findGraphic(int drawable) {
		for (int i = 0; i < graphics.length; i++) {
			if (graphics[i] != null && graphics[i].drawable == drawable) return graphics[i];
		}

		return null;
	}

	/**
	 * Get the graphic created from a drawable and with the same parameters that has already been added.
	 *
	 * @param drawable The drawable to find
	 * @return A graphic object created from the drawable or null if the drawable has not been added.
	 */
	public Graphic findGraphic(int drawable, boolean useMipMaps, int minFilter, int magFilter) {
		for (int i = 0; i < graphics.length; i++) {
			if (graphics[i] != null
					&& graphics[i].drawable == drawable
					&& graphics[i].useMipMaps == useMipMaps
					&& graphics[i].minFilter == minFilter
					&& graphics[i].magFilter == magFilter) {
				return graphics[i];
			}
		}

		return null;
	}

	/**
	 * Set the number of cleanups since a graphic has last been used needed
	 * to remove the graphic from the GraphicHelper.
	 * @param cleanups
	 */
	public void setCleanupsTilRemoval(int cleanups) {
		cleanupsTilRemoval = cleanups;
	}

	/**
	 * Get the number of cleanups since a graphic has last been used needed
	 * to remove the graphic from the GraphicHelper.
	 * @return
	 */
	public int getCleanupsTilRemoval() {
		return cleanupsTilRemoval;
	}

	/**
	 * Will find all graphics that have not been recently used and mark them for removal.
	 */
	public void cleanUp() {
		for (int i = 1; i < graphics.length; i++) {
			if (graphics[i] != null) {
				graphics[i].cleanup();
			}
		}
	}

	/**
	 * Perform outstanding graphic commands (load, unload, remove).
	 * 
	 * @param gl
	 */
	public void handleGraphics(GL11 gl) {
		int sampleSize = 1;
		boolean success = false;
		boolean changed = false;

		do {
			try {
				for (int g = 0; g < graphics.length; g++) {
					if (graphics[g] != null) {
						if (graphics[g].shouldLoad()) {                             // Should we load it?
							loadGraphic(gl, g, sampleSize);
							changed = true;
						} else if (graphics[g].shouldUnload()) {                    // Should we unload it?
							unloadGraphic(gl, g);
							changed = true;
						} else if (graphics[g].shouldRemove()) {
							unloadGraphic(gl, g);
							graphics[g].removed();
							graphics[g] = null;
							numGFX--;
							changed = true;
						}
					}
				}
				
				success = true;
			} catch (OutOfMemoryError e) {  // Not enough memory to load all the graphics. BobEngine will try down sampling them.
				sampleSize++;
				Log.e("BobEngine", "Not enough memory. Retrying in sample size " + Integer.toString(sampleSize));
				success = false;
			}
		} while (!success);  // Try again

		if (changed) gl.glFinish();
	}

	public void loadAllGraphics(GL10 gl) {
		int sampleSize = 1;
		boolean success = false;
		do {
			try {
				for (int g = 0; g < graphics.length; g++) {
					if (graphics[g] != null) {
						loadGraphic((GL11) gl, g, sampleSize);
					}
				}

				success = true;
			} catch (OutOfMemoryError e) {  // Not enough memory to load all the graphics. BobEngine will try down sampling them.
				sampleSize++;
				Log.e("BobEngine", "Not enough memory. Retrying in sample size " + Integer.toString(sampleSize));
				success = false;
			}
		} while (!success);  // Try again

		gl.glFinish();
	}

	/**
	 * Load a particular graphic.
	 * 
	 * @param gl The OpenGL object to handle gl functions
	 * @param g The index of the graphic in graphics[] to load
	 * @param sampleSize The sample size to load the graphic.
	 */
	private void loadGraphic(GL11 gl, int g, int sampleSize) {
		Bitmap bmp;
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inSampleSize = sampleSize;

		InputStream is = context.getResources().openRawResource(graphics[g].drawable);

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

		// Generate an ID for the graphic
		final int[] texID = new int[1];
		gl.glGenTextures(1, texID, 0);
		graphics[g].id = texID[0];

		// Tell openGL which texture we are working with
		gl.glBindTexture(GL11.GL_TEXTURE_2D, graphics[g].id);

		// Create mipmaps and set texture parameters.
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, graphics[g].minFilter);                 // Filtering for downscaling
		gl.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, graphics[g].magFilter);                 // Upscale filtering
		if (graphics[g].useMipMaps) gl.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE); // Use mipmapping

		// Texture wrapping
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);

		// This assigns bmp to the texture ID we are working with (g)
		GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bmp, 0);

		// Set the face rotation
		gl.glFrontFace(GL11.GL_CCW);

		bmp.recycle();

		graphics[g].loaded();

		gl.glFinish();
	}

	/**
	 * Unload a particular graphic.
	 * @param gl OpenGL object for unloading
	 * @param g The index of the graphic to delete.
	 */
	private void unloadGraphic(GL11 gl, int g) {
		int[] tex = { graphics[g].id };
		gl.glDeleteTextures(1, tex, 0);
		graphics[g].deleted();
	}

	/**
	 * Returns the number of added graphics. Included graphics that aren't loaded.
	 */
	public int getNumGraphics() {
		return numGFX;
	}
}
