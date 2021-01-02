package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;

/**
 * DotStar-based battery meter
 *
 * This OpMode uses the internal battery voltage meter on the REV Expansion Hub to display a color
 * battery indicator on DotStar LEDs. The code uses I2C/SPI-bridged LEDs by default, but LEDs
 * driven using digital outputs work as well.
 * 
 * Written for DotStarBridgedLED version 2.0.0.
 *
 * @author AJ Foster
 * @version 2.0.0
 */
@TeleOp(name = "DotStar Battery Meter", group = "Examples")
public class DotStarBatteryMeter extends OpMode {

    // Blinkin module and battery sensor variables.
    DotStarBridgedLED leds;
    VoltageSensor battery;

    // Min and max voltages used for choosing colors. Anything near or below the MIN will be red,
    // and everything near or above the MAX will be green.
    private static final double MIN_VOLTAGE = 11.0;
    private static final double MAX_VOLTAGE = 13.5;

    @Override
    public void init() {
        // On a REV Expansion Hub, the name of the sensor is the name of the hub.
        battery = hardwareMap.voltageSensor.get("Expansion Hub 1"); // Change to match your hub!

        // Set up the LEDs. Change this to your configured name.
        leds = hardwareMap.get(DotStarBridgedLED.class, "leds");

        // Use ModernRoboticsDIM if using Modern Robotics hardware.
        // leds.setController(DotStarBridgedLED.Controller.ModernRoboticsDIM);

        // Set the length of the strip.
        leds.setLength(30);
    }

    @Override
    public void loop() {
        /* WARNING: You probably don't want to do this every time the loop runs.
         *
         * One possibility is to place this code in a separate function and call it occasionally
         * when you want to update the LEDs. A better idea might be to set up this code in a
         * separate thread. See the DotStarRainbow class for an example of how to do that.
         *
         * If you do run this code every time the loop runs, you may find your robot become slow
         * to respond. You may even get a "Stuck in Loop" error because the loop took too
         * long to run. Reach out if you have questions.
         */

        // Ask for the current battery voltage.
        double voltage = battery.getVoltage();

        // Choose the color closest to the current voltage.
        double hue = getBatteryVoltageHue(voltage);

        // Convert hue into RGB.
        int color = Color.HSVToColor(new float[]{(float) hue, 1.0f, 0.25f});

        // Choose how many pixels to light.
        int pixelsToLight = getNumberOfPixelsToLight(voltage);

        // Set the LEDs.
        for (int i = 0; i < leds.pixels.length; i++) {
            if (i < pixelsToLight) {
                leds.setPixel(i, color);
            }
            else {
                leds.setPixel(1, 0, 0, 0);
            }
        }
        leds.update();
    }

    /**
     * Returns a hue corresponding to the given voltage based on MIN_VOLTAGE and MAX_VOLTAGE.
     *
     * We use 0.0 as the minimum red hue, and 120.0 as the maximum green hue. Values vary
     * continuously between these two endpoints.
     *
     * @param voltage The battery voltage (decimal) to indicate.
     * @return Hue between 0.0 and 120.0.
     */
    private double getBatteryVoltageHue(double voltage) {
        double range = MAX_VOLTAGE - MIN_VOLTAGE;

        // If voltage is below the minimum, use a red hue.
        if (voltage - MIN_VOLTAGE <= 0) {
            return 0.0; // Red
        }

        // If voltage is above the maximum, use a green hue.
        else if (MAX_VOLTAGE - voltage <= 0) {
            return 120.0; // Green
        }

        // If voltage is in between min/max, continuously choose a color between red and green.
        else {
            return (voltage - MIN_VOLTAGE) * 120.0 / range;
        }
    }

    /**
     * Returns a number of pixels to light on the strip proportional to the voltage.
     *
     * @param voltage The battery voltage (decimal) to indicate.
     * @return Number of pixels to light, between 1 and the total number of pixels.
     */
    private int getNumberOfPixelsToLight(double voltage) {
        double range = MAX_VOLTAGE - MIN_VOLTAGE;

        // If voltage is below the minimum, light only one pixel.
        if (voltage - MIN_VOLTAGE <= 0) {
            return 1;
        }

        // If voltage is above the maximum, light every pixel.
        else if (MAX_VOLTAGE - voltage <= 0) {
            return leds.pixels.length;
        }

        // If voltage is in between min/max, light a proportional number of pixels.
        else {
            return (int) Math.ceil((voltage - MIN_VOLTAGE) * leds.pixels.length / range);
        }
    }
}
