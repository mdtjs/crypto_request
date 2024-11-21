package com.arya.crypto.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(RequestWrapper.class);

    private String body;
    private HttpServletRequest request;
    private Map<String, String[]> parameterMap = new HashMap<>();

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.request = request;

        log.info("Do request wrapper");

        String contentType = request.getContentType();
        if (contentType != null && contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            // todo 文件类型处理
            parseMultipartRequest(request);
        } else {
            // 复制请求体到body中
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                IOUtils.copy(request.getInputStream(), baos);
                body = baos.toString();
                if (body == null || body.isEmpty()) {
                    // 从parameterMap中获取参数
                    Map<String, String[]> params = request.getParameterMap();
                    parameterMap.putAll(params);
                }
            } catch (IOException e) {
                log.error("Failed to copy requestBody, cause: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }

    private void parseMultipartRequest(HttpServletRequest request) {

    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public String getParameter(String name) {
        String[] values = parameterMap.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<>(super.getParameterMap());
        for (String key : this.parameterMap.keySet()) {
            map.put(key, this.parameterMap.get(key));
//            setAttribute(key, this.parameterMap.get(key));
        }
        return Collections.unmodifiableMap(map);
    }

    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }

    public void addAllParameters(Map<String, String[]> otherParams) {
        for (Map.Entry<String, String[]> entry : otherParams.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
    }

    public void addParameter(String name, Object value) {
        if (value != null) {
            if (value instanceof String[]) {
                parameterMap.put(name, (String[]) value);
            } else if (value instanceof String) {
                parameterMap.put(name, new String[]{(String) value});
            } else {
                parameterMap.put(name, new String[]{String.valueOf(value)});
            }
        }
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
