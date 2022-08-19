package com.erma.util.str;

import java.util.Map;

/**
 * @Date 2022/6/21 14:55
 * @Created by erma66
 */
public class TemplateLoader {
    public static String loadTemplate(String source, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            source = source.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return source;
    }
}
