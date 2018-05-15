package org.divinechili.hack.exceptions;

public class PluginException extends HackException {
    public PluginException(String msg, Throwable clause) {
        super(msg, clause);
    }

    public PluginException(String msg) {
        super(msg);
    }
    public PluginException() {
        super("No message");
    }
}
