package com.arya.crypto.base;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
@Data
@JsonPropertyOrder({"code", "message", "data"})
public class CryptoResponse implements Serializable {

    private int code;

    private String message;

    private Object data;

    public CryptoResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static CryptoResponse success() {
        return new CryptoResponse(200, "success", null);

    }

    public static CryptoResponse success(Object data) {
        return new CryptoResponse(200, "success", data);
    }

    public static CryptoResponse error(int error, Throwable throwable) {
        return new CryptoResponse(error, throwable.getMessage(), null);
    }

    public static CryptoResponse error(int error, String message) {
        return new CryptoResponse(error, message, null);
    }
}
