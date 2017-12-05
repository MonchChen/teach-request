package com.monch.remote.api;

/**
 * @author 陈磊.
 */

class FailureRunnable implements Runnable {
    private ApiRequest request;
    private Exception exception;

    FailureRunnable(ApiRequest request, Exception exception) {
        this.request = request;
        this.exception = exception;
    }

    @Override
    public void run() {
        ApiCallback callback = request.getCallback();
        if (callback != null) {
            callback.onFailed(exception);
        }
        request.release();
    }
}
