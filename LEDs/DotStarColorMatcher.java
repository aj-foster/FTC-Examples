package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.util.Range;

/**
 * Display a color sensor reading on a DotStar LED strip
 *
 * This OpMode uses a color sensor to digitize a color and display it on the LED strip. It works
 * best on matte surfaces. Use this to get live feedback on what your robot is seeing.
 *
 * @author AJ Foster
 * @version 2.0.0
 */
@TeleOp(name = "DotStar Color Matcher", group = "Examples")
public class DotStarColorMatcher extends OpMode {

    // Hardware variables.
    ColorSensor color;
    DotStarBridgedLED leds;

    // How bright should any of the output colors be? (0-255)
    // Higher values cause the LEDs to show the color white more often.
    private final int MAX_OUTPUT_BRIGHTNESS = 100;

    @Override
    public void init() {
        // Set up color sensor.
        color = hardwareMap.colorSensor.get("color"); // Change this to your configured name!

        // Set up the LEDs. Change this to your configured name.
        leds = hardwareMap.get(DotStarBridgedLED.class, "leds");

        // Use ModernRoboticsDIM if using Modern Robotics hardware.
        leds.setController(DotStarBridgedLED.Controller.RevExpansionHub);

        // Set the length of the strip.
        leds.setLength(30);
    }

    @Override
    public void loop() {
        // Get the current color values.
        int red = color.red();
        int blue = color.blue();
        int green = color.green();

        telemetry.addData("Sensor RGB", "" + red + " " + green + " " + blue);

        // Figure out the maximum color value.
        int max = Math.max(Math.max(red, blue), green);

        // Scale the color values quadratically to emphasize the colors.
        red = (int) (Math.pow(red / (double) max, 2) * MAX_OUTPUT_BRIGHTNESS);
        blue = (int) (Math.pow(blue / (double) max, 2) * MAX_OUTPUT_BRIGHTNESS);
        green = (int) (Math.pow(green / (double) max, 2) * MAX_OUTPUT_BRIGHTNESS);

        // Make sure all values are in the acceptable range.
        red = Range.clip(red, 0, MAX_OUTPUT_BRIGHTNESS);
        blue = Range.clip(blue, 0, MAX_OUTPUT_BRIGHTNESS);
        green = Range.clip(green, 0, MAX_OUTPUT_BRIGHTNESS);

        telemetry.addData("Output RGB", "" + red + " " + green + " " + blue);

        // Update the LEDs with the scaled colors.
        for (int i = 0; i < leds.pixels.length; i++) {
            leds.setPixel(i, red, green, blue);
        }
        leds.update();
    }
}
