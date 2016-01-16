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
package com.bobbyloujo.bobengine.extra;

import android.util.Log;

import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.entities.Room;

/**
 * A cache of rooms. Useful for managing rooms in a large game.
 * Created by Benjamin on 11/23/2015.
 */
public class RoomCache {
	private BobView owner; // The BobView used to initialize new Room instances.
	private Room cache[];  // The cache to hold Rooms.
	private int cursor;    // A cursor for navigating the cache.

	/**
	 * Creates a new room cache for keep track of rooms and creating new
	 * rooms.
	 *
	 * @param owner The BobView used to initialize new Room instances.
	 * @param size  The number of Rooms to keep in the cache.
	 */
	public RoomCache(BobView owner, int size) {
		this.owner = owner;
		cache = new Room[size];
		cursor = 0;
	}

	/**
	 * Change the number of Rooms this cache can store. If the new size is smaller,
	 * the older Rooms in the cache will be removed.
	 *
	 * @param newSize The number of Rooms this cache should store
	 */
	public void changeSize(int newSize) {
		Room temp[] = new Room[newSize];
		int end = cursor;

		for (int i = 0, cur = cursor - 1; i < temp.length && cur != end; i++, cur--) {
			if (cur < 0) cur = cache.length - 1;
			temp[i] = cache[cur];
		}

		cache = temp;
	}

	/**
	 * Add a preexisting Room instance to the cache.
	 *
	 * @param room The room to add to this cache
	 */
	public void addRoom(Room room) {
		cache[cursor] = room;                      // Add the new instance to the cache.
		cursor++;                                  // Increment the cursor
		if (cursor >= cache.length)
			cursor = 0;    // Make sure the cursor doesn't go over the cache length.
	}

	/**
	 * Get the BobView used to initialize new Room instances.
	 *
	 * @return owner.
	 */
	public BobView getOwner() {
		return owner;
	}

	/**
	 * Searches the cache for an instance of the specified type of room. If no instance is
	 * found in the cache, a new instance will be made assuming that the room's constructor
	 * only takes a BobView as an argument. This new instance will be entered into
	 * the cache and the returned. If the cache is full, the oldest room will be removed.
	 *
	 * @param roomType The Class of the room type you want an instance of.
	 * @return an instance of roomType. If roomType has no constructor with only one
	 * parameter of type BobView, this function will return null.
	 * @throws IllegalArgumentException if roomType does not inherit Room.
	 */
	public Room getRoom(Class<? extends Room> roomType) {
		if (!Room.class.isAssignableFrom(roomType)) {                                      // Check if roomType is a Room
			throw new IllegalArgumentException("Class roomType does not inherit Room.");   // Not a room, you silly head!
		}

		for (int i = 0; i < cache.length; i++) {                                          // Look through the cache for an instance of roomType.
			if (cache[i] != null && cache[i].getClass() == roomType) {
				return cache[i];                                                          // Return it if one is found.
			}
		}

		try {
			Room newRoom = roomType.getConstructor(BobView.class).newInstance(owner);        // Create a new instance of roomType
			addRoom(newRoom);                                                                // Add the new instance to the cache.
			return newRoom;                                                                  // Return the new instance!
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getCause() != null) e.getCause().printStackTrace();
		}

		Log.e("BobEngine", "There was a problem getting or creating the room.");
		return null;
	}

	/**
	 * Returns an instance of the specified room type. If the an instance is not found in the cache,
	 * the new instance will be initialized with the provided arguments. If a new instance is created,
	 * it will be added to the cache.
	 *
	 * @param roomType The Class of the room type you want an instance of.
	 * @param args     The arguments for initializing a new instance of roomType if need be.
	 *                 Must match the arguments of one of roomType's public constructors.
	 * @return an instance of roomType.
	 * @throws IllegalArgumentException if roomType does not inherit Room or the arguments in args do not
	 *                                  match the parameters of any constructor of class roomType.
	 */
	public Room getRoom(Class<? extends Room> roomType, Object... args) {
		if (!Room.class.isAssignableFrom(roomType)) {                                       // Check if roomType is actually a Room
			throw new IllegalArgumentException("Class roomType does not inherit Room.");    // Something other than a Room was passed
		}

		for (int i = 0; i < cache.length; i++) {                       // Look through the cache for an instance of roomType
			if (cache[i] != null && cache[i].getClass() == roomType)
				return cache[i];                                       // Return the instance if found.
		}

		java.lang.reflect.Constructor constructors[] = roomType.getConstructors(); // Get all of roomType's constructors
		java.lang.reflect.Constructor constructor = null;                          // This will be the constructor matching the arguments given

		for (int i = 0; i < constructors.length && constructor == null; i++) {     // Find the correct constructor
			Class p[] = constructors[i].getParameterTypes();                       // Get the parameter types of constructor i

			if (p.length == args.length) {                                         // Correct constructor must have the same number of arguments
				boolean correctConstructor = true;                                 // Will be set to false if constructor i is not the correct constructor

				for (int j = 0; j < p.length; j++) {                                               // Compare the parameters of constructor i to the arguments given
					if (p[j].isPrimitive()) {                                                      // Primitives need to be handled individually b/c primitive parameters take object equivalent arguments
						if (p[j] == int.class && args[j].getClass() != Integer.class) {            // If the parameter is an int, check if the argument is an Integer
							correctConstructor = false;
						} else if (p[j] == double.class && args[j].getClass() != Double.class) {   // double and Double
							correctConstructor = false;
						} else if (p[j] == byte.class && args[j].getClass() != Byte.class) {       // byte and Byte
							correctConstructor = false;
						} else if (p[j] == short.class && args[j].getClass() != Short.class) {     // short and Short
							correctConstructor = false;
						} else if (p[j] == long.class && args[j].getClass() != Long.class) {       // long and Long
							correctConstructor = false;
						} else if (p[j] == float.class && args[j].getClass() != Float.class) {     // float and Float
							correctConstructor = false;
						} else if (p[j] == boolean.class && args[j].getClass() != Boolean.class) { // boolean and Boolean
							correctConstructor = false;
						} else if (p[j] == char.class && args[j].getClass() != Character.class) {  // char and Character
							correctConstructor = false;
						}
					} else if (p[j] != args[j].getClass() && !p[j].isAssignableFrom(args[j].getClass())) { // The parameter is not primitive, check if the argument is a compatible class type
						correctConstructor = false;
					}
				}

				if (correctConstructor) {            // If correctConstructor is still true, constructors[i] is the correct constructor.
					constructor = constructors[i];
				}
			}
		}

		if (constructor == null) { // No constructor matching the arguments was found.
			throw new IllegalArgumentException("Arguments passed do not match the parameters of any constructor of class roomType.");
		}

		try {
			Room newRoom = (Room) constructor.newInstance(args);     // Create a new instance of roomType.
			addRoom(newRoom);                                        // Add the new instance to the cache.
			return newRoom;                                          // Return the new instance.
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getCause() != null) e.getCause().printStackTrace();
		}

		Log.e("BobEngine", "There was a problem getting or creating the room.");
		return null;
	}
}
