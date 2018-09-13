package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Drive a servo using gamepad buttons
 *
 * In this example we use buttons A and B on the first gamepad to move a servo between two preset
 * positions. Change the SERVO_OPEN and SERVO_CLOSED variables and check the name of the servo in
 * the configuration file.
 *
 * @author AJ Foster
 * @version 1.0.0
 */
@TeleOp(name = "Servo Button", group = "Examples")
public class ServoButton extends OpMode {

    // Hardware variable.
    Servo servo;

    // Preset positions. Change these to fit your needs (0.0-1.0).
    double SERVO_CLOSED = 0.7;
    double SERVO_OPEN = 0.1;

    @Override
    public void init() {
        // Initialize hardware. Change this name to match your configuration file.
        servo = hardwareMap.servo.get("servo");
    }

    @Override
    public void loop() {

        // Set servo position based on the buttons A and B.
        if (gamepad1.a) {
            servo.setPosition(SERVO_CLOSED);
        }
        else if (gamepad1.b) {
            servo.setPosition(SERVO_OPEN);
        }

        // If we want the claw to do something else when neither button is pressed, we can:
        //
//        else {
//            claw.setPosition(...);
//        }
    }
}
