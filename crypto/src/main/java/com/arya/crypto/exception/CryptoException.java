package com.arya.crypto.exception;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
public class CryptoException extends Exception {

    private String code;
    private String message;

    public CryptoException() {

    }

    public CryptoException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
