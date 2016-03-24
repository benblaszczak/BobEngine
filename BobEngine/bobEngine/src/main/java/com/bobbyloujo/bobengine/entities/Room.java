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
package com.bobbyloujo.bobengine.entities;

import android.app.Activity;
import android.opengl.GLES10;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.components.Component;
import com.bobbyloujo.bobengine.systems.input.gamepad.GamepadInputHandler;
import com.bobbyloujo.bobengine.systems.Renderable;
import com.bobbyloujo.bobengine.systems.input.touch.TouchInputHandler;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.systems.quadrenderer.QuadRenderSystem;
import com.bobbyloujo.bobengine.systems.input.gamepad.Gamepad;
import com.bobbyloujo.bobengine.systems.input.touch.Touch;

/**
 * Rooms are collections of GameObjects. They handle updating and rendering each
 * object in the room.
 *
 * @author Ben
 */
public class Room extends Entity {
	// Constants
	public static final int DEF_LAYERS = 10;                       // Default number of layers.
	public static final double DEF_UNIT_DEM = 1;                   // Default grid unit dimensions as a fraction of the width of this Room.

	// Variables
	private int layers;           // Number of layers
	private double gridUnitX;     // Size of a unit on the grid on the X axis.
	private double gridUnitY;     // Size of a unit on the grid on the Y axis.
	private double gridWidth;     // The number of grid units that fit in the view width
	private double gridHeight;    // The number of grid units that fit in the view height

	// Input variables
	private boolean newpress[] = new boolean[Touch.MAX_FINGERS];         // Flags to indicate if a newpress event needs to be handled
	private boolean released[] = new boolean[Touch.MAX_FINGERS];         // Flags to indicate if a released event needs to be handled

	private int buttonNewpress[] = new int[Gamepad.MAX_CONTROLLERS];     // Flags to indicate if a controller newpress event needs to be handled
	private int buttonReleased[] = new int[Gamepad.MAX_CONTROLLERS];     // Flags to indicate if a controller released event needs to be handled

	// Camera variables
	private double camX;       // The X position of the camera.
	private double camY;       // The Y position of the camera.
	private double camZoom;    // The zoom level of the camera. 1 is default, 0> and <1 is zoomed in, >1 is zoomed out.
	private double cAnchorX;   // The zoom anchor X position. This position will stay in the same place when the camera is zoomed in/out.
	private double cAnchorY;   // The zoom anchor Y position.
	private float camLeft;     // The left edge of the camera
	private float camRight;    // The right edge of the camera
	private float camTop;      // The top edge of the camera
	private float camBottom;   // The bottom edge of the camera

	// Objects
	private BobView view;      // This room's containing BobView.

	// Special component lists
	private ArrayList<Renderable> renderables;                   // This list will be populated with render systems and will be used for finding render systems quickly
	private ArrayList<Updatable> updatables;                     // List of updatable components
	private ArrayList<TouchInputHandler> touchInputHandlers;     // List of touch screen input handlers
	private ArrayList<GamepadInputHandler> gamepadInputHandlers; // List of gamepad input handlers

	public Room(BobView view) {
		super();
		init(view, DEF_LAYERS);
	}

	public Room(BobView view, int layers) {
		super();
		init(view, layers);
	}

	private void init(BobView view, int layers) {
		this.view = view;

		this.layers = layers;

		setGridUnitX(DEF_UNIT_DEM);
		setGridUnitY(DEF_UNIT_DEM);

		for (int i = 0; i < buttonNewpress.length; i++) {
			buttonNewpress[i] = -1;
		}

		for (int i = 0; i < buttonReleased.length; i++) {
			buttonReleased[i] = -1;
		}

		// Camera initialization
		camX = 0;
		camY = 0;
		camZoom = 1;
		cAnchorX = 0;
		cAnchorY = 0;

		renderables = new ArrayList<Renderable>();
		updatables = new ArrayList<Updatable>();
		touchInputHandlers = new ArrayList<TouchInputHandler>();
		gamepadInputHandlers = new ArrayList<GamepadInputHandler>();
	}

	/**
	 * Get the BobView that contains this Room.
	 *
	 * @return BobView containing this Room.
	 */
	public BobView getView() {
		return view;
	}

	/**
	 * Returns the activity containing the BobView that contains this Room.
	 */
	public Activity getActivity() {
		return view.getActivity();
	}

	/**
	 * Get the Touch touch listener for this Room's containing BobView.
	 */
	public Touch getTouch() {
		return view.getTouch();
	}

	/**
	 * Get the controller helper for this Room's containing BobView.
	 */
	public Gamepad getController() {
		return view.getGamepad();
	}

	/**
	 * Returns a QuadRenderSystem for the specified Graphic. If one does not already exist for this room,
	 * a new one will be created.
	 *
	 * @param g Graphic to be used by the returned QuadRenderSystem.
	 * @return QuadRenderSystem for Graphic g
	 */
	public QuadRenderSystem getQuadRenderSystem(Graphic g) {
		QuadRenderSystem r;

		r = findQuadRenderSystem(g);

		if (r == null) {
			r = createQuadRenderSystem(g, QuadRenderSystem.DEF_INIT_TRANSFORMS);
		}

		return r;
	}

	/**
	 * Creates a new QuadRenderSystem for the specified Graphic with an initial buffer size that
	 * can hold minSprites number of transforms.
	 *
	 * <br />
	 * <br />
	 * If a QuadRenderSystem for the specified graphic already exists, a new one will not be created. Instead,
	 * the buffer of existing QuadRenderSystem will be increased to minSprites if it is smaller than
	 * minSprites, or be left alone if larger than minSprites.
	 *
	 * @param g Graphic for which to create a QuadRenderSystem.
	 * @param minSprites The minimum number of sprites the QuadRenderSystem should be able to render.
	 */
	public QuadRenderSystem createQuadRenderSystem(Graphic g, int minSprites) {
		QuadRenderSystem r;

		r = findQuadRenderSystem(g);

		if (r == null) {
			r = new QuadRenderSystem(this, g, layers, minSprites);
			addComponent(r);
			renderables.add(r);
		} else if (r.getBufferSize() < minSprites) {
			r.resizeBuffers(minSprites);
		}

		return r;
	}

	/**
	 * Searches for a QuadRenderSystem for the Graphic g and returns it. If one is not found,
	 * this will return null.
	 *
	 * @param g Graphic used by the QuadRenderSystem to be found.
	 * @return A QuadRenderSystem for Graphic g if found, null otherwise.
	 */
	private QuadRenderSystem findQuadRenderSystem(Graphic g) {

		for (Renderable r: renderables) {
			if (r instanceof QuadRenderSystem && r.getGraphic() != null && r.getGraphic().equals(g)) {
				return (QuadRenderSystem) r;
			}
		}

		ArrayList<Component> components = this.getEntireComponentTree();

		for (int o = 0; o < components.size(); o++) {
			if (components.get(o) != null && components.get(o) instanceof QuadRenderSystem) {
				QuadRenderSystem r = ((QuadRenderSystem) components.get(o));

				if (r.getGraphic() != null && r.getGraphic().equals(g)) {
					renderables.add(r);
					return r;
				}
			}
		}

		return null;
	}

	/**
	 * Set the grid unit size in pixels for the X axis to some absolute value.
	 * @param unit size of one unit in pixels.
	 */
	public void setGridUnitX(double unit) {
		gridUnitX = unit;
		gridWidth = getViewWidth() / gridUnitX;
	}

	/**
	 * Set the grid unit size in pixels for the Y axis to some absolute value.
	 * @param unit size of one unit in pixels
	 */
	public void setGridUnitY(double unit) {
		gridUnitY = unit;
		gridHeight = getViewHeight() / gridUnitY;
	}

	/**
	 * Set the grid unit size for the X axis by how many units should equal the width of the
	 * room.
	 *
	 * @param unitsPerRoomWidth Number of units that fit in the view width.
	 */
	public void setGridWidth(double unitsPerRoomWidth) {
		setGridUnitX(getViewWidth() / unitsPerRoomWidth);
	}

	/**
	 * Set the grid unit size for the Y axis by how many units should equal the height of the
	 * view.
	 *
	 * @param unitsPerRoomHeight Number of units that fit in the view height.
	 */
	public void setGridHeight(double unitsPerRoomHeight) {
		setGridUnitY(getViewHeight() / unitsPerRoomHeight);
	}

	/**
	 * Set the dimensions of the grid that should fit inside the height and width of the view.
	 * @param unitsPerViewWidth Number of units that fit in the view width.
	 * @param unitsPerViewHeight Number of units that fit in the view height.
	 */
	public void setGridDimensions(double unitsPerViewWidth, double unitsPerViewHeight) {
		setGridUnitX(getViewWidth() / unitsPerViewWidth);
		setGridUnitY(getViewHeight() / unitsPerViewHeight);
	}

	/**
	 * Returns the size of one grid unit in the X axis.
	 * @return the size of one grid unit in the X axis.
	 */
	public double getGridUnitX() {
		return gridUnitX;
	}

	/**
	 * Returns the size of one grid unit in the Y axis.
	 * @return the size of one grid unit in the Y axis.
	 */
	public double getGridUnitY() {
		return gridUnitY;
	}

	/**
	 * Returns the number of grid units that can fit in the width of the BobView.
	 *
	 * @return number of X axis grid units that fit in the width of the view.
	 */
	public double getWidth() {
		return gridWidth;
	}

	/**
	 * Returns the number of grid units that can fit in the height of the BobView.
	 *
	 * @return number of Y axis grid units that fit in the height of the view.
	 */
	public double getHeight() {
		return gridHeight;
	}

	/**
	 * Add a new GameObject to this room. Must be done for each GameObject to be
	 * draw in this room.
	 *
	 * @param o
	 *            - GameObject to add.
	 */
	public void addObject(Entity o) {
		addComponent(o);
	}

	/**
	 * Removes a GameObject from this room. If possible, consider moving the
	 * object off screen instead.
	 *
	 * @param o
	 *            - GameObject to remove.
	 */
	public void deleteObject(Entity o) {
		removeComponent(o);
	}

	/**
	 * Removes all GameObjects from this room.
	 */
	public void clearObjects() {
		getComponents().clear();
	}

	/**
	 * Indicate to the GraphicsHelper that the Graphics used by GameObjects in this Room
	 * have been used. This method is automatically called each frame if this Room
	 * is the current Room.
	 */
	public void indicateGraphicsUsed() {
		for (Renderable r: renderables) {
			if (r.getGraphic() != null) r.getGraphic().indicateUsed(getView().getGraphicsHelper().getCleanupsTilRemoval());
		}
	}

	/**
	 * Clean up all the Graphics used by GameObjects in this Room. This will clean up
	 * all the Graphics used in this Room regardless of the number of times the Graphics
	 * have been through a cleanup.
	 */
	public void clearAllGraphics() {
		for (Renderable r: renderables) {
			if (r.getGraphic() != null) r.getGraphic().forceCleanup();
		}
	}

	/**
	 * Returns the height of the BobView containing this Room.
	 *
	 * @return Height of the view, in pixels.
	 * */
	public int getViewHeight() {
		return view.getHeight();
	}

	/**
	 * Returns the width of the BobView containing this Room.
	 *
	 * @return Width of the view, in pixels.
	 */
	public int getViewWidth() {
		return view.getWidth();
	}

	/**
	 * Returns the screen width correction ratio for dealing with different size
	 * screens. This ratio is based off of the initial orientation of the device
	 * when the BobView is initialized!
	 */
	@Deprecated
	public double getRatioX() {
		return getView().getRatioX();
	}

	/**
	 * Returns the screen height correction ratio for dealing with different
	 * size screens. This ratio is based off of the initial orientation of the
	 * device when the BobView is initialized!
	 */
	@Deprecated
	public double getRatioY() {
		return getView().getRatioY();
	}

	/**
	 * Returns the number of layers this room renders.
	 * @return The number of layers this room renders.
	 */
	public int getNumLayers() {
		return layers;
	}

	/**
	 * Change the x position of the camera.
	 */
	public void setCameraX(double x) {
		camX = x;
	}

	/**
	 * Change the y position of the camera.
	 */
	public void setCameraY(double y) {
		camY = y;
	}

	/**
	 * Set the anchor point for zooming the camera. </br></br>
	 *
	 * HINT: this point will stay in the same location on the screen when zooming
	 * in and out.
	 *
	 * @param x anchor x position
	 * @param y anchor y position
	 */
	public void setCameraAnchor(int x, int y) {
		cAnchorX = x;
		cAnchorY = y;
	}

	/**
	 * Get the current x position of the camera.
	 */
	public double getCameraX() {
		return camX;
	}

	/**
	 * Get the current y position of the camera.
	 */
	public double getCameraY() {
		return camY;
	}

	/**
	 * Get the coordinate of the left edge of the camera.
	 */
	public float getCameraLeftEdge() {
		return camLeft;
	}

	/**
	 * Get the coordinate of teh right edge of the screen.
	 */
	public float getCameraRightEdge() {
		return camRight;
	}

	/**
	 * Get the coordinate of the bottom edge of the screen.
	 */
	public float getCameraBottomEdge() {
		return camBottom;
	}

	/**
	 * Get the coordinate of the top edge of the screen.
	 */
	public float getCameraTopEdge() {
		return camTop;
	}

	/**
	 * Set the zoom factor of the camera.
	 */
	public void setCameraZoom(double zoom) {
		camZoom = zoom;
	}

	/**
	 * Get the current zoom factor of the camera.
	 */
	public double getCameraZoom() {
		return camZoom;
	}

	/**
	 * Gathers the vertex, texture, and index data for each GameObject in this
	 * room and passes that information to openGL. Can be called from another
	 * room's draw method to draw both rooms at once. If overridden, call
	 * super.draw(gl).
	 *
	 * @param gl OpenGL ES 1.0 object to do pass drawing information to.
	 */
	public void draw(GL10 gl) {
		// Update camera
		gl.glMatrixMode(GLES10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(getCameraLeftEdge(), getCameraRightEdge(), getCameraBottomEdge(), getCameraTopEdge(), -1, 1);

		// Draw graphics
		gl.glMatrixMode(GLES10.GL_MODELVIEW);
		gl.glLoadIdentity();

		for (int l = 0; l < layers; l++) {
			for (int i = 0; i < renderables.size(); i++) {
				Renderable r = renderables.get(i);

				if (r.getGraphic() != null && r.getGraphic().shouldLoad()) {     // Load the graphic if needed
					getView().getGraphicsHelper().addGraphic(r.getGraphic());
				}

				r.render(gl, l);
			}
		}
	}

	/**
	 * Executes the update events for each GameObject in this room. This method
	 * also handles changes to an object's layer. Can be called from another
	 * room's step event to update both rooms at once. If overridden, call
	 * super.update(deltaTime).
	 *
	 * @param deltaTime Lag correction multiplier.
	 */
	public void update(double deltaTime) {
		// Handle input events
		for (int i = 0; i < Touch.MAX_FINGERS; i++) {
			if (newpress[i]) {
				newpress(i);

				for (int h = 0; h < touchInputHandlers.size(); h++) {
					touchInputHandlers.get(h).newpress(i);
				}

				newpress[i] = false;
			}

			if (released[i]) {
				released(i);

				for (int h = 0; h < touchInputHandlers.size(); h++) {
					touchInputHandlers.get(h).released(i);
				}

				released[i] = false;
			}
		}

		for (int i = 0; i < Gamepad.MAX_CONTROLLERS; i++) {
			if (buttonNewpress[i] != -1) {
				newpress(i, buttonNewpress[i]);

				for (int g = 0; g < gamepadInputHandlers.size(); g++) {
					gamepadInputHandlers.get(g).newpress(i, buttonNewpress[i]);
				}

				buttonNewpress[i] = -1;
			}

			if (buttonReleased[i] != -1) {
				released(i, buttonReleased[i]);

				for (int g = 0; g < gamepadInputHandlers.size(); g++) {
					gamepadInputHandlers.get(g).released(i, buttonReleased[i]);
				}

				buttonReleased[i] = -1;
			}
		}

		// Handle the step event
		step(deltaTime);

		// Update camera edges
		camLeft = (float) (camX * gridUnitX + cAnchorX - getView().getRenderer().getCameraWidth() * camZoom * (cAnchorX / getView().getRenderer().getCameraWidth()));
		camRight = (float) (camX * gridUnitX + cAnchorX + getView().getRenderer().getCameraWidth() * camZoom * ((getView().getRenderer().getCameraWidth() - cAnchorX) / getView().getRenderer().getCameraWidth()));
		camTop = (float) (camY * gridUnitY + cAnchorY + getView().getRenderer().getCameraHeight() * camZoom * ((getView().getRenderer().getCameraHeight() - cAnchorY) / getView().getRenderer().getCameraHeight()));
		camBottom = (float) (camY * gridUnitY + cAnchorY - getView().getRenderer().getCameraHeight() * camZoom * (cAnchorY / getView().getRenderer().getCameraHeight()));

		// Update each object
		for (int u = 0; u < updatables.size(); u++) {
			updatables.get(u).update(deltaTime);
		}
	}

	/**
	 * Event that happens every frame. Can be overridden.
	 *
	 * @param deltaTime
	 *            - [Time the last frame took]/[60 FPS] Will be 1 if the game is
	 *            running at 60FPS, > 1 if the game is running slow, and < 1 if
	 *            the game is running fast.
	 */
	public void step(double deltaTime) {

	}

	void updateComponentLists() {
		// Refresh special component lists
		ArrayList<Component> components = getEntireComponentTree();

		renderables.clear();
		updatables.clear();
		touchInputHandlers.clear();
		gamepadInputHandlers.clear();

		for (int i = 0; i < components.size(); i++) {
			Component c = components.get(i);

			if (c instanceof Updatable) {
				updatables.add((Updatable) c);
			}

			if (c instanceof Renderable) {
				renderables.add((Renderable) c);
			}

			if (c instanceof TouchInputHandler) {
				touchInputHandlers.add((TouchInputHandler) c);
			}

			if (c instanceof GamepadInputHandler) {
				gamepadInputHandlers.add((GamepadInputHandler) c);
			}
		}
	}

	/**
	 * Tell this room to handle a newpress input event on the main thread.
	 * @param index ID number of the pointer that triggered this event.
	 */
	public void signifyNewpress(int index) {
		newpress[index] = true;
	}

	/**
	 * Tell this room to handle a newpress button event on the main thread.
	 * @param controller the controller that triggered this event.
	 * @param button the button that triggered this event
	 */
	public void signifyNewpress(int controller, int button) {
		buttonNewpress[controller] = button;
	}

	/**
	 * Tell this room to handle a release input event on the main thread.
	 * @param index ID number of the pointer that triggered this event.
	 */
	public void signifyReleased(int index) {
		released[index] = true;
	}

	/**
	 * Tell this room to handle a released button event on the main thread.
	 * @param controller the controller that triggered this event.
	 * @param button the button that triggered this event
	 */
	public void signifyReleased(int controller, int button) {
		buttonReleased[controller] = button;
	}

	/**
	 * Touch screen newpress event. Executes the newpress event for each
	 * GameObject in this room. Can be overridden, but be sure to call
	 * super.newpress() in your override method.
	 */
	public void newpress(int index) {

	}

	/**
	 * Touch screen release event. Executes the release event for each
	 * GameObject in this room. Can be overridden, but be sure to call
	 * super.release() in your override method.
	 */
	public void released(int index) {

	}

	/**
	 * Gamepad newpress event. Executes the newpress event for each
	 * GameObject in this room. Can be overridden, but be sure to call
	 * super.newpress() in your override method.
	 */
	public void newpress(int player, int button) {

	}

	/**
	 * Gamepad release event. Executes the release event for each
	 * GameObject in this room. Can be overridden, but be sure to call
	 * super.release() in your override method.
	 */
	public void released(int player, int button) {

	}
}
