package org.divinechili.hack;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import javafx.stage.Stage;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.plugin.ScriptManagerD;

public class GamepadThread extends Thread
{
    public static float tolerance = 0.13f;
    public static boolean bDebug = false;

    AppD app;
    Stage stage;
    private ControllerManager controllers;

    public GamepadThread(AppD app, Stage stage) {
        super();
        this.app = app;
        this.stage = stage;
        this.controllers = new ControllerManager();

        Hack.gamepadThread = this;
    }

    public void shutdown() {
        controllers.quitSDLGamepad();
    }

    @Override
    public void run() {
        ScriptManagerD jsManager = new ScriptManagerD(app);
        controllers.initSDLGamepad();
        ControllerState currState = controllers.getState(0);
        ControllerState prevState = currState;

        String[] a = new String[0]; //Arguments passed to JS function

        while (true) {
            while (true) {
                if(Hack.bTick.get())
                    jsManager.callJavaScript("tick", a);

                currState = controllers.getState(0);

                if (!currState.isConnected) {
                    System.out.println("Controller is not connected!");
                    Hack.bControllerConnected.setValue(false);
                    try { Thread.sleep(200); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                    break;
                }

                // Axis-events
                // Left Joystick is outside of idle zone
                if (currState.leftStickY > tolerance || currState.leftStickY < -tolerance ||
                        currState.leftStickX > tolerance || currState.leftStickX < -tolerance) {
                    if(bDebug) System.out.println("Left  Joystick { X: " + currState.leftStickX + "\tY: " + currState.leftStickY);
                    // TODO: Add event dispatcher for listeners here
                }
                // Right Joystick is outside of idle zone
                if (currState.rightStickY > tolerance || currState.rightStickY < -tolerance ||
                        currState.rightStickX > tolerance || currState.rightStickX < -tolerance) {
                    if(bDebug) System.out.println("Right Joystick { X: " + currState.rightStickX + "\tY: " + currState.rightStickY);
                    // TODO: Add event dispatcher for listeners here
                }


                // Button-events
                // ABXY
                if (currState.a && !prevState.a) {
                    if(bDebug) System.out.println("Button A pressed");
                    jsManager.callJavaScript("aDown", a);
                } else if (!currState.a && prevState.a) {
                    if(bDebug) System.out.println("Button A released");
                    jsManager.callJavaScript("aUp", a);
                }
                if (currState.b && !prevState.b) {
                    if(bDebug) System.out.println("Button B pressed");
                    jsManager.callJavaScript("bDown", a);
                } else if (!currState.b && prevState.b) {
                    if(bDebug) System.out.println("Button B released");
                    jsManager.callJavaScript("bUp", a);
                }
                if (currState.x && !prevState.x) {
                    if(bDebug) System.out.println("Button X pressed");
                    jsManager.callJavaScript("xDown", a);
                } else if (!currState.x && prevState.x) {
                    if(bDebug) System.out.println("Button X released");
                    jsManager.callJavaScript("xUp", a);
                }
                if (currState.y && !prevState.y) {
                    if(bDebug) System.out.println("Button Y pressed");
                    jsManager.callJavaScript("yDown", a);
                } else if (!currState.y && prevState.y) {
                    if(bDebug) System.out.println("Button Y released");
                    jsManager.callJavaScript("yUp", a);
                }

                // D-Pad
                float dpadX = 0.0f;
                float dpadY = 0.0f;
                if (currState.dpadUp && !prevState.dpadUp) {
                    jsManager.callJavaScript("uDown", a);
                } else if (!currState.dpadUp && prevState.dpadUp) {
                    jsManager.callJavaScript("uUp", a);
                }

                if (currState.dpadDown && !prevState.dpadDown) {
                    jsManager.callJavaScript("dDown", a);
                } else if (!currState.dpadDown && prevState.dpadDown) {
                    jsManager.callJavaScript("dUp", a);
                }

                if (currState.dpadLeft && !prevState.dpadLeft) {
                    jsManager.callJavaScript("lDown", a);
                } else if (!currState.dpadLeft && prevState.dpadLeft) {
                    jsManager.callJavaScript("lUp", a);
                }

                if (currState.dpadRight && !prevState.dpadRight) {
                    jsManager.callJavaScript("rDown", a);
                } else if (!currState.dpadRight && prevState.dpadRight) {
                    jsManager.callJavaScript("rUp", a);
                }

                prevState = controllers.getState(0);
                // Update delay to reduce event spam
                try { Thread.sleep(50); }
                catch (InterruptedException e) {e.printStackTrace();};
            }
            while (true) {
                controllers = new ControllerManager();
                controllers.initSDLGamepad();
                if (controllers.getNumControllers() > 0) {
                    System.out.println("Controller detected!");
                    Hack.bControllerConnected.setValue(true);
                    break;
                } else {
                    if(bDebug) System.out.println("No controller detected...");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
