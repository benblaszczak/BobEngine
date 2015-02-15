package bobby.engine.template;

import android.content.Intent;

import bobby.engine.bobengine.SplashActivity;

/**
 * Created by Ben on 2/14/2015.
 */
public class SplashScreens extends SplashActivity {
    @Override
    protected void setup() {
        /**
         * You can add up to 10 layouts. Add them in the order
         * you want them to show.
         */

        addSplash(R.layout.splash, 3000); // Show splash.xml for 3000 milliseconds
    }

    @Override
    protected void end() {
        /* Start the main activity */
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);

        finish(); // Close this activity
    }
}
