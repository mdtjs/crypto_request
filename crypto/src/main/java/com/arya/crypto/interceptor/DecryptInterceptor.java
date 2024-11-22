package com.arya.crypto.interceptor;

import com.arya.crypto.annotation.Decrypt;
import com.arya.crypto.util.HttpCode;
import com.arya.crypto.exception.CryptoException;
import com.arya.crypto.filter.RequestWrapper;
import com.arya.crypto.util.CryptoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
@WebFilter("/")
public class DecryptInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(DecryptInterceptor.class);

    private Map<String, String[]> params = new HashMap<>();
    private static final String requestBodyKey = "data";
    // todo 实现本地缓存密钥
    private static final String secretKey = "secretkey";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (handler instanceof HandlerMethod) {
                HandlerMethod method = (HandlerMethod) handler;

                String requestBody = null;

                if (method.hasMethodAnnotation(Decrypt.class)) {
                    String contentType = request.getContentType();
                    if (contentType == null && !"GET".equals(request.getMethod())) {
                        throw new CryptoException(HttpCode.BAD_REQUEST, "Bad Request");
                    }

                    // 解密并重新封装map
                    if ((contentType != null && StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_FORM_URLENCODED_VALUE)) || "GET".equals(request.getMethod())) {
                        ServletInputStream inputStream = request.getInputStream();
                        String dataString = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                        if (!StringUtils.hasLength(dataString)) {
                            Map<String, String[]> parameterMap = request.getParameterMap();
                            String data = String.join(",", parameterMap.get("data"));
                            params = decryptAndParseStringToMap(data);
                        } else {
                            String bodyString = dataString.split("=")[1];
                            params = decryptAndParseStringToMap(bodyString);
                            requestBody = parseMapToJson(params);
                        }
                    }

                    // 解密并重新封装body
                    if ((contentType != null && StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_JSON_VALUE))) {
                        ServletInputStream inputStream = request.getInputStream();
                        String dataJson = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                        requestBody = decryptAndParseStringToJson(dataJson);
                    }

                    if (request instanceof RequestWrapper) {
                        RequestWrapper requestWrapper = (RequestWrapper) request;
                        requestWrapper.setBody(requestBody);
                        requestWrapper.addAllParameters(params);
                        return true;
                    }

                    return true;
                } else {
                    // 封装无需加密的请求
                    ServletInputStream inputStream = request.getInputStream();
                    String bodyString = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                    if (!StringUtils.hasLength(bodyString)) {
                        requestBody = bodyString;
                    } else {
                        if (request instanceof RequestWrapper) {
                            RequestWrapper requestWrapper = (RequestWrapper) request;
                            params = parseStringToMap(bodyString);
                            requestWrapper.setBody(bodyString);
                            requestWrapper.addAllParameters(params);
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage() + ", Error URL is: " + request.getServletPath());
            throw new CryptoException(HttpCode.BAD_REQUEST, "请求失败");
        }
        return true;
    }

    /**
     * application/x-www-form-urlencoded 解析加密参数
     */
    private Map<String, String[]> decryptAndParseStringToMap(String dataString) {
        Map<String, String[]> parameterMap = new HashMap<>();
        try {
            String dataJson = CryptoUtil.decryptRC4String(dataString, secretKey);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(dataJson);
            Iterator<String> fieldNames = jsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String name = fieldNames.next();
                String[] value = jsonNode.get(name).asText().trim().split(",");
                parameterMap.put(name, value);
            }
        } catch (Exception e) {
            log.error("Json cannot be case to map, cause: {}", e.getMessage());
        }
        return parameterMap;
    }

    private String parseMapToJson(Map<String, String[]> bodyMap) {
        String bodyString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            bodyString = mapper.writeValueAsString(bodyMap);
        } catch (JsonProcessingException e) {
            log.error(HttpCode.JSON_PARSE_ERROR, "JSON parse error", e);
        }
        return bodyString;
    }

    /**
     * application/x-www-form-urlencoded 解析参数
     */
    private Map<String, String[]> parseStringToMap(String dataString) {
        Map<String, String[]> parameterMap = new HashMap<>();
        try {
            String[] parameterPairs = dataString.split("&");
            for (String parameterPair : parameterPairs) {
                String[] kv = parameterPair.split("=");
                String name = kv[0];
                String[] value = kv[1].trim().split(",");
                parameterMap.put(name, value);
            }
        } catch (Exception e) {
            log.error("Json cannot be case to map, cause: {}", e.getMessage());
        }
        return parameterMap;
    }

    /**
     * application/json 解析加密参数
     */
    private String decryptAndParseStringToJson(String dataJson) {
        String bodyJson = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataNode = mapper.readTree(dataJson);
            bodyJson = CryptoUtil.decryptRC4String(dataNode.get(requestBodyKey).asText(), secretKey);
        } catch (Exception e) {
            log.error("Json cannot be case to map, cause: {}", e.getMessage());
        }
        return bodyJson;
    }
}
