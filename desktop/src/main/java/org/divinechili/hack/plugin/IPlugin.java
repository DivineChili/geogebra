package org.divinechili.hack.plugin;

import org.divinechili.hack.gui.GuiFactory;

public interface IPlugin {
    String getPluginName();

    String getPluginAuthor();

    GuiFactory getGuiFactory();

    Boolean hasError();
}
