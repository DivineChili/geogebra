package org.divinechili.hack.plugin;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class PluginPane extends TitledPane {
    public PluginPane() {
        super();
        this.setExpanded(false);
    }
    public PluginPane(String name) {
        super(name, null);
        this.setExpanded(false);
    }
    public PluginPane(String name, Node content) {
        super(name, content);
        this.setExpanded(false);
    }
}
