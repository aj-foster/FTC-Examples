package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

/**
 * Blinkin-based battery meter
 *
 * This OpMode uses the internal battery voltage meter on the REV Expansion Hub to display a color
 * battery indicator on LEDs with the Blinkin module. This program works with 5V and 12V LEDs.
 *
 * You can use the Blinkin Explorer to find the correct values of the COLORS (red to dark green).
 * Choose a servo value in the center of each color's range, and arrange the colors in the array
 * from red to dark green.
 * 
 * @author AJ Foster
 * @version 1.0.0
 */
@TeleOp(name = "Blinkin Battery Meter", group = "Examples")
public class BlinkinBatteryMeter extends OpMode {

    // Servo values for each color, arranged Red -> Dark Green.
    // Change to match your values using the Blinkin Explorer!
    private final double[] COLORS = {0.6705, 0.676, 0.6815, 0.687, 0.6925, 0.6985, 0.704, 0.7095};

    // Blinkin module and battery sensor variables.
    Servo blinkin;
    VoltageSensor battery;

    // Min and max voltages used for choosing colors. Anything near or below the MIN will be red,
    // and everything near or above the MAX will be green.
    private static final double MIN_VOLTAGE = 11.0;
    private static final double MAX_VOLTAGE = 13.5;

    @Override
    public void init() {
        // On a REV Expansion Hub, the name of the sensor is the name of the hub.
        battery = hardwareMap.voltageSensor.get("Expansion Hub 1"); // Change to match your hub!

        // Set up blinkin module.
        blinkin = hardwareMap.servo.get("blinkin"); // Change to match your configured name!
    }

    @Override
    public void loop() {
        // Ask for the current battery voltage.
        double voltage = battery.getVoltage();

        // Choose the color closest to the current voltage.
        double color = getBatteryVoltageColor(voltage);

        // Set the blinkin strip.
        blinkin.setPosition(color);
    }

    /**
     * Choose a color setting from the COLORS array for the given battery voltage.
     *
     * This method uses MIN_VOLTAGE and MAX_VOLTAGE to decide which servo power, as given by the
     * COLORS array, to display for the given voltage. It divides the MIN-MAX range into buckets
     * based on the number of colors available. Voltages near the MIN use the early colors, and
     * voltages near the MAX use later colors.
     *
     * @param voltage The battery voltage (decimal) to indicate.
     * @return Servo power, as given by COLORS, to display.
     */
    private double getBatteryVoltageColor(double voltage) {
        double difference = MAX_VOLTAGE - MIN_VOLTAGE;
        double bucketSize = difference / COLORS.length;

        // If voltage is below the minimum, use the first color.
        if (voltage - MIN_VOLTAGE <= 0) {
            return COLORS[0];
        }

        // If voltage is above the maximum, use the last color.
        else if (MAX_VOLTAGE - voltage <= 0) {
            return COLORS[COLORS.length - 1];
        }

        // If voltage is in between min/max, select the color closest to its position.
        else {
            int bucket = (int) Math.floor((voltage - MIN_VOLTAGE) / bucketSize);
            return COLORS[bucket];
        }
    }
}
