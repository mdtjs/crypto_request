package com.arya.crypto.advice;

import com.arya.crypto.annotation.Encrypt;
import com.arya.crypto.util.CryptoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collections;

@RestControllerAdvice
public class ResponseBodyEncodeAdvice implements ResponseBodyAdvice<Object> {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(Encrypt.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }

        String encryptedResponse = null;
        try {
            String responseBodyString = mapper.writeValueAsString(body);
            encryptedResponse = CryptoUtils.encrypt(responseBodyString, "secretkey");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        return Collections.singletonMap("data", encryptedResponse);
    }
}
