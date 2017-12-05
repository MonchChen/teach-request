package com.monch.remote.api;

/**
 * @author 陈磊.
 */

public interface HttpExecutor {

    // 网络请求提交
    void submit(ApiRequest request);
    // 网络请求取消
    void cancel(ApiRequest request);

}
