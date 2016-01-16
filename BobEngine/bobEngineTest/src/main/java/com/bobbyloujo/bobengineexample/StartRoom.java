package com.bobbyloujo.bobengineexample;

import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.extra.NumberDisplay;
import com.bobbyloujo.bobengine.extra.TextDisplay;

public class StartRoom extends Room {

	// Game objects
	private ManyIcons icons;
	private TextDisplay text;
	private BobEngineMothership big;
	NumberDisplay numberDisplay;

	double count = 0;

	public StartRoom(BobView container) {
		super(container);

		setGridWidth(20);
		setGridUnitY(getGridUnitX());

		//icons = new ManyIcons(this);

		/*
		text = new TextDisplay(this);
		text.setText("BobEngine test box");
		text.setAlignment(TextDisplay.CENTER);
		text.x = getWidth() / 2;
		text.y = getHeight() * 4/5;
		text.scale = 3;
		*/
		numberDisplay = new NumberDisplay(this);
		numberDisplay.setNumber(0);
		numberDisplay.setPrecision(0);
		numberDisplay.useCommas(true);
		numberDisplay.x = getWidth() / 2;
		numberDisplay.y = 1;
		numberDisplay.width = 1;
		numberDisplay.height = 1;
		numberDisplay.setAlignment(NumberDisplay.CENTER);
		numberDisplay.scale = 2;
		numberDisplay.layer = 3;

		/*
		text = new TextDisplay(this);
		//text.setText("BobEngine test box");
		text.setAlignment(TextDisplay.CENTER);
		text.setBoxWidth(getWidth());
		text.x = getWidth() / 2;
		text.y = getHeight() * 4/5;
		text.width = 1;
		text.height = 1;
		text.scale = 2;
		*/


		big = new BobEngineMothership();
		big.transform.x = getWidth() / 4;
		big.transform.y = getHeight() / 2;

		addComponent(big);

	}

	public void set() {
		//icons.set();
	}

	@Override
	public void step(double deltaTime) {
		numberDisplay.setNumber((int) (getView().getRenderer().getFPS() + .5));
		//count+=deltaTime;
		//text.setText("Score: " + String.valueOf((int) count));
	}
}
