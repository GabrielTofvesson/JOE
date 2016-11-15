package com.tofvesson.joe;

@SuppressWarnings({"unused", "WeakerAccess"})
public class NotALanguageFileException extends Exception {
    public NotALanguageFileException() { super(); }
    public NotALanguageFileException(String message) { super(message); }
    public NotALanguageFileException(String message, Throwable cause) { super(message, cause); }
    public NotALanguageFileException(Throwable cause) { super(cause); }
    public NotALanguageFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) { super(message, cause, enableSuppression, writableStackTrace); }
}
