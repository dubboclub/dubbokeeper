package com.dubboclub.dk.web.exception;

/**
 * Created by bieber on 2015/6/15.
 */
public class DataHadChangedException extends IllegalStateException {

    public DataHadChangedException() {
    }

    public DataHadChangedException(String s) {
        super(s);
    }

    public DataHadChangedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataHadChangedException(Throwable cause) {
        super(cause);
    }
}
