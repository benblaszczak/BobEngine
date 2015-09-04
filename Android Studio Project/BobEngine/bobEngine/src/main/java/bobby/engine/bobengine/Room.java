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
import java.util.ArrayList;

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
	public final int OBJECTS = 8000;                        // Maximum number of objects. This is kind of sloppy. I should find a better way to do this.
	public final int DEF_LAYERS = 10;                       // Default number of layers.
	private final int VERTEX_BYTES = 4 * 3 * 4 * OBJECTS;   // 4 bytes per float * 3 coords per vertex * 4 vertices * max objects
	private final int TEX_BYTES = 4 * 2 * 4 * OBJECTS;      // 4 bytes per float * 2 coords per vertex * 4 vertices
	private final int INDEX_BYTES = 4 * 4 * OBJECTS;        // 4 bytes per short * 4 indices per quad * max num of objects

	// Variables
	private int instances = 0;                              // The number of objects in this room
	private int index;                                      // The number of indices for all quads
	private short indices[] = new short[6];                 // The order in which to draw the vertices
	private int lastIndex[];                                // The number of indices last frame for each layer

	private int layers;           // Number of layers
	private float red[];          // Red values for each layer
	private float green[];        // Green values for each layer
	private float blue[];         // Blue values for each layer
	private float alpha[];        // alpha values for each layer

	// Input variables
	private boolean newpress[] = new boolean[Touch.MAX_FINGERS];
	private boolean released[] = new boolean[Touch.MAX_FINGERS];

	private int buttonNewpress[] = new int[Controller.MAX_CONTROLLERS];
	private int buttonReleased[] = new int[Controller.MAX_CONTROLLERS];

	// Camera variables
	private double camX;
	private double camY;
	private double camZoom;
	private double cAnchorX;
	private double cAnchorY;
	private float camLeft;
	private float camRight;
	private float camTop;
	private float camBottom;

	// Objects
	private ArrayList<GameObject>[] obs;
	private GameObject g;
	private BobView view;                 // This room's containing BobView.

	// openGL buffers
	public FloatBuffer vertexBuffer;      // Buffer that holds the room's vertices
	public ShortBuffer indexBuffer[];     // Buffer that holds the room's indices
	public FloatBuffer textureBuffer;     // Buffer that holds the room's texture coordinates

	public Room(BobView container) {
		init(container, DEF_LAYERS);
	}

	public Room(BobView container, int layers) {
		init(container, layers);
	}

	private void init(BobView container, int layers) {
		view = container;
		obs = new ArrayList[layers];
		for (int i = 0; i < layers; i++) {
			obs[i] = new ArrayList<GameObject>(OBJECTS);
		}

		instances = 0;

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
		indexBuffer = new ShortBuffer[layers];
		for (int i = 0; i < layers; i++) {
			indexBuffer[i] = vertexByteBuffer.asShortBuffer();
			indexBuffer[i].position(0);
		}

		this.layers = layers;
		lastIndex = new int[layers];

		red = new float[layers];
		green = new float[layers];
		blue = new float[layers];
		alpha = new float[layers];

		for (int i = 0; i < layers; i++) {
			red[i] = green[i] = blue[i] = alpha[i] = 1f;
		}

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
	public Controller getController() {
		return view.getController();
	}

	/**
	 * Gets next available instance id.
	 *
	 * @return An unused ID number to be given to a GameObject
	 */
	public int nextInstance() {
		instances++;
		return instances - 1;
	}

	/**
	 * Add a new GameObject to this room. Must be done for each GameObject to be
	 * draw in this room.
	 *
	 * @param o
	 *            - GameObject to add.
	 */
	public void addObject(GameObject o) {
		//objects[o.layer][o.id] = o;
		obs[o.layer].add(o);
	}

	/**
	 * Removes a GameObject from this room. If possible, consider moving the
	 * object off screen instead.
	 *
	 * @param o
	 *            - GameObject to remove.
	 */
	public void deleteObject(GameObject o) {
		obs[o.layer].remove(obs[o.layer].indexOf(o));
	}

	/**
	 * Removes all GameObjects from this room.
	 */
	public void clearObjects() {
		for (int l = 0; l < layers; l++) {
			obs[l].clear();
		}
	}

	/**
	 * Indicate to the GraphicsHelper that the Graphics used by GameObjects in this Room
	 * have been used. This method is automatically called each frame if this Room
	 * is the current Room.
	 */
	public void indicateGraphicsUsed() {
		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) {
				if (obs[l].get(o) != null) obs[l].get(o).getGraphic().indicateUsed(getView().getGraphicsHelper().getCleanupsTilRemoval());
			}
		}
	}

	/**
	 * Clean up all the Graphics used by GameObjects in this Room. This will clean up
	 * all the Graphics used in this Room regardless of the number of times the Graphics
	 * have been through a cleanup.
	 */
	public void clearAllGraphics() {
		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) {
				if (obs[l].get(o) != null) obs[l].get(o).getGraphic().forceCleanup();
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
		return view.getHeight();
	}

	/**
	 * Returns the width of the room. This is the same as the width of the
	 * room's containing BobView.
	 *
	 * @return Width of the room, in pixels.
	 * */
	public int getWidth() {
		return view.getWidth();
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
	 * Set the color intensity of the game objects on a layer.
	 *
	 * @param layer The layer to change the color intensity.
	 * @param r Intensity of red, from 0-1
	 * @param g Green intensity
	 * @param b Blue intensity
	 * @param a Alpha intensity
	 */
	public void setLayerColor(int layer, float r, float g, float b, float a) {
		red[layer] = r;
		green[layer] = g;
		blue[layer] = b;
		alpha[layer] = a;
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

		int numG = getView().getGraphicsHelper().getMaxGraphicID();

		for (int l = 0; l < layers; l++) {
			for (int t = 0; t <= numG; t++) {
				int numObs = 0;

				for (int o = 0; o < obs[l].size(); o++) {
					//g = objects[l][o];
					g = obs[l].get(o);

					if (g != null && g.getGraphicID() == t) {
						if (g.onScreen()) {
							numObs++;
						}
					}
				}

				if (numObs > 0) {
					vertexBuffer.clear();
					textureBuffer.clear();

					vertexBuffer.position(0);
					textureBuffer.position(0);
					indexBuffer[l].position(0);
					index = 0;

					for (int o = 0; o < obs[l].size(); o++) {
						//g = objects[l][o];
						g = obs[l].get(o);

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
					gl.glColor4f(red[l], green[l], blue[l], alpha[l]);

					gl.glBindTexture(GL11.GL_TEXTURE_2D, t);

					// Point to our vertex buffer
					gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

					// Draw the vertices as triangle strip
					gl.glDrawElements(GL10.GL_TRIANGLES, index, GL10.GL_UNSIGNED_SHORT, indexBuffer[l]);
				}
			}
		}

		// Load any recently used graphics that are not loaded.
		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) {
				if (obs[l].get(o) != null) {
					//g = objects[l][o];
					g = obs[l].get(o);

					if (g.getGraphic().shouldLoad()) {
						getView().getGraphicsHelper().addGraphic(g.getGraphic());
					}
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
		// Handle input events
		for (int i = 0; i < Touch.MAX_FINGERS; i++) {
			if (newpress[i]) newpress(i); newpress[i] = false;
			if (released[i]) released(i); released[i] = false;
		}

		for (int i = 0; i < Controller.MAX_CONTROLLERS; i++) {
			if (buttonNewpress[i] != -1) newpress(i, buttonNewpress[i]);
			buttonNewpress[i] = -1;
			if (buttonReleased[i] != -1) released(i, buttonReleased[i]);
			buttonReleased[i] = -1;
		}

		step(deltaTime);

		// Update camera edges
		camLeft = (float) (camX + cAnchorX - getView().getRenderer().getCameraWidth() * camZoom * (cAnchorX / getView().getRenderer().getCameraWidth()));
		camRight = (float) (camX + cAnchorX + getView().getRenderer().getCameraWidth() * camZoom * ((getView().getRenderer().getCameraWidth() - cAnchorX) / getView().getRenderer().getCameraWidth()));
		camTop = (float) (camY + cAnchorY + getView().getRenderer().getCameraHeight() * camZoom * ((getView().getRenderer().getCameraHeight() - cAnchorY) / getView().getRenderer().getCameraHeight()));
		camBottom = (float) (camY + cAnchorY - getView().getRenderer().getCameraHeight() * camZoom * (cAnchorY / getView().getRenderer().getCameraHeight()));

		// Perform step even for each object
		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) {
				if (obs[l].get(o) != null) {
					obs[l].get(o).update(deltaTime);
				}
			}
		}

		// Fix layers
		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) {
				if (obs[l].get(o) != null) {
					g = obs[l].get(o);

					if (g.layer != l) {
						obs[g.layer].add(g);
						obs[l].remove(o);
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
	 * Tell this room to handle a newpress input event on the main thread.
	 * @param index
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
	 * @param index
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
		// Perform step even for each object
		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) {
				if (obs[l].get(o) != null) {
					obs[l].get(o).newpress(index);
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
		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) { // I have no idea why, but these loops originally started at instance and count DOWN to 0... wut.
				if (obs[l].get(o) != null) {
					obs[l].get(o).newpress(controller, button);
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
		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) {
				if (obs[l].get(o) != null) {
					obs[l].get(o).released(index);
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
		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) {
				if (obs[l].get(o) != null) {
					obs[l].get(o).released(controller, button);
				}
			}
		}
	}

	/**
	 * Find the angle between two points.
	 *
	 * @return The angle between (x1, y1) and (x2, y2) in radians
	 */
	public double getAngle(double x, double y, double x2, double y2) {
		if (x < x2) return Math.atan((y - y2) / (x - x2));
		else return Math.atan((y - y2) / (x - x2)) + Math.PI;
	}

	/**
	 * Find the angle between two game objects.
	 *
	 * @param ob1 first object
	 * @param ob2 second object
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

		for (int l = 0; l < layers; l++) {
			for (int o = 0; o < obs[l].size(); o++) {
				if (obs[l].get(o) != null && objectAtPosition(obs[l].get(o), x, y)) {
					return obs[l].get(o);
				}
			}
		}

		return null;
	}
}
