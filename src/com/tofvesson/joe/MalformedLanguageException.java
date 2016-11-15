package com.tofvesson.joe;

public class MalformedLanguageException extends Exception {
    public MalformedLanguageException() {
        super();
    }

    public MalformedLanguageException(String message) {
        super(message);
    }

    public MalformedLanguageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedLanguageException(Throwable cause) {
        super(cause);
    }

    public MalformedLanguageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
