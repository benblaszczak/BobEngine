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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.opengl.GLES10;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.os.SystemClock;

/**
 * This class handles updating and rendering for it's BobView owner.
 * 
 * @author Ben
 * 
 */
public class BobRenderer implements Renderer {

	// Variables
	public static final long FPS = 60;                // The optimal speed that the game will run
	private float OPTIMAL_TIME = 1000 / FPS;          // Optimal time for a frame to take
	private float averageDelta = OPTIMAL_TIME;        // Average amount of time a frame takes
	private long lastTime;                            // Time the last frame took
	private long now;                                 // Time now
	private int frames = 0;                           // # of frames passed
	private long timeElapsed = 16;                    // Amount of time the frame took

	/* Camera variables */
	private double camwidth;
	private double camheight;

	/* Background color values */
	private float red = 1;
	private float green = 1;
	private float blue = 1;
	private float alpha = 1;

	// Objects
	private BobView myOwner;                          // The BobView that this BobRenderer belongs to.

	/**
	 * Sets the BobView associated with this BobRenderer.
	 * 
	 * @param newOwner
	 *            - BobView associated with this renderer.
	 */
	public void setOwner(BobView newOwner) {
		myOwner = newOwner;
	}

	/**
	 * Sets the background color for the BobView.
	 * 
	 * @param red
	 *            - Red value from 0 to 1
	 * @param green
	 *            - Green value from 0 to 1
	 * @param blue
	 *            - Blue value from 0 to 1
	 * @param alpha
	 *            - Alpha value from 0 to 1
	 */
	public void setBackgroundColor(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	/**
	 * Set up the surface. <br />
	 * <br />
	 * This method will load the textures, enable effects we need, disable
	 * effects we don't need, set up the client state for drawing the quads.
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		myOwner.getGraphicsHelper().handleGraphics((GL11) gl);// Load textures for the view

		red = green = blue = alpha = 1;

		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);          // How to interpret transparency
		gl.glAlphaFunc(GL10.GL_GREATER, 0);
		gl.glEnable(GL10.GL_BLEND);                                        // Enable transparency
		gl.glEnable(GL10.GL_TEXTURE_2D);                                   // Enable Texture Mapping

		// Disable all the things we don't need.
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_FOG);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_STENCIL_TEST);
		gl.glDisable(GL10.GL_SCISSOR_TEST);
		gl.glDisable(GL10.GL_DITHER);
		gl.glDisable(GL10.GL_CULL_FACE);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);                      // We will use vertex arrays for quad vertices
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);               // We will need to use portions of textures
	}

	/**
	 * Execute a frame. <br />
	 * <br />
	 * This method will update game logic and update the graphics.
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
        Room current = myOwner.getCurrentRoom();

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);                              // Get rid of the previous frame
		gl.glClearColor(red, green, blue, alpha);                          // BG color

		myOwner.getGraphicsHelper().handleGraphics((GL11) gl);

		if (current != null) {
            current.update(averageDelta / OPTIMAL_TIME);   // Update game logic
            current.draw(gl);                              // Draw graphics
		}

		now = SystemClock.uptimeMillis();
		if (lastTime > 0) timeElapsed = now - lastTime;                    // The amount of time the last frame took
		else timeElapsed = (long) averageDelta;                            // Only happens the first frame

		if (frames < FPS) {
			frames++;
			averageDelta = (timeElapsed + averageDelta * (frames - 1)) / frames;     // Update the average amount of time a frame takes
		} else {
			averageDelta = (timeElapsed + averageDelta * (FPS - 1)) / FPS;           // Update the average amount of time a frame takes
		}

		lastTime = now;

		if (averageDelta / OPTIMAL_TIME > 1.2) {
			averageDelta = OPTIMAL_TIME;
		}

		if (averageDelta / OPTIMAL_TIME < .8) {
			averageDelta = OPTIMAL_TIME;
		}

		/*
		 * double fps = (double) 1000 / (double) averageDelta; if
		 * (SystemClock.uptimeMillis() % 100 == 0) { Log.d("test", "FPS: " +
		 * Double.toString(fps)); // Show FPS in logcat }
		 */
	}

	/**
	 * Handle changes such as orientation changes. This also happens when the
	 * surface is created. <br />
	 * <br />
	 * This method will set the background color, set the viewport, remove
	 * perspective.
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (height == 0) {                        // Prevent A Divide By Zero By
			height = 1;                           // Making Height Equal One
		}

		camwidth = width;
		camheight = height;

        gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);      // Select The Projection Matrix
		gl.glLoadIdentity();                      // Reset The Projection Matrix
		GLU.gluOrtho2D(gl, 0, width, 0, height);  // Use orthogonic view. No perspective.

		gl.glMatrixMode(GL10.GL_MODELVIEW);       // Select The Modelview Matrix
		gl.glLoadIdentity();
	}

	/**
	 * The app is resumed from being paused. <br />
	 * <br />
	 * This method will reset the average delta time, resume the game.
	 */
	public void onResume() {
		averageDelta = 16.6f;
	}

    /**
     * Returns the width of the camera's view in pixels when the camera's zoom level is 1.
     */
    public double getCameraWidth() {
        return camwidth;
    }

    /**
     * Returns the height of the camera's view in pixels when the camera's zoom level is 1.
     */
    public double getCameraHeight() {
        return camheight;
    }
}
