package com.monch.remote.api;

/**
 * @author 陈磊.
 */
public interface ApiCallback {

    // 网络请求开始: 主线程
    void onStart(ApiRequest request);

    // 网络请求数据解析：子线程
    ApiResult onParse(byte[] bytes) throws RemoteException;

    // 请求成功：主线程
    void onComplete(ApiResult result);

    // 请求失败：主线程
    void onFailed(Throwable cause);

    // 请求取消：主线程
    void onCancel();

}
