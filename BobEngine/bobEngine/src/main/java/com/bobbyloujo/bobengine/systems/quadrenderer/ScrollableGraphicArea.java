package com.bobbyloujo.bobengine.systems.quadrenderer;

import com.bobbyloujo.bobengine.components.Transform;

/**
 * Created by bobby on 6/23/2016.
 */
public class ScrollableGraphicArea implements GraphicAreaTransformation {

    public Transform transform;

    public ScrollableGraphicArea() {
        transform = new Transform();
        transform.x = 0;
        transform.y = 0;
        transform.height = 1;
        transform.width = 1;
    }

    @Override
    public float getGraphicX() {
        return (float) transform.x;
    }

    @Override
    public float getGraphicY() {
        return (float) transform.y;
    }

    @Override
    public float getGraphicWidth() {
        return (float) transform.width;
    }

    @Override
    public float getGraphicHeight() {
        return (float) transform.height;
    }
}
