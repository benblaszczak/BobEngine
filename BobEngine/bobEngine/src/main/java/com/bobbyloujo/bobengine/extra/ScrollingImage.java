package com.bobbyloujo.bobengine.extra;

import com.bobbyloujo.bobengine.components.Transform;
import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.systems.quadrenderer.GraphicAreaTransformation;
import com.bobbyloujo.bobengine.systems.quadrenderer.Quad;
import com.bobbyloujo.bobengine.systems.quadrenderer.QuadRenderSystem;
import com.bobbyloujo.bobengine.systems.quadrenderer.ScrollableGraphicArea;

/**
 * Create a tiled, scrolling image.
 * Created by bobby on 6/23/2016.
 */
public class ScrollingImage extends Entity implements Quad {

    public Transform transform;
    public ScrollableGraphicArea graphic;
    public QuadRenderSystem renderSystem;

    public ScrollingImage(Room room) {
        super(room);

        transform = new Transform();
        graphic = new ScrollableGraphicArea();

        transform.height = room.getHeight();
        transform.width = room.getWidth();
        transform.x = transform.width / 2;
        transform.y = transform.height / 2;
    }

    public void setGraphic(Graphic g) {
        if (renderSystem != null) {
            renderSystem.removeQuad(this);
        }

        renderSystem = getRoom().getQuadRenderSystem(g);
        renderSystem.addQuad(this);
    }

    @Override
    public Transformation getTransformation() {
        return transform;
    }

    @Override
    public GraphicAreaTransformation getGraphicAreaTransformation() {
        return graphic;
    }
}
