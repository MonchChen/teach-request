package com.monch.remote.api;

import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author 陈磊.
 */

public class GetExecutor implements HttpExecutor {

    private OkHttpClient client;
    private Call call;

    GetExecutor(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void submit(ApiRequest request) {
        // url已经不为空，所以我们把参数拼装到url上就可以了
        Map<String, String> parameters = request.getParameters();
        final String url = makeUrl(request.getUrl(), parameters, request.getCharset());
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.get();
        Charset charset = request.getCharset();
        final Map<String, String> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(HttpUtils.encode(entry.getKey(), charset),
                        HttpUtils.encode(entry.getValue(), charset));
            }
        }
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

    private static String makeUrl(String url, Map<String, String> parameters, Charset charset) {
        if (parameters != null && !parameters.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            if (!url.contains("?")) {
                sb.append("?");
            } else if(url.contains("=")) {
                sb.append("&");
            }
            String key, value;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
                sb.append(key).append("=").append(HttpUtils.encode(value, charset)).append("&");
            }
            url = sb.toString();
        }
        return url;
    }

}
