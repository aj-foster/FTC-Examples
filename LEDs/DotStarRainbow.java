package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Create a moving rainbow effect using DotStar LEDs.
 *
 * This example uses the current time to create a moving rainbow effect on the DotStar LED strip.
 * You'll find this effect looks best when using the I2C/SPI bridge to drive the LEDs, as digital
 * outputs simply aren't fast enough to make a smooth transition.
 * 
 * ! Warning: Make sure you have updated your REV Robotics module to firmware 1.7.2 or greater.
 *
 * ! Warning: It's your job to ensure the LEDs don't draw too much current from your robot.
 */
@TeleOp(name = "DotStar Rainbow", group = "Examples")
public class DotStarRainbow extends OpMode {

    /* LEDs: Use this line if you drive the LEDs using an I2C/SPI bridge. */
    DotStarBridgedLED leds;

    /* Use this instead of the line above if you are driving the LEDs using digital outputs.
     * Warning: the color updates will be slow.
     */
    // DotStarLED leds;

    /* This example will use a timer to create waves of color. */
    ElapsedTime timer;

    /* Run the LED update code in a separate thread to avoid blocking important tasks. */
    Thread led_thread;

    @Override
    public void init() {

        /* Set up the LEDs
         *
         * Here we use the advanced way of setting up the DotStar LEDs. Using the parameters class,
         * we can set up the length of the strip and which module we're using to drive the LEDs.
         */

        // Use DotStarBridgedLED.Controller.ModernRoboticsDIM if that's the module driving the LEDs.
        DotStarBridgedLED.Parameters params = new DotStarBridgedLED.Parameters(DotStarBridgedLED.Controller.RevExpansionHub);

        // Change this to match the number of LEDs in the strip.
        params.length = 28;

        // Set up the LED strip.
        leds = new DotStarBridgedLED(params, hardwareMap.get(DotStarBridgedLED.class, "leds").getDeviceClient(), true);

        // Set up the timer we'll use for visual effects.
        timer = new ElapsedTime();
    }

    public void start() {

        /* We use a separate thread for updating the LEDs. This allows us to continually change the
         * colors without worrying about delaying more important tasks, like driving.
         */


        led_thread = new Thread() {
            public void run() {

                // Continue until the program stops.
                while (true) {

                    // Update each pixel in the strip.
                    for (int i = 0; i < leds.pixels.length; i++) {

                        // Calculate the new color based on its position and the current time.
                        double hue = (i * 18 + timer.seconds() * 72) % 360;
                        int color = Color.HSVToColor(new float[]{(float) hue, 1.0f, 0.25f});

                        // Update individual pixels with their new color.
                        leds.pixels[i].red = Color.red(color);
                        leds.pixels[i].blue = Color.blue(color);
                        leds.pixels[i].green = Color.green(color);
                    }

                    // Flush the current set of colors to the strip.
                    leds.update();
                }
            }
        };

        // Start the color updates.
        led_thread.start();
    }

    @Override
    public void loop() {}

    @Override
    public void stop() {

        // End the color updates.
        led_thread.interrupt();
    }
}
