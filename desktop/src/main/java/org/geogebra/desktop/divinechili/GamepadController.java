package org.geogebra.desktop.divinechili;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class GamepadController implements Runnable
{
    private static final float tolerance = 0.13f;

    @Override
    public void run() {
        ControllerManager controllers = new ControllerManager();
        controllers.initSDLGamepad();
        while (true) {
            while (true) {
                ControllerState currState = controllers.getState(0);

                if (!currState.isConnected) {
                    System.out.println("Controller is not connected!");
                    break;
                }

                // Axis-events
                // Left Joystick is outside of idle zone
                if (currState.leftStickY > tolerance || currState.leftStickY < -tolerance ||
                        currState.leftStickX > tolerance || currState.leftStickX < -tolerance) {
                    System.out.println("Left  Joystick { X: " + currState.leftStickX + "\tY: " + currState.leftStickY);
                    // TODO: Add event dispatcher for listeners here
                }
                // Right Joystick is outside of idle zone
                if (currState.rightStickY > tolerance || currState.rightStickY < -tolerance ||
                        currState.rightStickX > tolerance || currState.rightStickX < -tolerance) {
                    System.out.println("Right Joystick { X: " + currState.rightStickX + "\tY: " + currState.rightStickY);
                    // TODO: Add event dispatcher for listeners here
                }


                // Button-events
                // Right

            }
            while (true) {
                controllers = new ControllerManager();
                controllers.initSDLGamepad();
                if (controllers.getNumControllers() > 0) {
                    System.out.println("Controller detected!");
                    break;
                } else {
                    System.out.println("No controller detected...");
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
