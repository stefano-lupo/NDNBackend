package com.stefanolupo.ndngame.exceptions;

public class NdnException extends Exception {
    public NdnException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
