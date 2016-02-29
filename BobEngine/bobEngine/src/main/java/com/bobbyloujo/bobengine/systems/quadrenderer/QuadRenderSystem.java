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
package com.bobbyloujo.bobengine.systems.quadrenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import com.bobbyloujo.bobengine.components.Transform;
import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.systems.Renderable;
import com.bobbyloujo.bobengine.graphics.Graphic;

/**
 * This render system renders quads defined by transformables. Each quad has two transformables:
 * <p/>
 * Transformation: defines the position, size, angle, etc. to show the quad on the screen.
 * <p/>
 * GraphicTransformable: defines the rectangular area of the graphic to use to texture the quad.
 * <p/>
 * Created by Benjamin on 9/25/2015.
 */
public class QuadRenderSystem implements Renderable {

	public static final int DEF_INIT_TRANSFORMS = 3;

	public static final int GFX_VERTICES = 8;
	private static final int VERTICES = 8;
	private static final int INDICES = 6;

	private final int VERTEX_BYTES = 4 * 3 * 4;   // 4 bytes per float * 3 coords per vertex * 4 vertices
	private final int TEX_BYTES = 4 * 2 * 4;      // 4 bytes per float * 2 coords per vertex * 4 vertices
	private final int INDEX_BYTES = 4 * 4;        // 4 bytes per short * 4 indices per quad

	private Room room;
	private Graphic graphic;
	private int layers;

	private int numTransforms;
	private ArrayList<Transformation> transforms;
	private ArrayList<GraphicAreaTransformation> graphicTransforms;

	private short indices[] = new short[6];                 // The order in which to draw the vertices
	private int lastIndex[];                                // The number of indices last frame for each layer

	private float[] vertices = new float[VERTICES];
	private float[] gfxVertices = new float[GFX_VERTICES];

	private FloatBuffer vertexBuffer;      // Buffer that holds the render system's vertices
	private ShortBuffer indexBuffer[];     // Buffer that holds the render system's indices
	private FloatBuffer textureBuffer;     // Buffer that holds the render system's texture coordinates
	private int bufferSize;

	private float red[];          // Red values for each layer
	private float green[];        // Green values for each layer
	private float blue[];         // Blue values for each layer
	private float alpha[];        // alpha values for each layer

	public QuadRenderSystem(Room room, Graphic graphic, int layers) {
		init(room, graphic, layers, DEF_INIT_TRANSFORMS);
	}

	public QuadRenderSystem(Room room, Graphic graphic, int layers, int initTransforms) {
		init(room, graphic, layers, initTransforms);
	}

	private void init(Room room, Graphic graphic, int layers, int initTransforms) {
		this.room = room;
		this.graphic = graphic;
		this.layers = layers;

		resizeBuffers(initTransforms);

		lastIndex = new int[layers];

		numTransforms = 0;
		transforms = new ArrayList<Transformation>(initTransforms);
		graphicTransforms = new ArrayList<GraphicAreaTransformation>(initTransforms);

		red = new float[layers];
		green = new float[layers];
		blue = new float[layers];
		alpha = new float[layers];

		for (int i = 0; i < layers; i++) {
			red[i] = green[i] = blue[i] = alpha[i] = 1f;
		}
	}

	/**
	 * Get the Graphic object used by this render system.
	 * @return The Graphic object used by this render system.
	 */
	@Override
	public Graphic getGraphic() {
		return graphic;
	}

	/**
	 * Set the color of a particular layer.
	 *
	 * @param layer The layer for which to set the color.
	 * @param r The red value, from 0-1
	 * @param g The green value, from 0-1
	 * @param b The blue value, from 0-1
	 * @param a The alpha value, from 0-1
	 */
	public void setLayerColor(int layer, float r, float g, float b, float a) {
		red[layer] = r;
		green[layer] = g;
		blue[layer] = b;
		alpha[layer] = a;
	}

	/**
	 * Add an Entity which has Transformation and GraphicAreaTransformation components to this QuadRenderSystem.
	 * @param entity An Entity with transformables to add to this render system.
	 * @return True if Tranformables were added to this system, false otherwise.
	 */
	public boolean addEntity(Entity entity) {
		boolean success = false;
		ArrayList<Transformation> transforms = entity.getComponentsOfType(Transformation.class);
		ArrayList<GraphicAreaTransformation> graphicAreaTransformations = entity.getComponentsOfType(GraphicAreaTransformation.class);

		if (!transforms.isEmpty()) {
			for (int i = 0; i < transforms.size() && i < graphicAreaTransformations.size(); i++) {
				addTransform(transforms.get(i), graphicAreaTransformations.get(i));
			}

			success = true;
		}

		return success;
	}

	/**
	 * Remove an Entity from this system.
	 * @param entity The Entity to remove.
	 * @return True if the Entity had transforms that were removed from this system.
	 */
	public boolean removeEntity(Entity entity) {
		boolean success = false;

		ArrayList<Transformation> transforms = entity.getComponentsOfType(Transformation.class);

		if (!transforms.isEmpty()) {
			for (int i = 0; i < transforms.size(); i++) {
				int t = this.transforms.indexOf(transforms.get(i));
				if (t != -1) {
					removeTransform(t);
					success = true;
				}
			}
		}

		return success;
	}

	/**
	 * Add a pair of transforms that define a quad to this render system for rendering.
	 *
	 * @param transform The transformable defining where to render the quad on (or off) the screen.
	 * @param graphicTransform The transformable defining the portion of the image to use.
	 * @return A value to identify this pair of transforms for quick and easy removal
	 */
	public int addTransform(Transformation transform, GraphicAreaTransformation graphicTransform) {
		int i = 0;

		while (i < transforms.size()) {
			if (transforms.get(i) == transform && graphicTransforms.get(i) == graphicTransform) {
				return i;
			}

			i++;
		}

		i = 0;

		while (i < transforms.size() && transforms.get(i) == null) {
			i++;
		}

		transforms.add(i, transform);
		graphicTransforms.add(i, graphicTransform);

		numTransforms++;

		if (numTransforms > bufferSize) {
			resizeBuffers(numTransforms);
		}

		return i;
	}

	/**
	 * Remove a pair of transforms from this render system using the ID number return by
	 * addTransform(...).
	 *
	 * @param transform The ID of the transform pair to remove
	 * @return True if the pair was removed, false if the pair was not in the render system.
	 */
	public boolean removeTransform(int transform) {
		if (transform < transforms.size() && (transforms.get(transform) != null || graphicTransforms.get(transform) != null)) {
			transforms.remove(transform);
			graphicTransforms.remove(transform);

			numTransforms--;

			return true;
		}

		return false;
	}

	/**
	 * Remove a transform pair if you were silly and didn't keep track of the ID given to you by
	 * addTransform(...).
	 *
	 * @param transformation The Transformation to search for
	 * @param graphicAreaTransformation The GraphicTransformable to search for
	 * @return True if the pair was removed, false if the pair wasn't found.
	 */
	public boolean removeTransform(Transformation transformation, GraphicAreaTransformation graphicAreaTransformation) {
		for (int i = 0; i < transforms.size(); i++) {
			if (transforms.get(i) == transformation && graphicTransforms.get(i) == graphicAreaTransformation) {
				transforms.remove(i);
				graphicTransforms.remove(i);

				numTransforms--;

				return true;
			}
		}

		return false;
	}

	/**
	 * Removes all Transformables and GraphicAreaTransformables from this QuadRenderSystem.
	 */
	public void removeAllTransforms() {
		transforms.clear();
		graphicTransforms.clear();
	}

	/**
	 * Change the size of the vertex, texture, and index buffers.
	 *
	 * @param quads The number of quads the buffers should be able to hold.
	 */
	public void resizeBuffers(int quads) {
		// Set up vertex buffer
		ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(VERTEX_BYTES * quads);    // a float has 4 bytes so we allocate for each coordinate 4 bytes
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = vertexByteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();   // allocates the memory from the bytebuffer
		vertexBuffer.position(0);                                                         // puts the curser position at the beginning of the buffer

		// Set up texture buffer
		vertexByteBuffer = ByteBuffer.allocateDirect(TEX_BYTES * quads);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		textureBuffer = vertexByteBuffer.asFloatBuffer();
		textureBuffer.position(0);

		// Set up index buffer
		vertexByteBuffer = ByteBuffer.allocateDirect(INDEX_BYTES * quads);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		indexBuffer = new ShortBuffer[layers];
		for (int i = 0; i < layers; i++) {
			indexBuffer[i] = vertexByteBuffer.asShortBuffer();
			indexBuffer[i].position(0);
		}

		bufferSize = quads;
	}

	/**
	 * Get the size of the vertex, texture, and index buffers.
	 *
	 * @return The size of the buffers.
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * Render the quads in this system.
	 *
	 * @param gl OpenGL ES 1 object.
	 * @param layer layer to render.
	 */
	public void render(GL10 gl, int layer) {
		boolean obFound = false;
		int gID;
		int numIndices = 0;    // The number of indices for all objects

		if (graphic == null) {
			gID = 0;
		} else {
			gID = graphic.id;
		}

		for (int i = 0; i < numTransforms; i++) {
			Transformation t = transforms.get(i);
			GraphicAreaTransformation g = graphicTransforms.get(i);

			if (t.getLayer() == layer && onScreen(t, room) && Transform.getRealVisibility(t)) {
				if (!obFound) {
					vertexBuffer.clear();
					textureBuffer.clear();

					vertexBuffer.position(0);
					textureBuffer.position(0);
					indexBuffer[layer].position(0);

					obFound = true;
				}

				vertexBuffer.put(getVertices(t));
				textureBuffer.put(getVertices(g));
				numIndices += INDICES;
			}
		}

		if (obFound) {
			if (numIndices != lastIndex[layer]) {
				if (numIndices > indices.length) {
					indices = new short[numIndices + 1];
				}

				for (int i = 0; i < numIndices; i += 6) {
					indices[i + 0] = (short) (((i / 6) * 4) + 0);
					indices[i + 1] = (short) (((i / 6) * 4) + 1);
					indices[i + 2] = (short) (((i / 6) * 4) + 2);
					indices[i + 3] = (short) (((i / 6) * 4) + 1);
					indices[i + 4] = (short) (((i / 6) * 4) + 2);
					indices[i + 5] = (short) (((i / 6) * 4) + 3);
				}

				indexBuffer[layer].clear();
				indexBuffer[layer].put(indices);
				lastIndex[layer] = numIndices;
			}

			vertexBuffer.position(0);
			textureBuffer.position(0);
			indexBuffer[layer].position(0);

			// Add color
			gl.glColor4f(red[layer] * alpha[layer], green[layer] * alpha[layer], blue[layer] * alpha[layer], alpha[layer]);

			// Bind the texture
			gl.glBindTexture(GL11.GL_TEXTURE_2D, gID);

			// Point to our vertex and texture buffers
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

			// Draw the vertices as triangles
			gl.glDrawElements(GL10.GL_TRIANGLES, numIndices, GL10.GL_UNSIGNED_SHORT, indexBuffer[layer]);
		}
	}

	/**
	 * Determines if the Transformation t is within the bounds of the screen in
	 * Room room.
	 *
	 * @param t the Transformation to inspect
	 * @param room the Room whose bounds to use
	 * @return True if the Transformation is on screen, false if not.
	 */
	public static boolean onScreen(Transformation t, Room room) {
		Transformation parent;
		double x = t.getX();
		double y = t.getY();
		double width = Math.abs(t.getWidth());
		double height = t.getHeight();
		double scale = t.getScale();
		boolean followCamera = Transform.getRealShouldFollowCamera(t);

		double gridUnitX = room.getGridUnitX();
		double gridUnitY = room.getGridUnitY();

		double screenLeft = room.getCameraLeftEdge() / gridUnitX;
		double screenRight = room.getCameraRightEdge() / gridUnitX;
		double screenTop = room.getCameraTopEdge() / gridUnitY;
		double screenBottom = room.getCameraBottomEdge() / gridUnitY;

		parent = t.getParent();

		while (parent != null) {
			double cos = 1;
			double sin = 0;
			double oX = x;
			double oY = y;

			if (parent.getAngle() != 0) {
				cos = Math.cos(Math.toRadians(parent.getAngle()));
				sin = Math.sin(Math.toRadians(parent.getAngle()));
			}

			x = oX * cos - oY * sin;
			y = oX * sin + oY * cos;

			x += parent.getX();
			y += parent.getY();
			scale *= parent.getScale();
			parent = parent.getParent();
		}

		width *= scale;
		height *= scale;

		if (followCamera) {
			screenLeft = 0;
			screenRight = room.getWidth();
			screenBottom = 0;
			screenTop = room.getHeight();
		}

		if (x > -width / 2 + screenLeft && x < width / 2 + screenRight) {
			if (y > -height / 2 + screenBottom && y < height / 2 + screenTop) {
				return true;
			}
		}

		return false;
	}

	private float[] getVertices(Transformation t) {
		// Data
		Transformation parent;
		double x = t.getX();
		double y = t.getY();
		double width = t.getWidth();
		double height = t.getHeight();
		double angle = t.getAngle();
		double scale = t.getScale();
		boolean shouldFollowCamera = Transform.getRealShouldFollowCamera(t);

		parent = t.getParent();

		while (parent != null) {
			double cos = Math.cos(Math.toRadians(parent.getAngle()));
			double sin = Math.sin(Math.toRadians(parent.getAngle()));

			x *= parent.getScale();
			y *= parent.getScale();

			double oX = x;
			double oY = y;

			x = oX * cos - oY * sin;
			y = oX * sin + oY * cos;

			x += parent.getX();
			y += parent.getY();
			angle += parent.getAngle();
			scale *= parent.getScale();
			parent = parent.getParent();
		}

		if (shouldFollowCamera) {
			x += room.getCameraLeftEdge() / room.getGridUnitX();
			y += room.getCameraBottomEdge() / room.getGridUnitY();
		}

		height *= scale;
		width *= scale;

		x *= room.getGridUnitX();
		y *= room.getGridUnitY();

		height *= room.getGridUnitY();
		width *= room.getGridUnitX();

		if (angle != 0) {                                      // Don't do unnecessary calculations
			double sin;
			double cos;

			cos = Math.cos(Math.toRadians(angle + 180));
			sin = Math.sin(Math.toRadians(angle + 180));

			vertices[0] = (float) ((x - (x - width / 2)) * cos - (y - (y - height / 2)) * sin + x);   // Bottom Left X
			vertices[1] = (float) ((x - (x - width / 2)) * sin + (y - (y - height / 2)) * cos + y);   // Bottom Left Y
			vertices[2] = (float) ((x - (x - width / 2)) * cos - (y - (y + height / 2)) * sin + x);   // Top Left X
			vertices[3] = (float) ((x - (x - width / 2)) * sin + (y - (y + height / 2)) * cos + y);   // Top Left Y
			vertices[4] = (float) ((x - (x + width / 2)) * cos - (y - (y - height / 2)) * sin + x);   // Bottom Right X
			vertices[5] = (float) ((x - (x + width / 2)) * sin + (y - (y - height / 2)) * cos + y);   // Bottom Right Y
			vertices[6] = (float) ((x - (x + width / 2)) * cos - (y - (y + height / 2)) * sin + x);   // Top Right X
			vertices[7] = (float) ((x - (x + width / 2)) * sin + (y - (y + height / 2)) * cos + y);   // Top Right Y
		} else {
			vertices[0] = (float) (x - width / 2);     // Bottom Left X
			vertices[1] = (float) (y - height / 2);    // Bottom Left Y
			vertices[2] = vertices[0];                 // Top Left X (Same as Bottom X)
			vertices[3] = (float) (y + height / 2);    // Top Left Y
			vertices[4] = (float) (x + width / 2);     // Bottom Right X
			vertices[5] = vertices[1];                 // Bottom Right Y (Same as Left Y)
			vertices[6] = vertices[4];                 // Top Right X (Same as Bottom X)
			vertices[7] = vertices[3];                 // Top Right Y (Same as Left Y)
		}

		return vertices;
	}

	private float[] getVertices(GraphicAreaTransformation g) {
		// Data
		float leftX;   // Left X coordinate of the frame on the graphic sheet
		float rightX;  // Right X coordinate
		float topY;    // Top Y coordinate
		float bottomY; // Bottom Y coordinate

		float x = g.getGraphicX();
		float y = g.getGraphicY();
		float gWidth = g.getGraphicWidth();
		float gHeight = g.getGraphicHeight();

		float height;
		float width;

		if (graphic != null) {
			height = graphic.height;
			width = graphic.width;
		} else {
			height = 1;
			width = 1;
		}

		leftX = x;
		rightX = x + gWidth;
		topY = y;
		bottomY = y + gHeight;

		leftX += 1f / (gWidth * width * 100f);      // Prevent other parts of the graphic from "spilling over" the edges.
		rightX -= 1f / (gWidth * width * 100f);
		topY += 1f / (gHeight * height* 100f);
		bottomY -= 1f / (gHeight * height * 100f);

		gfxVertices[0] = leftX;
		gfxVertices[1] = bottomY;
		gfxVertices[2] = leftX;
		gfxVertices[3] = topY;
		gfxVertices[4] = rightX;
		gfxVertices[5] = bottomY;
		gfxVertices[6] = rightX;
		gfxVertices[7] = topY;

		return gfxVertices;
	}
}
