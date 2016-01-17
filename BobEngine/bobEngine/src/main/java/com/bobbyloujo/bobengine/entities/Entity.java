/**
 * BobEngine - 2D game engine for Android
 *
 * Copyright (C) 2014, 2015, 2016 Benjamin Blaszczak
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
package com.bobbyloujo.bobengine.entities;

import android.app.Activity;

import com.bobbyloujo.bobengine.components.Component;
import com.bobbyloujo.bobengine.view.BobView;

import java.util.ArrayList;

/**
 * An Entity in the game. Entities are collections of Components and are also
 * components themselves. An Entity can hold data, perform logic, and be separate
 * or be a component of another parent Entity.
 *
 * Created by Ben on 9/24/2015.
 */
public class Entity implements Component {

	// Constants
    public static final int INIT_COMP_CAPACITY = 1;  // The initial size of the Component list

	// Data
    private Entity parent;                           // This Entity's parent, if it exists
	private Room room;                               // The Room this Entity belongs to, if this Entity belongs to a Room.
	private ArrayList<Component> components;         // The Component list

	/**
	 * Creates a new Entity without a parent.
	 */
    public Entity() {
		parent = null;
		room = null;
		components = new ArrayList<Component>(INIT_COMP_CAPACITY);
    }

	/**
	 * Creates an Entity with a parent. This will add this new Entity as a Component
	 * to the parent Entity.
	 *
	 * @param parent The Entity to add this Entity to.
	 */
    public Entity(Entity parent) {
		this();
		parent.addComponent(this);
    }

    /* UTILITY */

	/**
	 * Add a Component to this Entity.
	 * @param component
	 * @return
	 */
    public boolean addComponent(Component component) {
		boolean success = false;

		if (component instanceof Entity) {
			((Entity) component).parent = this;
			((Entity) component).room = getRoom();
			((Entity) component).onParentAssigned();
		}

		success = components.add(component);

		if (getRoom() != null) {
			getRoom().updateComponentLists();
		}

        return success;
    }

	/**
	 * Remove a component from this Entity.
	 *
	 * @param component The component to remove
	 * @return True if the component was removed, false if the component
	 * did not belong to this Entity.
	 */
    public boolean removeComponent(Component component) {
		boolean success = false;

		if (component instanceof Entity) {
			((Entity) component).parent = null;
			((Entity) component).room = null;
		}

		success = components.remove(component);

		if (getRoom() != null) {
			getRoom().updateComponentLists();
		}

        return success;
    }

	/**
	 * Get the list of components belonging to this Entity.
	 * @return This Entity's components.
	 */
    public ArrayList<Component> getComponents() {
        return components;
    }

	/**
	 * Get a list of all components of the particular type.
	 *
	 * @param type The type of component to search for.
	 * @param <T> Same as type
	 * @return An ArrayList of all components of type T belonging to this Entity.
	 */
	public <T extends Component> ArrayList<T> getComponentsOfType(Class<T> type) {
		ArrayList<T> comOfType = new ArrayList<T>();

		for (Component c: components) {
			if (type.isInstance(c)) {
				comOfType.add(type.cast(c));
			}
		}

		return comOfType;
	}

	/**
	 * Returns the first Component of the specified type that is found.
	 * @param type Class type of the component to search for.
	 * @return A Component of the specified type if found, null otherwise.
	 */
	public <T extends Component> T getComponent(Class<T> type) {
		T c = null;

		for (int i = 0; i < components.size() && c == null; i++) {
			if (type.isInstance(components.get(i))) {
				c = (T) components.get(i);
			}
		}

		return c;
	}

	/**
	 * Get a list of all the components belonging to this Entity and this
	 * Entity's components.
	 *
	 * @return Lots of components.
	 */
    public ArrayList<Component> getEntireComponentTree() {
		ArrayList<Component> allComponents = new ArrayList<Component>();

		getEntireComponentTree(allComponents);

        return allComponents;
    }

	/**
	 * Get a list of all the components belonging to this Entity and this
	 * Entity's components and store it in allComponents.
	 *
	 * @param allComponents List to populate with components.
	 */
	private void getEntireComponentTree(ArrayList<Component> allComponents) {
		for (int i = 0; i < components.size(); i++) {
			Component c = components.get(i);

            allComponents.add(c);

			if (c instanceof Entity) {
				((Entity) c).getEntireComponentTree(allComponents);
			}
		}
	}

	/**
	 * Get the Room that this Entity belongs to.
	 *
	 * @return The Room that this Entity belongs to if it belongs to a Room,
	 * null otherwise.
	 */
    public Room getRoom() {
		if (room != null) {
			return room;
		}

		if (this instanceof Room) {
			room = (Room) this;
		} else if (parent == null) {
			room = null;
		} else {
			room = parent.getRoom();
		}

		return room;
    }

	/**
	 * Get the BobView that this Entity's Room belongs to if this Entity
	 * belongs to a Room.
	 *
	 * @return The BobView that this Entity's Room belongs to if this Entity
	 * belongs to a Room, null otherwise.
	 */
    public BobView getView() {
		if (getRoom() == null) return null;

        return getRoom().getView();
    }

	/**
	 * Get the Activity that this Entity's BobView belongs to if this Entity
	 * belongs to a Room.
	 *
	 * @return The Activity that this Entity's BobView belongs to if this Entity
	 * belongs to a Room, null otherwise.
	 */
    public Activity getActivity() {
		if (getRoom() == null) return null;

        return getRoom().getActivity();
    }

    /* EVENTS */

	/**
	 * This event is triggered any time a parent Entity is assigned to this Entity.
	 */
	public void onParentAssigned() {

	}
}
