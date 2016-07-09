package com.bobbyloujo.bobengine.extra;

import com.bobbyloujo.bobengine.components.ParentAssignmentHandler;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.systems.Renderable;
import com.bobbyloujo.bobengine.systems.ShadeRenderer;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.systems.quadrenderer.QuadRenderSystem;

/**
 * This component can be added to a QuadRenderSystem or ShadeRenderer to create a flashing
 * or pulsing effect.
 * Created by Benjamin on 4/11/2016.
 */
public class FlashingEffect implements Updatable, ParentAssignmentHandler {

    private Renderable renderer;
    private int framesPerFlash;
    private int repeat;
    private int loops;
    private int layer;
    private boolean shouldPulse;
    private int direction;

    private float redMin, redMax, redClear;
    private float greenMin, greenMax, greenClear;
    private float blueMin, blueMax, blueClear;
    private float alphaMin, alphaMax, alphaClear;

    private int frames;

    public FlashingEffect() {
        layer = 2;
        repeat = -1;
        loops = 0;
        framesPerFlash = 60;
        shouldPulse = true;
        direction = 1;

        redMin = greenMin = blueMin = 0.5f;
        redMax = greenMax = blueMax = 1f;
        alphaMin = alphaMax = 1f;

        redClear = blueClear = greenClear = alphaClear = 1f;
    }

    public void goFromMaxToMin(boolean maxToMin) {
        if (maxToMin) {
            direction = -1;
        } else {
            direction = 1;
        }
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void setShouldPulse(boolean shouldPulse) {
        this.shouldPulse = shouldPulse;
    }

    public void setFramesPerFlash(int framesPerFlash) {
        this.framesPerFlash = framesPerFlash;
    }

    public void setRepeats(int repeats) {
        this.loops = this.repeat = repeats;
    }

    public void setMinColor(float r, float g, float b, float a) {
        this.redMin = r;
        this.greenMin = g;
        this.blueMin = b;
        this.alphaMin = a;
    }

    public void setMaxColor(float r, float g, float b, float a) {
        this.redMax = r;
        this.greenMax = g;
        this.blueMax = b;
        this.alphaMax = a;
    }

    public void setClearColor(float r, float g, float b, float a) {
        this.redClear = r;
        this.greenClear = g;
        this.blueClear = b;
        this.alphaClear = a;
    }

    public void start() {
        loops = 0;

        if (direction == 1) {
            frames = 0;
        } else if (direction == -1) {
            frames = framesPerFlash;
        }
    }

    @Override
    public void onParentAssigned(Entity parent) {
        if (parent instanceof QuadRenderSystem) {
            renderer = (QuadRenderSystem) parent;
        } else if (parent instanceof ShadeRenderer) {
            renderer = (ShadeRenderer) parent;
        }
    }

    @Override
    public void update(double deltaTime) {
        if (repeat == -1 || loops < repeat) {

            if (shouldPulse) {
                if (loops % 2 == 0) {
                    frames += direction;
                } else {
                    frames -= direction;
                }
            } else {
                frames += direction;
            }

            if (frames > framesPerFlash || frames < 0) {
                loops++;

                if (!shouldPulse) {
                    if (direction == 1) {
                        frames = 0;
                    } else if (direction == -1) {
                        frames = framesPerFlash;
                    }
                }
            }

            float red = redMin + (redMax - redMin) * ((float) frames / (float) framesPerFlash);
            float green = greenMin + (greenMax - greenMin) * ((float) frames / (float) framesPerFlash);
            float blue = blueMin + (blueMax - blueMin) * ((float) frames / (float) framesPerFlash);
            float alpha = alphaMin + (alphaMax - alphaMin) * ((float) frames / (float) framesPerFlash);

            if (renderer != null) {
                if (renderer instanceof QuadRenderSystem) {
                    ((QuadRenderSystem) renderer).setLayerColor(layer, red, green, blue, alpha);
                } else if (renderer instanceof ShadeRenderer) {
                    ((ShadeRenderer) renderer).setLayerColor(layer, red, green, blue, alpha);
                }
            }

            if (loops >= repeat && repeat != -1) {
                if (renderer != null) {
                    if (renderer instanceof QuadRenderSystem) {
                        ((QuadRenderSystem) renderer).setLayerColor(layer, redClear, greenClear, blueClear, alphaClear);
                    } else if (renderer instanceof ShadeRenderer) {
                        ((ShadeRenderer) renderer).setLayerColor(layer, redClear, greenClear, blueClear, alphaClear);
                    }
                }
            }
        }
    }
}
