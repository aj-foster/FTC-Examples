package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Example of setting a Blinkin pattern
 *
 * Use the Blinkin Explorer class to find the servo value to use for your favorite pattern on your
 * hardware. There may be a range of values that work, and it's a good idea to choose the midpoint
 * of the range.
 *
 * @author AJ Foster
 * @version 1.0.0
 */
@TeleOp(name = "Blinkin Example", group = "Examples")
public class BlinkinBasicExample extends OpMode {

    // Hardware variable
    Servo blinkin;

    // Servo value for the Blinkin pattern we want to use. Use the Blinkin Explorer to find this
    // value for your pattern and hardware.
    private final double BLINKIN_PATTERN = 0.5;

    @Override
    public void init() {
        // Initialize Blinkin module.
        blinkin = hardwareMap.servo.get("blinkin"); // Change to your configured name!
    }

    @Override
    public void start() {
        // Set Blinkin pattern.
        blinkin.setPosition(BLINKIN_PATTERN);
    }

    @Override
    public void loop() {
        // Your code here.
    }
}
