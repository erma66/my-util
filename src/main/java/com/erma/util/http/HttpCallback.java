package com.erma.util.http;

/**
 * @Date 2022/8/4 11:40
 * @Created by yzfeng
 */
public interface HttpCallback {
    void onSuccess(String content);

    void onFail(Exception e);

    void onCancle();
}
