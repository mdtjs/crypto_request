package com.arya.crypto.example;

import com.arya.crypto.model.User;
import com.arya.crypto.util.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
public class PostExample {

    private static final String secretKey = "secretkey";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public final OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    String getUserJson() throws IOException {
        return null;
    }

    public static void main(String[] args) throws IOException {
        PostExample postExample = new PostExample();

        User user = new User();
        user.setName("user2");
        user.setIdCard("147258");
        user.setTimestamp(System.currentTimeMillis());

        String encryptedUser = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            String userString = mapper.writeValueAsString(user);
            encryptedUser = CryptoUtil.encryptRC4String(userString, secretKey);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("data", encryptedUser);

        String data = mapper.writeValueAsString(bodyMap);
        String responseBody = postExample.post("http://localhost:8080/crypto/encrypted-body", data);
        System.out.println(responseBody);
    }
}
