package com.bobbyloujo.jumpybug;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.Graphic;

/**
 * Created by Ben on 1/6/2015.
 */
public class GameView extends BobView {

	// Rooms
	public static StartRoom start;   // The start screen
	public static GameRoom game;     // The actual game
	public static GameOver gameOver; // The game over screen

	// Graphics
	public static Graphic bug;
	public static Graphic title;
	public static Graphic play;
	public static Graphic flower;
	public static Graphic bg;
	public static Graphic over;

	public GameView(Context context) {
		super(context);
	}

	@Override
	protected void onCreateGraphics() {
		/*
         * Here is where we will create our graphics that we will use with our
         * game objects. You'll notice that it is very simple and requires only
         * one called to getGraphicsHelper().addGraphic(drawable) per graphic.
         *
         * Graphics can also be added elsewhere in your program, such as in the
         * constructor of a game object. In a game object or room, you can use
         * the getView() function to get the view that that object or room
         * belongs to. So, just called getView().getGraphicsHelper().addGraphic(drawable)
         * to add a graphic.
         *
         * The purpose of the onCreateGraphics() method is to provide a place to add
         * all your graphics or add graphics that are used by more than one game object.
         */

        /*
         * Here tell the graphics helper how to filter our graphics when they aren't
         * displayed at their native resolutions. We use GL10.GL_NEAREST here
         * because we have retro style graphics and we don't want them to be blurred.
         *
         * For more information, look up OpenGL texture filtering.
         */
		getGraphicsHelper().setParameters(false, GL10.GL_NEAREST, GL10.GL_NEAREST);

		// Now, add the graphics!
		bug = getGraphicsHelper().addGraphic(R.drawable.bug);
		title = getGraphicsHelper().addGraphic(R.drawable.title);
		play = getGraphicsHelper().addGraphic(R.drawable.play);
		flower = getGraphicsHelper().addGraphic(R.drawable.flower);
		bg = getGraphicsHelper().addGraphic(R.drawable.background);
		over = getGraphicsHelper().addGraphic(R.drawable.gameover);
	}


	@Override
	protected void onCreateRooms() {
        /*
         * Here is where you will initialize and setup your rooms. This method is a bit
         * more important than the onCreateGraphics() method. By initializing and setting
         * up your rooms here you are guaranteed to be able to use the getWidth() and
         * getHeight() methods for getting the width and height of the view. Using these
         * methods sooner (for example: in the GameView constructor) will result in an
         * exception due to the view not being inflated yet.
         *
         * It is safe to initialize and setup rooms in this method or anytime after
         * this method is called.
         */


		// Initialize the rooms
		start = new StartRoom(this);
		game = new GameRoom(this);
		gameOver = new GameOver(this);

		// Setup the rooms
		start.set();

		goToRoom(start); // Go to the start screen
	}
}
