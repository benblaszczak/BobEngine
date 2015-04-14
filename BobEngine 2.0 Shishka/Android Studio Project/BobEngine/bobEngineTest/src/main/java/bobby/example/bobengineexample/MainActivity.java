package bobby.example.bobengineexample;

import android.os.Bundle;
import bobby.engine.bobengine.BobActivity;

/**
 * This module is my playground for testing new BobEngine features. It's full
 * of sloppy code showing off lots of random things that BobEngine can do. Look
 * through it if you like, but be warned that is messy!
 */

public class MainActivity extends BobActivity {
	// View objects
	public GameView view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        useImmersiveMode();
		
		view = new GameView(this);
		
		setContentView(view);
	}
}
