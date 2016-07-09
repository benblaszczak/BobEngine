package com.bobbyloujo.bobengineexample;

import com.bobbyloujo.bobengine.extra.ScrollingImage;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.view.BobView;
import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.extra.NumberDisplay;
import com.bobbyloujo.bobengine.extra.TextDisplay;

import javax.microedition.khronos.opengles.GL11;

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

		final ScrollingImage s = new ScrollingImage(this);
		s.transform.layer = 0;
		s.graphic.transform.width = 2;
		s.graphic.transform.height = 2;
		s.graphic.transform.x = 0;

		s.addComponent(new Updatable() {
			@Override
			public void update(double deltaTime) {
				s.graphic.transform.x += 0.01;
			}
		});


		getView().getGraphicsHelper().setParameters(true, GL11.GL_LINEAR_MIPMAP_LINEAR, GL11.GL_LINEAR, true);
		s.setGraphic(getView().getGraphicsHelper().getGraphic(R.drawable.ic_launcher));

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
