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
package com.bobbyloujo.bobengine.systems;

import javax.microedition.khronos.opengles.GL10;

import com.bobbyloujo.bobengine.components.Component;
import com.bobbyloujo.bobengine.graphics.Graphic;

/**
 * This interface is for a Component that can render things on the screen using
 * OpenGL.
 *
 * todo tiled background could be a quad that doesn't move but the graphic transform does...
 *
 * Created by Benjamin on 11/19/2015.
 */
public interface Renderable extends Component {
	void render(GL10 gl, int layer);
	Graphic getGraphic();
}
