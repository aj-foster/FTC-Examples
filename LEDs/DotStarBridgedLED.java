package org.firstinspires.ftc.teamcode;

import android.support.annotation.NonNull;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDeviceWithParameters;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple;
import com.qualcomm.robotcore.hardware.I2cWaitControl;
import com.qualcomm.robotcore.hardware.configuration.I2cSensor;

import java.util.Arrays;

/**
 * Represents a DotStar LED strip when plugged in via an I2C/SPI bridge.
 *
 * DotStar LEDs (i.e. https://www.adafruit.com/product/2238) are collections of LEDs which are
 * programmable using SPI. While it is possible to use two digital outputs as data and clock lines,
 * an I2C/SPI Bridge can manage the digital writes at a much higher frequency. This is required for
 * "smooth" color changes. However, a similar class for driving the LEDs via two digital outputs
 * is also available.
 *
 * Output intensity is artificially limited by the theoretical maximum allowed by the digital IO
 * controller {@link Parameters#maxOutputAmps}. Be aware that exceeding the allowed current can
 * damage your devices. It is your responsibility to ensure this doesn't happen.
 *
 * @author AJ Foster and Mike Nicolai
 * @version 1.0.1
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@I2cSensor(name = "DotStar LEDs via SPI Bridge", description = "DotStar LED strip connected via an I2C/SPI bridge", xmlTag = "DotStarBridgedLED")
public class DotStarBridgedLED extends I2cDeviceSynchDeviceWithParameters<I2cDeviceSynchSimple, DotStarBridgedLED.Parameters> {

    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    /**
     * Array representing the individual pixel groups in the LED strip.
     *
     * Sizing can be set using {@link Parameters#length} during initialization.
     * */
    public DotStarBridgedLED.Pixel[] pixels;
    private int[] colors;


    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public DotStarBridgedLED(I2cDeviceSynch deviceClient) {
        this(new Parameters(), deviceClient, true);
    }

    public DotStarBridgedLED(DotStarBridgedLED.Parameters params, I2cDeviceSynchSimple deviceClient, boolean isOwned) {
        super(deviceClient, isOwned, params);

        // Pass logging parameters through to device client.
        this.deviceClient.setLogging(this.parameters.loggingEnabled);
        this.deviceClient.setLoggingTag(this.parameters.loggingTag);

        // Create arrays for pixels and current color values.
        this.pixels = new DotStarBridgedLED.Pixel[params.length];
        this.colors = new int[params.length * 3];

        for (int i = 0; i < params.length; i++) {
            pixels[i] = new DotStarBridgedLED.Pixel(0, 0, 0);
        }

        // We ask for an initial callback here; that will eventually call internalInitialize().
        this.registerArmingStateCallback(true);
        this.engage();
    }


    //----------------------------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------------------------

    // Can be called publicly via #initialize(Parameters).
    @Override
    protected synchronized boolean internalInitialize(@NonNull Parameters parameters) {
        this.parameters = parameters.clone();
        this.deviceClient.setI2cAddress(parameters.i2cAddr);

        return true;
    }


    //----------------------------------------------------------------------------------------------
    // Public API
    //----------------------------------------------------------------------------------------------

    /** Reset each pixel in the set to "off". */
    public void clear() {
        for (DotStarBridgedLED.Pixel pixel : pixels) {
            pixel.reset();
        }
    }

    /** Flush the current array of pixels to the device. */
    public void update() {
        // Number of bytes necessary to write out the pixels, including header and end frames.
        int bufferLength =
                4                               // Header frame: 1 word of zeroes
                        + 4 * pixels.length             // Each pixel: 1 word
                        + (pixels.length + 15) / 16;    // End frame: 1 byte for every 16 pixels.

        // This will be written out to the I2C device once filled.
        byte[] buffer = new byte[bufferLength];

        // Temporarily holds just the colors from the pixels.
        int[] colors = new int[pixels.length * 3];

        // Used to track the total expected current drawn.
        double current = 0.0;

        // Iterate the pixels to learn the total current drawn and collect the colors in order.
        for (int i = 0; i < pixels.length; i++) {
            Pixel pixel = pixels[i];
            current += pixel.current();

            // When written out to the strip, we'll need the colors in BGR order.
            colors[i * 3] = pixel.blue;
            colors[i * 3 + 1] = pixel.green;
            colors[i * 3 + 2] = pixel.red;
        }

        // Ensure the total current will not exceed our theoretical maximum.
        if (current > parameters.maxOutputAmps) {
            double scale = parameters.maxOutputAmps / current;

            // Scale (reduce) each color value (0 - 255) and round down.
            for (int i = 0; i < colors.length; i++) {
                colors[i] = (int) Math.floor(colors[i] * scale);
            }
        }

        // Skip the first four bytes (header frame).
        for (int i = 0, j = 0; i < bufferLength; i++) {

            if (i < 4) {
                buffer[i] = 0;
                continue;
            }

            // NOTE: Writing zeroes instead of 0xff reduces odd end pixels if not
            // using the entire strip. This isn't to spec, however.
            // If we have written all of our colors, fill in the end frame with 0x00.
            if (j >= colors.length) {
                buffer[i] = (byte) 0x00;
                continue;
            }

            // We still have colors to write, so write {0xff, blue, green, red}.
            if (i % 4 == 0) {
                buffer[i] = (byte) 0xff;
                continue;
            }

            // As we write colors, progress along the colors array.
            buffer[i] = (byte) colors[j];
            j++;
        }

        // Write to the LED strip.
        write(buffer);
    }


    //----------------------------------------------------------------------------------------------
    // Utility
    //----------------------------------------------------------------------------------------------

    /**
     * Writes out the given buffer to the LEDs via the I2C/SPI bridge.
     *
     * This method will attempt to write the given data in the most efficient way possible based on
     * on the parameters.i2cMaxBuffer. In the worst case, bytes will be written one at a time. The
     * destination register is determined by parameters.writeRegister. Atomic write waiting is used
     * to guarantee that writes are made successfully (though not necessarily completed by the time
     * this method returns).
     *
     * @param buffer Raw data to write out, including frame boundaries and termination bytes.
     */
    protected void write(byte[] buffer) {

        // Write the largest "chunks" possible for the I2C bus.
        if (parameters.i2cMaxBuffer > 1) {
            int left = 0, right;

            // Use left, right to track the bounds of the next chunk to write.
            while (left < buffer.length) {
                // Minus one, because the register counts as a byte.
                right = Math.min(left + parameters.i2cMaxBuffer - 1, buffer.length);

                this.deviceClient.write(
                        this.parameters.writeRegister,
                        Arrays.copyOfRange(buffer, left, right),
                        I2cWaitControl.WRITTEN
                );

                left = right;
            }
        }

        // If necessary, write one byte at a time.
        else {
            for (byte item : buffer) {
                this.deviceClient.write8(this.parameters.writeRegister, item);
                this.deviceClient.waitForWriteCompletions(I2cWaitControl.WRITTEN);
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    // HardwareDevice
    //----------------------------------------------------------------------------------------------

    @Override
    public String getDeviceName() {
        return "DotStar LED via I2C/SPI Bridge";
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Adafruit;
    }


    //----------------------------------------------------------------------------------------------
    // Parameters
    //----------------------------------------------------------------------------------------------

    /**
     * Instances of Parameters contain data indicating how the LED strip is to be initialized.
     */
    public static class Parameters implements Cloneable {

        //------------------------------------------------------------------------------------------
        // State
        //------------------------------------------------------------------------------------------

        /** The write address of the I2C/SPI bridge. (Default: 0x50) */
        public I2cAddr i2cAddr = I2cAddr.create8bit(0x50);

        /** Maximum size of the I2C buffer as determined by the hardware. (Default: 27 bytes) */
        public int i2cMaxBuffer = 27; // Default to Modern Robotics Core DIM (lowest known).

        /** Number of pixels present in the LED strip. (Default: 30) */
        public int length = 30;

        /** Whether to log the actions of this device. (Default: no) */
        public boolean loggingEnabled = false;

        /** Label to use when logging the actions of this device. (Default: DotStarBridgedLED) */
        public String loggingTag = "DotStarBridgedLED";

        /** Maximum output current (in amps) as determined by the hardware. (Default: 0.2 amps) */
        public double maxOutputAmps = 0.2; // Default to Modern Robotics Core DIM (lowest known).

        /** Bridge "register" (buffer prefix) to pass-through to the LEDs. (Default: 0x01) */
        public int writeRegister = 0x01;


        //------------------------------------------------------------------------------------------
        // Construction
        //------------------------------------------------------------------------------------------

        public Parameters() {}

        public Parameters(Controller controller) {
            switch (controller) {
                case RevExpansionHub:
                    this.i2cMaxBuffer = 100;
                    this.maxOutputAmps = 1.5;
                    break;

                default:
                    break;
            }
        }

        public Parameters clone() {
            try {
                return (Parameters) super.clone();
            }
            catch (CloneNotSupportedException e)
            {
                throw new RuntimeException("Internal error: DotStarBridgedLED.Parameters not cloneable");
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    // Pixels
    //----------------------------------------------------------------------------------------------

    /**
     * Pixels represent a single pixel group in the strip of LEDs, with red, green, and blue values.
     */
    public static class Pixel {

        //------------------------------------------------------------------------------------------
        // State
        //------------------------------------------------------------------------------------------

        /** Value of the red channel, from 0 to 255. */
        public int red;

        /** Value of the blue channel, from 0 to 255. */
        public int blue;

        /** Value of the green channel, from 0 to 255. */
        public int green;

        /** Estimate of maximum amps drawn per color. */
        public static final double ampsDrawn = 0.02;


        //------------------------------------------------------------------------------------------
        // Construction
        //------------------------------------------------------------------------------------------

        public Pixel(int red, int green, int blue) {
            this.red = bound(red);
            this.blue = bound(blue);
            this.green = bound(green);
        }


        //------------------------------------------------------------------------------------------
        // Public API
        //------------------------------------------------------------------------------------------

        /**
         * Gives an estimate of the amount of current (in amps) needed to display the pixel.
         *
         * @return Estimated current drawn (in amps).
         */
        public double current() {
            return (red + blue + green) / 255.0 * ampsDrawn;
        }


        /**
         * Resets the pixel to "off" with values (0, 0, 0).
         */
        public void reset() {
            red = 0;
            blue = 0;
            green = 0;
        }


        //------------------------------------------------------------------------------------------
        // Utility
        //------------------------------------------------------------------------------------------

        /**
         * Returns the given value, clipped to the range 0 - 255.
         *
         * @param value Color value to clip.
         * @return      Color value clipped to the nearest value in the range 0 - 255.
         */
        private int bound(int value) {
            return Math.max(0, Math.min(255, value));
        }
    }


    /**
     * Used to control settings such as the maximum I2C buffer size and maximum current available.
     */
    public enum Controller {
        ModernRoboticsDIM,
        RevExpansionHub,
        Other
    }
}
