package com.bobbyloujo.bobengineexample;

import com.bobbyloujo.bobengine.components.Transform;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.entities.SimpleGameObject;
import com.bobbyloujo.bobengine.extra.Util;
import com.bobbyloujo.bobengine.systems.Updatable;

/**
 * Created by Benjamin on 11/25/2015.
 */
public class BobEngineMothership extends Entity implements Updatable {
	private static final double ACCELERATION = 0.007;

	public Transform transform;
	private double velocity;
	private double angle;
	private double dScale = 0.01;

	public BobEngineMothership() {
		super();

		transform = new Transform();
		velocity = 0;
	}

	public BobEngineMothership(Entity parent) {
		super(parent);

		transform = new Transform();
		velocity = 0;
	}

	@Override
	public void onParentAssigned() {
		addComponent(new Middle());
		addComponent(new Front());
		addComponent(new Back());
		addComponent(new LeftSmall());
		addComponent(new LeftBig());
		addComponent(new RightSmall());
		addComponent(new RightBig());
	}

	@Override
	public void update(double deltaTime) {
		if (getRoom().getTouch().held()) {
			angle = Util.getAngle(transform.x, transform.y, getRoom().getTouch().getX(), getRoom().getTouch().getY());
			velocity += ACCELERATION;
		} else if (velocity > 0) {
			velocity -= ACCELERATION * 2;
		} else {
			velocity = 0;
		}

		transform.x += velocity * Math.cos(angle);
		transform.y += velocity * Math.sin(angle);
		transform.angle = Math.toDegrees(angle) - 90;

		transform.scale += dScale;
		if (transform.scale > 4 || transform.scale < .5) {
			dScale = -dScale;
		}
	}

	private class Middle extends SimpleGameObject {
		@Override
		public void onParentAssigned() {
			setGraphic(getView().getGraphicsHelper().addGraphic(R.drawable.ic_launcher));

			transform.parent = BobEngineMothership.this.transform;
			transform.width = 2;
			transform.height = 2;
		}
	}

	private class Front extends SimpleGameObject {
		@Override
		public void onParentAssigned() {
			setGraphic(getView().getGraphicsHelper().addGraphic(R.drawable.ic_launcher));

			transform.parent = BobEngineMothership.this.transform;
			transform.width = 2;
			transform.height = 2;
			transform.y = transform.height;
		}
	}

	private class Back extends SimpleGameObject {
		@Override
		public void onParentAssigned() {
			setGraphic(getView().getGraphicsHelper().addGraphic(R.drawable.ic_launcher));

			transform.parent = BobEngineMothership.this.transform;
			transform.width = 2;
			transform.height = 2;
			transform.y = -transform.height;
		}
	}

	private class LeftBig extends SimpleGameObject implements Updatable {
		@Override
		public void onParentAssigned() {
			setGraphic(getView().getGraphicsHelper().addGraphic(R.drawable.ic_launcher));

			transform.parent = BobEngineMothership.this.transform;
			transform.width = 2;
			transform.height = 2;
			transform.x = -transform.width / 2;
			transform.y = -transform.height / 2;
		}

		@Override
		public void update(double deltaTime) {
			transform.angle++;
		}
	}

	private class LeftSmall extends SimpleGameObject {
		@Override
		public void onParentAssigned() {
			setGraphic(getView().getGraphicsHelper().addGraphic(R.drawable.ic_launcher));

			transform.parent = BobEngineMothership.this.transform;
			transform.width = 2;
			transform.height = 2;
			transform.x = -transform.width / 2;
			transform.y = transform.height / 2;
			transform.scale = 0.5;
		}
	}

	private class RightBig extends SimpleGameObject implements Updatable {
		@Override
		public void onParentAssigned() {
			setGraphic(getView().getGraphicsHelper().addGraphic(R.drawable.ic_launcher));

			transform.parent = BobEngineMothership.this.transform;
			transform.width = 2;
			transform.height = 2;
			transform.x = +transform.width / 2;
			transform.y = -transform.height / 2;
		}

		@Override
		public void update(double deltaTime) {
			transform.angle--;
		}
	}

	private class RightSmall extends SimpleGameObject {
		@Override
		public void onParentAssigned() {
			setGraphic(getView().getGraphicsHelper().addGraphic(R.drawable.ic_launcher));

			transform.parent = BobEngineMothership.this.transform;
			transform.width = 2;
			transform.height = 2;
			transform.x = transform.width / 2;
			transform.y = transform.height / 2;
			transform.scale = 0.5;
		}
	}
}
