package com.arya.crypto.base;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
public class BaseController {

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    private CryptoResponse handleIOException(Throwable e) {
        return CryptoResponse.error(500, e.getMessage());
    }
}
