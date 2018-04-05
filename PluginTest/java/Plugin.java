import javafx.scene.Node;
import javafx.scene.text.Text;
import org.divinechili.hack.gui.GuiFactory;
import org.divinechili.hack.plugin.IPlugin;

public class Plugin implements IPlugin{
    @Override
    public String getPluginName() {
        return "Test Plugin";
    }

    @Override
    public String getPluginAuthor() {
        return "DivineChili";
    }

    @Override
    public GuiFactory getGuiFactory() {
        return new GuiFactory() {
            @Override
            public Node constructGui() {
                return new Text("Hello World!\nThis is a test plugin!");
            }
        };
    }

    @Override
    public Boolean hasError() {
        return false;
    }
}
