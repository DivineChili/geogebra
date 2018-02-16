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
                    try { Thread.sleep(200); }
                    catch (InterruptedException e) { e.printStackTrace(); }
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
                // ABXY
                if (currState.a) {
                    System.out.println("Button A pressed");
                }
                if (currState.b) {
                    System.out.println("Button B pressed");
                }
                if (currState.x) {
                    System.out.println("Button X pressed");
                }
                if (currState.y) {
                    System.out.println("Button Y pressed");
                }

                // D-Pad
                float dpadX = 0.0f;
                float dpadY = 0.0f;
                if (currState.dpadUp) {
                    dpadX++;
                }
                if (currState.dpadDown) {
                    dpadX--;
                }
                if (currState.dpadLeft) {
                    dpadY++;
                }
                if (currState.dpadRight) {
                    dpadY--;
                }

                if (dpadX != 0.0f) System.out.println("D-pad X: "+ dpadX);
                if (dpadY != 0.0f) System.out.println("D-pad Y: "+ dpadY);

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
