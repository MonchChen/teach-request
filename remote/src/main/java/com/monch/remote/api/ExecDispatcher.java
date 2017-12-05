package com.monch.remote.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author 陈磊.
 */
public class ExecDispatcher implements HttpExecutor {

    private static final int TIMEOUT = 60;

    // 网络请求客户端处理器
    private OkHttpClient client;
    // 正在执行网络处理的请求集合
    private Map<ApiRequest, HttpExecutor> mRequestMap = new HashMap<>();

    public ExecDispatcher() {
        client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void submit(ApiRequest request) {
        request.checkRequest();
        HttpExecutor executor;
        switch (request.getType()) {
            case ApiRequest.POST:
                // post
                executor = new PostExecutor(client);
                break;
            case ApiRequest.UPLOAD:
                // upload
                executor = new UploadExecutor(client);
                break;
            default:
                // get
                executor = new GetExecutor(client);
                break;
        }
        mRequestMap.put(request, executor);
        executor.submit(request);
    }

    @Override
    public void cancel(ApiRequest request) {
        if (request != null && mRequestMap.containsKey(request)) {
            HttpExecutor executor = mRequestMap.get(request);
            executor.cancel(request);
        }
    }

}
