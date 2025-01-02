package com.arya.crypto.util;

import org.junit.jupiter.api.Test;

class CryptoUtilsTest {

    @Test
    void decode() {
        String secretKey = "secretkey";
        String data = "{\"name\":\"user2\",\"idCard\":\"147258\",\"timestamp\":1735813965426}";
        String encrypt = CryptoUtils.encrypt(data, secretKey);
        System.out.println(encrypt);

        String decode = CryptoUtils.decode(encrypt, secretKey);
        System.out.println(decode);
    }
}