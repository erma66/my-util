package com.erma.util.http;

import com.erma.util.str.StrUtils;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * @Date 2022/8/3 18:27
 * @Created by erma66
 */
@Service
@Slf4j
public class HttpAsyncExecutor {
    private CloseableHttpAsyncClient asyncClient;

    @PostConstruct
    private void init() {
        RequestConfig requestConfig = RequestConfig.custom()
                //连接超时,连接建立时间,三次握手完成时间
                .setConnectTimeout(6000)
                //请求超时,数据传输过程中数据包之间间隔的最大时间
                .setSocketTimeout(6000)
                //使用连接池来管理连接,从连接池获取连接的超时时间
                .setConnectionRequestTimeout(10000)
                .build();
        //配置io线程
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                //执行线程的线程数
                .setIoThreadCount(10)
                .setSoKeepAlive(true)
                .build();
        //设置连接池大小
        ConnectingIOReactor ioReactor = null;
        ThreadFactory threadFactor = new DefaultThreadFactory("async-http");
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig, threadFactor);
        } catch (IOReactorException e) {
            log.error("init async client error", e);
            return;
        }
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        //最大连接数
        connManager.setMaxTotal(200);
        //分配给同一个route(路由)最大的并发连接数
        connManager.setDefaultMaxPerRoute(200);
        asyncClient = HttpAsyncClients.custom().
                setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
        asyncClient.start();
    }

    public Future<HttpResponse> doGet(String url, Map<String, String> headers, HttpCallback callback) {
        HttpGet httpGet = new HttpGet(url);
        addHeaders(httpGet, headers);
        return asyncClient.execute(httpGet, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                handleResponse(httpResponse, callback);
            }

            @Override
            public void failed(Exception e) {
                callback.onFail(e);
            }

            @Override
            public void cancelled() {
                callback.onCancle();
            }
        });
    }

    private void handleResponse(HttpResponse httpResponse, HttpCallback callback) {
        try {
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = StrUtils.readStream(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
                callback.onSuccess(result);
            } else {
                callback.onFail(new RuntimeException(httpResponse.getStatusLine().getReasonPhrase()));
            }
        } catch (IOException e) {
            log.error("read http response error", e);
            callback.onFail(e);
        }
    }

    public Future<HttpResponse> doPost(String url, Map<String, String> headers, String data, HttpCallback callback) {
        HttpPost httpPost = new HttpPost(url);
        addHeaders(httpPost, headers);
        HttpEntity httpEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);
        return asyncClient.execute(httpPost, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                handleResponse(httpResponse, callback);
            }

            @Override
            public void failed(Exception e) {
                callback.onFail(e);
            }

            @Override
            public void cancelled() {
                callback.onCancle();
            }
        });
    }

    private void addHeaders(HttpMessage httpMessage, Map<String, String> headers) {
        if (CollectionUtils.isEmpty(headers)) {
            return;
        }
        for (var entry : headers.entrySet()) {
            httpMessage.addHeader(entry.getKey(), entry.getValue());
        }
    }

    @PreDestroy
    private void onDestroy() {
        if (asyncClient != null) {
            try {
                asyncClient.close();
            } catch (IOException e) {
                log.error("close async client error", e);
            }
        }
    }

}
