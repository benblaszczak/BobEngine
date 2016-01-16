package com.bobbyloujo.jumpybug;

import com.bobbyloujo.bobengine.entities.SimpleGameObject;
import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.entities.GameObject;
import com.bobbyloujo.bobengine.extra.NumberDisplay;
import com.bobbyloujo.bobengine.entities.Room;

/**
 * Created by Ben on 1/6/2015.
 */
public class GameOver extends Room {

    // Objects
    public GameObject play;             // Play button
	private Background background;      // Background panels
    private NumberDisplay score;        // Score display
    private SimpleGameObject gameOver;        // Game Over graphic

    public GameOver(BobView container) {
        super(container);

		getView().setBackgroundColor(0, 1, 1, 1);

        setGridHeight(160);
        setGridUnitX(getGridUnitY());

        // Initialize
        play = new GameObject(this);
        score = new NumberDisplay(this);
        gameOver = new SimpleGameObject(this);
        background = new Background(this);
    }

    public void set() {
        play.x = getWidth() / 2;
        play.y = getHeight() / 4;
        play.width = getWidth() / 4;
        play.height = play.width / 2;
        play.setGraphic(GameView.play, 2);

        score.setNumber(GameView.game.getScore());
        score.y = getHeight() / 2;
        score.width = getWidth() / 5;
        score.height = score.width;
        score.x = getWidth() / 2;
        score.setAlignment(1);

        gameOver.transform.x = getWidth() / 2;
        gameOver.transform.y = getHeight() * 5 / 6;
        gameOver.transform.width = getWidth() / 3;
        gameOver.transform.height = gameOver.transform.width;
        gameOver.setGraphic(GameView.over);

        background.set();
    }

    /**
     * Step event happens every frame.
     *
     * @param dt
     */
    @Override
    public void step(double dt) {
        if (getTouch().held(getTouch().getPointerTouchingObject(play))) {  // Is the play button being touched?
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
