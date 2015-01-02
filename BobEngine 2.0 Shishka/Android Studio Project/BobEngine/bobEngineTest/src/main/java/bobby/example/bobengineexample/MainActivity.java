package bobby.example.bobengineexample;

import android.os.Bundle;
import bobby.engine.bobengine.BobActivity;

/**
 * This example project demonstrates how to set up the most basic BobEngine project:
 * 1 view with 1 room that has 1 object
 */

public class MainActivity extends BobActivity {
	// View objects
	public GameView view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		useImmersiveMode();
		
		super.onCreate(savedInstanceState);
		
		view = new GameView(this);
		
		setContentView(view);
	}
}
