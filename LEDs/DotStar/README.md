# DotStar LED Classes and Examples

This repository provides a few ways to integrate with DotStar LEDs:

1. [DotStar LEDs](https://www.adafruit.com/product/2238?length=1) driven using an [I2C/SPI bridge](https://sandboxelectronics.com/?product=sc18is602-i2c-to-spi-bridge-module)
2. [DotStar LEDs](https://www.adafruit.com/product/2238?length=1) driven using two digital outputs

and also

1. Direct control of the LEDs
2. Control via pattern classes

### I2C/SPI Bridge

If you plan to use the I2C/SPI bridge, you'll want to copy the `DotStarBridgedLED` class to your code.
This is what you will call when working with LEDs.
After configuring a `DotStarBridgedLED` I2C device (download the new class to your robot to see the option in the list of I2C devices) you can use it like this:

```java
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "My Program", group = "")
public class MyProgram extends OpMode {
    DotStarBridgedLED leds;

    @Override
    public void init() {
        leds = hardwareMap.get(DotStarBridgedLED.class, "leds");
        leds.setLength(30);
        
        for (int i = 0; i < leds.pixels.length; i++) {
          leds.setPixel(i, 255, 0, 0)
        }

        leds.update();
    }

    // ...
}
```

### Digital IO

If instead you plan to use digital IO pins, you'll want to copy the `DotStarLED` class to your code.
For this class, you'll need to configure two digital IO pins: one for `clock`, one for `data`.
Then use them like this:

```java
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "My Program", group = "")
public class MyProgram extends OpMode {
    DotStarLED leds;

    @Override
    public void init() {
        DigitalChannel clock = hardwareMap.digitalChannel.get("clock");
        DigitalChannel data = hardwareMap.digitalChannel.get("data");
        leds = new DotStarLED(30, clock, data);
        
        for (int i = 0; i < leds.pixels.length; i++) {
          leds.setPixel(i, 255, 0, 0)
        }

        leds.update();
    }

    // ...
}
```

`DotStarBatteryMeter`, `DotStarColorMatcher`, and `DotStarRainbow` provide examples of controlling the LEDs directly.

### Pattern Classes

In the `Patterns` directory there are several classes and an interface.
Copy the `DotStarPattern` class and `IDotStarPattern` interface, along with any `DSPattern...` classes you would like to use.
The `DotStarPatternTester` OpMode provides an example of using the patterns.

### Additional Notes

Driving LEDs can be an expensive task.
It takes time and hardware resources that might be better spent on driving around.
It also takes power, and will drain your robot's battery.

Be sure to read the comments of the various classes you choose to use.
There are important warnings about electrical current and I2C buffer sizes.
