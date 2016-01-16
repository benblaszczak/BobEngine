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
package com.bobbyloujo.bobengine.view;

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
import android.view.SurfaceHolder;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;

import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.extra.BobActivity;
import com.bobbyloujo.bobengine.extra.RoomCache;
import com.bobbyloujo.bobengine.graphics.GraphicsHelper;
import com.bobbyloujo.bobengine.systems.input.gamepad.Gamepad;
import com.bobbyloujo.bobengine.systems.input.touch.Touch;

/**
 * A BobEngine view that contains and renders Rooms with Entities. This class
 * can be extended to create custom BobViews that contain Rooms and Graphics.
 *
 * @author Benjamin Blaszczak
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
	private Gamepad gamepad;                               // Object that assists in handing gamepad input
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

		gamepad = new Gamepad(this);
		setOnTouchListener(myTouch = new Touch());      // Initialize the touch listener and assign it to this view

		setPreserveEGLContextOnPause(true);

		onCreateGraphics();                             // Create graphics

		/*
		 * We need to wait until the layout containing this BobView has been inflated
		 * before we initialize the rooms. This is because the getViewWidth() and getViewHeight()
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
	public void goToRoom(Class<? extends Room> roomType) {
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
	public void goToRoom(Class<? extends Room> roomType, Object... args) {
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
	 * Set the gamepad object to be used for input.
	 *
	 * @param gamepad
	 */
	public void setGamepad(Gamepad gamepad) {
		this.gamepad = gamepad;
	}

	/**
	 * Returns this BobView's gamepad helper.
	 */
	public Gamepad getGamepad() {
		return gamepad;
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
		return getScreenRotation() == Surface.ROTATION_0 || getScreenRotation() == Surface.ROTATION_180;
	}

	/**
	 * Returns true if the device orientation is landscape.
	 */
	public boolean isLandscape() {
		return getScreenRotation() == Surface.ROTATION_90 || getScreenRotation() == Surface.ROTATION_270;
	}

	/**
	 * Updates the screen width correction ratio. Can be a bit taxing, so try not to call it too
	 * much. This should only need to be updated if the orientation of the device has changed.
	 */
	@TargetApi(17)
	@Deprecated
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
	@Deprecated
	public double getRatioX() {
		return ratioX;
	}

	/**
	 * Updates the screen height correction ratio. Can be a bit taxing, so try not to call it too
	 * much.
	 */
	@TargetApi(17)
	@Deprecated
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
	@Deprecated
	public double getRatioY() {
		return ratioY;
	}

	/**
	 * Sets the background color for the BobView.
	 *
	 * @param red   Red value from 0 to 1
	 * @param green Green value from 0 to 1
	 * @param blue  Blue value from 0 to 1
	 * @param alpha Alpha value from 0 to 1
	 */
	public void setBackgroundColor(float red, float green, float blue, float alpha) {
		renderer.setBackgroundColor(red, green, blue, alpha);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
	}

	@Override
	public void onResume() {
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		renderer.onResume();
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int index, KeyEvent event) {
		if (gamepad != null && gamepad.onKeyDown(index, event)) {
			return true;
		}

		return super.onKeyDown(index, event);
	}

	@Override
	public boolean onKeyUp(int index, KeyEvent event) {
		if (gamepad != null && gamepad.onKeyUp(index, event)) {
			return true;
		}

		return super.onKeyUp(index, event);
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (gamepad != null && gamepad.onGenericMotionEvent(event)) {
			return true;
		}

		return super.onGenericMotionEvent(event);
	}

	/**
	 * This method should be used to add graphics using getGraphicsHelper().addGraphic(drawable).
	 * addGraphic returns a Graphic object which should be stored so that it can be accessed
	 * later by game objects.
	 *
	 * It is no longer necessary to use this method.
	 */
	protected void onCreateGraphics() {

	}

	/**
	 * This method should be where you initialize your rooms. It is called after the layout has been
	 * inflated, so it is safe to use getViewHeight(), and getViewWidth().
	 */
	protected abstract void onCreateRooms();
}
