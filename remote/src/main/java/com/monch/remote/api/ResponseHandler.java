package com.monch.remote.api;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author 陈磊.
 */

public class ResponseHandler implements Callback {

    private static Handler mMainHandler = new Handler(Looper.getMainLooper());
    // 主线程执行器
    private static Executor mMainPoster = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            mMainHandler.post(command);
        }
    };

    private ApiRequest request;

    ResponseHandler(ApiRequest request) {
        this.request = request;
    }

    static void onStart(final ApiRequest request) {
        final ApiCallback callback = request.getCallback();
        if (callback == null) return;
        callback.onStart(request);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        mMainPoster.execute(new FailureRunnable(request, e));
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        // 子线程
        if (!response.isSuccessful()) {
            throw new IOException("Response code is " + response.code());
        }
        ApiCallback callback = request.getCallback();
        if (callback != null) {
            ApiResult result;
            ResponseBody body = response.body();
            byte[] bytes = body != null ? body.bytes() : new byte[0];
            try {
                result = callback.onParse(bytes);
            } catch (RemoteException e) {
                // 失败
                mMainPoster.execute(new FailureRunnable(request, e));
                return;
            }
            // 成功
            mMainPoster.execute(new SuccessRunnable(request, result));
        }
    }

}
