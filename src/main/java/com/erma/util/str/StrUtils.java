package com.erma.util.str;

import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StrUtils {
    private StrUtils() {
    }

    /**
     * 字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 字符串是否为空，适配前端
     */
    public static boolean isEmptyWeb(String str) {
        return str == null || str.isEmpty() || str.equals("null") || str.equals("undefined");
    }

    /**
     * 字符串是不是以某个字符开始
     */
    public static boolean isFirstChar(String str, char ch) {
        return !isEmpty(str) && str.charAt(0) == ch;
    }

    /**
     * 字符串是不是以某个字符结束
     */
    public static boolean isLastChar(String str, char ch) {
        return !isEmpty(str) && str.charAt(str.length() - 1) == ch;
    }

    /**
     * 某个字符在字符串中出现了几次
     */
    public static int countChar(String str, char ch) {
        if (isEmpty(str)) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }

        return count;
    }

    /**
     * 某个子串在字符串中出现了几次
     */
    public static int countSubString(String str, String subStr) {
        if (isEmpty(str) || isEmpty(subStr)) {
            return 0;
        }

        if (subStr.length() == 1) {
            return countChar(str, subStr.charAt(0));
        }

        int count = 0;

        for (int i = 0; i <= str.length() - subStr.length(); ) {
            if (str.charAt(i) == subStr.charAt(0)) {
                boolean equals = true;
                for (int j = 1; j < subStr.length(); j++) {
                    if (str.charAt(i + j) != subStr.charAt(j)) {
                        equals = false;
                        break;
                    }
                }
                if (equals) {
                    count++;
                }
                i += subStr.length();
            } else {
                i++;
            }
        }

        return count;
    }

    public static String getRandomId(int length) {
        length = Math.min(length, 32);
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }

    public static String replaceSlash(String str) {
        if (StringUtils.hasText(str)) {
            return str.replace("\\\"", "\"");
        }
        return str;
    }

    public static String readStream(InputStream inputStream, Charset cs) throws IOException {
        if (cs == null) {
            cs = StandardCharsets.UTF_8;
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, cs));
            String l;
            while ((l = reader.readLine()) != null) {
                sb.append(l);
            }
        } finally {
            inputStream.close();
        }
        return sb.toString();
    }
}
