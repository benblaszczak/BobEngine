/**
 * BobEngine - 2D game engine for Android
 * <p/>
 * Copyright (C) 2014, 2015 Benjamin Blaszczak
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

package bobby.engine.bobengine;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;

/**
 * A BobEngine view that contains and renders Rooms with GameObjects. This class
 * should be extended to create custom BobViews that contain Rooms and Graphics.
 *
 * @author Ben
 * @modified 9/21/15
 */
public abstract class BobView extends GLSurfaceView {

	// Constants
	private final int INIT_BASE_W = 720;                   // Initial base screen width resolution for correction ratio
	private final int INIT_BASE_H = 1280;                  // Initial base screen height resolution

	private final int DEF_CACHE_SIZE = 2;                  // The default size of this BobView's room cache.

	// Objects
	private Room currentRoom;                              // The room that is currently being updated and drawn
	private Activity myActivity;                           // The activity that this BobView belongs to.
	private Touch myTouch;                                 // An object which handles touch screen input
	private Controller controller;                       // Object that assists in handing controller input
	private BobRenderer renderer;                          // This BobView's renderer
	private GraphicsHelper graphicsHelper;                 // An object that assists in loading graphics
	private RoomCache cache;                               // A cache that can be used to store instances of rooms or create new instances

	// Variables
	private Point screen;                                  // The size of the screen in pixels.
	private double ratioX;                                 // Screen width correction ratio for handling different sized screens
	private double ratioY;                                 // Screen height correction ratio
	private boolean created;                               // Flag that indicates this view has already been created.

	/**
	 * BobView is used to display BobEngine content.
	 *
	 * @param context
	 * @param attr
	 */
	public BobView(Context context, AttributeSet attr) {
		super(context, attr);

		init(context, DEF_CACHE_SIZE);
	}

	/**
	 * BobView is used to display BobEngine content.
	 *
	 * @param context
	 */
	public BobView(Context context) {
		super(context);

		init(context, DEF_CACHE_SIZE);
	}

	/**
	 * BobView is used to display BobEngine content. You can specify the
	 * size of this BobView's room cache with cacheSize.
	 *
	 * @param context
	 * @param cacheSize The number of rooms for this BobView's room cache
	 *                  to store.
	 */
	public BobView(Context context, int cacheSize) {
		super(context);

		init(context, cacheSize);
	}

	/**
	 * BobEngine BobView initialization.
	 *
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(17)
	private void init(Context context, int cacheSize) {
		created = false;

		myActivity = (Activity) context;                // This BobView's activity

		graphicsHelper = new GraphicsHelper(context);   // Initialize the graphics helper

		setEGLConfigChooser(8, 8, 8, 8, 16, 0);         // These two lines enable transparent
		getHolder().setFormat(PixelFormat.TRANSLUCENT); // backgrounds.

		renderer = new BobRenderer();                   // Initialize the renderer
		setRenderer(renderer);                          // and assign it to this view

		setOnTouchListener(myTouch = new Touch(this));  // Initialize the touch listener and assign it to this view

		onCreateGraphics();                             // Create graphics

		/*
		 * We need to wait until the layout containing this BobView has been inflated
		 * before we initialize the rooms. This is because the getWidth() and getHeight()
		 * functions in the rooms rely on the View being inflated. Therefore, to use them
		 * in the constructors of the rooms / game objects, the View has to be inflated first.
		 */
		this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {           // The layout has been inflated
				if (!created) onCreateRooms();       // Create the rooms
				created = true;                      // Make sure we only create the rooms once.
			}

		});

		/* Screen Size and Correction Ratios */
		WindowManager wm;                                                                // For getting information about the display.

		wm = (WindowManager) myActivity.getSystemService(Context.WINDOW_SERVICE);

		screen = new Point();

		if (myActivity instanceof BobActivity) {                                         // Best to use a BobActivity but not required
			screen.x = ((BobActivity) myActivity).getScreenWidth();
			screen.y = ((BobActivity) myActivity).getScreenHeight();
		} else {                                                                         // Must account for cases where a BobActivity is not used.

			try {
				wm.getDefaultDisplay().getRealSize(screen);
			} catch (NoSuchMethodError e) {
				screen.x = wm.getDefaultDisplay().getWidth();
				screen.y = wm.getDefaultDisplay().getHeight();
			}
		}

		updateRatioX();
		updateRatioY();

		cache = new RoomCache(this, cacheSize);
	}

	public RoomCache getRoomCache() {
		return cache;
	}

	/**
	 * Returns the current room.
	 */
	public Room getCurrentRoom() {
		return currentRoom;
	}

	/**
	 * Changes the current room to nextRoom.
	 *
	 * @param nextRoom the room to switch to.
	 */
	public void goToRoom(Room nextRoom) {
		currentRoom = nextRoom;
		currentRoom.indicateGraphicsUsed();
	}

	/**
	 * Changes the current room to an instance of roomType. The instance is
	 * either one that is found in this BobView's RoomCache or a newly
	 * created instance.
	 *
	 * @param roomType The type of room to go to. Must be a class that inherits
	 *                 Room.
	 */
	public void goToRoom(Class roomType) {
		goToRoom(cache.getRoom(roomType));
	}

	/**
	 * Changes the current room to an instance of roomType. The instance
	 * is either one that is found in this BobView's RoomCache or a newly
	 * created instance using the given arguments.
	 *
	 * @param roomType The type of room to go to. Must be a class that inherits
	 *                 Room.
	 * @param args     The arguments with which to create a new instance of roomType if
	 *                 an instance is not found in the cache. These arguments must match
	 *                 the parameters of one of roomType's constructors.
	 */
	public void goToRoom(Class roomType, Object... args) {
		goToRoom(cache.getRoom(roomType, args));
	}

	/**
	 * @return This BobView's GraphicsHelper
	 */
	public GraphicsHelper getGraphicsHelper() {
		return graphicsHelper;
	}

	/**
	 * <u><i>Use a BobRenderer instead.</u></i>
	 */
	@Override
	public void setRenderer(Renderer renderer) {
		Log.e("BobEngine", "BobRenderer not used!! Use a BobRenderer instead of a Renderer!");
		super.setRenderer(renderer);
	}

	/**
	 * Returns the Activity that contains this BobView.
	 */
	public Activity getActivity() {
		return myActivity;
	}

	/**
	 * Returns this BobView's Touch touch listener.
	 */
	public Touch getTouch() {
		return myTouch;
	}

	/**
	 * Set the controller object to be used for input.
	 *
	 * @param controller
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Returns this BobView's controller helper.
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * Sets this BobView's renderer and starts the rendering thread. BobViews
	 * should always use BobRenderers. When a BobView is initialized it automatically
	 * given a renderer.
	 *
	 * @param renderer - The BobRenderer for this view.
	 */
	private void setRenderer(BobRenderer renderer) {
		super.setRenderer(renderer);
		renderer.setOwner(this);
		this.renderer = renderer;
	}

	/**
	 * Returns this BobView's BobRenderer.
	 */
	public BobRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Gets the screen orientation.
	 *
	 * @return Surface.ROTATION_0 for no rotation (portrait) <br />
	 * Surface.ROTATION_90 for 90 degree rotation (landscape) <br />
	 * Surface.ROTATION_180 for 180 degree rotation (reverse
	 * portrait/upside down portrait) <br />
	 * Surface.ROTATION_270 for 270 degree rotation (reverse landscape)
	 */
	public float getScreenRotation() {
		return myActivity.getWindowManager().getDefaultDisplay().getRotation();
	}

	/**
	 * Returns true if the orientation is portrait. (Including reverse portrait)
	 */
	public boolean isPortrait() {
		if (getScreenRotation() == Surface.ROTATION_0 || getScreenRotation() == Surface.ROTATION_180) {
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the device orientation is landscape.
	 */
	public boolean isLandscape() {
		if (getScreenRotation() == Surface.ROTATION_90 || getScreenRotation() == Surface.ROTATION_270) {
			return true;
		}

		return false;
	}

	/**
	 * Updates the screen width correction ratio. Can be a bit taxing, so try not to call it too
	 * much. This should only need to be updated if the orientation of the device has changed.
	 */
	@TargetApi(17)
	public void updateRatioX() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		try {
			wm.getDefaultDisplay().getRealSize(screen);
		} catch (NoSuchMethodError e) {
			screen.x = wm.getDefaultDisplay().getWidth();
		}

		if (isPortrait()) ratioX = (double) screen.x / (double) INIT_BASE_W;
		else ratioX = (double) screen.x / (double) INIT_BASE_H;
	}

	/**
	 * Returns the screen width correction ratio for dealing with different size screens.
	 * <p/>
	 * For example, you may do:     <br/>
	 * x = x + 1 * getRatioX();     <br/>
	 * instead of:                  <br/>
	 * x = x + 1;                   <br/>
	 * <p/>
	 * So that the object moves to the right at the same speed relative to screen
	 * size on all different screen sizes.
	 */
	@TargetApi(17)
	public double getRatioX() {
		return ratioX;
	}

	/**
	 * Updates the screen height correction ratio. Can be a bit taxing, so try not to call it too
	 * much.
	 */
	@TargetApi(17)
	public void updateRatioY() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		try {
			wm.getDefaultDisplay().getRealSize(screen);
		} catch (NoSuchMethodError e) {
			screen.y = wm.getDefaultDisplay().getHeight();
		}

		if (isPortrait()) ratioY = (double) screen.y / (double) INIT_BASE_H;
		else ratioY = (double) screen.y / (double) INIT_BASE_W;
	}

	/**
	 * Returns the screen height correction ratio for dealing with different size screens.
	 */
	@TargetApi(17)
	public double getRatioY() {
		return ratioY;
	}

	/**
	 * Sets the background color for the BobView.
	 *
	 * @param red   - Red value from 0 to 1
	 * @param green - Green value from 0 to 1
	 * @param blue  - Blue value from 0 to 1
	 * @param alpha - Alpha value from 0 to 1
	 */
	public void setBackgroundColor(float red, float green, float blue, float alpha) {
		renderer.setBackgroundColor(red, green, blue, alpha);
	}

	@Override
	public void onResume() {
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int index, KeyEvent event) {
		if (controller != null && controller.onKeyDown(index, event)) {
			return true;
		}

		return super.onKeyDown(index, event);
	}

	@Override
	public boolean onKeyUp(int index, KeyEvent event) {
		if (controller != null && controller.onKeyUp(index, event)) {
			return true;
		}

		return super.onKeyUp(index, event);
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (controller != null && controller.onGenericMotionEvent(event)) {
			return true;
		}

		return super.onGenericMotionEvent(event);
	}

	/**
	 * This method should be used to add graphics using getGraphicsHelper().addGraphic(drawable).
	 * addGraphic returns a Graphic object which should be stored so that it can be accessed
	 * later by game objects.
	 */
	protected abstract void onCreateGraphics();

	/**
	 * This method should be where you initialize your rooms. It is called after the layout has been
	 * inflated, so it is safe to use getHeight(), and getWidth().
	 */
	protected abstract void onCreateRooms();

	/**
	 * A cache of rooms. Useful for managing rooms in a large game.
	 */
	public static class RoomCache {

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
		 * @param newSize
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
		 * @param room
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
				if (cache[i] != null && cache[i].getClass() == roomType)
					return cache[i];     // Return it if one is found.
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
}
