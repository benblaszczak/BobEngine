/**
 * BobEngine - 2D game engine for Android
 *
 * Copyright (C) 2014, 2015, 2016 Benjamin Blaszczak
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
package com.bobbyloujo.bobengine.extra;

import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.systems.Updatable;
import com.bobbyloujo.bobengine.R;
import com.bobbyloujo.bobengine.entities.Room;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.entities.SimpleGameObject;
import com.bobbyloujo.bobengine.graphics.Graphic;

/**
 * Can be used to display text on the screen. <br/><br/>
 *
 * <b>NOTE:</b> you use the method setGraphic(Graphic g, int columns, int rows) to set the graphic. <br/><br/>
 *
 * Created by Benjamin on 4/14/2015.
 */
public class TextDisplay extends Entity implements Transformation, Updatable {

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
					.50, .781, .5, .469, .406, .406,
					.320, .344, .344, .406, .375, .406,
					.344, .406, .406, .156, .313, .156,
					.188, .281, .25, .281, .333, .5475,
					.125, .156, .188, .25, .375, .375};

	// Data
	public Transformation parent;
	public double x;
	public double y;
	public double width;
	public double height;
	public double angle;
	public double scale;
	public int layer;
	public boolean visible;
	public boolean followCamera;

	private Graphic graphic;
	private String text;
	private Character characters[];
	private String order;
	private double kerning[];

	// Variables
	private int columns;          // Number of columns of frames in the the graphic
	private int rows;             // Number of rows of frames in the graphic
	private int xOnGfx;           // X position in pixels on the graphic sheet.
	private int yOnGfx;           // Y position in pixels on the graphic sheet.
	private int widthOnGfx;       // Width in pixels on the graphic sheet.
	private int heightOnGfx;      // Height in pixels on the graphic sheet.

	private double boxWidth;
	private double realWidth;

	private int alignment;
	private int lines;
	private boolean hasChanged;

	/**
	 * Create an entity that can display text.
	 */
	public TextDisplay() {
		init();
	}

	/**
	 * Create an Entity that can display text and give it a parent.
	 * @param parent The parent that this TextDisplay belongs to.
	 */
	public TextDisplay(Entity parent) {
		super(parent);
		init();
	}

	private void init() {
		y = getRoom().getViewHeight();
		x = 0;
		width = 50;
		height = 50;
		scale = 1;
		layer = 2;
		visible = true;
		setBoxWidth(getRoom().getViewWidth());
		alignment = LEFT;
		characters = new Character[1];

		kerning = DEF_KERN;
		order = DEF_ORDER;
		hasChanged = false;

		text = "";
	}

	@Override
	public void onParentAssigned() {
		if (graphic == null) {
			Graphic g = getView().getGraphicsHelper().addGraphic(R.drawable.characters);
			setGraphic(g, 6, 13);
		}
	}

	/**
	 * Set the string of text to output.
	 *
	 * @param text
	 */
	public void setText(final String text) {
		if (!this.text.equals(text)) {
			this.text = text;
			hasChanged = true;
		}
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
	 * Set the widths of the characters to allow proper kerning. <br/><br/>
	 *
	 * Characters should be aligned to the left of the frame on graphic sheet. Kerning
	 * values are the width of the character in pixels divided by the width of a single
	 * frame. (For a character that takes up the whole frame, this would be 1.)
	 * <br/><br/>
	 * Kerning values should be in the same order as set by setOrder().
	 *
	 * This override will set all kerning values to 1 for fixed width typefaces.
	 */
	public void setKerning() {
		for (int i = 0; i < kerning.length; i++) {
			kerning[i] = 1;
		}
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
	 * This override will set all kerning values to all.
	 */
	public void setKerning(double all) {
		for (int i = 0; i < kerning.length; i++) {
			kerning[i] = all;
		}
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
	 * This override will set the value of char c to k.
	 */
	public void setKerning(char c, double k) {
		if (order.indexOf(c) != -1) {
			kerning[order.indexOf(c)] = k;
		}
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
				if (hide) characters[i].transform.visible = false;
				else characters[i].transform.visible = true;
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

	public void setFont(Font f) {
		this.graphic = getView().getGraphicsHelper().addGraphic(f.drawable);
		setKerning(f.getKerning());
		setOrder(f.getOrder());

		if (f.width == 0) {
			setGraphic(this.graphic, f.rows, f.columns);
		} else {
			setGraphic(this.graphic, f.rows, f.columns, f.x, f.y, f.width, f.height);
		}
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
	 * If the text has changed, this will update the characters (quads).
	 */
	public void updateText() {
		if (hasChanged) {
			calculateText();
			hasChanged = false;
		}
	}

	/**
	 * Calculates the positions and frames of each character.
	 */
	private void calculateText() {
		// Variables
		double cursor = 0;             // Keeps track of where to place the next character
		int line = 0;                  // Keeps track of the line we are placing characters on
		int firstChar = 0;             // The first character in characters[] on line number line
		int wordStart = 0;             // The first character in the current word
		double wordWidth = 0;          // The actual width of the current word in pixels
		double lineWidth = 0;          // The width of the entire line.

		realWidth = 0;

		lines = 1;  // We have a least one line, though it might be blank if text == ""

		if (characters.length < text.length()) {
			for (Character c: characters) {
				if (c != null) c.removeFromRenderer();
			}

			characters = new Character[text.length()];  // Resize the character array if need be
		}

		for (int i = 0; i < text.length(); i++) { // Output each character in String text

			if (text.charAt(i) == '\n') {  // We need to go to the next line.
				firstChar = i + 1;         // The next character is the first of the next line
				line++;                    // Go to the next line
				lines++;                   // We've added another line
				cursor = 0;                // The cursor needs to go to the starting point of the line
				if (lineWidth > realWidth) realWidth = lineWidth;
				lineWidth = 0;
			} else if (getFrameFromChar(text.charAt(i)) != -1) {                      // Character at i is a valid character
				if (characters[i] == null) characters[i] = new Character(getRoom());  // Create a new character object if needed
				characters[i].transform.visible = true;                               // This character should be visible
				characters[i].transform.parent = this;
				characters[i].transform.layer = layer;
				characters[i].transform.height = height;
				characters[i].transform.width = width;
				characters[i].transform.followCamera = followCamera;
				characters[i].setGraphic(graphic);                                    // and graphic info
				characters[i].graphic.makeGrid(rows, columns, xOnGfx, yOnGfx, widthOnGfx, heightOnGfx, graphic.width, graphic.height);
				characters[i].graphic.frame = getFrameFromChar(text.charAt(i));       // and the correct frame

				/* Check if we've started a new word */
				if (text.charAt(i) == ' ' || text.charAt(i) == '-' ) {
					wordStart = i;
					wordWidth = 0;
				}

				if (text.charAt(i) == '.' || text.charAt(i) == '!' || text.charAt(i) == '?') {
					if ((i + 1 < text.length()) && (text.charAt(i + 1) == ' ' || text.charAt(i + 1) == '-')) {
						wordStart = i + 1;
						wordWidth = 0;
					} else {
						wordStart = i;
						wordWidth = 0;
					}
				}

				characters[i].transform.y = -line * height - height / 2;    // Set the y according to the line character i is on
				characters[i].transform.x = cursor + width / 2;             // Place the character at the cursor
				wordWidth += width * getKerning(text.charAt(i));            // Increase the width of the current word according to the width of the character
				lineWidth += width * getKerning(text.charAt(i));

				if (alignment == LEFT) {                                    // Align the characters such that each line begins at x
					cursor += width * getKerning(text.charAt(i));           // Move the cursor forward according to the width of the character

					if (cursor > boxWidth && wordWidth < boxWidth) { // Go to next line
						i = wordStart;  // Go back to the beginning of the word
						firstChar = i;  // First character of the next line will be i
						line++;         // Go to next line
						lines++;        // Increase the total quantity of lines by 1
						cursor = 0;     // Cursor goes back to line start
						lineWidth -= wordWidth;
						if (lineWidth > realWidth) realWidth = lineWidth;
						lineWidth = 0;
					}
				}
				else if (alignment == CENTER) {                          // Align the characters such that the center of each line is at x
					cursor += width * getKerning(text.charAt(i)) / 2;    // Move the cursor

					/* Move all previous characters to the left by half the width of character i so that the line is centered */
					for (int l = i; l >= firstChar; l--) {
						if (characters[l] != null) {
							characters[l].transform.x -= width * getKerning(text.charAt(i)) / 2;
						}
					}

					if (cursor > boxWidth / 2 && wordWidth < boxWidth) {                        // Word wrap, need to go to the next line
						for (int l = i; l >= firstChar; l--) {                                      // Add half the width of the current word to the x of each character on this line
							if (characters[l] != null) characters[l].transform.x += wordWidth / 2;  //    because the current word will be moving down a line.
						}

						i = wordStart;  // Go back to the beginning of the word
						firstChar = i;  // First character of the next line will be i
						line++;         // Go to next line
						lines++;        // Increase the total quantity of lines by 1
						cursor = 0;     // Cursor goes back to line start
						lineWidth -= wordWidth;
						if (lineWidth > realWidth) realWidth = lineWidth;
						lineWidth = 0;
					}
				} else if (alignment == RIGHT){     // Align the characters such the end of each line ends at x

					/* Move each character on the line to the left by the width of the new character i */
					for (int l = i; l >= firstChar; l--) {
						if (characters[l] != null) characters[l].transform.x -= width * getKerning(text.charAt(i));
					}

					// "Someday I'll be as popular as AndEngine!" cried the Little Engine that Could.

					/* Word wrap, end of line reached */
					if (-(characters[firstChar].transform.x - width * getKerning(text.charAt(firstChar))) > boxWidth && wordWidth < boxWidth) {
						for (int l = i; l >= firstChar; l--) {                                  // Move all characters on the line to the right by the width of the current word
							if (characters[l] != null) characters[l].transform.x += wordWidth;  //    because the current word is moving down to the next line
						}

						i = wordStart;  // Go back to the beginning of the word
						firstChar = i;  // First character of the next line will be i
						line++;         // Go to next line
						lines++;        // Increase the total quantity of lines by 1
						cursor = 0;     // Cursor goes back to line start
						lineWidth -= wordWidth;
						if (lineWidth > realWidth) realWidth = lineWidth;
						lineWidth = 0;
					}
				}

				if (characters[i] != null) characters[i].line = line;                 // and the line number character i is on.
			}
		}

		for (int i = text.length(); i < characters.length; i++) { // Hide all extra characters
			if (characters[i] != null) characters[i].transform.visible = false;
		}

		if (lineWidth > realWidth) realWidth = lineWidth;
	}

	@Override
	public void update(double dt) {
		updateText();
	}

	public void setGraphic(Graphic graphic, int rows, int cols) {
		this.graphic = graphic;
		this.columns = cols;
		this.rows = rows;
		this.xOnGfx = 0;
		this.yOnGfx = 0;
		this.widthOnGfx = graphic.width;
		this.heightOnGfx = graphic.height;
	}

	public void setGraphic(Graphic graphic, int rows, int cols, int x, int y, int width, int height) {
		this.graphic = graphic;
		this.columns = cols;
		this.rows = rows;
		this.xOnGfx = x;
		this.yOnGfx = y;
		this.widthOnGfx = width;
		this.heightOnGfx = height;
	}

	@Override public Transformation getParent() {
		return parent;
	}

	@Override public double getX() {
		return x;
	}

	@Override public double getY() {
		return y;
	}

	@Override public double getAngle() {
		return angle;
	}

	@Override public double getWidth() {
		return width;
	}

	@Override public double getHeight() {
		return height;
	}

	@Override public double getScale() {
		return scale;
	}

	@Override public int getLayer() {
		return layer;
	}

	@Override public boolean getVisibility() {
		return visible;
	}

	@Override public boolean shouldFollowCamera() {
		return followCamera;
	}

	/**
	 * An extension of Quad that adds some useful attributes for characters
	 */
	private class Character extends SimpleGameObject {

		// Attributes
		public int line;

		public Character(Room room) {
			super(room);
		}
	}

	/**
	 * Predefinable font values.
	 */
	public static class Font {
		private String order = DEF_ORDER;
		private double kerning[] = DEF_KERN;

		private int drawable;
		private int rows;
		private int columns;
		private int x;
		private int y;
		private int width;
		private int height;

		public Font(int drawable, int columns, int rows) {
			this.drawable = drawable;
			this.rows = rows;
			this.columns = columns;
			this.x = 0;
			this.y = 0;
			this.width = 0;
			this.height = 0;
		}

		public Font(int drawable, int columns, int rows, int x, int y, int width, int height) {
			this.drawable = drawable;
			this.rows = rows;
			this.columns = columns;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
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
		 * Set the widths of the characters to allow proper kerning. <br/><br/>
		 *
		 * Characters should be aligned to the left of the frame on graphic sheet. Kerning
		 * values are the width of the character in pixels divided by the width of a single
		 * frame. (For a character that takes up the whole frame, this would be 1.)
		 * <br/><br/>
		 * Kerning values should be in the same order as set by setOrder().
		 *
		 * This override will set all kerning values to 1 for fixed width typefaces.
		 */
		public void setKerning() {
			for (int i = 0; i < kerning.length; i++) {
				kerning[i] = 1;
			}
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
		 * This override will set all kerning values to all.
		 */
		public void setKerning(double all) {
			for (int i = 0; i < kerning.length; i++) {
				kerning[i] = all;
			}
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
		 * This override will set the value of char c to k.
		 */
		public void setKerning(char c, double k) {
			if (order.indexOf(c) != -1) {
				kerning[order.indexOf(c)] = k;
			}
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

		public double[] getKerning() {
			return kerning;
		}

		public String getOrder() {
			return order;
		}

		public int getDrawable() {
			return drawable;
		}

		public int getRows() {
			return rows;
		}

		public int getColumns() {
			return columns;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}
}
