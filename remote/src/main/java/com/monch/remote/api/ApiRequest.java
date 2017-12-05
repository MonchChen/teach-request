package com.monch.remote.api;

import android.text.TextUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈磊.
 * 网络请求的必要数据
 */
public class ApiRequest {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    // Http 执行器
    private static HttpExecutor mHttpExecutor = new ExecDispatcher();

    public static final int GET = 0;
    public static final int POST = 1;
    public static final int UPLOAD = 2;
    public static final int DOWNLOAD = 3;

    private final String url;
    private final int type;   // 网络请求类型：get/post/upload/download
    private final Map<String, String> headers;    // 请求头数据
    private final Map<String, String> parameters; // 网络请求参数
    private final Map<String, File> files;    // 上传文件件的集合
    private final Charset charset;    // 编码方式，默认utf-8
    private final Object tag; // 标记，作用: 在你的一个特殊的生命周期里，一直作为参数传递，避免因线程操作，而导致某些不可控的改变
    private ApiCallback callback;

    public ApiRequest(Builder builder) {
        if (builder == null) {
            throw new NullPointerException("new ApiRequest builder is null.");
        }
        this.url = builder.url;
        this.type = builder.type;
        this.headers = builder.headers;
        this.parameters = builder.parameters;
        this.files = builder.files;
        this.charset = builder.charset;
        this.tag = builder.tag;
    }

    void checkRequest() {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("Http request url is null.");
        } else if (type != GET && type != POST &&
                type != UPLOAD && type != DOWNLOAD) {
            throw new IllegalArgumentException("Http request type is not find.");
        }
    }

    public String getUrl() {
        return url;
    }

    public int getType() {
        return type;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public Charset getCharset() {
        return charset;
    }

    public Object getTag() {
        return tag;
    }

    public ApiCallback getCallback() {
        return callback;
    }

    public static void setHttpExecutor(HttpExecutor executor) {
        if (executor == null) {
            throw new NullPointerException("setHttpExecutor is null.");
        }
        mHttpExecutor = executor;
    }

    public static HttpExecutor getHttpExecutor() {
        return mHttpExecutor;
    }

    public void request(ApiCallback callback) {
        this.callback = callback;
        mHttpExecutor.submit(this);
    }

    public void cancel() {
        mHttpExecutor.cancel(this);
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private int type = GET;   // 网络请求类型：get/post/upload/download
        private Map<String, String> headers;    // 请求头数据
        private Map<String, String> parameters; // 网络请求参数
        private Map<String, File> files;    // 上传文件件的集合
        private Charset charset = DEFAULT_CHARSET;    // 编码方式，默认utf-8
        private Object tag; // 标记，作用: 在你的一个特殊的生命周期里，一直作为参数传递，避免因线程操作，而导致某些不可控的改变
        private Builder() {}
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        public Builder get() {
            this.type = GET;
            return this;
        }
        public Builder post() {
            this.type = POST;
            return this;
        }
        public Builder upload() {
            this.type = UPLOAD;
            return this;
        }
        public Builder download() {
            this.type = DOWNLOAD;
            return this;
        }
        public Builder addHeader(String key, String value) {
            if (headers == null) {
                headers = HttpUtils.getStringMap();
            }
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                headers.put(key, value);
            }
            return this;
        }
        public Builder addParameter(String key, String value) {
            if (parameters == null) {
                parameters = HttpUtils.getStringMap();
            }
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                parameters.put(key, value);
            }
            return this;
        }
        public Builder addFile(String key, File value) {
            if (files == null) {
                files = HttpUtils.getFileMap();
            }
            if (!TextUtils.isEmpty(key) && value != null && value.exists()) {
                files.put(key, value);
            }
            return this;
        }
        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }
        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }
        public ApiRequest build() {
            return new ApiRequest(this);
        }
    }

    @Override
    public int hashCode() {
        final int urlHash = url != null ? url.hashCode() * 7 : 0;
        final int typeHash = type * 11;
        final int headerHash = headers != null ? headers.hashCode() * 13 : 0;
        final int parameterHash = parameters != null ? parameters.hashCode() * 17 : 0;
        final int fileHash = files != null ? files.hashCode() * 23 : 0;
        final int tagHash = tag != null ? tag.hashCode() * 27 : 0;
        return urlHash + typeHash + headerHash + parameterHash + fileHash + tagHash;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this) || (obj != null && obj instanceof ApiRequest && hashCode() == obj.hashCode());
    }

    public void release() {
        HttpUtils.releaseStringMap(headers);
        HttpUtils.releaseStringMap(parameters);
        HttpUtils.releaseFileMap(files);
    }

}
