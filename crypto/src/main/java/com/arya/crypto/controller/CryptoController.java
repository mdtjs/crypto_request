package com.arya.crypto.controller;

import com.arya.crypto.annotation.Decrypt;
import com.arya.crypto.annotation.Encrypt;
import com.arya.crypto.base.BaseController;
import com.arya.crypto.base.CryptoResponse;
import com.arya.crypto.util.HttpCode;
import com.arya.crypto.model.User;
import org.springframework.web.bind.annotation.*;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
@RestController
@RequestMapping("/crypto")
public class CryptoController extends BaseController {

//    Logger log = LoggerFactory.getLogger(CryptoController.class);

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @Encrypt
    public CryptoResponse get() {
        User user = new User();
        user.setName("user1");
        user.setIdCard("123456");
        user.setTimestamp(System.currentTimeMillis());
        return CryptoResponse.success(user);
    }

    @RequestMapping(value = "/get-params", method = RequestMethod.GET)
    @Decrypt
    public CryptoResponse get(
            @RequestParam String id
    ) {
        if ("123456".equals(id)) {
            User user = new User();
            user.setName("user1");
            user.setIdCard("123456");
            user.setTimestamp(System.currentTimeMillis());
            System.out.println("success");
            return CryptoResponse.success(user);
        } else {
            return CryptoResponse.success(HttpCode.BAD_REQUEST);
        }
    }

    /** 无加密 @RequestBody application/json */
    @RequestMapping(value = "/body", method = RequestMethod.POST)
    public CryptoResponse body(
            @RequestBody User user
    ) {
        System.out.println("Get unencrypted requestBody: " + user);
        return CryptoResponse.success();
    }

    /** 加密 @RequestBody application/json */
    @RequestMapping(value = "/encrypted-body", method = RequestMethod.POST)
    @Decrypt
    public CryptoResponse encryptedBody(
            @RequestBody User user
    ) {
        // post application/json
        System.out.println("Get encrypted requestBody: " + user);
        return CryptoResponse.success();
    }

    /** 无加密 @RequestParam application/x-www-form-urlencoded */
    @RequestMapping(value = "/param", method = RequestMethod.POST)
    public CryptoResponse param(
            @RequestParam String name,
            @RequestParam String idCard,
            @RequestParam Long timestamp
    ) {
        System.out.println("Get unencrypted requestParams: {name:" + name + " idCard:" + idCard + " timestamp:" + timestamp + "}");
        return CryptoResponse.success();
    }

    /** 加密 @RequestParam application/x-www-form-urlencoded */
    @RequestMapping(value = "/encrypted-param", method = RequestMethod.POST)
    @Decrypt
    public CryptoResponse encryptedParam(
            @RequestParam String name,
            @RequestParam String idCard,
            @RequestParam Long timestamp
    ) {
        System.out.println("Get encrypted requestParams: {name:" + name + " idCard:" + idCard + " timestamp:" + timestamp + "}");
        return CryptoResponse.success();
    }
}
