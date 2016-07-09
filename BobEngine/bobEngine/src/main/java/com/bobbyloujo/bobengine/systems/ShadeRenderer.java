package com.bobbyloujo.bobengine.systems;

import android.util.Log;

import com.bobbyloujo.bobengine.components.ParentAssignmentHandler;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.graphics.Graphic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * Created by Benjamin on 4/11/2016.
 */
public class ShadeRenderer extends Entity implements Renderable, ParentAssignmentHandler {

    private final int VERTEX_BYTES = 4 * 3 * 4;   // 4 bytes per float * 3 coords per vertex * 4 vertices
    private final int TEX_BYTES = 4 * 2 * 4;      // 4 bytes per float * 2 coords per vertex * 4 vertices
    private final int INDEX_BYTES = 4 * 6;        // 4 bytes per short * 4 indices per quad

    private FloatBuffer vertexBuffer;      // Buffer that holds the render system's vertices
    private ShortBuffer indexBuffer;       // Buffer that holds the render system's indices
    private FloatBuffer textureBuffer;     // Buffer that holds the render system's texture coordinates

    private static final int VERTICES = 8;
    private float[] vertices = new float[VERTICES];
    private static final float[] TEX_VERTS = {0, 1, 0, 0, 1, 1, 1, 0}; // "Texture" vertices. Uses the default text, which is white.
    private final static short INDICES[] = {0,1,2,1,2,3};

    private float red[];          // Red values for each layer
    private float green[];        // Green values for each layer
    private float blue[];         // Blue values for each layer
    private float alpha[];        // alpha values for each layer

    public ShadeRenderer() {
        resizeBuffers();
    }

    @Override
    public void onParentAssigned(Entity parent) {
        int layers = getRoom().getNumLayers(); // todo room not necessarily assigned at this point. Will cause null pointer exception!
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
            red[i] = green[i] = blue[i] = alpha[i] = 0f;

            if (r != null && i < r.length) {
                red[i] = r[i];
                green[i] = g[i];
                blue[i] = b[i];
                alpha[i] = a[i];
            }
        }
    }

    /**
     * Change the size of the vertex, texture, and index buffers.
     */
    public void resizeBuffers() {
        // Set up vertex buffer
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(VERTEX_BYTES);    // a float has 4 bytes so we allocate for each coordinate 4 bytes
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = vertexByteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();   // allocates the memory from the bytebuffer
        vertexBuffer.position(0);                                                         // puts the curser position at the beginning of the buffer

        // Set up texture buffer
        vertexByteBuffer = ByteBuffer.allocateDirect(TEX_BYTES);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = vertexByteBuffer.asFloatBuffer();
        textureBuffer.position(0);
        textureBuffer.put(TEX_VERTS);

        // Set up index buffer
        vertexByteBuffer = ByteBuffer.allocateDirect(INDEX_BYTES);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        indexBuffer = vertexByteBuffer.asShortBuffer();
        indexBuffer.position(0);
        indexBuffer.put(INDICES);
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
                red[i] = green[i] = blue[i] = alpha[i] = 0f;

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

    @Override
    public void render(GL10 gl, int layer) {
        if (alpha[layer] > 0.0001f) {
            float x = 0;
            float y = 0;
            float width = getRoom().getViewWidth();
            float height = getRoom().getViewHeight();

            x += getRoom().getCameraLeftEdge() * getRoom().getGridUnitX();
            y += getRoom().getCameraBottomEdge() * getRoom().getGridUnitY();

            vertices[0] = x;             // Bottom Left X
            vertices[1] = y;             // Bottom Left Y
            vertices[2] = vertices[0];   // Top Left X (Same as Bottom X)
            vertices[3] = y + height;    // Top Left Y
            vertices[4] = x + width;     // Bottom Right X
            vertices[5] = vertices[1];   // Bottom Right Y (Same as Left Y)
            vertices[6] = vertices[4];   // Top Right X (Same as Bottom X)
            vertices[7] = vertices[3];   // Top Right Y (Same as Left Y)

            vertexBuffer.clear();
            vertexBuffer.put(vertices);

            vertexBuffer.position(0);
            textureBuffer.position(0);
            indexBuffer.position(0);

            gl.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            gl.glColor4f(red[layer] * alpha[layer], green[layer] * alpha[layer], blue[layer] * alpha[layer], alpha[layer]);

            // Point to our vertex and texture buffers
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

            // Draw the vertices as triangles
            gl.glDrawElements(GL10.GL_TRIANGLES, INDICES.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
        }
    }

    @Override
    public Graphic getGraphic() {
        return null;
    }


}
