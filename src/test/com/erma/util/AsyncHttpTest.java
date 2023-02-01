package com.erma.util;

import com.erma.util.http.HttpAsyncExecutor;
import com.erma.util.http.HttpCallback;

/**
 * @Date 2023/2/1 15:58
 * @Created by yzfeng
 */
public class AsyncHttpTest {

    public static void main(String[] args) {
        HttpAsyncExecutor asyncExecutor=new HttpAsyncExecutor();
        asyncExecutor.doGet("https://www.baidu.com", null, new HttpCallback() {
            @Override
            public void onSuccess(String content) {
                System.out.println(content);
            }

            @Override
            public void onFail(Exception e) {
                System.out.println("fail");
            }

            @Override
            public void onCancle() {
                System.out.println("cancle");
            }
        });
    }
}
