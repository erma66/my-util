package com.erma.util;

import com.erma.util.encryt.AESUtils;

/**
 * @Date 2023/2/1 16:42
 * @Created by yzfeng
 */
public class AESTest {

    public static void main(String[] args) throws Exception {
        String key = "fUlRZCYauitVNB4Q";
        // 数据库加密to_base64(AES_ENCRYPT(‘123’,'B_/9sg{ft$&HVGDh'))
        String src = "null";
        String encrypt = AESUtils.encrypt(src, key);
        System.out.println(encrypt);
        System.out.println(AESUtils.decrypt(encrypt, key));
    }
}
