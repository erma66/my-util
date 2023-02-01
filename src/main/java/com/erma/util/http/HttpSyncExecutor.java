package com.erma.util.http;

import com.erma.util.str.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Date 2022/8/3 18:27
 * @Created by yzfeng
 */
@Slf4j
public class HttpSyncExecutor implements HttpExecutor<Void> {
    private CloseableHttpClient httpClient;

    public HttpSyncExecutor() {
        init();
    }

    private void init() {
        RequestConfig requestConfig = RequestConfig.custom()
                //连接超时,连接建立时间,三次握手完成时间
                .setConnectTimeout(6000)
                //请求超时,数据传输过程中数据包之间间隔的最大时间
                .setSocketTimeout(6000)
                //使用连接池来管理连接,从连接池获取连接的超时时间
                .setConnectionRequestTimeout(10000)
                .build();
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString()));
        headers.add(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive"));

        httpClient = HttpClients.custom()
                .setDefaultHeaders(headers)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Override
    public Void doGet(String url, Map<String, String> headers, HttpCallback callback) {
        HttpGet httpGet = new HttpGet(url);
        addHeaders(httpGet, headers);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            handleResponse(httpResponse, callback);
        } catch (IOException e) {
            callback.onFail(e);
        }
        return null;
    }

    private void handleResponse(HttpResponse httpResponse, HttpCallback callback) {
        try {
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = StrUtils.readStream(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
                callback.onSuccess(result);
            } else {
                callback.onFail(new RuntimeException(String.valueOf(httpResponse.getStatusLine().getStatusCode())));
            }
        } catch (IOException e) {
            log.error("read http response error", e);
            callback.onFail(e);
        }
    }

    @Override
    public Void doPost(String url, Map<String, String> headers, String data, HttpCallback callback) {
        HttpPost httpPost = new HttpPost(url);
        addHeaders(httpPost, headers);
        HttpEntity httpEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            handleResponse(httpResponse, callback);
        } catch (IOException e) {
            callback.onFail(e);
        }
        return null;
    }

    private void addHeaders(HttpMessage httpMessage, Map<String, String> headers) {
        if (CollectionUtils.isEmpty(headers)) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpMessage.addHeader(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void destroy() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("close async client error", e);
            }
        }
    }
}
