package com.monch.remote.api;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author 陈磊.
 */

public class UploadExecutor implements HttpExecutor {

    private static final String DEFAULT_CONTENT = "Content-Disposition";

    private OkHttpClient client;
    private Call call;

    UploadExecutor(OkHttpClient client) {
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
        // body
        builder.post(formBody(request.getParameters(), request.getFiles(), charset));
        builder.tag(request.getTag());
        call = client.newCall(builder.build());
        ResponseHandler.onStart(request);
        call.enqueue(new ResponseHandler(request));
    }

    private RequestBody formBody(Map<String, String> parameters, Map<String, File> files, Charset charset) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String name = HttpUtils.encode(entry.getKey(), charset);
                byte[] content = entry.getValue().getBytes(charset);
                builder.addPart(Headers.of(DEFAULT_CONTENT, "form-data; name=\"" + name + "\""),
                        RequestBody.create(MediaType.parse(charset.name()), content));
            }
        }
        if (files != null && !files.isEmpty()) {
            for (Map.Entry<String, File> entry : files.entrySet()) {
                String name = HttpUtils.encode(entry.getKey(), charset);
                File file = entry.getValue();
                String filename = file.getName();
                builder.addPart(Headers.of(DEFAULT_CONTENT, "form-data; name=\"" + name + "\"; filename=\"" + filename + "\""),
                        RequestBody.create(MediaType.parse(getMimeType(filename)), file));
            }
        }
        return builder.build();
    }

    private static String getMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    @Override
    public void cancel(ApiRequest request) {

    }
}
