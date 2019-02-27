package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * ! Warning: Make sure you have updated your REV Robotics module to firmware 1.7.2 or greater.
 *
 * ! Warning: It's your job to ensure the LEDs don't draw too much current from your robot.
 *
 * @author Rick Van Smith
 * @version 1.0.0
 */
@TeleOp(name = "DotStar Pattern Test", group = "Test")
public class DotStarPatternTester extends OpMode {
    DotStarBridgedLED leds;

    /* The patterns to run on our LEDs. */
    IDotStarPattern rainbow;
    IDotStarPattern rainbowShift;
    IDotStarPattern halfAndHalf;
    IDotStarPattern chase;
    IDotStarPattern twinkle;
    IDotStarPattern indicator;
    DotStarPatternRunner ledDisplay = new DotStarPatternRunner();
    boolean displayIndicator = false;
    float[] redHsv = new float[]{0f, 0f, 0f};
    float[] yellowHsv = new float[]{0f, 0f, 0f};
    float[] greenHsv = new float[]{0f, 0f, 0f};

    @Override
    public void init() {
        // Set up the LEDs. Change this to your configured name.
        leds = hardwareMap.get(DotStarBridgedLED.class, "leds");

        // Use ModernRoboticsDIM if using Modern Robotics hardware.
        leds.setController(DotStarBridgedLED.Controller.RevExpansionHub);

        // Set the length of the strip.
        leds.setLength(60);

        // Create the twinkle pattern to run
        twinkle = new dsPatternTwinkle(leds);

        // Create the rainbow patterns to run.
        rainbow = new dsPatternRainbow(leds);
        rainbowShift = new dsPatternRainbowShift(leds);

        // We used Half and Half to indicate our collector contents, gold for
        // gold and silver for silver.  Since we had two collectors we split
        // the lights in half.
        halfAndHalf = new dsPatternHalfAndHalf(leds);

        // This shows how to change the pattern default colors.
        List<Integer> chaseColors = new ArrayList<Integer>();
        chaseColors.add(0, Color.BLACK);
        chaseColors.add(1, Color.GREEN);
        chase = new dsPatternChase(leds);
        chase.setPatternColors(chaseColors);

        // We wanted to try and use a level indicator to show kids their drive
        // speed.  A traditional "voltage" indicator would go red, yellow then
        // green.  In this case we wanted the mid value to be green to show
        // where we want them to be so we updated the default colors.
        indicator = new dsPatternLevelIndicator(leds);
        List<Integer> indicatorColors = new ArrayList<Integer>();
        indicatorColors.add(0, Color.YELLOW);
        indicatorColors.add(1, Color.GREEN);
        indicatorColors.add(2, Color.RED);
        indicator.setPatternColors(indicatorColors);
    }

    public void start() {
        ledDisplay.setPattern(rainbow);
        ledDisplay.startPattern();
    }

    @Override
    public void loop() {
        try {
            if(gamepad1.a) {
                displayIndicator = false;
                ledDisplay.setPattern(twinkle);
                ledDisplay.startPattern();
            } else if(gamepad1.b) {
                displayIndicator = false;
                ledDisplay.setPattern(halfAndHalf);
                ledDisplay.startPattern();
            } else if(gamepad1.y) {
                displayIndicator = false;
                ledDisplay.setPattern(chase);
                ledDisplay.startPattern();
            } else if(gamepad1.x) {
                displayIndicator = true;
            } else if (gamepad1.left_bumper) {
                displayIndicator = false;
                ledDisplay.setPattern(rainbow);
                ledDisplay.startPattern();
            } else if (gamepad1.right_bumper) {
                displayIndicator = false;
                ledDisplay.setPattern(rainbowShift);
                ledDisplay.startPattern();
            }
            if(displayIndicator) {
                // The indicator pattern is the only one in this tester that
                // takes external input and puts it on the display.  In this
                // case it is the power applied by the joystick.
                double powerX = gamepad1.left_stick_x;
                double powerY = gamepad1.left_stick_y;
                double magnitude = Math.sqrt(powerX*powerX + powerY*powerY);
                indicator.setMeasuredValue(magnitude);
                ledDisplay.setPattern(indicator);
                ledDisplay.startPattern();
                telemetry.addData("Joystick X", powerX);
                telemetry.addData("Joystick Y", powerY);
                telemetry.addData("Joystick Magnitude", magnitude);
            }
        } catch (Throwable ex) {
            telemetry.addData("Exception: ", ex.getMessage());
        }
    }

    @Override
    public void stop() {
        ledDisplay.terminate();
    }
}
