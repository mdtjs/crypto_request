package com.arya.crypto.util;

import java.util.Base64;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
public class CryptoUtils {

    /**
     * 加密 编码采用url安全的withoutPadding模式
     *
     * @param data 待加密字符串
     * @param key  密钥
     * @return 加密数据
     */
    public static String encrypt(String data, String key) {
        if (data == null || key == null) {
            return null;
        }

        byte[] b_data = data.getBytes();
        byte[] dataBytes = RC4Utils.RC4Base(b_data, key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(dataBytes);
    }

    /**
     * 解密
     *
     * @param data 待解码字符串
     * @param key  密钥
     * @return 解密数据
     */
    public static String decode(String data, String key) {
        if (data == null || key == null) {
            return null;
        }

        byte[] decodeBytes = Base64.getUrlDecoder().decode(data.getBytes());
        return new String(RC4Utils.RC4Base(decodeBytes, key));
    }
}
