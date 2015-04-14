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

	/** This object's number of collision boxes. */
	private int colBoxes;

	// Objects
	/** The room that this object is in. */
	protected Room myRoom;


	/**
	 * This object's collision boxes. Use giveCollisionBox() to give this object
	 * a new collision box.
	 */
	public double box[][];

	private int numQuads;
	private Quad[] quads;
	protected Quad main;

	/**
	 * Initialization. Requires a unique Id number and the room containing this
	 * GameObject.
	 * @param id - ID number
	 * @param containingRoom - Room that this object is in.
	 */
	public GameObject(int id, Room containingRoom) {
		this.id = id;
		myRoom = containingRoom;

		x = y = width = height = 100;

		frame = 0;
		angle = 0;
		box = new double[MAX_COL_BOXES][4];
		colBoxes = 0;
		layer = 2;

		main = new Quad();
		main.x = x;
		main.y = y;
		main.height = height;
		main.width = width;
		main.angle = angle;

		numQuads = 1;
		quads = new Quad[DEFAULT_NUM_QUADS];
		quads[0] = main;
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
		return main.getGraphicID();
	}

	public Graphic getGraphic() {
		return main.getGraphic();
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
		main.setGraphic(graphic);
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
		main.setGraphic(graphic, frames);
	}

	/**
	 * Set the graphic for this object with a specific number of columns of frames.
	 *
	 * @param graphic
	 * @param columns The number of columns of frames
	 * @param framesPerColumn The number of frames in a column
	 */
	public void setGraphic(Graphic graphic, int columns, int framesPerColumn) {
		main.setGraphic(graphic, columns, framesPerColumn);
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
		main.setPreciseGraphic(graphicSheet, x, y, height, width, frames);
	}

	/**
	 * Updates this object's vertices to the specified x and y positions.
	 *
	 * <br />
	 * <b>It is better to change the x and y properties of this object instead.
	 * Your call to this function will likely be overridden.</b>
	 *
	 * @param x
	 * @param y
	 */
	public void updatePosition(double x, double y) {
		main.x = x;
		main.y = y;
		main.updatePosition();
	}

	/**
	 * Changes this object's current frame. Do not call this method directly.
	 * Change the value of variable "frame" instead.
	 *
	 * @param frame
	 *            - the new frame
	 */
	protected void setFrame(int frame) {
		main.setFrame(frame);
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
	 *
	 * @param q
	 */
	public void addQuad(Quad q) {
		numQuads++;
		quads[numQuads - 1] = q;
	}

	/**
	 *
	 * @param i
	 * @param q
	 */
	public void setQuad(int i, Quad q) {
		quads[i] = q;
	}

	/**
	 * Gets the vertex data for drawing this object. Can be overridden for
	 * advanced purposes but be sure to also create getGraphicVerts() and
	 * getIndices() overrides that match your getVertices() function.
	 * 
	 * @return This object's vertices.
	 */
	public float[] getVertices() {
		float verts[] = null;
		for (int i = 0; i < numQuads; i++) {
			if (quads[i].onScreen()) verts = concatenate(verts, quads[i].getVertices());
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
		float coords[] = null;
		for (int i = 0; i < numQuads; i++) {
			if (quads[i].onScreen()) coords = concatenate(coords, quads[i].getGraphicVerts());
		}
		return coords;
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

		for (int i = 0; i < numQuads; i++) {
			if (quads[i].onScreen()) quadsOnScreen++;
		}

		return 6*quadsOnScreen;
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

		for (int i = 0; i < numQuads; i++) {
			quads[i].update(deltaTime);
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
		for (int i = 0; i < numQuads; i++) {
			if (quads[i].onScreen()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * A textured quad with basic attributes such as x, y, height, width, etc...
	 */
	public class Quad {
		public double x;        // X coordinate, midpoint
		public double y;        // Y coordinate, midpoint
		public double height;   // height of this quad
		public double width;    // width of this quad
		public double angle;    // Rotation angle of this quad

		private Graphic myGraphic;
		private int frame;
		private int animFPS;
		private int startFrame;
		private int endFrame;
		private int frameCount;
		private float tX;
		private float tY;
		private float animHeight;
		private float animWidth;
		private int totalFrames;
		private int frameRow;

		public float vertices[];
		public float graphicCoords[];

		public Quad() {
			x = y = height = width = 100;
			angle = 0;

			totalFrames = 1;
			frameRow = 1;
			animFPS = 0;
			startFrame = 0;
			endFrame = totalFrames - 1;

			vertices = new float[12];
			graphicCoords = new float[8];
		}

		/**
		 * Update event that happens every frame.
		 * @param deltaTime
		 */
		public void update(double deltaTime) {
			updatePosition();
			setFrame(frame);

			// Animate
			if (animFPS > 0) {
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
					if (startFrame < endFrame) frame++;
					else frame--;
					frameCount = 0;
				} else {
					frameCount++;
				}

				if (startFrame <= endFrame) {
					if (frame > endFrame) {
						frame = startFrame;
					}
				} else {
					if (frame < endFrame) {
						frame = startFrame;
					}
				}
			}
		}

		/**
		 * Updates this quad's vertices.
		 */
		public void updatePosition() {
			// Data
			double sin;
			double cos;
			int y = (int) this.y;
			int x = (int) this.x;

			if (angle != 0) {                          // Don't do unnecessary calculations
				cos = (double) Math.cos(Math.toRadians(angle + 180));
				sin = (double) Math.sin(Math.toRadians(angle + 180));

				vertices[0] = (float) ((x - (x - width / 2)) * cos - (y - (y - height / 2)) * sin + x);   // Bottom Left X
				vertices[1] = (float) ((x - (x - width / 2)) * sin + (y - (y - height / 2)) * cos + y);   // Bottom Left Y
				vertices[3] = (float) ((x - (x - width / 2)) * cos - (y - (y + height / 2)) * sin + x);   // Top Left X
				vertices[4] = (float) ((x - (x - width / 2)) * sin + (y - (y + height / 2)) * cos + y);   // Top Left Y
				vertices[6] = (float) ((x - (x + width / 2)) * cos - (y - (y - height / 2)) * sin + x);   // Bottom Right X
				vertices[7] = (float) ((x - (x + width / 2)) * sin + (y - (y - height / 2)) * cos + y);   // Bottom Right Y
				vertices[9] = (float) ((x - (x + width / 2)) * cos - (y - (y + height / 2)) * sin + x);   // Top Right X
				vertices[10] = (float) ((x - (x + width / 2)) * sin + (y - (y + height / 2)) * cos + y);  // Top Right Y
			} else {
				vertices[0] = (float) (x - width / 2);     // Bottom Left X
				vertices[1] = (float) (y - height / 2);    // Bottom Left Y
				vertices[3] = vertices[0];                 // Top Left X (Same as Bottom X)
				vertices[4] = (float) (y + height / 2);    // Top Left Y
				vertices[6] = (float) (x + width / 2);     // Bottom Right X
				vertices[7] = vertices[1];                 // Bottom Right Y (Same as Left Y)
				vertices[9] = vertices[6];                 // Top Right X (Same as Bottom X)
				vertices[10] = vertices[4];                // Top Right Y (Same as Left Y)
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
		 * Set the graphic for this quad with 1 frame. For better performance
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
		 * Set the graphic for this quad with a specific number of frames. For better performance
		 * when using many graphics, put multiple graphics onto a single graphic sheet and use
		 * setGraphic(Graphic graphic, int x, int y, ...) or setPreciseGraphic(...).
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
		 * @param graphic
		 * @param columns The number of columns of frames
		 * @param framesPerColumn The number of frames in a column
		 */
		public void setGraphic(Graphic graphic, int columns, int framesPerColumn) {
			setPreciseGraphic(graphic, 0, 0, 1, 1f / columns, framesPerColumn);
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
			tX = x / graphicSheet.width;
			tY = y / graphicSheet.height;

			animWidth = width / graphicSheet.width;
			animHeight = height / graphicSheet.height;
			frameRow = frames;
		}

		/**
		 * Set graphic information for this quad with precise values for x, y,
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
			tX = x;
			tY = y;

			animWidth = width;
			animHeight = height;
			frameRow = frames;
		}

		/**
		 * Changes this quad's current frame. Do not call this method directly.
		 * Change the value of variable "frame" instead.
		 *
		 * @param frame
		 *            - the new frame
		 */
		protected void setFrame(int frame) {
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

			graphicCoords[0] = leftX;
			graphicCoords[1] = bottomY;
			graphicCoords[2] = leftX;
			graphicCoords[3] = topY;
			graphicCoords[4] = rightX;
			graphicCoords[5] = bottomY;
			graphicCoords[6] = rightX;
			graphicCoords[7] = topY;
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
		}

		/**
		 * Gets the vertex data for drawing this quad. Can be overridden for
		 * advanced purposes but be sure to also create getGraphicVerts() and
		 * getIndices() overrides that match your getVertices() function.
		 *
		 * @return This object's vertices.
		 */
		public float[] getVertices() {
			return vertices;
		}

		/**
		 * Gets the graphic coordinate data for drawing this quad. Can be
		 * overridden for advanced purposes but be sure to also create getVertices()
		 * and getIndices() overrides that match your getGraphicVerts() function.
		 *
		 * @return This object's graphic coordinates.
		 */
		public float[] getGraphicVerts() {
			return graphicCoords;
		}

		/**
		 * Determines if this quad is on the screen or beyond the edge of the
		 * screen.
		 *
		 * @return True if this object is on the screen or false if the object is
		 *         beyond the screen's bounds.
		 */
		public boolean onScreen() {
			boolean flip = false;
			if (width < 0) flip = true;
			width = Math.abs(width);

			if (x > -width / 2 + getRoom().getCameraLeftEdge() && x < width / 2 + getRoom().getCameraRightEdge()) {
				if (y > -height / 2 + getRoom().getCameraBottomEdge() && y < height / 2 + getRoom().getCameraTopEdge()) {
					if (flip) width = -width;
					return true;
				}
			}

			if (flip) width = -width;

			return false;
		}
	}
}
