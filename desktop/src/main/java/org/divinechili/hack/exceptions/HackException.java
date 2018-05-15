package org.divinechili.hack.exceptions;

public class HackException extends Exception {

    public HackException(String msg, Throwable clause) {
        super(msg, clause);
    }

    public HackException(String msg) {
        super(msg);
    }
    public HackException() {
        super("No message");
    }
}