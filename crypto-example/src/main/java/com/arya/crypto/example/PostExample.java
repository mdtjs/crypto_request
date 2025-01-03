package com.arya.crypto.example;

import com.arya.crypto.model.User;
import com.arya.crypto.util.CryptoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
public class PostExample {

    private static final String secretKey = "secretkey";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final OkHttpClient client = new OkHttpClient();
    public static final ObjectMapper mapper = new ObjectMapper();

    static Response post(String url, String json) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, json))
                .build();

        return client.newCall(request).execute();
    }

    static String getUserJson() {
        User user = new User();
        user.setName("user2");
        user.setIdCard("147258");
        user.setTimestamp(System.currentTimeMillis());

        String encryptedBody = null;
        try {
            String userString = mapper.writeValueAsString(user);
            encryptedBody = CryptoUtils.encrypt(userString, secretKey);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        return encryptedBody;
    }

    public static void main(String[] args) throws IOException {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("data", getUserJson());
        Response response = post("http://localhost:8080/crypto/encrypted-body", mapper.writeValueAsString(bodyMap));

        // 基于返回结构进行简单处理
        JsonNode jsonNode = mapper.readTree(response.body().string());
        String decodeResponseBody = CryptoUtils.decode(jsonNode.get("data").textValue(), secretKey);

        System.out.println(decodeResponseBody);
    }
}
