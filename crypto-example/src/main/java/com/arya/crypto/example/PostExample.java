package com.arya.crypto.example;

import com.arya.crypto.model.User;
import com.arya.crypto.util.CryptoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
public class PostExample {

    private static final String APPLICATION_JSON_VALUE = "application/json";

    private static final String secretKey = "secretkey";

    public static void main(String[] args) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();

        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        user.setName("user2");
        user.setIdCard("147258");
        user.setTimestamp(System.currentTimeMillis());

        String encryptedUser = null;
        try {
            String userString = mapper.writeValueAsString(user);
            encryptedUser = CryptoUtil.encryptRC4String(userString, secretKey);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assert encryptedUser != null;
        HashMap<String, String> map = new HashMap<>();
        map.put("data", encryptedUser);

        String data = mapper.writeValueAsString(map);

        RequestBody requestBody = RequestBody.create(data, MediaType.parse(APPLICATION_JSON_VALUE));
        Request request = new Request.Builder()
                .url("http://localhost:8080/crypto/encrypted-body")
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
    }
}
