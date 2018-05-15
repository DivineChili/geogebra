package org.divinechili.hack.plugin;

import org.divinechili.hack.gui.GuiFactory;

public interface IGameController {

    String getName();
    String getAuthor();
    void init();
    GuiFactory getGUI();
    void shutdown();
    boolean hasError();
}
