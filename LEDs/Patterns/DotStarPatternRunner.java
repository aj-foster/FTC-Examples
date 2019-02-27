package org.firstinspires.ftc.teamcode;

/**
 * This class is used to run the patterns in a seperate thread so as not to
 * effect the main robot program.
 *
 * DotStar LEDs (i.e. https://www.adafruit.com/product/2238) are collections of LEDs which are
 * programmable using SPI. While it is possible to use two digital outputs as data and clock lines,
 * an I2C/SPI Bridge can manage the digital writes at a much higher frequency. This is required for
 * "smooth" color changes. However, a similar class for driving the LEDs via two digital outputs
 * is also available.
 *
 * @author Rick Van Smith
 * @version 1.0.0
 */
import android.graphics.Color;

public class DotStarPatternRunner {
	private boolean stopPattern = false;
    private boolean patternStopped = true;
    private boolean threadDie = false;
	private IDotStarPattern newPattern = null;
    private IDotStarPattern runningPattern = null;
	private Thread patternThread = null;
	private Object syncObject = new Object();

    /**
     * Takes a pattern and allows it to be executed.
     * @param newPattern IDotStarPattern pattern defined to execute in a thread.
     */
	public void setPattern(IDotStarPattern newPattern) {
	    this.newPattern = newPattern;
    }

    /**
     * Starts the pattern in a seperate thread. If the thread doesn't exist,
     * it will be created.
     */
    public void startPattern() {
	    stopPattern();

	    synchronized(syncObject) {
	        runningPattern = newPattern;
	        stopPattern = false;
            if(patternThread == null) {
                patternThread = new Thread() {
                    public void run() {
                        while (!threadDie) {
                            if (!stopPattern && runningPattern != null) {
                                patternStopped = false;
                                if (runningPattern.isStaticPattern()) {
                                    runningPattern.update();
                                    patternStopped = true;
                                } else {
                                    while (!stopPattern) {
                                        runningPattern.update();
                                    }
                                    patternStopped = true;
                                }
                            }
                            synchronized (syncObject) {
                                try {
                                    syncObject.wait();
                                } catch (InterruptedException ex) {
                                    threadDie = true;
                                    stopPattern = true;
                                }
                            }
                        }
                    }
                };
                patternThread.start();
            } else {
	            syncObject.notify();
            }
        }
    }

    /**
     * Stops the pattern and waits for a signal to start running a pattern again.
     */
    public void stopPattern() {
        stopPattern = true;
    }


    /**
     * This terminates the pattern running thread.
     */
    public void terminate()
    {
        synchronized(syncObject) {
            threadDie = true;
            stopPattern = true;
            syncObject.notify();
        }
    }
}