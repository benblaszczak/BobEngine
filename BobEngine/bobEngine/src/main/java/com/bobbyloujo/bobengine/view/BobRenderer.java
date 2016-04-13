/**
 * BobEngine - 2D game engine for Android
 * <p/>
 * Copyright (C) 2014, 2015, 2016 Benjamin Blaszczak
 * <p/>
 * BobEngine is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser Public License
 * version 2.1 as published by the free software foundation.
 * <p/>
 * BobEngine is provided without warranty; without even the implied
 * warranty of merchantability or fitness for a particular
 * purpose. See the GNU Lesser Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General
 * Public License along with BobEngine; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301 USA
 */
package com.bobbyloujo.bobengine.view;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.os.SystemClock;
import android.util.Log;

import com.bobbyloujo.bobengine.entities.Room;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * This class handles updating and rendering for it's BobView owner.
 *
 * @author Ben
 */
public class BobRenderer implements Renderer {

	// Constants
	private static final int FRAME_DROP_THRES = 30;

	// Variables
	public static final long OPTIMAL_FPS = 60;        // The optimal speed that the game will run
	private float OPTIMAL_TIME = 1000 / OPTIMAL_FPS;  // Optimal time for a frame to take
	private float averageDelta = OPTIMAL_TIME;        // Average amount of time a frame takes
	private long lastTime;                            // Time the last frame took
	private int frames = 0;                           // # of frames passed
	private boolean outputFPS = false;

	private double low;   // The lowest FPS
	private double high;  // The highest FPS

	/* Camera variables */
	private double camWidth;
	private double camHeight;

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
	 * @param newOwner - BobView associated with this renderer.
	 */
	public void setOwner(BobView newOwner) {
		myOwner = newOwner;
	}

	/**
	 * Sets the background color for the BobView.
	 *
	 * @param red   - Red value from 0 to 1
	 * @param green - Green value from 0 to 1
	 * @param blue  - Blue value from 0 to 1
	 * @param alpha - Alpha value from 0 to 1
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
		myOwner.getGraphicsHelper().handleGraphics((GL11) gl);             // Load textures for the view

		low = high = -1;

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
		Room current = myOwner.getCurrentRoom();       // Current room
		long now = SystemClock.uptimeMillis();         // Current time
		long timeElapsed = (long) OPTIMAL_TIME;        // Amount of time the frame took

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);                              // Get rid of the previous frame
		gl.glClearColor(red, green, blue, alpha);                          // BG color

		myOwner.getGraphicsHelper().handleGraphics((GL11) gl);

		if (current != null) {
			current.update(1/*averageDelta / OPTIMAL_TIME*/);   // Update game logic
			current.draw(gl);                              // Draw graphics
		}

		if (lastTime > 0) {
			timeElapsed = now - lastTime;                  // The amount of time the last frame took
		}

		if (frames < OPTIMAL_FPS) {
			frames++;
			averageDelta = (timeElapsed + averageDelta * (frames - 1)) / frames;     // Update the average amount of time a frame takes
		} else {
			averageDelta = (timeElapsed + averageDelta * (OPTIMAL_FPS - 1)) / OPTIMAL_FPS;           // Update the average amount of time a frame takes
		}

		lastTime = now;

		if (outputFPS && frames % 60 == 0) {
			double fps = (double) 1000 / (double) averageDelta;

			if (1000.0 / timeElapsed < low || low == -1) {
				low = 1000.0 / timeElapsed;
			}

			if (1000.0 / timeElapsed > high || high == -1) {
				high = 1000.0 / timeElapsed;
			}

			if (1000.0 / timeElapsed < FRAME_DROP_THRES) {
				Log.d("fps", "FRAME DROPPED. FPS: " + (1000.0 / timeElapsed));
			}

			if (SystemClock.uptimeMillis() % 100 <= 10) {
				Log.d("fps", "FPS: " + fps + "    LOW: " + low + "    HIGH: " + high); // Show FPS in logcat
			}
		}

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

		camWidth = width;
		camHeight = height;

		//myOwner.getGraphicsHelper().loadAllGraphics(gl);

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
	protected void onResume() {
		averageDelta = OPTIMAL_TIME;
		frames = 0;
		lastTime = 0;
	}

	/**
	 * Returns the width of the camera's view in pixels when the camera's zoom level is 1.
	 */
	public double getCameraWidth() {
		return camWidth;
	}

	/**
	 * Returns the height of the camera's view in pixels when the camera's zoom level is 1.
	 */
	public double getCameraHeight() {
		return camHeight;
	}

	/**
	 * Returns the average frames per second.
	 * @return Average FPS
	 */
	public double getFPS() {
		return 1000.0 / averageDelta;
	}

	/**
	 * Determines if the FPS should be output to logcat.
	 * @param outputFPS True to show FPS in logcat, false to not show FPS.
	 */
	public void outputFPS(boolean outputFPS) {
		this.outputFPS = outputFPS;
	}
}
