package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Example OpMode for driving 4 motors using tank drive
 *
 * This program assumes we have four DC motors (AndyMark NeveRest, REV HD Hex Motor, etc.)
 * connected to wheels on the robot's drive train. We'll use the two joysticks on the first
 * driver's gamepad to control each side of the robot (left and right).
 *
 * @author AJ Foster
 * @version 1.0.0
 */
@TeleOp(name = "4 Motor Tank Drive", group = "Examples")
public class TankDriveFourMotor extends OpMode {

    // Declare our motor variables.
    DcMotor left_front, left_back, right_back, right_front;

    @Override
    public void init() {

        /* Initialize hardware using our configuration file. The names in quotes should match
         * what we call each motor in the "Configure Robot" menu of the phone application.
         */
        left_front = hardwareMap.dcMotor.get("left_front");
        left_back = hardwareMap.dcMotor.get("left_back");
        right_front = hardwareMap.dcMotor.get("right_front");
        right_back = hardwareMap.dcMotor.get("right_back");

        /* Since the motors are oriented in opposite directions, one side will need to be
         * reversed. Depending on which type of motors are on the robot, we may need to reverse
         * the left side instead.
         */
        right_front.setDirection(DcMotor.Direction.REVERSE);
        right_back.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        /* Ask for the current values of each joystick in the Y (up/down) direction.
         * WARNING: Up is -1, and down is +1 for Logitech F310 gamepads. Switching which side of
         * the drive train has been reversed can resolve this, or we could add a negative sign
         * when we read the values.
         */
        double left_power = gamepad1.left_stick_y;
        double right_power = gamepad1.right_stick_y;

        // Set power to the motors.
        left_front.setPower(left_power);
        left_back.setPower(left_power);

        right_front.setPower(right_power);
        right_back.setPower(right_power);
    }
}
