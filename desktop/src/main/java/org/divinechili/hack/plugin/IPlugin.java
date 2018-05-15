package org.divinechili.hack.plugin;

import org.divinechili.hack.gui.GuiFactory;

public interface IPlugin {
    String getPluginName();

    String getPluginAuthor();

    void preInitialization();
    void postInitialization();
    void cleanUp();

    GuiFactory getGuiFactory();

    boolean hasError();
}
