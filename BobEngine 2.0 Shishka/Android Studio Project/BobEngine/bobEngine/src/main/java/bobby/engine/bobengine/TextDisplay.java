/**
 * BobEngine - 2D game engine for Android
 *
 * Copyright (C) 2014, 2015 Benjamin Blaszczak
 *
 * BobEngine is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser Public License
 * version 2.1 as published by the free software foundation.
 *
 * BobEngine is provided without warranty; without even the implied
 * warranty of merchantability or fitness for a particular
 * purpose. See the GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with BobEngine; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301 USA
 *
 */

package bobby.engine.bobengine;

/**
 * Can be used to display text on the screen. <br/><br/>
 *
 * <b>NOTE:</b> you use the method setGraphic(Graphic g, int columns, int rows) to set the graphic. <br/><br/>
 *
 * Created by Benjamin on 4/14/2015.
 */
public class TextDisplay extends GameObject {

	// Alignments
	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;

	// Defaults
	private final static String DEF_ORDER = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?.,\"()$%':;/+=";
	private final static double DEF_KERN[] =
			{.375, .4375, .375, .406, .406, .313,
					.4375, .406, .156, .188, .406, .156,
					.656, .406, .4375, .4375, .406, .281,
					.313, .313, .406, .4375, .656, .406,
					.406, .344, .375, .563, .4375, .4375,
					.469, .375, .375, .469, .4375, .125,
					.313, .469, .375, .656, .469, .563,
					.406, .563, .4375, .406, .4375, .469,
					.5, .781, .5, .469, .406, .406,
					.219, .344, .344, .406, .375, .406,
					.344, .406, .406, .156, .313, .156,
					.188, .281, .25, .281, .313, .4375,
					.125, .156, .188, .25, .375, .375};

	// Data
	private String text;
	private Character characters[];
	private String order;
	private double kerning[];

	// Variables
	private int columns;
	private int rows;
	private double boxWidth;
	private int alignment;
	private int lines;
	private double realWidth;
	private boolean hasChanged;

	/**
	 * Initialization. Requires a unique Id number and the room containing this
	 * GameObject.
	 *
	 * @param id             - ID number
	 * @param containingRoom - Room that this object is in.
	 */
	public TextDisplay(int id, Room containingRoom) {
		super(id, containingRoom);

		Graphic g = getView().getGraphicsHelper().addGraphic(R.drawable.characters);
		setGraphic(g, 13, 6);

		y = getRoom().getHeight();
		x = 0;
		setBoxWidth(getRoom().getWidth());
		alignment = LEFT;
		characters = new Character[1];

		kerning = DEF_KERN;
		order = DEF_ORDER;
		hasChanged = false;
	}

	/**
	 * Set the string of text to output.
	 *
	 * @param text
	 */
	public void setText(final String text) {
		this.text = text;  // Just so we can retrieve this text later if we need
		hasChanged = true;
	}

	/**
	 * Set the width of the box in pixels. Whole words beyond this width will
	 * wrap to the next line.
	 * @param boxWidth
	 */
	public void setBoxWidth(double boxWidth) {
		this.boxWidth = boxWidth;
	}

	/**
	 * Set the alignment using TextDisplay.LEFT, .CENTER, and .RIGHT.
	 * @param alignment
	 */
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	/**
	 * Set the widths of the characters to allow proper kerning. <br/><br/>
	 *
	 * Characters should be aligned to the left of the frame on graphic sheet. Kerning
	 * values are the width of the character in pixels divided by the width of a single
	 * frame. (For a character that takes up the whole frame, this would be 1.)
	 * <br/><br/>
	 * Kerning values should be in the same order as set by setOrder().
	 *
	 * @param kerning
	 */
	public void setKerning(double kerning[]) {
		this.kerning = kerning;
	}

	/**
	 * Set the order of the characters on the graphic sheet. Characters should
	 * be arranged in columns.
	 * @param order The first character in this string should be the top left character
	 *              on the graphic sheet. The second should be the one underneath the first, etc.
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * Returns the string of text that is being output.
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the number of lines used to display this text.
	 * @return
	 */
	public int getNumLines() {
		return lines;
	}

	public double getRealWidth() {
		return realWidth;
	}

	/**
	 * Show or a hide a line of text. Does not change the position of any
	 * text. Only affects visibility.
	 *
	 * @param line Line # to hide starting at 0
	 * @param hide True to hide, false to show
	 */
	public void hideLine(int line, boolean hide) {
		for (int i = 0; i < characters.length; i++) {
			if (characters[i] != null && characters[i].line == line) {
				if (hide) characters[i].visible = false;
				else characters[i].visible = true;
			}
		}
	}

	/**
	 * Gets the width of the specified character.
	 * @param c
	 * @return
	 */
	public double getKerning(char c) {
		if (getFrameFromChar(c) < kerning.length) {
			return kerning[getFrameFromChar(c)];
		}

		return 1;
	}

	/**
	 * Returns the frame on the graphic sheet of the specified character.
	 * @param c
	 * @return
	 */
	public int getFrameFromChar(char c) {
		return order.indexOf(c);
	}

	/**
	 * Updates the positions of each character if the whole TextDisplay has moved.
	 */
	public void updatePosition() {
		for (int i = 0; i < characters.length; i++) {
			if (characters[i] != null) {
				characters[i].x = x + characters[i].xOff;
				characters[i].y = y + characters[i].yOff;
			}
		}
	}

	/**
	 * If the text has changed, this will update the characters (quads).
	 */
	public void updateText() {
		if (hasChanged) calculateText();
	}

	/**
	 * Calculates the positions and frames of each character.
	 */
	private void calculateText() {
		// Variables
		double cursor = x;       // Keeps track of where to place the next character
		int line = 0;            // Keeps track of the line we are placing characters on
		int firstChar = 0;       // The first character in characters[] on line number line
		int wordStart = 0;       // The first character in the current word
		double wordWidth = 0;    // The actual width of the current word in pixels
		double lineWidth = 0;    // The width of the entire line.
		final String text = this.text; // Remember what text we are outputting.

		realWidth = 0;

		lines = 1;  // We have a least one line, though it might be blank if text == ""

		if (characters.length < text.length()) characters = new Character[text.length()];  // Resize the character array if need be

		for (int i = 0; i < text.length(); i++) { // Output each character in String text

			if (text.charAt(i) == '\n') {  // We need to go to the next line.
				firstChar = i + 1;         // The next character is the first of the next line
				line++;                    // Go to the next line
				lines++;                   // We've added another line
				cursor = x;                // The cursor needs to go to the starting point of the line
				if (lineWidth > realWidth) realWidth = lineWidth;
				lineWidth = 0;
			} else if (getFrameFromChar(text.charAt(i)) != -1) {               // Character at i is a valid character
				if (characters[i] == null) characters[i] = new Character();    // Create a new character object if needed
				characters[i].visible = true;                                  // This character should be visible
				characters[i].width = width;                                   // Set character's width
				characters[i].height = height;                                 // and height
				characters[i].setGraphic(columns, rows);                       // and graphic info
				characters[i].frame = getFrameFromChar(text.charAt(i));        // and the correct frame

				/* Check if we've started a new word */
				if (text.charAt(i) == ' ' || text.charAt(i) == '-' || text.charAt(i) == '.' || text.charAt(i) == '!' || text.charAt(i) == '?') {
					wordStart = i;
					wordWidth = 0;
				}

				characters[i].y = y - line * height - height / 2;       // Set the y according to the line character i is on
				characters[i].x = cursor + width / 2;                   // Place the character at the cursor
				wordWidth += width * getKerning(text.charAt(i));        // Increase the width of the current word according to the width of the character
				lineWidth += width * getKerning(text.charAt(i));

				if (alignment == LEFT) {                                    // Align the characters such that each line begins at x
					cursor += width * getKerning(text.charAt(i));           // Move the cursor forward according to the width of the character

					if (cursor - x > boxWidth && wordWidth < boxWidth) { // Go to next line
						i = wordStart;  // Go back to the beginning of the word
						firstChar = i;  // First character of the next line will be i
						line++;         // Go to next line
						lines++;        // Increase the total quantity of lines by 1
						cursor = x;     // Cursor goes back to line start
						lineWidth -= wordWidth;
						if (lineWidth > realWidth) realWidth = lineWidth;
						lineWidth = 0;
					}
				}
				else if (alignment == CENTER) {                          // Align the characters such that the center of each line is at x
					cursor += width * getKerning(text.charAt(i)) / 2;    // Move the cursor

					/* Move all previous characters to the left by half the width of character i so that the line is centered */
					for (int l = i; l >= firstChar; l--) {
						characters[l].x -= width * getKerning(text.charAt(i)) / 2;
					}

					if (cursor - x > boxWidth / 2 && wordWidth < boxWidth) {  // Word wrap, need to go to the next line
						for (int l = i; l >= firstChar; l--) { // Add half the width of the current word to the x of each character on this line
							characters[l].x += wordWidth / 2;  //    because the current word will be moving down a line.
						}

						i = wordStart;  // Go back to the beginning of the word
						firstChar = i;  // First character of the next line will be i
						line++;         // Go to next line
						lines++;        // Increase the total quantity of lines by 1
						cursor = x;     // Cursor goes back to line start
						lineWidth -= wordWidth;
						if (lineWidth > realWidth) realWidth = lineWidth;
						lineWidth = 0;
					}
				} else if (alignment == RIGHT){     // Align the characters such the end of each line ends at x

					/* Move each character on the line to the left by the width of the new character i */
					for (int l = i; l >= firstChar; l--) {
						characters[l].x -= width * getKerning(text.charAt(i));
					}

					/* Word wrap, end of line reached */
					if (x - characters[firstChar].x - width * getKerning(text.charAt(firstChar)) / 2 > boxWidth && wordWidth < boxWidth) {
						for (int l = i; l >= firstChar; l--) { // Move all characters on the line to the right by the width of the current word
							characters[l].x += wordWidth;      //    because the current word is moving down to the next line
						}

						i = wordStart;  // Go back to the beginning of the word
						firstChar = i;  // First character of the next line will be i
						line++;         // Go to next line
						lines++;        // Increase the total quantity of lines by 1
						cursor = x;     // Cursor goes back to line start
						lineWidth -= wordWidth;
						if (lineWidth > realWidth) realWidth = lineWidth;
						lineWidth = 0;
					}
				}

				characters[i].line = line;                 // and the line number character i is on.
			}
		}

		for (int i = 0; i < characters.length; i++) {
			characters[i].xOff = characters[i].x - x;  // Calculate the x offset of the character in case the whole display is moved
			characters[i].yOff = characters[i].y - y;  // and the y position relative to the y position of the display
		}

		for (int i = text.length(); i < characters.length; i++) { // Hide all extra characters
			if (characters[i] != null) characters[i].visible = false;
		}

		if (lineWidth > realWidth) realWidth = lineWidth;
	}

	/**
	 * Updates the quad array for this object.
	 */
	private void updateQuads() {
		if (getMaxQuads() < characters.length) {
			setMaxQuads(characters.length);
		}

		for (int i = 0; i < characters.length; i++) {
			if (hasChanged) setQuad(i, characters[i]);
		}
	}

	@Override
	public void step(double dt) {
		updateText();
		updatePosition();
		updateQuads();
		hasChanged = false;
	}

	@Override
	public void setGraphic(Graphic graphic, int columns, int rows) {
		super.setGraphic(graphic, columns, rows);
		this.columns = columns;
		this.rows = rows;
	}

	/**
	 * An extension of Quad that adds some useful attributes for characters
	 */
	private class Character extends Quad {

		// Attributes
		public double xOff;
		public double yOff;
		public int line;

	}
}
