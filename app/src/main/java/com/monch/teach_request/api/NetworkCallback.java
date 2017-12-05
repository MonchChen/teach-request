package com.monch.teach_request.api;

import android.util.Log;

import com.monch.remote.api.ApiCallback;
import com.monch.remote.api.ApiRequest;
import com.monch.remote.api.ApiResult;
import com.monch.remote.api.RemoteException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 陈磊.
 */

public abstract class NetworkCallback implements ApiCallback {

    private static final String TAG = "NetworkCallback";

    private static final int JSON_EXCEPTION = -1;
    private static final int LOGIN_EXCEPTION = -10;

    private ApiRequest request;
    private long startTime;

    @Override
    public void onStart(ApiRequest request) {
        // 线程2
        this.request = request;
        startTime = System.currentTimeMillis();
    }

    @Override
    public ApiResult onParse(byte[] bytes) throws RemoteException {
        // 线程3
        if (bytes != null) {
            String string = new String(bytes, request.getCharset());
            try {
                JSONObject jsonObject = new JSONObject(string);
                int code = jsonObject.optInt("code");
                if (code == LOGIN_EXCEPTION) {
                    throw new RemoteException(LOGIN_EXCEPTION, "Logout.");
                }
                return parse(jsonObject);
            } catch (JSONException e) {
                throw new RemoteException(JSON_EXCEPTION, e.getMessage());
            }
        }
        return null;
    }

    public abstract ApiResult parse(JSONObject jsonObject) throws RemoteException;

    @Override
    public void onComplete(ApiResult result) {
        finish();
        complete(result);
    }

    public abstract void complete(ApiResult result);

    @Override
    public void onFailed(Throwable cause) {
        if (cause instanceof RemoteException) {
            RemoteException remoteException = (RemoteException) cause;
            if (remoteException.getCode() == LOGIN_EXCEPTION) {
                // 您已经被踢出了，在这里处理踢出逻辑
            }
        }
        Log.e(TAG, "url=" + request.getUrl(), cause);
        finish();
        failed(cause);
    }

    public abstract void failed(Throwable cause);

    @Override
    public void onCancel() {
        finish();
    }

    private void finish() {
        Log.i(TAG, "url=" + request.getUrl() + ", time=" + (System.currentTimeMillis() - startTime));
    }

}
