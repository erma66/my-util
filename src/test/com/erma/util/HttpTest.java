package com.erma.util;

import com.erma.util.http.HttpAsyncExecutor;
import com.erma.util.http.HttpCallback;
import com.erma.util.http.HttpExecutor;
import com.erma.util.http.HttpSyncExecutor;
import org.apache.http.HttpResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Date 2023/2/1 15:58
 * @Created by yzfeng
 */
public class HttpTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("start async http");
        HttpExecutor<Future<HttpResponse>> asyncExecutor = new HttpAsyncExecutor();
        asyncExecutor.doGet("http://www.baidu.com", null, new HttpCallback() {
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
        }).get();
        asyncExecutor.destroy();

        System.out.println("start sync http");
        HttpExecutor<Void> syncExecutor = new HttpSyncExecutor();
        syncExecutor.doGet("http://www.baidu.com", null, new HttpCallback() {
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
        syncExecutor.destroy();
    }
}
