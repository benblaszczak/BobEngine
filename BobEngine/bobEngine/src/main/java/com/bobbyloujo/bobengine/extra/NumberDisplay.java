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

import com.bobbyloujo.bobengine.R;
import com.bobbyloujo.bobengine.components.Transformation;
import com.bobbyloujo.bobengine.entities.Entity;
import com.bobbyloujo.bobengine.entities.SimpleGameObject;
import com.bobbyloujo.bobengine.graphics.Graphic;
import com.bobbyloujo.bobengine.systems.Updatable;

import java.text.DecimalFormat;

import javax.microedition.khronos.opengles.GL10;

/**
 * This class can be used to display a number. The default font
 * graphic is found in the res/drawable folder of the BobEngine library project
 * with the name "numbers.png". You may copy it's format and create
 * your own fonts. If you only have one font, simply name it "numbers.png"
 * and put it in your project's res/drawable folder. Otherwise, use the function
 * setGraphic() to change the font.
 *
 * @author Ben
 */
public class NumberDisplay extends Entity implements Updatable, Transformation {

	// Constants

	/** Right-align the number displayed. x will be on the rightmost edge of the NumberDisplay */
	public static final int RIGHT = 0;

	/** Center-align the number displayed. x will be in the center of the NumberDisplay */
	public static final int CENTER = 1;

	/** Left-align the number displayed. x will be on the leftmost edge of the NumberDisplay */
	public static final int LEFT = 2;

	private static final String COMMAS = "#,###";  // Format to show commas
	private static final String NO_COMMAS = "###"; // Format to hide commas
	private static final String DEF_PREC = "";     // Default level of precision to show in decimal numbers.

	// Kerning values
	private double[] kerning = { 0.73, 0.435, .72, .708, .74, .70, .653, .607, .656, .653, .27, 0.31}; // The kerning values for each character, with default values.

	// Variables
	private char[] text;
	private double number;      // The number being displayed
	private double realWidth;   // The real width of this NumberDisplay, included all characters being displayed.
	private int alignment;      // The text alignment of this NumberDisplay (Right, left, center)
	private boolean hasChanged; // Flag that indicates the number has changed.

	private DecimalFormat formatter;
	private String format;
	private String precision; // A string defining the precision of decimal numbers.
	private String commas;    // A string defining the formatting of commas.

	private Graphic graphic;  // The graphic used for this NumberDisplay
	private int rows;         // The number of rows of frames the graphic has
	private int cols;         // The number of columns the graphic has

	private SimpleGameObject[] digits; // The SimpleGameObjects used to display all the digits.

	// Transform data
	public Transformation parent;
	public double x;
	public double y;
	public double height;
	public double width;
	public double scale;
	public double angle;
	public int layer;
	public boolean followCamera;
	public boolean visible;

	public NumberDisplay() {
		init();
	}

	public NumberDisplay(Entity parent) {
		super(parent);
		init();
	}

	private void init() {
		x = y = width = height = 1;
		scale = 1;
		visible = true;

		alignment = 0;
		number = 0;
		hasChanged = true;

		precision = DEF_PREC;
		commas = COMMAS;
		format = commas + precision;
		formatter = new DecimalFormat(format);

		digits = new SimpleGameObject[1];
	}

	@Override
	public void onParentAssigned() {
		if (graphic == null) {
			getView().getGraphicsHelper().setParameters(true, GL10.GL_LINEAR_MIPMAP_LINEAR, GL10.GL_LINEAR_MIPMAP_LINEAR);
			setGraphic(getView().getGraphicsHelper().addGraphic(R.drawable.numbers), 12, 1);
		}
	}

	/**
	 * Tell this NumberDisplay which graphic to use. The graphic should have 12 frames for
	 * all ten digits, a period, and a comma, in that order. Frames can be in a grid of
	 * any dimensions that can hold all 12 frames.
	 *
	 * @param graphic The Graphic to use.
	 * @param rows The number of rows of frames.
	 * @param cols The number of columns of frames.
	 */
	public void setGraphic(Graphic graphic, int rows, int cols) {
		this.graphic = graphic;
		this.rows = rows;
		this.cols = cols;
	}

	/**
	 * Set the width of each digit. These values should be decimals representing percentages
	 * of the total width of the number graphic. (ex: .25 would take away 25% of the space in front of the
	 * digit.)
	 *
	 * @param d0 Width from 0 to 1 of digit 0
	 * @param d1 Width of digit 1
	 * @param d2 Width of digit 2
	 * @param d3 Width of digit 3
	 * @param d4 Width of digit 4
	 * @param d5 Width of digit 5
	 * @param d6 Width of digit 6
	 * @param d7 Width of digit 7
	 * @param d8 Width of digit 8
	 * @param d9 Width of digit 9
	 * @param decimal Width of the decimal point
	 * @param comma Width of the comma
	 */
	public void setKerning(double d0, double d1, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, double decimal, double comma) {
		kerning[0] = d0;
		kerning[1] = d1;
		kerning[2] = d2;
		kerning[3] = d3;
		kerning[4] = d4;
		kerning[5] = d5;
		kerning[6] = d6;
		kerning[7] = d7;
		kerning[8] = d8;
		kerning[9] = d9;
	}

	/**
	 * Set the width of each digit. These values should be decimals representing percentages
	 * of the total width of a frame in the number graphic. (ex: .25 would take away 25% of the space in
	 * a single frame of the graphic.)
	 *
	 * @param kerning an array holding all 12 kerning values. (digits 0-9, period, comma)
	 */
	public void setKerning(double kerning[]){
		this.kerning = kerning;
	}

	/**
	 * Set the alignment of this number display.
	 *
	 * @param alignment One of the three alignment constants in this class (LEFT, CENTER, RIGHT)
	 */
	public void setAlignment(int alignment) {
		if (alignment == LEFT || alignment == CENTER || alignment == RIGHT) {
			this.alignment = alignment;
		} else {                   // You didn't even pick a real alignment...
			this.alignment = LEFT; // so you get left because why not.
		}
	}

	/**
	 * Tell this NumberDisplay if it should format the number with commas.
	 * @param useCommas true to show commas, false to not show commas.
	 */
	public void useCommas(boolean useCommas) {
		if (useCommas) {
			commas = COMMAS;
		} else {
			commas = NO_COMMAS;
		}

		format = commas + precision;
	}

	/**
	 * Set the level of precision to display when displaying a decimal number.
	 *
	 * @param precision the number of digits to display after the decimal point.
	 */
	public void setPrecision(int precision) {
		String prec = "";

		if (precision > 0) {
			prec = ".";

			for (int i = 0; i < precision; i++) {
				prec = prec + "0";
			}
		}

		this.precision = prec;

		format = commas + this.precision;
	}

	/**
	 * Change the number that this NumberDisplay displays.
	 * @param num
	 */
	public void setNumber(double num) {
		if (Math.abs(number - num) > .00005) {
			number = num;
			hasChanged = true;
		}
	}

	/**
	 * Retrieve the number this number is displaying.
	 * @return
	 */
	public double getNumber() {
		return number;
	}

	/**
	 * The real width is the sum of the width of each character displayed
	 * by this NumberDisplay (digits, commas, and decimal point)
	 *
	 * @return the real width
	 */
	public double getRealWidth() {
		return realWidth;
	}

	/* TRANSFORMABLE */

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

	@Override
	public double getWidth() {
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

	/* private */

	private void placeDigits() {
		double cursor = 0;
		double shift;
		formatter.applyPattern(format);
		text = formatter.format(number).toCharArray();

		if (text.length > digits.length) {
			for (int i = 0; i < digits.length; i++) {
				if (digits[i] != null) digits[i].removeFromRenderer();
			}

			digits = new SimpleGameObject[text.length];
		}

		for (int i = 0; i < text.length; i++) {
			if (digits[i] == null) {
				digits[i] = new SimpleGameObject(getRoom());
				digits[i].setGraphic(graphic);
				digits[i].graphic.makeGrid(rows, cols);
				digits[i].transform.parent = this;
				digits[i].transform.height = height;
				digits[i].transform.width = width;
			}
			digits[i].transform.visible = true;
			digits[i].graphic.frame = getFrameFromChar(text[i]);

			digits[i].transform.x = cursor + width / 2;
			cursor += width * kerning[getFrameFromChar(text[i])];
		}

		for (int i = text.length; i < digits.length; i++) {
			if (digits[i] != null) digits[i].transform.visible = false;
		}

		realWidth = cursor;

		switch (alignment) {
			case LEFT:
				shift = 0;
				break;
			case CENTER:
				shift = realWidth / 2;
				break;
			case RIGHT:
				shift = realWidth;
				break;
			default:
				shift = 0;
				break;
		}

		for (int i = 0; i < digits.length; i++) {
			digits[i].transform.x -= shift;
		}
	}

	private int getFrameFromChar(char c) {
		if (Character.isDigit(c)) {
			return Character.getNumericValue(c); // digit
		} else if (c == '.') {
			return 10;                           // period
		} else if (c == ',') {
			return 11;                           // comma
		}

		return -1; // ya dingus
	}

	/* events */

	@Override
	public void update(double deltaTime) {
		if (hasChanged) {
			placeDigits();
			hasChanged = false;
		}

		for (int i = 0; i < digits.length; i++) {
			if (digits[i] != null) digits[i].transform.layer = layer;
		}
	}
}
