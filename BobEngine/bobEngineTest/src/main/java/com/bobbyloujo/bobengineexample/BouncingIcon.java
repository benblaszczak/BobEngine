package com.bobbyloujo.bobengineexample;

import com.bobbyloujo.bobengine.entities.GameObject;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.sound.SoundPlayer;

import java.util.Random;

public class BouncingIcon extends GameObject {
	// Constants
	private final int FRAMES = 1;

	// variables
	private int dX;
	private int dY;
	private double speed = .2;

	private SoundPlayer sounds;
	private int POP;

	public BouncingIcon(Entity parent) {
		super(parent);

		//sounds = new SoundPlayer(getView().getContext());
		//POP = sounds.newSound(R.raw.pop);

		x = getRoom().getWidth() / 2;
		y = getRoom().getHeight() / 2;
		height = 1;
		width = 1;

		setGraphic(GameView.icon);
	}

	// Set/Reset
	public void set(double x, double y, double scale, int layer) {
		this.x = x;
		this.y = y;
		this.layer = layer;
		this.scale = scale;

		height = getRoom().getHeight() / 10;
		width = height;

        //getRoom().setCameraX(0);
        //getRoom().setCameraY(0);
        //getRoom().setCameraZoom(2);
        //getRoom().setCameraAnchor(getRoom().getWidth() / 2, getRoom().getHeight() / 2);

		Random rand = new Random();

		dX = rand.nextInt() % 2 == 0 ? 1 : -1;
		dY = rand.nextInt() % 2 == 0 ? 1 : -1;

		frame = 0;
	}

	// Step
	@Override
	public void step(double deltaTime) {
		x += dX * speed * deltaTime;
		y += dY * speed * deltaTime;

		if (x > getRoom().getWidth() - width / 2 * scale) {
			dX = -dX;
			x = getRoom().getWidth() - width / 2 * scale;
		}

		if (x < width / 2 * scale) {
			dX = -dX;
			x = width / 2 * scale;
		}

		if (y > getRoom().getHeight() - height / 2 * scale) {
			dY = -dY;
			y = getRoom().getHeight() - height / 2 * scale;
		}

		if (y < height / 2 * scale) {
			dY = -dY;
			y = height / 2 * scale;
		}
	}

	@Override
	public void newpress(int index) {

		if (getTouch().objectTouched(this)) {
			//sounds.play(POP);
		}
	}
}
