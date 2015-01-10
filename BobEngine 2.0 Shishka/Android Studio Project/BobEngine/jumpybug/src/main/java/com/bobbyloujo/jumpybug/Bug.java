package com.bobbyloujo.jumpybug;

import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Room;

/**
 * Created by Ben on 1/6/2015.
 */
public class Bug extends GameObject {

    // Constants
    private final double ACC = .8;   // The acceleration of gravity. Determines how fast the bug falls.
    private final int JUMP_V = 20;   // Velocity to give the bug to make it jump.

    // Variables
    private double vy;               // The bug's velocity on the y axis.

    /**
     * Initialization. Requires a unique Id number and the room containing this
     * GameObject.
     *
     * @param id             - ID number
     * @param containingRoom - Room that this object is in.
     */
    public Bug(int id, Room containingRoom) {
        super(id, containingRoom);

        /**
         * Assign the bug graphic we created in GameView to this object.
         * This graphic has only 1 frame. Graphics with many frames must
         * have those frames arranged vertically and all frames must be the
         * same size.
         */
        setGraphic(GameView.bug, 1);

        /**
         * Collision detection.
         *
         * You can give any object up to 10 collision boxes. Boxes are defined with 2 points.
         * (0,0) is the top left corner of the object, and (1,1) is the bottom right.
         *
         * Detection is done with a function call in the room containing the two objects
         * that are being compared. In the step event in GameRoom you'll find a loop
         * that checks the collision between the bug and each flower.
         */
        giveCollisionBox(.25,.25,.75,.75);
    }

    /**
     * Set up and reset the bug.
     */
    public void set() {
        x = getRoom().getWidth() / 2;
        y = getRoom().getHeight() * 3 / 4;
        width = getRoom().getWidth() / 8;
        height = width;
        vy = 0;
    }

    /**
     * Step event happens each frame.
     */
    @Override
    public void step(double dt) {
        vy -= ACC;                 // Acceleration of gravity
        y += vy;                   // y velocity

        angle = vy / 2;            // Change the bug's angle based on the bug's y velocity

        if (y < height / 2) {      // Hit the ground, game over!
            GameView.gameOver.set();
            getView().goToRoom(GameView.gameOver);
        }
    }

    /**
     * The screen has been touched.
     *
     * @param index
     */
    @Override
    public void newpress(int index) {
        // Make the bug jump
        if (y + JUMP_V < getRoom().getHeight()) vy = JUMP_V;
    }
}
