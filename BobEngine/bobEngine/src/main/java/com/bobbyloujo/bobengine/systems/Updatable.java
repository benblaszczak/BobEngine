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

import com.bobbyloujo.bobengine.components.Component;

/**
 * This is the interface for a Component that updates every frame.
 *
 * Created by Benjamin on 11/14/2015.
 */
public interface Updatable extends Component {
	void update(double deltaTime);
}
