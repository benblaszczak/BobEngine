package com.bobbyloujo.jumpybug;

import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.NumberDisplay;
import bobby.engine.bobengine.Room;

/**
 * Created by Ben on 1/6/2015.
 */
public class GameOver extends Room {

    // Objects
    public GameObject play;             // Play button
    private Background bg1;             // Background panel 1
    private Background bg2;             // Background panel 2
    private NumberDisplay score;        // Score display
    private GameObject gameOver;        // Game Over graphic

    public GameOver(BobView container) {
        super(container);

        // Initialize
        play = new GameObject(this);
        bg1 = new Background(this);
        bg2 = new Background(this);
        score = new NumberDisplay(this);
        gameOver = new GameObject(this);
    }

    public void set() {
        play.x = getWidth() / 2;
        play.y = getHeight() / 4;
        play.width = getWidth() / 4;
        play.height = play.width / 2;
        play.setGraphic(GameView.play, 2);

        bg1.set(getWidth() / 2, 4);
        bg2.set(getWidth() * 3 / 2, 4);

        score.setNumber(GameView.game.getScore());
        score.y = getHeight() / 2;
        score.width = getWidth() / 5;
        score.height = score.width;
        score.x = getWidth() / 2;
        score.setAlignment(1);

        gameOver.x = getWidth() / 2;
        gameOver.y = getHeight() * 5 / 6;
        gameOver.width = getWidth() / 3;
        gameOver.height = gameOver.width;
        gameOver.setGraphic(GameView.over, 1);
    }

    /**
     * Step event happens every frame.
     *
     * @param dt
     */
    @Override
    public void step(double dt) {
        if (getTouch().objectTouched(play)) {  // Is the play button being touched?
            play.frame = 1;                    // Pressed frame.
        } else {
            play.frame = 0;                    // Not pressed frame.
        }
    }

    /**
     * Released event is fired when a finger/stylus is released from the touchscreen.
     *
     * @param index - ID number of the finger that fired this event.
     */
    public void released(int index){
        super.released(index);                       // MUST call super for the newpress and released events in rooms. (But not in GameObjects!)

        /**
         * getTouch() provides many useful functions for getting
         * touchscreen input. See the touchInput example for more
         * information.
         */
        if (getTouch().objectTouched(play)) {        // Play button touched
            GameView.game.set();                     // Set up the game room.
            getView().goToRoom(GameView.game);       // Go to the game room.
        }
    }
}
