package com.arya.crypto.example;

import com.arya.crypto.util.CryptoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.LinkedHashMap;

public class GetExample {

    private static final String secretKey = "secretkey";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final OkHttpClient client = new OkHttpClient();

    public static final ObjectMapper mapper = new ObjectMapper();

    static Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        return client.newCall(request).execute();
    }

    private static String getParam() {
        LinkedHashMap<String, String> paramsMap = new LinkedHashMap<>();
        paramsMap.put("id", "123456");

        String encryptedParams = null;
        try {
            String requestParams = mapper.writeValueAsString(paramsMap);
            encryptedParams = CryptoUtils.encrypt(requestParams, secretKey);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        return encryptedParams;
    }

    public static void main(String[] args) throws IOException {
        StringBuffer sb = new StringBuffer("http://localhost:8080/crypto/get-params");
        sb.append("?data=");
        sb.append(getParam());

        Response response = get(sb.toString());

        // 基于返回结构进行简单处理
        JsonNode jsonNode = mapper.readTree(response.body().string());
        String decodeResponseBody = CryptoUtils.decode(jsonNode.get("data").textValue(), secretKey);

        System.out.println(decodeResponseBody);
    }
}
