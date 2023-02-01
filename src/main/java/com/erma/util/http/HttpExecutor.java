package com.erma.util.http;

import java.util.Map;

/**
 * @Date 2023/2/1 17:02
 * @Created by yzfeng
 */
public interface HttpExecutor<T> {
    T doGet(String url, Map<String, String> headers, HttpCallback callback);

    T doPost(String url, Map<String, String> headers, String data, HttpCallback callback);

    void destroy();
}
