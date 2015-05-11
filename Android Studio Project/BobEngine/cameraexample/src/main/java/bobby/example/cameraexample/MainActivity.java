package bobby.example.cameraexample;

import android.os.Bundle;

import bobby.engine.bobengine.BobActivity;
import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.GameObject;
import bobby.engine.bobengine.Graphic;
import bobby.engine.bobengine.Room;

public class MainActivity extends BobActivity {

    BobView view;        // View for displaying BobEngine content
    Room room;           // Room for containing GameObjects
    GameObject object;   // An object to display
    Graphic g;           // The graphic to give to our object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new BobView(this) {
            @Override
            protected void onCreateGraphics() {
                g = getGraphicsHelper().addGraphic(R.drawable.ic_launcher);
            }

            @Override
            protected void onCreateRooms() {

                /**
                 * Room and object initialization
                 */
                room = new Room(view) {
					@Override
					public void step(double dt) {
						object.x += 1 * object.getRatioX();
					}
				};

                object = new GameObject(room.nextInstance(), room);
                object.x = room.getWidth() / 2;                     // Center of the screen when camera (x,y) is (0,0)
                object.y = room.getHeight() / 2;                    // Center of the screen
                object.width = room.getWidth() / 10;
                object.height = object.width;
                object.setGraphic(g);

                room.addObject(object);

                goToRoom(room); // This is how we tell BobEngine which room to update and draw!!

                /**
                 * Camera function tests.
                 */

                // Position
                /**
                 * (0,0) is the default camera position. Moving the camera will translate the objects
                 * in the opposite direction. For example: room.setCameraX(100) would translate
                 * all objects in the room to the left by 100 pixels. It will appear as if the camera
                 * moved to the right.
                 */
                room.setCameraX(0);
                room.setCameraY(0);

                // Zoom
                /**
                 * 1 is the default zoom. Setting the zoom to a larger number will move the camera
                 * out further away from the objects. Setting the zoom to a fraction < 1 will move the
                 * camera in closer to the objects.
                 */
                room.setCameraZoom(1);

                // Anchor
                /**
                 * (0,0) is the default anchor point. The anchor is the point that will remain in place
                 * when zooming in and out. (0,0) is the bottom left corner. If the subject of your
                 * screen (player, etc...) is in the center of the screen, you may want to set the
                 * anchor to the center of the screen: (room.getWidth() / 2, room.getHeight() / 2).
                 * Then, the player will remain in the center when zooming in and out.
                 */
                room.setCameraAnchor(getWidth() / 2, getHeight() / 2);
            }
        };

        setContentView(view);
    }
}
