package com.lge.sureparkmanager.exceptions;

@SuppressWarnings("serial")
public class CommandParserException extends RuntimeException {
    public CommandParserException(String message) {
        super(message);
    }
}