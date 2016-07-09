package com.bobbyloujo.jumpybug;

import com.bobbyloujo.bobengine.extra.ScrollingImage;
import com.bobbyloujo.bobengine.graphics.GraphicsHelper;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.entities.GameObject;
import com.bobbyloujo.bobengine.entities.Room;

/**
 * Created by Ben on 1/6/2015.
 */
public class StartRoom extends Room {

	// Objects
	private GameObject title;           // The title graphic
	private GameObject playButton;      // The Play button
	//private Background background;      // Background panels
	private ScrollingImage background;

	public StartRoom(BobView container) {
		super(container);

		getView().setBackgroundColor(0, 1, 1, 1);

		setGridHeight(160);            // The grid should be 160 units tall
		setGridUnitX(getGridUnitY());  // We want square units, the units on the X axis should be the same as the Y

		// Initialize objects
		/**
		 * Game object initialization requires the room that the object is placed in.
		 * This is all that is need to ensure that these objects will be placed within
		 * this room.
		 */
		title = new GameObject(this);
		playButton = new GameObject(this);

		//background = new Background(this);
		background = new ScrollingImage(this);
		getView().getGraphicsHelper().setParameters(true, GraphicsHelper.MIN_PIXEL_MIPMAP, GraphicsHelper.MAG_PIXEL, true);
		background.setGraphic(getView().getGraphicsHelper().getGraphic(R.drawable.background));
		background.addComponent(new Updatable() {
			@Override
			public void update(double deltaTime) {
				background.graphic.transform.x += 0.01;
			}
		});
	}

	/**
	 * Set method. I like to define one of these for each room and game object. The purpose of
	 * this method is the setup and reset the room. This makes it easy to set up the initial
	 * state of the game and to reset whatever rooms need to be reset when the player hits the
	 * play or play again button.
	 */
	public void set() {
		/*
         * Setting up the title graphic. Since we used a generic GameObject for the title graphic,
         * we'll just do all the setup for it here. The title graphic doesn't do much, so I don't
         * feel the need to define it's own class.
         */
		title.x = getWidth() / 2;            // getWidth() returns the width of this view. This line centers the graphic horizontally.
		title.y = getHeight() * 3 / 4;       // and this positions it 3/4 of the way up the screen.
		title.width = getWidth() * 3 / 4;    // The width of the title will be 3/4 the width of the screen.
		title.height = title.width / 2;          // Our graphic is twice as wide as it is tall.
		title.setGraphic(GameView.title);        // We assign the graphic that we added in our GameView to our title object. This graphic has 1 frame.

        /*
         * Setting up the play button will be very similar.
         */
		playButton.x = getWidth() / 2;
		playButton.y = getHeight() / 4;
		playButton.width = getWidth() / 4;
		playButton.height = playButton.width / 2;
		playButton.setGraphic(GameView.play, 2);  // Play button has 2 frames: one for pressed and one for not pressed.

		//background.set();
	}

	/**
	 * Step event happens every frame.
	 *
	 * @param dt
	 */
	@Override
	public void step(double dt) {
		if (getTouch().held(getTouch().getPointerTouchingObject(playButton))) {  // Is the play button being touched?
			playButton.frame = 1;                    // Pressed frame.
		} else {
			playButton.frame = 0;                    // Not pressed frame.
		}
	}

	public void released(int index) {
		/**
		 * getTouch() provides many useful functions for getting
		 * touchscreen input. See the touchInput example for more
		 * information.
		 */
		if (getTouch().objectTouched(playButton)) {  // Play button touched
			GameView.game.set();                     // Set up the game room.
			getView().goToRoom(GameView.game);       // Go to the game room.
		}
	}
}
