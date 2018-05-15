package org.divinechili.hack.exceptions;

public class IllegalGameLoadException extends PluginException {
    public IllegalGameLoadException(String msg, Throwable clause) {
        super(msg, clause);
    }

    public IllegalGameLoadException(String msg) {
        super(msg);
    }
    public IllegalGameLoadException() {
        super("No message");
    }
}
