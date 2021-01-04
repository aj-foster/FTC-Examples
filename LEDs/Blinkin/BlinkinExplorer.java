package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Explore patterns and colors on the Blinkin module
 *
 * Different servo / PWM ports and different Blinkin modules require slightly different output
 * values to select a certain pattern or color. This OpMode allows you to step through the output
 * values and find which ones you want to use in your program.
 *
 * Use the A and B buttons on the gamepad (player 1) to step up and down through values slowly, and
 * X and Y to step through quickly. There will be multiple values that activate any given mode, so
 * you probably want to choose a value in the middle of the range.
 *
 * @author AJ Foster
 * @version 1.0.0
 */
@TeleOp(name = "Blinkin Explorer", group = "Utilities")
public class BlinkinExplorer extends OpMode {

    Servo blinkin;
    int setting = 1500;
    ElapsedTime timer;

    @Override
    public void init() {
        // Set up the Blinkin module and the timer we'll use for button "debouncing."
        blinkin = hardwareMap.get(Servo.class, "blinkin");
        timer = new ElapsedTime();
    }

    @Override
    public void loop() {
        // Use A to decrease the value slowly.
        if (gamepad1.a && timer.seconds() > 0.25) {
            setting -= 1;
            timer.reset();
        }

        // Use B to increase the value slowly.
        else if (gamepad1.b && timer.seconds() > 0.25) {
            setting += 1;
            timer.reset();
        }

        // Use X to decrease the value quickly.
        else if (gamepad1.x && timer.seconds() > 0.25) {
            setting -= 10;
            timer.reset();
        }

        // Use Y to increase the value quickly.
        else if (gamepad1.y && timer.seconds() > 0.25) {
            setting += 10;
            timer.reset();
        }

        // Calculate desired servo output from current setting.
        int REVPulseWidth = setting;
        int MRPulseWidth = 1500 + ((setting - 1500) / 2);
        double servoOutput = (REVPulseWidth - 500) / 2000.0;

        // Set Blinkin signal.
        blinkin.setPosition(servoOutput);

        // Display debugging information.
        telemetry.addData("***/", "Use A/B to go down/up slowly");
        telemetry.addData("***\\", "Use X/Y to go down/up quickly");
        telemetry.addData("Current Value", servoOutput);
        telemetry.addData("Pulse Width (REV)", "approx. " + REVPulseWidth);
        telemetry.addData("Pulse Width (MR)", "approx. " + MRPulseWidth);
    }
}
