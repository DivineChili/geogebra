import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.divinechili.hack.GamepadThread;
import org.divinechili.hack.Hack;
import org.divinechili.hack.gui.GuiFactory;
import org.divinechili.hack.plugin.IPlugin;

public class GamepadSupportPlugin implements IPlugin {

    public static GamepadThread gamepadThread = null;

    @Override
    public String getPluginName() {
        return "Controller!";
    }

    @Override
    public String getPluginAuthor() {
        return "DivineChili & HenrikK";
    }

    @Override
    public void preInitialization() {
    }

    @Override
    public void postInitialization() {

        // Start Gamepad Thread!
        try {
            gamepadThread = new GamepadThread(Hack.appRef, Hack.ProgramWrapper);
            gamepadThread.start();
            Hack.gamepadThread = gamepadThread;

            System.out.println("Gamepad Thread started successfully!");
        }catch (Exception e) {
            System.out.println("Something went wrong with gamepad init!");
            e.printStackTrace();
        }
    }

    @Override
    public void cleanUp() {
        GamepadSupportPlugin.gamepadThread.shutdown();
    }

    @Override
    public GuiFactory getGuiFactory() {
        return () -> {
            CheckBox bDebugToggle = new CheckBox("Console Debug");
            bDebugToggle.selectedProperty().addListener((event, newValue, oldvalue) -> {
                GamepadThread.bDebug = !newValue;
                gamepadThread.shutdown();
                gamepadThread = new GamepadThread(Hack.appRef, Hack.ProgramWrapper);
                gamepadThread.start();
                Hack.gamepadThread = gamepadThread;
            });
            return bDebugToggle;
        };
    }

    @Override
    public boolean hasError() {
        return false;
    }
}
