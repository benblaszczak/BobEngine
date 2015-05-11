/**
 * BobEngine - 2D game engine for Android
 * 
 * Copyright (C) 2014, 2015 Benjamin Blaszczak
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
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.security.Key;

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
	private Controller myController;                       // Object that assists in handing controller input
	private BobRenderer renderer;                          // This BobView's renderer
	private GraphicsHelper graphicsHelper;                 // An object that assists in loading graphics

	// Variables
	private Point screen;                                  // The size of the screen in pixels.
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
    @TargetApi(17)
	private void init(Context context) {
		myActivity = (Activity) context;                // This BobView's activity
		
		graphicsHelper = new GraphicsHelper(context);   // Initialize the graphics helper

        if (Build.HARDWARE.contains("goldfish")) {      // Check if we are running in the emulator
            setEGLConfigChooser(8, 8, 8, 8, 16, 0);     // Need to do this or sometimes the app will crash in the emulator
        }

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
	 * Set the controller object to be used for input.
	 * @param controller
	 */
	public void setController(Controller controller){
		myController = controller;
	}

	/**
	 * Returns this BobView's controller helper.
	 */
	public Controller getController() {
		return myController;
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
	 * Returns this BobView's BobRenderer.
	 */
	public BobRenderer getRenderer() {
		return renderer;
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

		if(isPortrait()) ratioX = (double) screen.x / INIT_BASE_W;
		else ratioX =  (double) screen.x / INIT_BASE_H;
	}

    /**
     * Returns the screen width correction ratio for dealing with different size screens.
     *
     * <br /><br />
     *
     * For example, you may do:             <br /><br />
     *
     * x = x + 1 * getRatioX();             <br /><br />
     *
     * instead of:                          <br /><br />
     *
     * x = x + 1;                           <br /><br />
     *
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

		if(isPortrait()) ratioY = (double) screen.y / INIT_BASE_H;
		else ratioY = (double) screen.y / INIT_BASE_W;
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

	@Override
	public void onResume(){
		setFocusable(true);
		requestFocus();
		super.onResume();
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
