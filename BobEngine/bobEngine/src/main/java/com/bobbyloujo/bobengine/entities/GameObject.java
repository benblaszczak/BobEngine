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
package com.bobbyloujo.bobengine.entities;

import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.systems.input.gamepad.Gamepad;
import com.bobbyloujo.bobengine.systems.input.gamepad.GamepadInputHandler;
import com.bobbyloujo.bobengine.systems.input.touch.Touch;
import com.bobbyloujo.bobengine.systems.input.touch.TouchInputHandler;
import com.bobbyloujo.bobengine.systems.quadrenderer.AnimatedGraphicAreaTransform;
import com.bobbyloujo.bobengine.systems.quadrenderer.GraphicAreaTransformation;
import com.bobbyloujo.bobengine.systems.quadrenderer.QuadRenderSystem;
import com.bobbyloujo.bobengine.view.BobRenderer;

/**
 * An Entity that does it all. Outputs a graphic, updates, handles input. Pretty much everything you could ask for.
 *
 * @author Ben
 */
public class GameObject extends Entity implements Updatable, Transformation, GraphicAreaTransformation, TouchInputHandler, GamepadInputHandler {

	/* TRANSFORM DATA */

	/** X-Coord; Midpoint!. */
	public double x;
	/** Y-Coord; Midpoint!. */
	public double y;
	/** This object's current rotation angle. */
	public double angle;
	/** Height of this object in px. */
	public double height;
	/** Width of this object in px. */
	public double width;
	/** Scale of this object */
	public double scale;
	/** The layer that this object is to be drawn on. */
	public int layer;

	/** This object's visibility. */
	public boolean visible;
	/** Flag indicating if this object should follow the camera */
	public boolean followCamera;

	/* GRAPHIC TRANSFORM DATA */

	/** This object's current frame. */
	public int frame;

	private float gfxX = 0;
	private float gfxY = 0;
	private float gfxWidth = 1;
	private float gfxHeight = 1;

	private int frameRows;

	private int fps = 0;
	private int start = 0;
	private int end = 0;
	private int gameFramesPassed = 0;

	private int timesPlayed = 0;
	private int loop = 0;
	private boolean animFinished = false;

	// Objects
	private Transformation transformParent;
	private QuadRenderSystem renderSystem;

	/**
	 * Create a GameObject in the specified room.
	 * @param room The room that this object is in.
	 */
	public GameObject(Room room) {
		super(room);
		init();
	}

	/**
	 * Create a GameObject in the specified parent.
	 * @param parent The parent that this object is in.
	 */
	public GameObject(Entity parent) {
		super(parent);
		init();
	}

	/**
	 * Initialization.
	 */
	private void init() {
		x = y = width = height = 100;
		scale = 1;

		frame = 0;
		angle = 0;
		layer = 2;
		visible = true;
		followCamera = false;

		gfxX = 0;
		gfxY = 0;
		gfxWidth = 1;
		gfxHeight = 1;
		frameRows = 1;

		renderSystem = getRoom().getQuadRenderSystem(new Graphic());
	}

	/**
	 * Returns the Graphic object used by this GameObject (Used by the Render
	 * System of this object)
	 */
	public Graphic getGraphic() {
		return renderSystem.getGraphic();
	}

	/**
	 * Set the graphic for this object with 1 frame. For better performance
	 * when using many graphics, put multiple graphics onto a single graphic sheet and use
	 * setGraphic(Graphic graphic, int x, int y, ...) or setPreciseGraphic(...).
	 *
	 * @param graphic
	 *            - The graphic to use. Should only have one image on it.
	 *            Should not be a sheet of graphics.
	 */
	public void setGraphic(Graphic graphic) {
		setPreciseGraphic(graphic, 0, 0, 1, 1, 1);
	}

	/**
	 * Set the graphic for this object with a specific number of frames. This method assumes
	 * all frames are arranged vertically in one column.
	 *
	 * </br>
	 * For better performance when using many graphics, put multiple graphics onto a single
	 * graphic sheet and use setGraphic(Graphic graphic, int x, int y, ...) or setPreciseGraphic(...).
	 *
	 * @param graphic
	 *            - The graphic to use. Should only have one image on it.
	 *            Should not be a sheet of graphics.
	 * @param frames
	 *            - The number of frames this graphic has.
	 */
	public void setGraphic(Graphic graphic, int frames) {
		setPreciseGraphic(graphic, 0, 0, 1, 1, frames);
	}

	/**
	 * Set the graphic for this object with a specific number of columns of frames.
	 *
	 * @param graphic The Graphic object to use. This should be added to your GraphicsHelper.
	 * @param columns The number of columns of frames
	 * @param rows The number of frames in a column
	 */
	public void setGraphic(Graphic graphic, int columns, int rows) {
		setPreciseGraphic(graphic, 0, 0, 1, 1f / (float) columns, rows);
	}

	/**
	 * Set the graphic for this object using a Parameters object.
	 * @param params Predefined parameter object
	 */
	public void setGraphic(Graphic.Parameters params) {
		gfxX = (float) params.x / (float) params.graphic.width;
		gfxY = (float) params.y / (float) params.graphic.height;
		gfxWidth = (float) params.width / (float) params.graphic.width;
		gfxHeight = (float) params.height / (float) params.graphic.height;
		frameRows = params.rows;

		QuadRenderSystem r = getRoom().getQuadRenderSystem(params.graphic);
		r.addTransform(this, this);
		renderSystem = r;
	}

	/**
	 * Set graphic information for this object using pixel coordinates. Should only be used
	 * when there is only one drawable folder.
	 * <br/>
	 * <br/>
	 * NOTE: if a graphic has more than one frame, they should all be the same size and
	 * be stacked vertically in the same image file.
	 *
	 * @param graphic The graphic sheet to use.
	 * @param x The x coordinate of the graphic on the sheet, in pixels.
	 * @param y The y coordinate of the graphic on the sheet, in pixels.
	 * @param height The height of a single frame of the graphic on the sheet, in pixels.
	 * @param width The width of a single frame of the graphic on the sheet, in pixels.
	 * @param frameRows The number of rows of frames the graphic has.
	 */
	public void setGraphic(Graphic graphic, int x, int y, int height, int width, int frameRows) {
		gfxX = (float) x / (float) graphic.width;
		gfxY = (float) y / (float) graphic.height;
		gfxWidth = (float) width / (float) graphic.width;
		gfxHeight = (float) height / (float) graphic.height;
		this.frameRows = frameRows;

		QuadRenderSystem r = getRoom().getQuadRenderSystem(graphic);
		r.addTransform(this, this);
		renderSystem = r;
	}

	/**
	 * Set graphic information for this object with precise values for x, y,
	 * width, height. Use this when there is more than one drawable folder.
	 *
	 * @param graphicSheet The graphic sheet to use.
	 * @param x The x coordinate of the graphic on the sheet, from 0 to 1.
	 * @param y The y coordinate of the graphic on the sheet, from 0 to 1.
	 * @param height The height of a single frame of the graphic on the sheet, from 0 to 1.
	 * @param width The width of a single frame of the graphic on the sheet, from 0 to 1.
	 * @param frameRows The number of frameRows the graphic has.
	 */
	public void setPreciseGraphic(Graphic graphicSheet, float x, float y, float height, float width, int frameRows) {
		gfxX = x;
		gfxY = y;
		gfxWidth = width;
		gfxHeight = height;
		this.frameRows = frameRows;

		QuadRenderSystem r = getRoom().getQuadRenderSystem(graphicSheet);
		r.addTransform(this, this);
		renderSystem = r;
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
	 * Animate this GameObject using a predefined animation.
	 * @param anim - A predefined animation.
	 */
	public void animate(AnimatedGraphicAreaTransform.Animation anim) {
		animate(anim.fps, anim.startFrame, anim.endFrame, anim.loop);
	}

	/**
	 * Animates this GameObject. Will loop through start frame to end frame at a
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
	 * Animates this GameObject for a limited number of times.
	 *
	 * @param fps speed
	 * @param start start frame
	 * @param end end frame
	 * @param loop times to play
	 */
	public void animate(int fps, int start, int end, int loop) {
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

	/* UTILITY */

	/**
	 * Returns the screen width correction ratio for dealing with different size screens.
	 * This ratio is based off of the initial orientation of the device when the BobView is initialized!
	 */
	public double getRatioX() {
		return getView().getRatioX();
	}
	
	/**
	 * Returns the screen height correction ratio for dealing with different size screens.
	 * This ratio is based off of the initial orientation of the device when the BobView is initialized!
	 */
	public double getRatioY() {
		return getView().getRatioY();
	}
	
	/**
	 * Returns the Touch touch listener for the BobView containing this GameObject.
	 */
	public Touch getTouch() {
		return getView().getTouch();
	}

	/**
	 * Returns the controller helper for the BobView containing this GameObject.
	 */
	@Deprecated
	public Gamepad getController() {
		return getView().getGamepad();
	}

	/**
	 * Returns the gamepad helper for the BobView containing this GameObject.
	 */
	public Gamepad getGamepad() {
		return getView().getGamepad();
	}

	/**
	 * Determines if this object is on the screen or beyond the edge of the
	 * screen.
	 *
	 * @return True if this object is on the screen or false if the object is
	 *         beyond the screen's bounds.
	 */
	public boolean onScreen() {
		return QuadRenderSystem.onScreen(this, getRoom());
	}

	/* TRANSFORMATION METHODS */

	@Override
	public Transformation getParent() {
		return transformParent;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getScale() {
		return scale;
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public boolean getVisibility() {
		return visible;
	}

	@Override
	public boolean shouldFollowCamera() {
		return followCamera;
	}

	public void setParent(Transformation parent) {
		transformParent = parent;
	}

	/* GRAPHIC TRANSFORMABLE METHODS */

	@Override
	public float getGraphicX() {
		return gfxX + gfxWidth * (float) (frame / frameRows);
	}

	@Override
	public float getGraphicY() {
		return gfxY + (gfxHeight / (float) frameRows) * (float) (frame % frameRows);
	}

	@Override
	public float getGraphicWidth() {
		return gfxWidth;
	}

	@Override
	public float getGraphicHeight() {
		return gfxHeight / (float) frameRows;
	}

	/* EVENT METHODS */

	/**
	 * Update event that happens every frame. Super class method handles animation.
	 * You should override the step() method for game logic.
	 *
	 * @param deltaTime lag correction factor
	 */
	@Override
	public void update(double deltaTime) {

		/* ANIMATION LOGIC */

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
				if (start <= end)
					frame++;                      // Animating forwards, increase frame
				else
					frame--;                                   // Animating backwards, decrease frame
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

		/* END ANIMATION LOGIC */

		step(deltaTime);
	}
	
	/**
	 * Event that happens every frame. Can be overridden.
	 *
	 * @param deltaTime - [Time the last frame took]/[60 FPS] <br /> 
	 * Will be 1 if the game is running at 60FPS, > 1 if the game
	 * is running slow, and < 1 if the game is running fast.
	 */
	public void step(double deltaTime) {
		
	}

	/* TOUCH INPUT EVENTS */

	/**
	 * Touch screen newpress event. Can be overridden, no need to called
	 * super.newpress().
	 *
	 * @param index
	 *           - The number ID of the pointer that triggered this newpress
	 */
	public void newpress(final int index) {

	}

	/**
	 * Touch screen released event. Can be overridden, no need to call
	 * super.released().
	 *
	 * @param index
	 *           - The number ID of the pointer that triggered this release
	 */
	public void released(final int index) {

	}

	/* GAMEPAD INPUT EVENTS */

	/**
	 * Gamepad newpress event. Can be overridden, no need to called
	 * super.newpress().
	 *
	 * @param player The player number that caused this event.
	 * @param button The ID of the button that was pressed (Gamepad.X, Gamepad.R1, etc.)
	 */
	public void newpress(final int player, final int button) {

	}

	/**
	 * Gamepad released event. Can be overridden, no need to call
	 * super.released().
	 *
	 * @param player The player number that caused this event.
	 * @param button The ID of the button that was pressed (Gamepad.X, Gamepad.R1, etc.)
	 */
	public void released(final int player, final int button) {

	}
}
