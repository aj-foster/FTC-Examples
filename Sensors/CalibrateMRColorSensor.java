package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor.Command;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor.Register;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Calibrate a Modern Robotics I2C Color Sensor
 *
 * Written by AJ Foster in June 2017. This code was created for version 3.1 of the FTC App, and will
 * not work with versions 1.x or 2.x. Mileage may vary with versions after 3.1.
 *
 * This code allows a user -- with the help of a gamepad -- calibrate a Modern Robotics I2C color
 * sensor for both black and white states. Instructions for completing the calibration are included
 * in the driver's station telemetry.
 *
 * Although Modern Robotics recommends the calibration steps written in the instructions, this may
 * not be best for all uses. A team could choose, for example, to calibrate black as a slightly
 * illuminated dark grey floor tile and white as slightly illuminated white gaffer's tape. After
 * every calibration, it is a good idea to check the thresholds used in other OpModes to see if
 * they are still correct.
 *
 * Use this code at your own risk. You should always (1) use proper safety attire, (2) test your
 * code before using it in a situation that could harm someone, and (3) make sure you understand
 * what a piece of code (including this one) does before running it.
 *
 * @author AJ Foster
 * @version 2.0.0
 */

@TeleOp(name = "Calibrate MR Color", group = "Utilities")
@Disabled
public class CalibrateMRColorSensor extends OpMode {

    // Color Sensor to calibrate. Require that it is a Modern Robotics sensor.
    private ModernRoboticsI2cColorSensor color;

    // Track whether a calibration was recently requested.
    private int command;

    /**
     * Initialize color sensor hardware.
     */
    @Override
    public void init() {
        // Change the value "color" to the name of your color sensor.
        color = hardwareMap.get(ModernRoboticsI2cColorSensor.class, "color");
    }

    /**
     * Respond to gamepad input to perform calibrations.
     *
     * As explained in the prompt, X will begin a black-level calibration, and Y will begin a
     * white-level calibration. We prevent calibrations from being triggered within one second
     * of one another using a timer.
     */
    @Override
    public void loop() {

        /* Read the current value of the command register. If a calibration is not in progress, this
         * register should read either 0x00 or 0x01 depending on the mode of the sensor. Otherwise,
         * the register will contain the command we recently wrote.
         */
        command = color.readUnsignedByte(Register.COMMAND);

        if (!(command == 0 || command == 1)) {
            // Something is in progress. Do nothing.
        }
        else if (gamepad1.x || gamepad2.x) {
            color.writeCommand(Command.CALIBRATE_BLACK);
        }
        else if (gamepad1.y || gamepad2.y) {
            color.writeCommand(Command.CALIBRATE_WHITE);
        }
        else if (gamepad1.a || gamepad2.a) {
            color.enableLed(true);
        }
        else if (gamepad1.b || gamepad2.b) {
            color.enableLed(false);
        }

        /* The following simply deals with the telemetry printed to the Driver's Station phone.
         * Status information is printed along with some instructions.
         */
        switch (command) {
            case 0x00:
            case 0x01:
                telemetry.addData("Status", "Ready");
                break;

            case 0x42:
                telemetry.addData("Status", "Calibrating black...");
                break;

            case 0x43:
                telemetry.addData("Status", "Calibrating white...");
                break;

            default:
                telemetry.addData("Status", "Something is in progress... ");
        }

        telemetry.addData("Instructions", "Hold the sensor away from any source of light and at "
                + "least 1.5m from the nearest object. Press X to begin the dark-level calibration."
                + " Once complete, hold the sensor 5cm from a white non-reflective object. "
                + "Press Y to begin a white-level calibration.");
    }
}
