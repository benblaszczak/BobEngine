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

import android.app.Activity;
import android.util.Log;


/**
 * Super class for all the game objects in the game.
 * 
 * @author Ben
 * @version alpha
 */

public class GameObject {
	// Constants
	private final int MAX_COL_BOXES = 10;       // The maximum number of collision boxes this GameObject can have
	private final int DEFAULT_NUM_QUADS = 5;    // The default max number of quads

	// Data
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
	/** The layer that this object is to be drawn on. */
	public int layer;
	/** This object's ID number. */
	public int id;
	/** This object's current frame. */
	public int frame;
	public boolean visible;

	/** This object's number of collision boxes. */
	private int colBoxes;

	// Objects
	/** The room that this object is in. */
	protected Room myRoom;

	private Graphic myGraphic;

	/**
	 * This object's collision boxes. Use giveCollisionBox() to give this object
	 * a new collision box.
	 */
	public double box[][];

	private Quad[] quads;
	protected Quad main;
	private int maxQuads;

	/**
	 * Create a GameObject with the specified id number and specifed room
	 * that you intend to add this object to. Does not automatically add this
	 * object to the room.
	 * @param id - ID number
	 * @param room - Room that this object is in.
	 */
	public GameObject(int id, Room room) {
		init(id, room, false);
	}

	/**
	 * Create a GameObject in the specified room.
	 * @param room The room that this object is in.
	 */
	public GameObject(Room room) {
		init(room.nextInstance(), room, true);
	}

	/**
	 * Initialization. Requires a unique Id number and the room containing this
	 * GameObject.
	 * @param id - ID number
	 * @param room - Room that this object is in.
	 */
	private void init(int id, Room room, boolean addToRoom) {
		this.id = id;
		myRoom = room;
		if (addToRoom) room.addObject(this);

		myGraphic = new Graphic();

		x = y = width = height = 100;

		frame = 0;
		angle = 0;
		box = new double[MAX_COL_BOXES][4];
		colBoxes = 0;
		layer = 2;
		visible = true;

		quads = new Quad[DEFAULT_NUM_QUADS];

		main = new Quad();
		main.x = x;
		main.y = y;
		main.height = height;
		main.width = width;
		main.angle = angle;
		main.visible = true;

		maxQuads = DEFAULT_NUM_QUADS;
	}

	/**
	 * Give this GameObject a collision box. Each coordinate has a range of 0 to
	 * 1. 0 is the left/top edge of the object and 1 is the right/bottom edge.
	 * 
	 * @param x1
	 *            - Left edge of the collision box
	 * @param y1
	 *            - Top edge
	 * @param x2
	 *            - Right edge
	 * @param y2
	 *            - Bottom edge
	 */
	public void giveCollisionBox(double x1, double y1, double x2, double y2) {

		if (colBoxes < MAX_COL_BOXES) {
			box[colBoxes][0] = x1;
			box[colBoxes][1] = x2;
			box[colBoxes][2] = y1;
			box[colBoxes][3] = y2;

			colBoxes++;
		} else {
			Log.d("BobEngine", "Hit maximum number of collision boxes.");
		}
	}
	
	/**
	 * Returns the number of collision boxes this object has.
	 */
	public int getNumColBoxes() {
		return colBoxes;
	}

	/**
	 * Returns this object's graphic's ID number
	 */
	public int getGraphicID() {
		return myGraphic.id;
	}

	public Graphic getGraphic() {
		return myGraphic;
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
		myGraphic = graphic;
		main.setGraphic(1);
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
		myGraphic = graphic;
		main.setGraphic(frames);
	}

	/**
	 * Set the graphic for this object with a specific number of columns of frames.
	 *
	 * @param graphic
	 * @param columns The number of columns of frames
	 * @param rows The number of frames in a column
	 */
	public void setGraphic(Graphic graphic, int columns, int rows) {
		myGraphic = graphic;
		main.setGraphic(columns, rows);
	}

	public void setGraphic(Graphic.Parameters params) {
		myGraphic = params.graphic;
		main.setGraphic(params);
	}

	/**
	 * Set graphic information for this object using pixel coordinates. Should only be used
	 * when there is only one drawable folder.
	 * <br/>
	 * <br/>
	 * NOTE: if a graphic has more than one frame, they should all be the same size and
	 * be stacked vertically in the same image file.
	 *
	 * @param graphicSheet
	 *            - The graphic sheet to use.
	 * @param x
	 *            - The x coordinate of the graphic on the sheet, in pixels.
	 * @param y
	 *            - The y coordinate of the graphic on the sheet, in pixels.
	 * @param height
	 *            - The height of a single frame of the graphic on the sheet, in
	 *            pixels.
	 * @param width
	 *            - The width of a single frame of the graphic on the sheet, in
	 *            pixels.
	 * @param frames
	 *            - The number of frames the graphic has.
	 */
	public void setGraphic(Graphic graphicSheet, int x, int y, int height, int width, int frames) {
		main.setGraphic(graphicSheet, x, y, height, width, frames);
	}

	/**
	 * Set graphic information for this object with precise values for x, y,
	 * width, height. Use this when there is more than one drawable folder.
	 *
	 * @param graphicSheet
	 *            - The graphic sheet to use.
	 * @param x
	 *            - The x coordinate of the graphic on the sheet, from 0 to 1.
	 * @param y
	 *            - The y coordinate of the graphic on the sheet, from 0 to 1.
	 * @param height
	 *            - The height of a single frame of the graphic on the sheet,
	 *            from 0 to 1.
	 * @param width
	 *            - The width of a single frame of the graphic on the sheet,
	 *            from 0 to 1.
	 * @param frames
	 *            - The number of frames the graphic has.
	 */
	public void setPreciseGraphic(Graphic graphicSheet, float x, float y, float height, float width, int frames) {
		myGraphic = graphicSheet;
		main.setPreciseGraphic(x, y, height, width, frames);
	}

	/**
	 * Animates this GameObject. Will loop through start frame to end frame at a
	 * speed of FPS frames per second.
	 * 
	 * @param FPS
	 *            - the speed of the animation in frames per second
	 * @param start
	 *            - the first frame of the animation.
	 * @param end
	 *            - the last frame of the animation.
	 */
	public void animate(int FPS, int start, int end) {
		main.animate(FPS, start, end);
	}

	/**
	 * Animate this GameObject using a predefined animation.
	 * @param anim - A predefined animation.
	 */
	public void animate(Animation anim) {
		main.animate(anim);
	}

	/**
	 * Animates this GameObject for a limited number of times.
	 *
	 * @param FPS speed
	 * @param start start frame
	 * @param end end frame
	 * @param times times to play
	 */
	public void animateLimited(int FPS, int start, int end, int times) {
		main.animateLimited(FPS, start, end, times);
	}

	/**
	 * The animation will have finished when the final frame in the animation has been
	 * shown for the correct amount of time determined by the FPS set by the call
	 * to the animate() method.
	 *
	 * @return True for a single frame when the animation has finished, false otherwise.
	 */
	public boolean animationFinished() {
		return main.animationFinished();
	}

	/**
	 * Add a quad to the first empty space in the list. NOTE: if the list is full,
	 * this method will double the size of the list to make room for the new quad.
	 *
	 * @param q The Quad to add
	 */
	public void addQuad(Quad q) {
		int i = 0;

		while (i < quads.length && quads[i] != null) {
			i++;
		}

		if (i == quads.length) {
			setMaxQuads(getMaxQuads() * 2);
		}

		quads[i] = q;
	}

	/**
	 * Set the Quad at a specific spot in the list. This will overwrite
	 * the quad that currently occupies that place in the list. By default,
	 * the main quad for this GameObject is in place 0. This can be useful
	 * for reordering the list to determine the order in which the quads are
	 * drawn. Quad 0 is drawn first, therefor will be behind all the other
	 * quads.
	 *
	 * @param i - The place in the list to put q
	 * @param q - The new quad for place i
	 */
	public void setQuad(int i, Quad q) {
		quads[i] = q;
	}

	/**
	 * Resize the list of quads by setting a new max number of quads.
	 * If set to a number less than the current max, quads at the end
	 * of the list will simply be removed from the list.
	 *
	 * @param max
	 */
	public void setMaxQuads(int max){
		if (max != maxQuads) {
			Quad temp[] = quads.clone();

			quads = new Quad[max];

			for (int i = 0; i < max && i < temp.length; i++) {
				quads[i] = temp[i];
			}

			maxQuads = max;
		}
	}

	/**
	 * Returns the max number of quads that can be added to this GameObject.
	 * This can be changed with setMaxQuads(int max).
	 * @return
	 */
	public int getMaxQuads() {
		return quads.length;
	}

	/**
	 * The main quad shares the attributes of this GameObject.
	 * @return This GameObject's main Quad.
	 */
	public Quad getMainQuad() {
		return main;
	}

	public Quad getQuad(int i) {
		if (i > 0 && i < quads.length) return quads[i];
		else return null;
	}

	/**
	 * Gets the vertex data for drawing this object. Can be overridden for
	 * advanced purposes but be sure to also create getGraphicVerts() and
	 * getIndices() overrides that match your getVertices() function.
	 * 
	 * @return This object's vertices.
	 */
	public float[] getVertices() {
		float verts[];
		int numQuads = 0;
		int maxQuads = quads.length;
		int cursor = 0;

		for (int i = 0; i < maxQuads; i++) {
			if (quads[i] != null && quads[i].visible && visible) numQuads++;
		}

		verts = new float[numQuads * Quad.VERT_SIZE];

		for (int i = 0; i < maxQuads; i++) {
			if (quads[i] != null && quads[i].visible && visible) cursor = quads[i].getVertices(cursor, verts);
		}

		return verts;
	}

	/**
	 * Gets the graphic coordinate data for drawing this object. Can be
	 * overridden for advanced purposes but be sure to also create getVertices()
	 * and getIndices() overrides that match your getGraphicVerts() function.
	 * 
	 * @return This object's graphic coordinates.
	 */
	public float[] getGraphicVerts() {
		float verts[];
		int numQuads = 0;
		int cursor = 0;

		for (int i = 0; i < getMaxQuads(); i++) {
			if (quads[i] != null && quads[i].visible && visible) numQuads++;
		}

		verts = new float[numQuads * Quad.GFX_VERT_SIZE];

		for (int i = 0; i < getMaxQuads(); i++) {
			if (quads[i] != null && quads[i].visible && visible) cursor = quads[i].getGraphicVerts(cursor, verts);
		}

		return verts;
	}

	/**
	 * Gets the number of indices for drawing this object. Can be overridden for
	 * advanced purposes but be sure to also create getVertices() and
	 * getGraphicVerts() overrides that match your getIndices() function.
	 * 
	 * @return The number of indices needed to draw this object.
	 */
	public int getIndices() {
		int quadsOnScreen = 0;

		for (int i = 0; i < getMaxQuads(); i++) {
			if (quads[i] != null && quads[i].visible && visible) quadsOnScreen++;
		}

		return 6 * quadsOnScreen;
	}
	
	/**
	 * Returns an array containing the contents of a and b.
	 * Useful for rendering more than one quad with one game
	 * object. Either a or b can be null. If both are null, the
	 * function will return null.
	 * 
	 * @param a
	 * @param b
	 * @return {a + b}, or null if both a and b are null
	 */
	public float[] concatenate(float a[], float b[]) {
		if (a != null && b != null) {
			float[] totVert = new float[a.length + b.length];

			for (int i = 0; i < a.length; i++) {
				totVert[i] = a[i];
			}

			for (int i = 0; i < b.length; i++) {
				totVert[a.length + i] = b[i];
			}

			return totVert;
		}
		else if (a == null) {
			return b;
		}
		else if (b == null) {
			return a;
		}
		else {
			return null;
		}
	}

	/**
	 * Returns this game object's containing room.
	 */
	public Room getRoom() {
		return myRoom;
	}
	
	/**
	 * Returns the BobView containing this GameObject
	 */
	public BobView getView() {
		return myRoom.getView();
	}
	
	/**
	 * Returns the Activity containing the BobView that contains this GameObject.
	 */
	public Activity getActivity() {
		return myRoom.getActivity();
	}
	
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
	public Controller getController() {
		return getView().getController();
	}

	/**
	 * Update event that happens every frame. Super class method handles position
	 * updating, frame changing, and animation. You should override the step() method
	 * for game logic.
	 * 
	 * @param deltaTime
	 */
	public void update(double deltaTime) {
		step(deltaTime);
		main.x = x;
		main.y = y;
		main.height = height;
		main.width = width;
		main.angle = angle;
		main.frame = frame;

		for (int i = 0; i < getMaxQuads(); i++) {
			if (quads[i] != null) quads[i].update(deltaTime);
		}

		frame = main.frame;
	}
	
	/**
	 * Event that happens every frame. Can be overridden.
	 * 
	 * @param deltaTime - [Time the last frame took]/[60 FPS] <br /> 
	 * Will be 1 if the game is running at 60FPS, > 1 if the game
	 * is running slow, and < 1 if the game is running fast.
	 */
	public void step (double deltaTime) {
		
	}

	/**
	 * Touch screen newpress event. Can be overridden, no need to called
	 * super.newpress().
     *
     * @param index
     *           - The number ID of the pointer that triggered this newpress
	 */
	public void newpress(int index) {

	}

	/**
	 * Gamepad newpress event. Can be overridden, no need to called
	 * super.newpress().
	 *
	 * @param controller The player number that caused this event.
	 * @param button The ID of the button that was pressed
	 */
	public void newpress(int controller, int button) {

	}

	/**
	 * Touch screen released event. Can be overridden, no need to call
	 * super.released().
     *
     * @param index
     *           - The number ID of the pointer that triggered this release
	 */
	public void released(int index) {

	}

	/**
	 * Gamepad released event. Can be overridden, no need to call
	 * super.released().
	 *
	 * @param controller The player number that caused this event.
	 * @param button The ID of the button that was pressed
	 */
	public void released(int controller, int button) {

	}

	/**
	 * Determines if this object is on the screen or beyond the edge of the
	 * screen.
	 * 
	 * @return True if this object is on the screen or false if the object is
	 *         beyond the screen's bounds.
	 */
	public boolean onScreen() {
		for (int i = 0; i < getMaxQuads(); i++) {
			if (quads[i] != null && quads[i].onScreen()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * A textured quad with basic attributes such as x, y, height, width, etc...
	 */
	public class Quad {
		// Constants
		public static final int VERT_SIZE = 8;
		public static final int GFX_VERT_SIZE = 8;

		// General Attributes
		public double x;        // X coordinate, midpoint
		public double y;        // Y coordinate, midpoint
		public double height;   // height of this quad
		public double width;    // width of this quad
		public double angle;    // Rotation angle of this quad
		public boolean visible; // Visibility of this object

		// Animation variables and attributes
		public int frame;
		private int animFPS;
		private int startFrame;
		private int endFrame;
		private int timesTilStop;
		private int timesPlayed;
		private int frameCount;
		private boolean animFinished;

		// Texture attributes
		private float tX;
		private float tY;
		private float animHeight;
		private float animWidth;
		private int totalFrames;
		private int frameRow;

		// States
		private boolean isOnScreen;

		public Quad() {
			x = y = height = width = 100;
			angle = 0;
			visible = true;

			totalFrames = 1;
			frameRow = 1;
			animFPS = 0;
			startFrame = 0;
			endFrame = totalFrames - 1;
			timesPlayed = 0;
			timesTilStop = 0;
			animFinished = false;

			setGraphic(1);
			addQuad(this);
		}

		/**
		 * Update event that happens every frame.
		 * @param deltaTime
		 */
		public void update(double deltaTime) {
			isOnScreen = false;

			// Animate
			animFinished = false;

			if (animFPS > 0 && !(timesPlayed >= timesTilStop && timesTilStop > 0)) {
				if (startFrame <= endFrame) {
					if (frame < startFrame || frame > endFrame) {
						frame = startFrame;
					}
				} else {
					if (frame > startFrame || frame < endFrame) {
						frame = startFrame;
					}
				}

				if (frameCount >= BobRenderer.FPS / animFPS) {
					if (startFrame <= endFrame) frame++;
					else frame--;
					frameCount = 0;
				} else {
					frameCount++;
				}

				if (startFrame <= endFrame) {
					if (frame > endFrame) {
						frame = startFrame;
						animFinished = true;
						timesPlayed++;
					}
				} else {
					if (frame < endFrame) {
						animFinished = true;
						frame = startFrame;
						timesPlayed++;
					}
				}
			} else {
				animFinished = true;
			}

			if (timesPlayed >= timesTilStop && timesTilStop > 0) {
				frame = endFrame;
			}
		}

		/**
		 * Returns this quad's graphic's ID number
		 */
		public int getGraphicID() {
			return myGraphic.id;
		}

		public Graphic getGraphic() {
			return myGraphic;
		}

		/**
		 * Set the graphic for this quad with a specific number of frames. For better performance
		 * when using many graphics, put multiple graphics onto a single graphic sheet and use
		 * setGraphic(Graphic graphic, int x, int y, ...) or setPreciseGraphic(...).
		 *
		 * @param frames
		 *            - The number of frames this graphic has.
		 */
		public void setGraphic(int frames) {
			setPreciseGraphic(0, 0, 1, 1, frames);
		}

		/**
		 * Set the graphic for this object with a specific number of columns of frames.
		 *
		 * @param columns The number of columns of frames
		 * @param rows The number of frames in a column
		 */
		public void setGraphic(int columns, int rows) {
			setPreciseGraphic(0, 0, 1, 1f / (float) columns, rows);
		}

		public void setGraphic(Graphic.Parameters params) {
			setGraphic(params.graphic, params.x, params.y, params.height, params.width, params.rows);
		}

		/**
		 * Set graphic information for this quad using pixel coordinates. Should only be used
		 * when there is only one drawable folder.
		 * <br/>
		 * <br/>
		 * NOTE: if a graphic has more than one frame, they should all be the same size and
		 * be stacked vertically in the same image file.
		 *
		 * @param graphicSheet
		 *            - The graphic sheet to use.
		 * @param x
		 *            - The x coordinate of the graphic on the sheet, in pixels.
		 * @param y
		 *            - The y coordinate of the graphic on the sheet, in pixels.
		 * @param height
		 *            - The height of a single frame of the graphic on the sheet, in
		 *            pixels.
		 * @param width
		 *            - The width of a single frame of the graphic on the sheet, in
		 *            pixels.
		 * @param frames
		 *            - The number of frames the graphic has.
		 */
		public void setGraphic(Graphic graphicSheet, int x, int y, int height, int width, int frames) {
			myGraphic = graphicSheet;
			tX = (float) x / (float) graphicSheet.width;
			tY = (float) y / (float) graphicSheet.height;

			animWidth = (float) width / (float) graphicSheet.width;
			animHeight = (float) height / (float) graphicSheet.height;
			frameRow = frames;
		}

		/**
		 * Set graphic information for this quad with precise values for x, y,
		 * width, height. Use this when there is more than one drawable folder.
		 *
		 * @param x
		 *            - The x coordinate of the graphic on the sheet, from 0 to 1.
		 * @param y
		 *            - The y coordinate of the graphic on the sheet, from 0 to 1.
		 * @param height
		 *            - The height of a single frame of the graphic on the sheet,
		 *            from 0 to 1.
		 * @param width
		 *            - The width of a single frame of the graphic on the sheet,
		 *            from 0 to 1.
		 * @param frames
		 *            - The number of frames the graphic has.
		 */
		public void setPreciseGraphic(float x, float y, float height, float width, int frames) {
			tX = x;
			tY = y;

			animWidth = width;
			animHeight = height;
			frameRow = frames;
		}

		/**
		 * Animates this quad. Will loop through start frame to end frame at a
		 * speed of FPS frames per second.
		 *
		 * @param FPS
		 *            - the speed of the animation in frames per second
		 * @param start
		 *            - the first frame of the animation.
		 * @param end
		 *            - the last frame of the animation.
		 */
		public void animate(int FPS, int start, int end) {
			animFPS = FPS;
			startFrame = start;
			endFrame = end;
			timesTilStop = 0;
		}

		/**
		 * Animates this quad using a predefined animation.
		 * @param anim - a predefined animation.
		 */
		public void animate(Animation anim) {
			if (anim.fps != animFPS && anim.startFrame != startFrame && anim.endFrame != endFrame && anim.loop != timesTilStop) {
				timesPlayed = 0;
			}

			animFPS = anim.fps;
			startFrame = anim.startFrame;
			endFrame = anim.endFrame;
			timesTilStop = anim.loop;
		}

		/**
		 * Animates this quad for a limited number of times.
		 *
		 * @param FPS speed
		 * @param start start frame
		 * @param end end frame
		 * @param times times to play
		 */
		public void animateLimited(int FPS, int start, int end, int times) {
			if (FPS != animFPS && start != startFrame && end != endFrame && times != timesTilStop) {
				timesPlayed = 0;
			}

			animate(FPS, start, end);
			timesTilStop = times;
		}

		/**
		 * The animation will have finished when the final frame in the animation has been
		 * shown for the correct amount of time determined by the FPS set by the call
		 * to the animate() method.
		 *
		 * @return True for a single frame when the animation has finished, false otherwise.
		 */
		public boolean animationFinished() {
			return animFinished;
		}

		public int getVertices(int cursor, float[] allVertices) {
			// Data
			double sin;
			double cos;

			if (angle != 0) {                          // Don't do unnecessary calculations
				cos = Math.cos(Math.toRadians(angle + 180));
				sin = Math.sin(Math.toRadians(angle + 180));

				allVertices[cursor] = (float) ((x - (x - width / 2)) * cos - (y - (y - height / 2)) * sin + x);       // Bottom Left X
				allVertices[cursor + 1] = (float) ((x - (x - width / 2)) * sin + (y - (y - height / 2)) * cos + y);   // Bottom Left Y
				allVertices[cursor + 2] = (float) ((x - (x - width / 2)) * cos - (y - (y + height / 2)) * sin + x);   // Top Left X
				allVertices[cursor + 3] = (float) ((x - (x - width / 2)) * sin + (y - (y + height / 2)) * cos + y);   // Top Left Y
				allVertices[cursor + 4] = (float) ((x - (x + width / 2)) * cos - (y - (y - height / 2)) * sin + x);   // Bottom Right X
				allVertices[cursor + 5] = (float) ((x - (x + width / 2)) * sin + (y - (y - height / 2)) * cos + y);   // Bottom Right Y
				allVertices[cursor + 6] = (float) ((x - (x + width / 2)) * cos - (y - (y + height / 2)) * sin + x);   // Top Right X
				allVertices[cursor + 7] = (float) ((x - (x + width / 2)) * sin + (y - (y + height / 2)) * cos + y);   // Top Right Y
			} else {
				allVertices[cursor] = (float) (x - width / 2);         // Bottom Left X
				allVertices[cursor + 1] = (float) (y - height / 2);    // Bottom Left Y
				allVertices[cursor + 2] = allVertices[cursor + 0];     // Top Left X (Same as Bottom X)
				allVertices[cursor + 3] = (float) (y + height / 2);    // Top Left Y
				allVertices[cursor + 4] = (float) (x + width / 2);     // Bottom Right X
				allVertices[cursor + 5] = allVertices[cursor + 1];     // Bottom Right Y (Same as Left Y)
				allVertices[cursor + 6] = allVertices[cursor + 4];     // Top Right X (Same as Bottom X)
				allVertices[cursor + 7] = allVertices[cursor + 3];     // Top Right Y (Same as Left Y)
			}

			cursor += VERT_SIZE;
			return cursor;
		}

		public int getGraphicVerts(int cursor, float[] allGraphicVerts) {
			// Data
			float leftX;   // Left X coordinate of the frame on the graphic sheet
			float rightX;  // Right X coordinate
			float topY;    // Top Y coordinate
			float bottomY; // Bottom Y coordinate

			leftX = tX + animWidth * (frame / frameRow);
			rightX = tX + animWidth * ((frame / frameRow) + 1);
			topY = tY + (animHeight / frameRow) * (frame % frameRow);
			bottomY = tY + (animHeight / frameRow) + (animHeight / frameRow) * (frame % frameRow);

			leftX += 1f / (animWidth * myGraphic.width * 100f);
			rightX -= 1f / (animWidth * myGraphic.width * 100f);
			topY += 1f / (animHeight * myGraphic.height * 100f);
			bottomY -= 1f / (animHeight * myGraphic.height * 100f);

			allGraphicVerts[cursor] = leftX;
			allGraphicVerts[cursor + 1] = bottomY;
			allGraphicVerts[cursor + 2] = leftX;
			allGraphicVerts[cursor + 3] = topY;
			allGraphicVerts[cursor + 4] = rightX;
			allGraphicVerts[cursor + 5] = bottomY;
			allGraphicVerts[cursor + 6] = rightX;
			allGraphicVerts[cursor + 7] = topY;

			cursor += GFX_VERT_SIZE;
			return cursor;
		}

		/**
		 * Determines if this quad is on the screen or beyond the edge of the
		 * screen.
		 *
		 * @return True if this object is on the screen or false if the object is
		 *         beyond the screen's bounds.
		 */
		public boolean onScreen() {
			if (!visible) return false;

			if (!isOnScreen) {
				boolean flip = false;
				if (width < 0) flip = true;
				width = Math.abs(width);

				if (x > -width / 2 + getRoom().getCameraLeftEdge() && x < width / 2 + getRoom().getCameraRightEdge()) {
					if (y > -height / 2 + getRoom().getCameraBottomEdge() && y < height / 2 + getRoom().getCameraTopEdge()) {
						if (flip) width = -width;
						isOnScreen = true;
						return true;
					}
				}

				if (flip) width = -width;
				return false;
			} else {
				return true;
			}
		}
	}

	public class Animation {
		public int startFrame;
		public int endFrame;
		public int fps;
		public int loop;

		public Animation(int start, int end, int fps, int loop) {
			startFrame = start;
			endFrame = end;
			this.fps = fps;
			this.loop = loop;
		}
	}
}
