package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Drive a servo using a joystick
 *
 * In this example we use a joystick on the first gamepad to move a servo. Check the name of the
 * servo in the configuration file.
 *
 * @author AJ Foster
 * @version 1.0.0
 */
@TeleOp(name = "Servo Joystick", group = "Examples")
public class ServoJoystick extends OpMode {

    // Hardware variable.
    Servo servo;

    @Override
    public void init() {
        // Initialize hardware. Change this name to match your configuration file.
        servo = hardwareMap.servo.get("servo");
    }

    @Override
    public void loop() {

        // Get the current value of the joystick. Up is negative on Logitch F310 gamepads.
        double joystick = -gamepad1.left_stick_y;

        // Scale and shift the range of values from (-1.0, 1.0) to (0.0, 1.0).
        double position = (joystick / 2.0) + 0.5;

        // Set the servo position.
        servo.setPosition(position);
    }
}
