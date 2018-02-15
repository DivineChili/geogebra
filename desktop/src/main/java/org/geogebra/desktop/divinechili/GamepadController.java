package org.geogebra.desktop.divinechili;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class GamepadController implements Runnable
{
    private static ControllerManager controllers = null;

    @Override
    public void run() {
        controllers = new ControllerManager();
        controllers.initSDLGamepad();

        while(true) {
            ControllerState currState = controllers.getState(0);

            if(!currState.isConnected) {
                System.out.println("Controller is not connected!");
                break;
            }
        }
    }
}
