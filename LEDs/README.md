# LED Classes and Examples

Teams have several options when it comes to adding LEDs to their robots:

* **Novice**: A non-programmable strip of LED lights. Use this to have a nice, constant glow — perhaps in your team colors.
* **Beginner**: [REV Robotics Blinkin](http://www.revrobotics.com/rev-11-1105/) with the REV 12V LED strip. Every light in the strip will have the same color, but we can still use this to do some cool things.
* **Intermediate**: [REV Robotics Blinkin](http://www.revrobotics.com/rev-11-1105/) with the REV 5V LED strip. With this strip, each individual LED can have a different color. REV has a ton of pre-made patterns that look great.
* **Advanced**: [DotStar LEDs](https://www.adafruit.com/product/2238?length=1) driven using two digital outputs. By correctly flipping the digial output pins on and off, you can ask each of the LEDs in the DotStar array to do whatever you'd like. Ditial I/O is slow, so also consider:
* **Awesome**: [DotStar LEDs](https://www.adafruit.com/product/2238?length=1) driven using an [I2C/SPI bridge](https://sandboxelectronics.com/?product=sc18is602-i2c-to-spi-bridge-module) (legal since the 2018-2019 season). Using this extra bridge, you can still ask each LED in the array to do whatever you'd like, but **fast**. Use this method to get smooth custom color transitions.

Non-programmable LEDs require power and nothing else. If the REV Blinkin module has the preset patterns you want, you can select the pattern using a servo (or other PWM) output. For full control, you can drive DotStar LEDs using two digital outputs or an I2C port together with the I2C/SPI bridge.

In this directory you'll find examples that use each method as well as helper classes for the more advanced techniques. Helper classes can be copied and pasted into your TeamCode package as-is, whereas you'll want to study the examples and modify them to your needs.
