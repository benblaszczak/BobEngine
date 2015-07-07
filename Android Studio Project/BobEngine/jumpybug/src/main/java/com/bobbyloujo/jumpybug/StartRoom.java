package com.bobbyloujo.jumpybug;

import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Room;

/**
 * Created by Ben on 1/6/2015.
 */
public class StartRoom extends Room {

	// Objects
	private GameObject title;           // The title graphic
	private GameObject playButton;      // The Play button
	private Background bg1;             // Background panel 1
	private Background bg2;             // Background panel 2

	public StartRoom(BobView container) {
		super(container);

		// Initialize objects
		/**
		 * Game object initialization requires the room that the object is placed in.
		 * This is all that is need to ensure that these objects will be placed within
		 * this room.
		 */
		title = new GameObject(this);
		playButton = new GameObject(this);
		bg1 = new Background(this);
		bg2 = new Background(this);
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
		title.height = title.width / 2;      // Our graphic is twice as wide as it is tall.
		title.setGraphic(GameView.title, 1); // We assign the graphic that we added in our GameView to our title object. This graphic has 1 frame.

        /*
         * Setting up the play button will be very similar.
         */
		playButton.x = getWidth() / 2;
		playButton.y = getHeight() / 4;
		playButton.width = getWidth() / 4;
		playButton.height = playButton.width / 2;
		playButton.setGraphic(GameView.play, 2);  // Play button has 2 frames: one for pressed and one for not pressed.

		bg1.set(getWidth() / 2, 4);               // Visible, will move off screen
		bg2.set((int) bg1.x + getWidth(), 4);     // Off screen, will move on screen.
	}

	/**
	 * Step event happens every frame.
	 *
	 * @param dt
	 */
	@Override
	public void step(double dt) {
		if (getTouch().objectTouched(playButton)) {  // Is the play button being touched?
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
