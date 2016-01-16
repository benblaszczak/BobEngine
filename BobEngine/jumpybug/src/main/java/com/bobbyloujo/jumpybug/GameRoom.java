package com.bobbyloujo.jumpybug;

import com.bobbyloujo.bobengine.systems.collision.CollisionSystem;
import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.extra.NumberDisplay;
import com.bobbyloujo.bobengine.entities.Room;

/**
 * Created by Ben on 1/6/2015.
 */
public class GameRoom extends Room {

    // Constants
    private final int SPACE = 90;       // The space between each flower in grid units
    private final int NUM_FLOW = 3;     // Number of flowers

    // Variables
    private int distance;               // Distance travelled by the bug
    private int currentFlower;          // Flower that is currently being sent across the screen
    private int score;                  // Player score. Number of flowers passed.

    // Objects
    private Bug bug;                    // The bug
    private FlowerPair[] flowers;       // The flowers
	private Background background;      // The background panels
    private NumberDisplay scoreDis;     // Score display

    // Other
    private CollisionSystem bugFlowerColSys; // Bug and flower collision system.

    public GameRoom(BobView container) {
        super(container);

		getView().setBackgroundColor(0, 1, 1, 1);

		setGridHeight(160);
        setGridUnitX(getGridUnitY());

        bugFlowerColSys = new CollisionSystem();
        addComponent(bugFlowerColSys);

        // Initialize game objects
        bug = new Bug(this);
		scoreDis = new NumberDisplay(this);        // NumberDisplay is a built in GameObject for displaying numbers
        background = new Background(this);

        flowers = new FlowerPair[NUM_FLOW];
        for (int i = 0; i < NUM_FLOW; i++) {
            flowers[i] = new FlowerPair(this, bugFlowerColSys);
        }

        bugFlowerColSys.addCollidable(bug);
    }

    /**
     * Set/reset the room.
     */
    public void set() {
        // Variables
        distance = 0;
        currentFlower = 0;
        score = 0;

        // Objects
        bug.set();

        scoreDis.x = getWidth() / 2;
        scoreDis.y = getHeight() * 3 / 4;
        scoreDis.width = getWidth() / 5;
        scoreDis.height = scoreDis.width;
        scoreDis.layer = 3;
        scoreDis.setNumber(score);
        scoreDis.setAlignment(1); // 0 for left alignment, 1 for center, 2 for right

        background.set();

        for (FlowerPair f: flowers) {
            if (f != null) f.sendToLeftEdge();
        }
    }

    public void incrementScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    /**
     * Step event happens every frame.
     *
     * @param dt - delta time. Will be 1 if the game is running at 60fps. Will be lower if the game is running fast, higher if
     *           the game is running slow.
     */
    @Override
    public void step(double dt) {
        distance += FlowerPair.SPEED;

        scoreDis.setNumber(score);                                                   // Update score display

        /**
         * Send a new flower when we've moved the correct distance.
         *
         * getRatioX() can be used to adjust values for screens with different x dimensions. There is
         * also a getRatioY() method for the y dimension.
         */
        if (distance >= SPACE) {

            // Move the next flowers off the right side of the screen
            flowers[currentFlower].sendToRightEdge();

            // Random y positions
            flowers[currentFlower].randomizeY();

            // Increment to the next flower.
            currentFlower++;
            if (currentFlower == NUM_FLOW) currentFlower = 0;

            distance = 0;
        }
    }
}
