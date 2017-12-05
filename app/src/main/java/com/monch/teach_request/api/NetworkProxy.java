package com.monch.teach_request.api;

import com.monch.remote.api.ApiRequest;
import com.monch.remote.api.HttpExecutor;

/**
 * @author 陈磊.
 */

public class NetworkProxy implements HttpExecutor {

    public static void init() {
        HttpExecutor defaultHttpExecutor = ApiRequest.getHttpExecutor();
        HttpExecutor proxy = new NetworkProxy(defaultHttpExecutor);
        ApiRequest.setHttpExecutor(proxy);
    }

    private HttpExecutor executor;

    private NetworkProxy(HttpExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void submit(ApiRequest request) {
        // 在这里我们可以添加一些请求前的统一操作
        executor.submit(request);
    }

    @Override
    public void cancel(ApiRequest request) {
        executor.cancel(request);
    }
}
