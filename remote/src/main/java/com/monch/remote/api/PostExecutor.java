package com.monch.remote.api;

import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author 陈磊.
 */
public class PostExecutor implements HttpExecutor {

    private OkHttpClient client;
    private Call call;

    PostExecutor(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void submit(ApiRequest request) {
        Request.Builder builder = new Request.Builder();
        builder.url(request.getUrl());
        Charset charset = request.getCharset();
        // headers
        final Map<String, String> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(HttpUtils.encode(entry.getKey(), charset),
                        HttpUtils.encode(entry.getValue(), charset));
            }
        }
        // parameters
        Map<String, String> parameters = request.getParameters();
        FormBody.Builder form = new FormBody.Builder();
        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                form.addEncoded(HttpUtils.encode(entry.getKey(), charset),
                        HttpUtils.encode(entry.getValue(), charset));
            }
        }
        builder.post(form.build());
        builder.tag(request.getTag());
        call = client.newCall(builder.build());
        ResponseHandler.onStart(request);
        call.enqueue(new ResponseHandler(request));
    }

    @Override
    public void cancel(ApiRequest request) {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }
}
