import javafx.fxml.FXMLLoader;
import javafx.scene.text.Text;
import org.divinechili.hack.gui.GuiFactory;
import org.divinechili.hack.plugin.IGameController;

import java.io.IOException;

public class MarioGebra implements IGameController {
    @Override
    public String getName() {
        return "Mario Gebra";
    }

    @Override
    public String getAuthor() {
        return "DivineChili & HenrikK";
    }

    @Override
    public void init() {
        System.out.println("MarioGebra is starting!");
    }

    @Override
    public void shutdown() {
        System.out.println("Shutting down MarioGebra");
    }

    @Override
    public GuiFactory getGUI() {
        return () -> {
            try {
                return FXMLLoader.load(getClass().getResource("mariogebragui.fxml"));
            } catch(IOException e) {
                return new Text("Gui failed to load");
            }
        };
    }

    @Override
    public boolean hasError() {
        return false;
    }
}
