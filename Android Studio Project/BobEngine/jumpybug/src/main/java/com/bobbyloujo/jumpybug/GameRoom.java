package com.bobbyloujo.jumpybug;

import java.util.Random;

import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.NumberDisplay;
import bobby.engine.bobengine.Room;

/**
 * Created by Ben on 1/6/2015.
 */
public class GameRoom extends Room {

    // Constants
    private final int SPACE = 100;      // The space between each flower
    private final int GAP = 300;        // The gap between top and bottom flowers
    private final int NUM_FLOW = 3;     // Number of flowers

    // Variables
    private int distance;               // Distance travelled by the bug
    private int currentFlower;          // Flower that is currently being sent across the screen
    private int score;                  // Player score. Number of flowers passed.

    // Objects
    private Bug bug;                    // The bug
    private Flower[] topFlowers;        // The upside down top flowers
    private Flower[] bottomFlowers;     // The right side up bottom flowers
    private Background bg1;             // Background panel 1
    private Background bg2;             // Background panel 2
    private NumberDisplay scoreDis;     // Score display

    // Other
    private Random rand;                // Random number generator.

    public GameRoom(BobView container) {
        super(container);

        rand = new Random();

        // Initialize game objects
        bug = new Bug(this);
        bg1 = new Background(this);
        bg2 = new Background(this);
        scoreDis = new NumberDisplay(this);        // NumberDisplay is a built in GameObject for displaying numbers

        topFlowers = new Flower[NUM_FLOW];
        bottomFlowers = new Flower[NUM_FLOW];
        for (int i = 0; i < NUM_FLOW; i++) {
            topFlowers[i] = new Flower(this);
            bottomFlowers[i] = new Flower(this);
        }
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
        bg1.set(getWidth() / 2, 4);
        bg2.set(getWidth() * 3 / 2, 4);

        scoreDis.x = getWidth() / 2;
        scoreDis.y = getHeight() * 3 / 4;
        scoreDis.width = getWidth() / 5;
        scoreDis.height = scoreDis.width;
        scoreDis.setNumber(score);
        scoreDis.setAlignment(1); // 0 for left alignment, 1 for center, 2 for right

        for (int i = 0; i < NUM_FLOW; i++) {
            topFlowers[i].set(true);
            bottomFlowers[i].set(false);
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
        distance++;

        scoreDis.setNumber(score);                                                   // Update score display

        /**
         * Send a new flower when we've moved the correct distance.
         *
         * getRatioX() can be used to adjust values for screens with different x dimensions. There is
         * also a getRatioY() method for the y dimension.
         */
        if (distance % SPACE * getRatioX() == 0) {

            // Move the next flowers off the right side of the screen
            topFlowers[currentFlower].x = getWidth() + topFlowers[currentFlower].width;
            bottomFlowers[currentFlower].x = getWidth() + bottomFlowers[currentFlower].width;

            // Random y positions
            int offset = rand.nextInt(getHeight() / 4);
            topFlowers[currentFlower].y = getHeight() + offset - GAP * getRatioY();
            bottomFlowers[currentFlower].y = offset;

            // Increment to the next flower.
            currentFlower++;
            if (currentFlower == NUM_FLOW) currentFlower = 0;
        }

        /**
         * Collision detection.
         *
         * After assigning collision boxes to two objects, you can check if they have collided
         * using checkCollision(object1, object2). Here we compare the bug to all the flowers.
         */
        for(int i = 0; i < NUM_FLOW; i++) {
            if (checkCollision(bug, topFlowers[i]) || checkCollision(bug, bottomFlowers[i])) {
                GameView.gameOver.set();
                getView().goToRoom(GameView.gameOver);
            }
        }
    }
}
