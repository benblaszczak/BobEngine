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

import android.util.Log;

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
 * This render system renders quads defined by transformations. Each quad has two transformations:
 * <br>
 * <b>Transformation:</b> defines the position, size, angle, etc. to show the quad on the screen.
 * <br>
 * <b>GraphicTransformation:</b> defines the rectangular area of the graphic to use to texture the quad.
 * <br>
 * Created by Benjamin on 9/25/2015.
 */
public class QuadRenderSystem extends Entity implements Renderable {

	public static final int DEF_INIT_QUADS = 3;

	public static final int GFX_VERTICES = 8;
	private static final int VERTICES = 8;
	private static final int INDICES = 6;

	private final int VERTEX_BYTES = 4 * 3 * 4;   // 4 bytes per float * 3 coords per vertex * 4 vertices
	private final int TEX_BYTES = 4 * 2 * 4;      // 4 bytes per float * 2 coords per vertex * 4 vertices
	private final int INDEX_BYTES = 4 * INDICES;  // 4 bytes per short * 6 indices per quad

	private Graphic graphic;

	private int numQuads;             // The number of quads in this system.
	private ArrayList<Quad> quads;    // The quads in this system.

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

    /**
     * Make a new QuadRenderSystem.
     * @param graphic The graphic used to render the quads.
     */
	public QuadRenderSystem(Graphic graphic) {
		init(graphic, DEF_INIT_QUADS);
	}

    /**
     * Make a new QuadRenderSystem.
     * @param graphic The graphic used to render the quads.
     * @param initBufferSize The initial number of Quads that can be held in the buffer. This will
     *                       automatically expand if more Quads are added.
     */
	public QuadRenderSystem(Graphic graphic, int initBufferSize) {
		init(graphic, initBufferSize);
	}

	private void init(Graphic graphic, int initBufferSize) {
		this.graphic = graphic;

		numQuads = 0;
		quads = new ArrayList<Quad>(initBufferSize);
	}

	@Override
	public void onParentAssigned() {
		int layers = getRoom().getNumLayers();

		lastIndex = new int[layers];

		float[] r,g,b,a;

		r = red;
		g = green;
		b = blue;
		a = alpha;

		red = new float[layers];
		green = new float[layers];
		blue = new float[layers];
		alpha = new float[layers];

		for (int i = 0; i < layers; i++) {
			red[i] = green[i] = blue[i] = alpha[i] = 1f;

			if (r != null && i < r.length) {
				red[i] = r[i];
				green[i] = g[i];
				blue[i] = b[i];
				alpha[i] = a[i];
			}
		}

		resizeBuffers(quads.size());
	}

	/**
	 * Get the Graphic object used by this render system.
	 * @return The Graphic object used by this render system.
	 */
	@Override
	public Graphic getGraphic() {
		return graphic;
	}

	public void setGraphic(Graphic graphic) {
		this.graphic = graphic;
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
		if (getRoom() == null) {
			float[] or, og, ob, oa; // original values
			int layers = Room.DEF_LAYERS;

			or = red;
			og = green;
			ob = blue;
			oa = alpha;

			if (layer >= Room.DEF_LAYERS) {
				layers = layer+1;
			}

			red = new float[layers];
			green = new float[layers];
			blue = new float[layers];
			alpha = new float[layers];

			for (int i = 0; i < layers; i++) {
				red[i] = green[i] = blue[i] = alpha[i] = 1f;

				if (or != null && i < or.length) {
					red[i] = or[i];
					green[i] = og[i];
					blue[i] = ob[i];
					alpha[i] = oa[i];
				}
			}
		}

		if (layer < red.length && layer >= 0) {
			red[layer] = r;
			green[layer] = g;
			blue[layer] = b;
			alpha[layer] = a;
		} else {
			Log.e("BobEngine", "Can't change layer color. Layer not in range.");
		}
	}

	/**
	 * Returns the value of the color on the layer.
	 * @param layer the layer
	 * @return value of the color for the layer
	 */
	public float getRed(int layer) {
		return red[layer];
	}

	/**
	 * Returns the value of the color on the layer.
	 * @param layer the layer
	 * @return value of the color for the layer
	 */
	public float getGreen(int layer) {
		return green[layer];
	}

	/**
	 * Returns the value of the color on the layer.
	 * @param layer the layer
	 * @return value of the color for the layer
	 */
	public float getBlue(int layer) {
		return blue[layer];
	}

	/**
	 * Returns the value of the color on the layer.
	 * @param layer the layer
	 * @return value of the color for the layer
	 */
	public float getAlpha(int layer) {
		return alpha[layer];
	}

	/**
	 * Add a quad to this render system.
	 * @param quad The quad to add.
     */
	public void addQuad(Quad quad) {
		quads.add(quad);
		numQuads++;

		if (numQuads > bufferSize) {
			resizeBuffers(numQuads);
		}
	}

	/**
	 * Remove a quad to this render system.
	 * @param quad The quad to remove.
     */
	public void removeQuad(Quad quad) {
		quads.remove(quad);
		numQuads--;
	}

	/**
	 * Removes all Quads from this QuadRenderSystem.
	 */
	public void removeAllQuads() {
		quads.clear();
		numQuads = 0;
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
		vertexBuffer = vertexByteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();   // allocates the memory from the byte buffer
		vertexBuffer.position(0);                                                         // puts the cursor position at the beginning of the buffer

		// Set up texture buffer
		vertexByteBuffer = ByteBuffer.allocateDirect(TEX_BYTES * quads);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		textureBuffer = vertexByteBuffer.asFloatBuffer();
		textureBuffer.position(0);

		// Set up index buffer
		int layers = Room.DEF_LAYERS;

		if (getRoom() != null) {
			layers = getRoom().getNumLayers();
		}

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

		for (int i = 0; i < numQuads; i++) {
			Transformation t = quads.get(i).getTransformation();
			GraphicAreaTransformation g = quads.get(i).getGraphicAreaTransformation();

			if (t.getLayer() == layer && onScreen(t, getRoom()) && Transform.getRealVisibility(t)) {
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

		double screenLeft = room.getCameraLeftEdge();
		double screenRight = room.getCameraRightEdge();
		double screenTop = room.getCameraTopEdge();
		double screenBottom = room.getCameraBottomEdge();

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
		Room room = getRoom();
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
			x += room.getCameraLeftEdge();
			y += room.getCameraBottomEdge();
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
