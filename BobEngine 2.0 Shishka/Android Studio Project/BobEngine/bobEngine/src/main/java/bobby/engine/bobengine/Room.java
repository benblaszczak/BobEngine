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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.app.Activity;
import android.opengl.GLES10;

/**
 * Rooms are collections of GameObjects. They handle updating and rendering each
 * object in the room.
 *
 * @author Ben
 *
 */
public class Room {
	// Constants
	public final int OBJECTS = 8000;                       // Maximum number of quads. This is kind of sloppy. I should find a better way to do this.
	public final int LAYERS = 5;                           // Default number of layers.
	public final int VERTEX_BYTES = 4 * 3 * 4 * OBJECTS;   // 4 bytes per float * 3 coords per vertex * 4 vertices * max objects
	public final int TEX_BYTES = 4 * 2 * 4 * OBJECTS;      // 4 bytes per float * 2 coords per vertex * 4 vertices
	public final int INDEX_BYTES = 4 * 4 * OBJECTS;        // 4 bytes per short * 4 indices per quad * max num of objects

	// Variables
	public int instances = 0;                              // The number of objects in this room
	public int index;                                      // The number of indices for all quads
	public short indices[] = new short[6];                 // The order in which to draw the vertices
	public int lastIndex[];                                // The number of indices last frame for each layer
	public int layers;                                     // Number of layers - 1. (ex. 5 layers would be 0-4 so layers is 4)

	// Camera variables
	private int camx;
	private int camy;
	private double camzoom;
	private double canchorx;
	private double canchory;

	// Objects
	protected GameObject[][] objects;
	private GameObject g;
	private BobView myView;                                // This room's containing BobView.

	// openGL buffers
	public FloatBuffer vertexBuffer;                       // Buffer that holds the room's vertices
	public ShortBuffer indexBuffer[];                      // Buffer that holds the room's indices
	public FloatBuffer textureBuffer;                      // Buffer that holds the room's texture coords

	public Room(BobView container) {
		myView = container;
		objects = new GameObject[LAYERS + 1][OBJECTS + 1];

		// Set up vertex buffer
		ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(VERTEX_BYTES);            // a float has 4 bytes so we allocate for each coordinate 4 bytes
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = vertexByteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();   // allocates the memory from the bytebuffer
		vertexBuffer.position(0);                                                         // puts the curser position at the beginning of the buffer

		// Set up texture buffer
		vertexByteBuffer = ByteBuffer.allocateDirect(TEX_BYTES);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		textureBuffer = vertexByteBuffer.asFloatBuffer();
		textureBuffer.position(0);

		// Set up index buffer
		vertexByteBuffer = ByteBuffer.allocateDirect(INDEX_BYTES);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		indexBuffer = new ShortBuffer[LAYERS];
		for (int i = 0; i < LAYERS; i++) {
			indexBuffer[i] = vertexByteBuffer.asShortBuffer();
			indexBuffer[i].position(0);
		}

		layers = LAYERS - 1;
		lastIndex = new int[LAYERS];

		// Camera initialization
		camx = 0;
		camy = 0;
		camzoom = 1;
		canchorx = 0;
		canchory = 0;
	}

	/**
	 * Get the BobView that contains this Room.
	 *
	 * @return BobView containing this Room.
	 */
	public BobView getView() {
		return myView;
	}

	/**
	 * Returns the activity containing the BobView that contains this Room.
	 */
	public Activity getActivity() {
		return myView.getActivity();
	}

	/**
	 * Get the Touch touch listener for this Room's containing BobView.
	 */
	public Touch getTouch() {
		return myView.getTouch();
	}

	/**
	 * Get the controller helper for this Room's containing BobView.
	 */
	public Controller getController() {
		return myView.getController();
	}

	/**
	 * Gets next available instance id.
	 *
	 * @return An unused ID number to be given to a GameObject
	 */
	public int nextInstance() {
		instances++;
		return instances;
	}

	/**
	 * Add a new GameObject to this room. Must be done for each GameObject to be
	 * draw in this room.
	 *
	 * @param o
	 *            - GameObject to add.
	 */
	public void addObject(GameObject o) {
		objects[o.layer][o.id] = o;
	}

	/**
	 * Removes a GameObject from this room. If possible, consider moving the
	 * object off screen instead.
	 *
	 * @param o
	 *            - GameObject to remove.
	 */
	public void deleteObject(GameObject o) {
		objects[o.layer][o.id] = null;
	}

	/**
	 * Removes all GameObjects from this room.
	 */
	public void clearObjects() {
		for (int l = 0; l < LAYERS; l++) {
			for (int o = 0; o <= instances; o++) {
				objects[l][o] = null;
			}
		}
	}

	/**
	 * Returns the height of the room. This is the same as the height of the
	 * room's containing BobView.
	 *
	 * @return Height of the room, in pixels.
	 * */
	public int getHeight() {
		return myView.getHeight();
	}

	/**
	 * Returns the width of the room. This is the same as the width of the
	 * room's containing BobView.
	 *
	 * @return Width of the room, in pixels.
	 * */
	public int getWidth() {
		return myView.getWidth();
	}

	/**
	 * Returns the screen width correction ratio for dealing with different size
	 * screens. This ratio is based off of the initial orientation of the device
	 * when the BobView is initialized!
	 */
	public double getRatioX() {
		return getView().getRatioX();
	}

	/**
	 * Returns the screen height correction ratio for dealing with different
	 * size screens. This ratio is based off of the initial orientation of the
	 * device when the BobView is initialized!
	 */
	public double getRatioY() {
		return getView().getRatioY();
	}

	/**
	 * Change the x position of the camera.
	 */
	public void setCameraX(int x) {
		camx = x;
	}

	/**
	 * Change the y position of the camera.
	 */
	public void setCameraY(int y) {
		camy = y;
	}

	/**
	 * Set the anchor point for zooming the camera. </br></br>
	 *
	 * HINT: this point will stay in the same location on the screen when zooming
	 * in and out.
	 *
	 * @param x
	 * @param y
	 */
	public void setCameraAnchor(int x, int y) {
		canchorx = x;
		canchory = y;
	}

	/**
	 * Get the current x position of the camera.
	 */
	public double getCameraX() {
		return camx;
	}

	/**
	 * Get the current y position of the camera.
	 */
	public double getCameraY() {
		return camy;
	}

	/**
	 * Get the coordinate of the left edge of the camera.
	 */
	public int getCameraLeftEdge() {
		return (int) (camx + canchorx - getView().getRenderer().getCameraWidth() * camzoom * (canchorx / getView().getRenderer().getCameraWidth()));
	}

	/**
	 * Get the coordinate of teh right edge of the screen.
	 */
	public int getCameraRightEdge() {
		return (int) (camx + canchorx + getView().getRenderer().getCameraWidth() * camzoom * ((getView().getRenderer().getCameraWidth() - canchorx) / getView().getRenderer().getCameraWidth()));
	}

	/**
	 * Get the coordinate of the bottom edge of the screen.
	 */
	public int getCameraBottomEdge() {
		return (int) (camy + canchory - getView().getRenderer().getCameraHeight() * camzoom * (canchory / getView().getRenderer().getCameraHeight()));
	}

	/**
	 * Get the coordinate of the top edge of the screen.
	 */
	public int getCameraTopEdge() {
		return (int) (camy + canchory + getView().getRenderer().getCameraHeight() * camzoom * ((getView().getRenderer().getCameraHeight() - canchory) / getView().getRenderer().getCameraHeight()));
	}

	/**
	 * Set the zoom factor of the camera.
	 */
	public void setCameraZoom(double zoom) {
		camzoom = zoom;
	}

	/**
	 * Get the current zoom factor of the camera.
	 */
	public double getCameraZoom() {
		return camzoom;
	}

	/**
	 * Gathers the vertex, texture, and index data for each GameObject in this
	 * room and passes that information to openGL. Can be called from another
	 * room's draw method to draw both rooms at once. If overridden, call
	 * super.draw(gl).
	 *
	 * @param gl
	 *            - openGL ES 1.0 object to do pass drawing information to.
	 */
	public void draw(GL10 gl) {
		// Update camera
		gl.glMatrixMode(GLES10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(getCameraLeftEdge(), getCameraRightEdge(), getCameraBottomEdge(), getCameraTopEdge(), -1, 1);

		// Draw graphics
		gl.glMatrixMode(GLES10.GL_MODELVIEW);
		gl.glLoadIdentity();

		int numG = getView().getGraphicsHelper().getNumGraphics();

		for (int l = 0; l <= layers; l++) {
			for (int t = 0; t < numG; t++) {
				int obs = 0;

				for (int o = 0; o <= instances; o++) {
					g = objects[l][o];

					if (g != null && g.getGraphicID() == t) {
						if (g.onScreen()) {
							obs++;
						}
					}
				}

				if (obs > 0) {
					vertexBuffer.clear();
					textureBuffer.clear();

					vertexBuffer.position(0);
					textureBuffer.position(0);
					indexBuffer[l].position(0);
					index = 0;

					for (int o = 0; o <= instances; o++) {
						g = objects[l][o];

						if (g != null && g.getGraphicID() == t) {
							if (g.onScreen()) {
								vertexBuffer.put(g.getVertices());
								textureBuffer.put(g.getGraphicVerts());
								index += g.getIndices();
							}
						}
					}

					if (index != lastIndex[l]) {
						if (index > indices.length) {
							indices = new short[index + 1];
						}

						for (int i = 0; i < index; i += 6) {
							indices[i + 0] = (short) (((i / 6) * 4) + 0);
							indices[i + 1] = (short) (((i / 6) * 4) + 1);
							indices[i + 2] = (short) (((i / 6) * 4) + 2);
							indices[i + 3] = (short) (((i / 6) * 4) + 1);
							indices[i + 4] = (short) (((i / 6) * 4) + 2);
							indices[i + 5] = (short) (((i / 6) * 4) + 3);
						}

						indexBuffer[l].clear();
						indexBuffer[l].put(indices);
						lastIndex[l] = index;
					}

					vertexBuffer.position(0);
					textureBuffer.position(0);
					indexBuffer[l].position(0);

					// Add color
					// TODO gl.glColor4f(red, green, blue, alpha);

					gl.glBindTexture(GL11.GL_TEXTURE_2D, t);

					// Point to our vertex buffer
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

					// Draw the vertices as triangle strip
					gl.glDrawElements(GL10.GL_TRIANGLES, index, GL10.GL_UNSIGNED_SHORT, indexBuffer[l]);
				}
			}
		}
	}

	/**
	 * Executes the update events for each GameObject in this room. This method
	 * also handles changes to an object's layer. Can be called from another
	 * room's step event to update both rooms at once. If overridden, call
	 * super.update(deltaTime).
	 *
	 * @param deltaTime
	 */
	public void update(double deltaTime) {
		step(deltaTime);

		// Perform step even for each object
		for (int l = 0; l <= LAYERS; l++) {
			for (int o = instances; o >= 0; o--) {
				if (objects[l][o] != null) {
					objects[l][o].update(deltaTime);
				}
			}
		}

		// Fix layers
		for (int l = 0; l <= LAYERS; l++) {
			for (int o = 0; o <= instances; o++) {
				if (objects[l][o] != null) {
					g = objects[l][o];

					if (g.layer != l) {
						objects[g.layer][o] = objects[l][o];
						objects[l][o] = null;
					}
				}
			}
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

	/**
	 * Touch screen newpress event. Executes the newpress event for each
	 * GameObject in this room. Can be overridden, but be sure to call
	 * super.newpress() in your override method.
	 */
	public void newpress(int index) {
		// Perform step even for each object
		for (int l = 0; l <= LAYERS; l++) {
			for (int o = instances; o >= 0; o--) {
				if (objects[l][o] != null) {
					objects[l][o].newpress(index);
				}
			}
		}
	}

	/**
	 * Gamepad newpress event. Executes the newpress event for each
	 * GameObject in this room. Can be overridden, but be sure to call
	 * super.newpress() in your override method.
	 */
	public void newpress(int controller, int button) {
		// Perform step even for each object
		for (int l = 0; l <= LAYERS; l++) {
			for (int o = instances; o >= 0; o--) {
				if (objects[l][o] != null) {
					objects[l][o].newpress(controller, button);
				}
			}
		}
	}

	/**
	 * Touch screen release event. Executes the release event for each
	 * GameObject in this room. Can be overridden, but be sure to call
	 * super.release() in your override method.
	 */
	public void released(int index) {
		// Perform released event for each object
		for (int l = 0; l <= LAYERS; l++) {
			for (int o = instances; o >= 0; o--) {
				if (objects[l][o] != null) {
					objects[l][o].released(index);
				}
			}
		}
	}

	/**
	 * Gamepad release event. Executes the release event for each
	 * GameObject in this room. Can be overridden, but be sure to call
	 * super.release() in your override method.
	 */
	public void released(int controller, int button) {
		// Perform released event for each object
		for (int l = 0; l <= LAYERS; l++) {
			for (int o = instances; o >= 0; o--) {
				if (objects[l][o] != null) {
					objects[l][o].released(controller, button);
				}
			}
		}
	}

	/**
	 *
	 * @return The angle between (x1, y1) and (x2, y2)
	 */
	public double getAngle(double x, double y, double x2, double y2) {
		return Math.tan((y2 - y) / (x2 - x));
	}

	/**
	 *
	 * @param ob1
	 * @param ob2
	 * @return The angle between ob1 and ob2
	 */
	public double getAngleBetween(GameObject ob1, GameObject ob2) {
		return getAngle(ob1.x, ob1.y, ob2.x, ob2.y);
	}

	/**
	 * Gets the distance between two points.
	 *
	 * @return Distance between (x1, y1) and (x2, y2)
	 */
	public double getDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * Gets the distance between two GameObjects.
	 *
	 * @param ob1
	 *            - GameObject 1
	 * @param ob2
	 *            - GameObject 2
	 * @return Distance between ob1 and ob2, in pixels.
	 */
	public double getDistanceBetween(GameObject ob1, GameObject ob2) {
		return Math.sqrt(Math.pow(ob1.x - ob2.x, 2) + Math.pow(ob1.y - ob2.y, 2));
	}

	/**
	 * Gets the distance between two GameObjects, squared (faster).
	 *
	 * @param ob1
	 *            - GameObject 1
	 * @param ob2
	 *            - GameObject 2
	 * @return Distance between ob1 and ob2, squared, in pixels.
	 */
	public double getDistanceBetweenSquared(GameObject ob1, GameObject ob2) {
		return Math.pow(ob1.x - ob2.x, 2) + Math.pow(ob1.y - ob2.y, 2);
	}

	/**
	 * Checks if GameObject 1 has collided with GameObject 2.
	 *
	 * @param ob1
	 *            - GameObject 1
	 * @param ob2
	 *            - GameObject 2
	 */
	public boolean checkCollision(GameObject ob1, GameObject ob2) {

		double absw1 = Math.abs(ob1.width);
		double absh1 = Math.abs(ob1.height);
		double absw2 = Math.abs(ob2.width);
		double absh2 = Math.abs(ob2.height);

		double radius = Math.sqrt(Math.pow((ob1.x - absw1 / 2), 2) + Math.pow((ob1.y + absh1 / 2), 2))
				+ Math.sqrt(Math.pow((ob2.x - absw2 / 2), 2) + Math.pow((ob2.y + absh2 / 2), 2));

		if (getDistanceBetween(ob1, ob2) <= radius) {
			for (int b1 = 0; b1 < ob1.getNumColBoxes(); b1++) {
				// Find the coordinates of the box defined by ob1.box[]
				double x1 = (ob1.x - absw1 / 2) + (ob1.box[b1][0] * absw1);
				double x2 = (ob1.x - absw1 / 2) + (ob1.box[b1][1] * absw1);
				double y1 = (ob1.y + absh1 / 2) - (ob1.box[b1][2] * absh1);
				double y2 = (ob1.y + absh1 / 2) - (ob1.box[b1][3] * absh1);

				// Compare the ob2's boxs to ob1's boxes
				for (int b2 = 0; b2 < ob2.getNumColBoxes(); b2++) {
					double mX1 = (ob2.x - absw2 / 2) + (ob2.box[b2][0] * absw2);
					double mX2 = (ob2.x - absw2 / 2) + (ob2.box[b2][1] * absw2);
					double mY1 = (ob2.y + absh2 / 2) - (ob2.box[b2][2] * absh2);
					double mY2 = (ob2.y + absh2 / 2) - (ob2.box[b2][3] * absh2);

					// Compare corners of other object to boundaries of plane box
					if (x1 >= mX1 && x1 <= mX2) {              // Left X
						if (y1 <= mY1 && y1 >= mY2) {          // Top Y
							return true;
						} else if (y2 <= mY1 && y2 >= mY2) {   // Bottom Y
							return true;
						}
					} else if (x2 >= mX1 && x2 <= mX2) {       // Right X
						if (y1 <= mY1 && y1 >= mY2) {          // Top Y
							return true;
						} else if (y2 <= mY1 && y2 >= mY2) {   // Bottom Y
							return true;
						}
					} else if (mX1 >= x1 && mX1 <= x2) {       // Compare plane box corners to other object's boundaries
						if (mY1 <= y1 && mY1 >= y2) {          // Top Y
							return true;
						} else if (mY2 <= y1 && mY2 >= y2) {   // Bottom Y
							return true;
						}
					} else if (mX2 >= x1 && mX2 <= x2) {       // Right X
						if (mY1 <= y1 && mY1 >= y2) {          // Top Y
							return true;
						} else if (mY2 <= y1 && mY2 >= y2) {   // Bottom Y
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the object is at position (x, y) according to ob's
	 * collision boxes.
	 *
	 * @param ob
	 *            - GameObject
	 */
	public boolean objectAtPosition(GameObject ob, double x, double y) {

		double absw = Math.abs(ob.width);
		double absh = Math.abs(ob.height);

		for (int b1 = 0; b1 < ob.getNumColBoxes(); b1++) {
			// Find the coordinates of the box defined by ob1.box[]
			double x1 = (ob.x - absw / 2) + (ob.box[b1][0] * absw);
			double x2 = (ob.x - absw / 2) + (ob.box[b1][1] * absw);
			double y1 = (ob.y + absh / 2) - (ob.box[b1][2] * absh);
			double y2 = (ob.y + absh / 2) - (ob.box[b1][3] * absh);

			// Compare corners of other object to boundaries of plane box
			if (x >= x1 && x <= x2) {              // Left X, Right X
				if (y <= y1 && y >= y2) {          // Top Y, Bottom Y
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if there is an object at position (x, y) according to ob's
	 * collision boxes. If yes, returns that object. If no, returns null.
	 */
	public GameObject objectAtPosition(double x, double y) {

		for (int l = 0; l <= LAYERS; l++) {
			for (int o = instances; o >= 0; o--) {
				if (objects[l][o] != null && objectAtPosition(objects[l][o], x, y)) {
					return objects[l][o];
				}
			}
		}

		return null;
	}
}
