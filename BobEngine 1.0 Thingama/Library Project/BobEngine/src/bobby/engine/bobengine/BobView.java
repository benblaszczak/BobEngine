/**
 * BobEngine - 2D game engine for Android
 * 
 * Copyright (C) 2014 Benjamin Blaszczak
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

package bobby.engine.bobengine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * A BobEngine view that contains and renders Rooms with GameObjects. This class
 * should be extended to create custom BobViews that contain Rooms and Graphics.
 * 
 * @author Ben
 * 
 */
public abstract class BobView extends GLSurfaceView {

	// Constants
	private final int INIT_BASE_W = 720;                   // Initial base screen width for correction ratio
	private final int INIT_BASE_H = 1280;                  // Initial base screen height

	// Objects
	private Room currentRoom;                              // The room that is currently being updated and drawn
	private Activity myActivity;                           // The activity that this BobView belongs to.
	private Touch myTouch;                                 // An object which handles touch screen input
	private BobRenderer renderer;                          // This BobView's renderer
	private GraphicsHelper graphicsHelper;                 // An object that assists in loading graphics

	// Variables
	private Point screen;                                  // The size of the screen in pixels.
	private Point base;                                    // The base screen dimensions to use for calculating correction ratios.
	private double ratioX;                                 // Screen width correction ratio for handling different sized screens
	private double ratioY;                                 // Screen height correction ratio
	private boolean created;                               // Flag that indicates this view has already been created.

	public BobView(Context context, AttributeSet attr) {
		super(context, attr);
		
		created = false;

		init(context);
	}

	public BobView(Context context) {
		super(context);

		created = false;
		
		init(context);
	}

	/**
	 * BobEngine BobView initialization.
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint({ "ClickableViewAccessibility", "NewApi" })
	private void init(Context context) {
		myActivity = (Activity) context;                // This BobView's activity
		
		graphicsHelper = new GraphicsHelper(context);   // Initialize the graphics helper

		renderer = new BobRenderer();                   // Initialize the renderer
		setRenderer(renderer);                          // and assign it to this view

		setOnTouchListener(myTouch = new Touch(this));  // Initialize the touch listener and assign it to this view

		onCreateGraphics();                             // Create graphics

		/*
		 * We need to wait until the layout containing this BobView has been inflated
		 * before we initialize the rooms. This is because the getWidth() and getHeight()
		 * functions in the rooms rely on the View being inflated. Therefore, to use them
		 * in the contructors of the rooms / game objects, the View has to be inflated first.
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
		int rot;                                                                         // The screen rotation

		wm = (WindowManager) myActivity.getSystemService(Context.WINDOW_SERVICE);

		screen = new Point();
		base = new Point();
		
		if (myActivity instanceof BobActivity) {                                         // Best to use a BobActivity but not required
			screen.x = ((BobActivity) myActivity).screenWidth;
			screen.y = ((BobActivity) myActivity).screenHeight;
		} else {                                                                         // Must account for cases where a BobActivity is not used.

			try {
				wm.getDefaultDisplay().getRealSize(screen);
			} catch (NoSuchMethodError e) {
				screen.x = wm.getDefaultDisplay().getWidth();
				screen.y = wm.getDefaultDisplay().getHeight();
			}
		}

		/*
		 * The following block of code creates x and y ratios of the current device's
		 * screen dimensions compared to some base dimensions. These ratios can be used
		 * to ensure the game plays the same on all different screen sizes.
		 * 
		 * For example, you may do:
		 * 
		 * x = x + 1 * getRatioX();
		 * 
		 * instead of:
		 * 
		 * x = x + 1;
		 * 
		 * So that the object moves to the right at the same speed relative to screen
		 * size on all different screen sizes.
		 * 
		 * These ratios are based on the orientation of the device when this view is created.
		 */
		rot = wm.getDefaultDisplay().getRotation();

		if (rot == Surface.ROTATION_0 || rot == Surface.ROTATION_180) {                   // Portrait orientation
			base.x = INIT_BASE_W;
			base.y = INIT_BASE_H;
		} else {                                                                          // Landscape orientation
			base.x = INIT_BASE_H;
			base.y = INIT_BASE_W;
		}

		ratioX = (double) screen.x / (double) base.x;
		ratioY = (double) screen.y / (double) base.y;
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
	 * @param nextRoom - the room to switch to.
	 */
	public void goToRoom(Room nextRoom) {
		currentRoom = nextRoom;
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
	 * Sets this BobView's renderer and starts the rendering thread. BobViews
	 * should always use BobRenderers. When a BobView is initialized it automatically
	 * given a renderer.
	 * 
	 * @param renderer
	 *            - The BobRenderer for this view.
	 */
	private void setRenderer(BobRenderer renderer) {
		super.setRenderer(renderer);
		renderer.setOwner(this);
		this.renderer = renderer;
	}

	/**
	 * Gets the screen orientation.
	 * 
	 * @return Surface.ROTATION_0 for no rotation (portrait) <br />
	 *         Surface.ROTATION_90 for 90 degree rotation (landscape) <br />
	 *         Surface.ROTATION_180 for 180 degree rotation (reverse
	 *         portrait/upside down portrait) <br />
	 *         Surface.ROTATION_270 for 270 degree rotation (reverse landscape)
	 */
	public float getScreenRotation() {
		return myActivity.getWindowManager().getDefaultDisplay().getRotation();
	}

	/**
	 * Returns true if the oriention is portrait. (Including reverse portrait)
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
	 * Returns the screen width correction ratio for dealing with different size screens.
	 * This ratio is based off of the initial orientation of the device when the BobView is initialized!
	 */
	public double getRatioX() {
		return ratioX;
	}
	
	/**
	 * Returns the screen height correction ratio for dealing with different size screens.
	 * This ratio is based off of the initial orientation of the device when the BobView is initialized!
	 */
	public double getRatioY() {
		return ratioY;
	}

	/**
	 * Sets the background color for the BobView.
	 * 
	 * @param red
	 *            - Red value from 0 to 1
	 * @param green
	 *            - Green value from 0 to 1
	 * @param blue
	 *            - Blue value from 0 to 1
	 * @param alpha
	 *            - Alpha value from 0 to 1
	 */
	public void setBackgroundColor(float red, float green, float blue, float alpha) {
		renderer.setBackgroundColor(red, green, blue, alpha);
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
}
