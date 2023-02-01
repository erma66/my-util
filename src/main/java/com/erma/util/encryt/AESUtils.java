package com.erma.util.encryt;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Date 2022/8/8 10:05
 * @Created by yzfeng
 */
@Slf4j
public final class AESUtils {
    public static String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            throw new RuntimeException("the key is empty");
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            throw new RuntimeException("the key length is not 16");
        }
        if (sSrc == null) {
            return null;
        }
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        //"算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(encrypted), StandardCharsets.UTF_8);
    }

    // 解密
    public static String decrypt(String sSrc, String sKey) {
        if (sKey == null) {
            throw new RuntimeException("the key is empty");
        }
        if (sKey.length() != 16) {
            throw new RuntimeException("the key length is not 16");
        }
        if (sSrc == null) {
            return null;
        }
        try {
            byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.getDecoder().decode(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                return new String(original, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("decrypt failed,src:{}", sSrc);
                return null;
            }
        } catch (Exception ex) {
            log.error("decrypt failed,src:{}", sSrc);
            return null;
        }
    }
}

