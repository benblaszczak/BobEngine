package com.bobbyloujo.bobengineexample;

import java.util.Random;

import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.entities.Entity;

/**
 * Created by Benjamin on 9/25/2015.
 */
public class ManyIcons extends Entity {

	// Constants
	private final int NUM_ICONS = 500;     // Number of bouncing icons
	private final double ICON_SCALE = .5;  // Scale of the icons

	private BouncingIcon[] icons;
	private Random rand;

	public ManyIcons(Room room) {
		super(room);

		getRoom().createQuadRenderSystem(getView().getGraphicsHelper().addGraphic(R.drawable.ic_launcher), NUM_ICONS);

		rand = new Random();
		icons = new BouncingIcon[NUM_ICONS];

		for (int i = 0; i < NUM_ICONS; i++) {
			icons[i] = new BouncingIcon(this);
		}
	}

	public void set() {
		for (int i = 0; i < NUM_ICONS; i++) {
			double x = (double) rand.nextInt(getRoom().getViewWidth()) / getRoom().getGridUnitX();
			double y = (double) rand.nextInt(getRoom().getViewHeight()) / getRoom().getGridUnitY();

			icons[i].set(x, y, ICON_SCALE, 1);
		}
	}
}
