package com.bobbyloujo.textdisplayexample;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import bobby.engine.bobengine.BobView;
import bobby.engine.bobengine.Room;
import bobby.engine.bobengine.TextDisplay;


public class MainActivity extends Activity {

	final String TEXT = "Who's the best? Bobby Lou Jo is the best."; // The text to display

	BobView view;     // BobView for showing BobEngine content.
	Room room;        // A room to put our TextDisplay in.
	TextDisplay text; // A specialized GameObject that will display text.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = new BobView(this) {
			@Override
			protected void onCreateGraphics() {

			}

			@Override
			protected void onCreateRooms() {
				// Initialize everything, go to the room.
				room = new Room(view);
				text = new TextDisplay(room);

				goToRoom(room);

				/* Set the position of the TextDisplay */
				text.x = room.getWidth() / 2;
				text.y = room.getHeight();

				/* The TextDisplay's width and height will be used for the individual characters (font size!) */
				text.width = text.height = room.getWidth() / 20;

				/* Change the text string output by the TextDisplay */
				text.setText(TEXT);

				/* Change the alignment. Options are LEFT, RIGHT, CENTER */
				/**
				 * Note: Text is aligned to the x position of the TextDisplay. When the alignment is set
				 * to LEFT, lines beginning at x; when the align is set to RIGHT, lines end at x; when
				 * the alignment is set to CENTER, lines are centered around x.
				 */
				text.setAlignment(TextDisplay.CENTER);

				/* Set the width of the TextDisplay. Words past this width will be wrapped to the next line. */
				text.setBoxWidth(room.getWidth() / 2);

				/**
				 * A QUICK NOTE ABOUT CHANGING THE FONT/TYPEFACE:
				 *
				 * You can create your own typeface to use with TextDisplay. The default typeface
				 * can be found in the bobEngine module drawable folder and is named 'characters.png'.
				 *
				 * CHARACTER ARRANGEMENT:
				 *
				 * Note that the characters are arranged in columns (just like frames in any other GameObject).
				 * You can arrange them in any order you want AND use whatever characters you want (you are not
				 * restricted to the characters shown on the default typeface). You must call setOrder(String order)
				 * with a string that contains all the characters on your typeface graphic in the order that they
				 * are arranged in your graphic.
				 *
				 * ***Use the setGraphic(Graphic g, int columns, int rows) method to set your graphic!
				 *
				 * KERNING:
				 *
				 * Kerning is super tedious and annoying, unfortunately, but there is no simple way to fix this (except
				 * by using a fixed-width font, but that isn't really a fix). For proper kerning, place your characters
				 * against the left edge of their frame in your typeface graphic. Measure the width of each character
				 * in pixels and then divide that width by the width of a single whole frame on your graphic to get the
				 * kerning value for that character (a character that takes up the whole frame would have a kerning value
				 * of 1). Create an array of doubles with the kerning values of each character in the order that they
				 * appear on you graphic. Call setKerning(double kerning[]) to set the kerning for your typeface.
				 */
			}
		};

		setContentView(view);
	}
}
