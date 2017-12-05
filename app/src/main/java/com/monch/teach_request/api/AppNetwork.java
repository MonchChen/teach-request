package com.monch.teach_request.api;

import com.monch.remote.api.ApiRequest;
import com.monch.remote.api.ApiResult;

import java.lang.reflect.Field;

/**
 * @author 陈磊.
 */

public abstract class AppNetwork extends NetworkCallback {

    private ApiRequest request;

    public AppNetwork(ApiRequest.Builder builder) {
        this.request = initRequest(builder);
        setCallback();
    }

    private ApiRequest initRequest(ApiRequest.Builder builder) {
        Class<?> clazz = null;
        Class<?> temp = getClass();
        do {
            if (temp.getSuperclass() == AppNetwork.class) {
                clazz = temp;
                break;
            }
        } while ((temp = temp.getSuperclass()) != null);
        if (clazz != null) {
            Network network = clazz.getAnnotation(Network.class);
            if (network != null) {
                builder.url(network.url());
                switch (network.type()) {
                    case ApiRequest.POST:
                        builder.post();
                        break;
                    case ApiRequest.UPLOAD:
                        builder.upload();
                        break;
                    case ApiRequest.DOWNLOAD:
                        builder.download();
                        break;
                    default:
                        builder.get();
                        break;
                }
            }
        }
        return builder.build();
    }

    private void setCallback() {
        try {
            Field callbackField = ApiRequest.class.getDeclaredField("callback");
            callbackField.setAccessible(true);
            callbackField.set(request, this);
        } catch (Exception e) {
            // ignore
        }
    }

    public void request() {
        request.request(this);
    }

    public ApiRequest getRequest() {
        return request;
    }

    @Override
    public void complete(ApiResult result) { }

    @Override
    public void failed(Throwable cause) { }
}
