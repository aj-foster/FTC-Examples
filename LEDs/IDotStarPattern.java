package org.firstinspires.ftc.teamcode;

import java.util.List;

/**
 * Represents a pattern to be executed by the pattern library.
 *
 * DotStar LEDs (i.e. https://www.adafruit.com/product/2238) are collections of LEDs which are
 * programmable using SPI. While it is possible to use two digital outputs as data and clock lines,
 * an I2C/SPI Bridge can manage the digital writes at a much higher frequency. This is required for
 * "smooth" color changes. However, a similar class for driving the LEDs via two digital outputs
 * is also available.
 *
 * @author Rick Van Smith
 * @version 1.0.0
 */

public interface IDotStarPattern {
	/**
	 * Gets the number of colors the chosen pattern uses.
	 * @return The number of colors that should be passed into setPatternColors.
	 */
	int getNumColors();

	/**
	 * Sets an external value used by the pattern to adjust the pattern.
	 * @param measurement A value used to adjust the pattern.  The pattern must be
	 *   resent to adjust to the new value.
	 */
	void setMeasuredValue(double measurement);

	/**
	 * Sets the RGB colors of a pattern.  Each pattern uses a different number
	 * of base colors to display the pattern.
	 * @param newColors A list of colors used by the pattern.
	 */
	void setPatternColors(List<Integer> newColors);

	/**
	 * Gets the list of the current pattern colors, the pattern default if none
	 * have been set.
	 * @return The list of colors used by the pattern.
	 */
	List<Integer> getPatternColors();

	/**
	 * If set to true, the pattern will not update, but remain as set in the
	 * first pass.  False will repeatedly call update in a loop.
	 * @param isStatic True is a single static light, false is a moving pattern
	 */
	void setStaticPattern(boolean isStatic);

	/**
	 * Returns whether the pattern is set to static or not.
	 * @return True if the pattern does not update, false if it is moving.
	 */
	boolean isStaticPattern();

	/**
	 * The pattern decides how to use the pattern delay.  Typically this would
	 * be the time between shifting the pattern.
	 * @param milliseconds A time in milliseconds to delay the pattern.
	 */
	void setPatternDelay(double milliseconds);

	/**
	 * Gets the time the pattern will use as a delay.
	 * @return The time in milliseconds the pattern will delay, exact usage is
	 * defined by the pattern.
	 */
	double getPatternDelay();

	/**
	 * The pattern decides how to use the pattern spacing.  Typically this would
	 * be how many pixels between pattern elements.
	 * @param pixelSpacing A spacing in pixels.
	 */
	void setPatternSpacing(int pixelSpacing);

	/**
	 * Gets the spacing the pattern will use.
	 * @return The spacing in pixels the pattern will use, exact usage is
	 * defined by the pattern.
	 */
	int getPatternSpacing();

	/**
	 * Update gets called by the DotStarPatternRunner in a loop to make the
	 * pattern update.
	 */
	void update();
}