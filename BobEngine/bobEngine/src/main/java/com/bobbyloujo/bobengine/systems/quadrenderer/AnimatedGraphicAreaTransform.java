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
package com.bobbyloujo.bobengine.systems.quadrenderer;

import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.view.BobRenderer;

/**
 * This class is a basic implementation of GraphicTransformable that includes
 * animation.
 *
 * Created by Benjamin on 9/25/2015.
 */
public class AnimatedGraphicAreaTransform implements GraphicAreaTransformation, Updatable {

	public float x = 0;
	public float y = 0;
	public float width = 1;
	public float height = 1;
	public int frame = 0;

	private int rows;

	private double fps = 0;
	private int start = 0;
	private int end = 0;
	private int gameFramesPassed = 0;

	private int timesPlayed = 0;
	private int loop = 0;
	private boolean animFinished = false;

	/* CREATION */

	public AnimatedGraphicAreaTransform() {
		this(0, 0, 1, 1, 1);
	}

	public AnimatedGraphicAreaTransform(int rows, int col) {
		this(0f, 0f, 1f / (float) col, 1f, rows);
	}

	public AnimatedGraphicAreaTransform(float x, float y, float width, float height, int frameRows) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rows = frameRows;
	}

	public AnimatedGraphicAreaTransform(int x, int y, int width, int height, int frameRows, int gfxWidth, int gfxHeight) {
		this.x = (float) x / (float) gfxWidth;
		this.y = (float) y / (float) gfxHeight;
		this.width = (float) width / (float) gfxWidth;
		this.height = (float) height / (float) gfxHeight;
		this.rows = frameRows;
	}

	/* UTILITY */

	/**
	 * This method will conveniently divide this graphic transform into a grid
	 * of frames.
	 *
	 * @param rows The number of rows of frames.
	 * @param cols The number of columns of frames.
	 */
	public void makeGrid(int rows, int cols) {
		this.x = 0f;
		this.y = 0f;
		this.width = 1f / (float) cols;
		this.height = 1f;
		this.rows = rows;
	}

	/**
	 * This method will conveniently divide this graphic transform into a grid
	 * of frames using only a portion of the graphic.
	 *
	 * @param rows The number of rows of frames.
	 * @param cols The number of columns of frames.
	 * @param x The X position on the graphic in pixels.
	 * @param y The Y position on the graphic in pixels.
	 * @param width The width on the graphic in pixels.
	 * @param height The height on the graphic in pixels.
	 * @param gfxWidth The width of the graphic sheet in pixels.
	 * @param gfxHeight The height of the graphic sheet in pixels.
	 */
	public void makeGrid(int rows, int cols, int x, int y, int width, int height, int gfxWidth, int gfxHeight) {
		this.x = (float) x / (float) gfxWidth;
		this.y = (float) y / (float) gfxHeight;
		this.width = (float) width / (float) gfxWidth / (float) cols;
		this.height = (float) height / (float) gfxHeight;
		this.rows = rows;
	}

	/**
	 * Stop the animation. The object will display the most recently shown
	 * frame.
	 */
	public void stopAnimation() {
		stopAnimation(frame);
	}

	/**
	 * Stop the current animation and set the frame to display.
	 * @param frame The frame number to show after the animation stops
	 */
	public void stopAnimation(int frame) {
		fps = 0;
		this.frame = frame;
		this.start = frame;
		this.end = frame;
		this.loop = 0;
	}

	/**
	 * Animates this GraphicTransform. Will loop through start frame to end frame at a
	 * speed of FPS frames per second.
	 *
	 * @param fps the speed of the animation in frames per second
	 * @param start the first frame of the animation.
	 * @param end the last frame of the animation.
	 */
	public void animate(int fps, int start, int end) {
		animate(fps, start, end, 0);
	}

	/**
	 * Animate this GraphicTransform using a predefined animation.
	 * @param anim - A predefined animation.
	 */
	public void animate(Animation anim) {
		animate(anim.fps, anim.startFrame, anim.endFrame, anim.loop);
	}

	/**
	 * Animates this GraphicTransform for a limited number of times.
	 *
	 * @param fps speed
	 * @param start start frame
	 * @param end end frame
	 * @param loop times to play
	 */
	public void animate(double fps, int start, int end, int loop) {
		if (this.fps != fps || this.start != start || this.end != end || this.loop != loop) {
			timesPlayed = 0;
		}

		this.fps = fps;
		this.start = start;
		this.end = end;
		this.loop = loop;

		if (fps == 0) {
			stopAnimation(start);
		}
	}

	/**
	 * The animation will have finished when the final frame in the animation has been
	 * shown for the correct amount of time determined by the FPS set by the call
	 * to the animate() method.
	 *
	 * @return True when the animation has finished, false otherwise.
	 */
	public boolean animationFinished() {
		return animFinished;
	}

	/* EVENTS */
	@Override
	public void update(double dt) {
		animFinished = false;

		if (fps > 0 && ((loop > 0 && timesPlayed <= loop) || loop == 0)) {    // Should animate if: fps > 0 and we've looped less than the max loops OR max loops is 0 so we should loop forever.
			if (start <= end) {                                               // Loop forward (start frame is less than end frame)
				if (frame < start || frame > end) {                           // Go back to the start frame if frame is out of animation range
					frame = start;
				}
			} else {                                                          // Loop backwards (start frame is greater than end frame)
				if (frame > start || frame < end) {                           // Go back to the start frame if frame is out of animation range
					frame = start;
				}
			}

			if (gameFramesPassed >= BobRenderer.OPTIMAL_FPS / fps) {    // Determine if we should go to the next frame
				if (start <= end) frame++;                      // Animating forwards, increase frame
				else frame--;                                   // Animating backwards, decrease frame
				gameFramesPassed = 0;                           // Reset counter
			} else {                                            // Should not go to next frame yet
				gameFramesPassed++;                             // Increase counter
			}

			if (start <= end) {             // If animating forwards
				if (frame > end) {          // Reached the end of the animation
					frame = start;          // Go back to start of the animation
					animFinished = true;    // The animation finished.
					timesPlayed++;          // Increment times played.
				}
			} else {                        // If animating backwards
				if (frame < end) {          // Reached end of the animation
					animFinished = true;    // The animation finished.
					frame = start;          // Go back to the start of the animation
					timesPlayed++;          // Increment times played.
				}
			}
		} else {                   // Should not animate anymore.
			animFinished = true;   // Animation over.
		}

		if (timesPlayed >= loop && loop > 0) { // Done looping
			frame = end;
		}
	}

	@Override public float getGraphicX() {
		return x + width * (float) (frame / rows);
	}

	@Override public float getGraphicY() {
		return y + (height / (float) rows) * (float) (frame % rows);
	}

	@Override public float getGraphicWidth() {
		return width;
	}

	@Override public float getGraphicHeight() {
		return height / (float) rows;
	}

	/**
	 * A predefined animation with start and end frames, fps, and loop fields.
	 */
	public static class Animation {
		public int startFrame;
		public int endFrame;
		public double fps;
		public int loop;

		/**
		 * Predefine an animation.
		 *
		 * @param start Start frame
		 * @param end End frame
		 * @param fps frames per second
		 * @param loop number of times to loop
		 */
		public Animation(int start, int end, double fps, int loop) {
			startFrame = start;
			endFrame = end;
			this.fps = fps;
			this.loop = loop;
		}
	}
}
