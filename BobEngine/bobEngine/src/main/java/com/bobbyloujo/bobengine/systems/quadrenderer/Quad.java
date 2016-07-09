package com.bobbyloujo.bobengine.systems.quadrenderer;

import com.bobbyloujo.bobengine.components.Transformation;

/**
 * This interface describes the necessary components for a Quad that is rendered by a
 * QuadRenderSystem.
 * Created by bobby on 6/23/2016.
 */
public interface Quad {
    Transformation getTransformation();
    GraphicAreaTransformation getGraphicAreaTransformation();
}
