package com.monch.remote.api;

/**
 * @author 陈磊.
 */

class SuccessRunnable implements Runnable {
    private ApiRequest request;
    private ApiResult result;

    SuccessRunnable(ApiRequest request, ApiResult result) {
        this.request = request;
        this.result = result;
    }

    @Override
    public void run() {
        ApiCallback callback = request.getCallback();
        if (callback != null) {
            callback.onComplete(result);
        }
        request.release();
        if (result != null) {
            result.release();
        }
    }
}
