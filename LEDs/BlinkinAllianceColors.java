package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Choose Alliance-colored LEDs using Blinkin
 *
 * This OpMode allows you to choose your alliance's colors during the initialization phase of the
 * program (before you hit start). Use the X and B buttons (blue and red on the Logitech gamepad)
 * to choose the colors. Use A to reset to black.
 *
 * You can use the Blinkin Explorer to find the correct values of the colors (red, blue, black).
 * Choose a servo value in the center of each color's range.
 *
 * @author AJ Foster
 * @version 1.0.0
 */
@TeleOp(name = "Blinkin Alliance Colors", group = "Examples")
public class BlinkinAllianceColors extends OpMode {

    // Hardware variable.
    Servo blinkin;

    // Change to match your values using the Blinkin Explorer!
    double BLINKIN_SOLID_BLACK = 0.7755;
    double BLINKIN_SOLID_BLUE = 0.7425;
    double BLINKIN_SOLID_RED = 0.6705;

    @Override
    public void init() {
        // Set up Blinkin hardware and default to black (off).
        blinkin = hardwareMap.servo.get("blinkin"); // Change to match your configured name!
        blinkin.setPosition(BLINKIN_SOLID_BLACK);
    }

    /**
     * The following runs continuously between pressing INIT and the start.
     */
    @Override
    public void init_loop() {
        // A resets
        if (gamepad1.a) {
            blinkin.setPosition(BLINKIN_SOLID_BLACK);
        }
        else if (gamepad1.b) {
            blinkin.setPosition(BLINKIN_SOLID_RED);
        }
        else if (gamepad1.x) {
            blinkin.setPosition(BLINKIN_SOLID_BLUE);
        }

        // Print instructions
        telemetry.addData("***", "Use X and B to choose LED colors");
    }

    @Override
    public void loop() {
        // Your code here.
    }
}
