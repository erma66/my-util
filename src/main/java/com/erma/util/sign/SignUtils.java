package com.erma.util.sign;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Date 2022/10/14 10:11
 * @Created by yzfeng
 */
@Slf4j
public class SignUtils {

    public static String sign(long timestamp, String secret) {
        String stringToSign = timestamp + "&" + secret;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(new String(Base64.getEncoder().encode(signData), StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("sign error.", e);
        }
        return null;
    }
}
