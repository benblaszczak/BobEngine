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
 * This class can be used to display an integer. The default font
 * graphic is found in the res/drawable folder of the BobEngine library project
 * with the name "numbers.png". You may copy it's format and create
 * your own fonts. If you only have one font, simply name it "numbers.png"
 * and put it in your project's res/drawable folder. Otherwise, use the function
 * setGraphic() to change the font.
 * 
 * @author Ben
 *
 */
public class NumberDisplay extends GameObject {
	
	// Kerning values
	private double[] before = { 0, 0.25, .09, .11, .09, .13, .08, .13, .13, .08};
	private double[] after = before;

	// Variables
	public int number;
	private int digits;
	private double realWidth;
	private int indices;
    private int position;

	// Graphic
	private static Graphic g;                         // The default number graphic

	public NumberDisplay(int id, Room containingRoom) {
		super(id, containingRoom);

		g = getView().getGraphicsHelper().addGraphic(R.drawable.numbers);

		setGraphic(g, 10);
	}

	/**
	 * Set or Reset this NumberDisplay. You can specify position,
	 * size, and layer. The number that is displayed will be set to 
	 * 0.
	 * 
	 * @param x - x position in pixels
	 * @param y - y position in pixels
	 * @param sizeRatio - The width of a single digit relative to the width 
	 * of the screen. The height is equal to the width in pixels. (ex: .25 
	 * would make the width of a single digit 1/4 the width of the screen)
	 * @param layer - The z-depth of this GameObject in the Room.
	 */
	public void set(double x, double y, double sizeRatio, int layer) {
		this.layer = layer;

		number = 0;
		frame = 0;

		realWidth = width = getRoom().getWidth() * sizeRatio;
		height = width;
		indices = 0;
		digits = 1;
        position = 0;

		this.x = x;
		this.y = y;
	}
	
	/**
	 * Set the amount of empty space before digits. These values should be decimals representing percentages
	 * of the total width of the number graphic. (ex: .25 would take away 25% of the space in front of the
	 * digit.)
	 * @param d0 - Value for 0
	 * @param d1 - Value for 1
	 * @param d2 - So on...
	 * @param d3
	 * @param d4
	 * @param d5
	 * @param d6
	 * @param d7
	 * @param d8
	 * @param d9
	 */
	public void setBeforeKerning(double d0, double d1, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9 ) {
		before[0] = d0;
		before[1] = d1;
		before[2] = d2;
		before[3] = d3;
		before[4] = d4;
		before[5] = d5;
		before[6] = d6;
		before[7] = d7;
		before[8] = d8;
		before[9] = d9;
	}

	public void setBeforeKerning(double before[]) {
		this.before = before;
	}
	
	/**
	 * Set the amount of empty space after digits. These values should be decimals representing percentages
	 * of the total width of the number graphic. (ex: .25 would take away 25% of the space after the
	 * digit.)
	 * @param d0 - Value for 0
	 * @param d1 - Value for 1
	 * @param d2 - So on...
	 * @param d3
	 * @param d4
	 * @param d5
	 * @param d6
	 * @param d7
	 * @param d8
	 * @param d9
	 */
	public void setAfterKerning(double d0, double d1, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9 ) {
		after[0] = d0;
		after[1] = d1;
		after[2] = d2;
		after[3] = d3;
		after[4] = d4;
		after[5] = d5;
		after[6] = d6;
		after[7] = d7;
		after[8] = d8;
		after[9] = d9;
	}

	public void setAfterKerning(double after[]) {
		this.after = after;
	}

    /**
     * Set the alignment of this number display.
     * <br /><br />
     * 0 - Align right   <br />
     * 1 - Align center  <br />
     * 2 - Align left    <br />
     * @param alignment
     */
    public void setAlignment(int alignment) {
        position = alignment;
    }

	/**
	 * Change the number that this NumberDisplay displays.
	 * @param num
	 */
	public void setNumber(int num) {
		number = num;
	}
	
	/**
	 * Retrieve the number this number is displaying.
	 * @return
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Returns the real width of this NumberDisplay including all
	 * digits of the displayed number.
	 * 
	 * @return
	 */
	public double getWidth() {
		return realWidth;
	}

	// Step
	public void update(double deltaTime) {

		if (realWidth < width) {
			realWidth = width;
		}

		digits = Integer.toString((int) number).length();
		indices = digits * 6;

		super.update(deltaTime);
	}

	// Get Vertices
	@Override
	public float[] getVertices() {
		float totSquare[] = new float[12 * digits];
		int tempNum;
		double oX;

		tempNum = (int) number;
		oX = x;
		realWidth = width * (digits - 1);

        for (int d = 0; d < digits; d++) {
            int digit = tempNum % 10;
            tempNum /= 10;

            // Remove space before
            switch (digit) {
                case 0:
                    realWidth -= width * before[0];
                    break;
                case 1:
                    realWidth -= width * before[1];
                    break;
                case 2:
                    realWidth -= width * before[2];
                    break;
                case 3:
                    realWidth -= width * before[3];
                    break;
                case 4:
                    realWidth -= width * before[4];
                    break;
                case 5:
                    realWidth -= width * before[5];
                    break;
                case 6:
                    realWidth -= width * before[6];
                    break;
                case 7:
                    realWidth -= width * before[7];
                    break;
                case 8:
                    realWidth -= width * before[8];
                    break;
                case 9:
                    realWidth -= width * before[9];
                    break;
            }

            // Remove space after
            switch (digit) {
                case 0:
                    realWidth -= width * after[0];
                    break;
                case 1:
                    realWidth -= width * after[1];
                    break;
                case 2:
                    realWidth -= width * after[2];
                    break;
                case 3:
                    realWidth -= width * after[3];
                    break;
                case 4:
                    realWidth -= width * after[4];
                    break;
                case 5:
                    realWidth -= width * after[5];
                    break;
                case 6:
                    realWidth -= width * after[6];
                    break;
                case 7:
                    realWidth -= width * after[7];
                    break;
                case 8:
                    realWidth -= width * after[8];
                    break;
                case 9:
                    realWidth -= width * after[9];
                    break;
            }
        }

        if (position == 0) {
            x = x + width / 2 + realWidth;
        } else if (position == 1) {
            x = x + realWidth / 2;
        } else if (position == 2) {
            x = x - width / 2;
        }

        tempNum = (int) number;

		for (int d = 0; d < digits; d++) {
			int digit = tempNum % 10;
			tempNum /= 10;

			// Remove space before
			switch (digit) {
			case 0:
				x += width * before[0];
				break;
			case 1:
				x += width * before[1];
				break;
			case 2:
				x += width * before[2];
				break;
			case 3:
				x += width * before[3];
				break;
			case 4:
				x += width * before[4];
				break;
			case 5:
				x += width * before[5];
				break;
			case 6:
				x += width * before[6];
				break;
			case 7:
				x += width * before[7];
				break;
			case 8:
				x += width * before[8];
				break;
			case 9:
				x += width * before[9];
				break;
			}

			updatePosition(x - width * d, y);
			float[] square = super.getVertices();

			for (int i = 0; i < 12; i++) {
				totSquare[(d * 12) + i] = square[i];
			}

			// Remove space after
			switch (digit) {
			case 0:
				x += width * after[0];
				break;
			case 1:
				x += width * after[1];
				break;
			case 2:
				x += width * after[2];
				break;
			case 3:
				x += width * after[3];
				break;
			case 4:
				x += width * after[4];
				break;
			case 5:
				x += width * after[5];
				break;
			case 6:
				x += width * after[6];
				break;
			case 7:
				x += width * after[7];
				break;
			case 8:
				x += width * after[8];
				break;
			case 9:
				x += width * after[9];
				break;
			}
		}

		x = oX;

		return totSquare;
	}

	// Get texture coords
	@Override
	public float[] getGraphic() {
		float totTexture[] = new float[8 * digits];
		int tempNum;

		tempNum = (int) number;
		for (int d = 0; d < digits; d++) {
			frame = tempNum % 10;
			tempNum /= 10;

			setFrame(frame);
			float[] texture = super.getGraphic();

			for (int i = 0; i < 8; i++) {
				totTexture[(d * 8) + i] = texture[i];
			}
		}

		return totTexture;
	}

	@Override
	public int getIndices() {
		return indices;
	}
}
