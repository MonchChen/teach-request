package com.monch.teach_request.api;

import com.monch.remote.api.ApiCallback;
import com.monch.remote.api.ApiRequest;
import com.monch.remote.api.ApiResult;
import com.monch.remote.api.RemoteException;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author 陈磊.
 */
// http://192.168.1.1:8094/teach/api/batch?method=login&username=admin&password=admin,register&username=chenlei&gender=1
public class BatchNetwork {

    public interface BatchCallback {
        void complete(ApiResult result);
        void failed(Throwable cause);
    }

    private ApiRequest request;

    // batch 接口，尽量应该用于get请求，而避免使用post请求
    public void request(BatchCallback callback, AppNetwork... networks) {
        if (networks == null || networks.length == 0) return;
        final String parameter = makeBatchParameter(networks);
        ApiRequest request = ApiRequest.create()
                .url("http://192.168.1.101:8094/teach/api/batch")
                .addParameter("method", parameter)
                .get()
                .build();
        request.request(new InnerCallback(callback, networks));
    }

    private static String makeBatchParameter(AppNetwork[] networks) {
        StringBuilder sb = new StringBuilder();
        for (AppNetwork network : networks) {
            ApiRequest request = network.getRequest();
            final String url = request.getUrl();
            final String path = getPath(url);
            sb.append(path).append("&");    // login&
            Map<String, String> parameters = request.getParameters();
            if (parameters != null && !parameters.isEmpty()) {
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    sb.append(parameter.getKey()).append("=")
                            .append(parameter.getValue()).append("&");
                    // login&username=admin&password=admin&
                }
                sb.deleteCharAt(sb.length() - 1);   // login&username=admin&password=admin
            }
            sb.append(","); // login&username=admin&password=admin,
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static String getPath(String url) {
        // http://192.168.1.1:8080/teach/api/login?p1=a&p2=b
        final String API = "/api/";
        int index = url.indexOf(API);
        if (index == -1) return null;
        return url.substring(index + API.length(), url.length());
    }

    private static class InnerCallback extends NetworkCallback {
        private BatchCallback callback;
        private AppNetwork[] networks;
        InnerCallback(BatchCallback callback, AppNetwork[] networks) {
            this.callback = callback;
            this.networks = networks;
        }
        @Override
        public void onStart(ApiRequest request) {
            super.onStart(request);
            ApiRequest req;
            for (AppNetwork network : networks) {
                req = network.getRequest();
                ApiCallback callback = req.getCallback();
                if (callback != null) {
                    callback.onStart(req);
                }
            }
        }
        @Override
        public ApiResult parse(JSONObject jsonObject) throws RemoteException {
            ApiResult result = new ApiResult();
            int code = jsonObject.optInt("code");
            result.setCode(code);
            result.setMessage(jsonObject.optString("message"));
            if (code == 0) {
                // 请求是成功的
                JSONObject dataObject = jsonObject.optJSONObject("data");
                if (dataObject != null) {
                    for (AppNetwork network : networks) {
                        final ApiRequest request = network.getRequest();
                        final String path = getPath(request.getUrl());
                        JSONObject pathJsonObject = dataObject.optJSONObject(path);
                        ApiCallback callback = request.getCallback();
                        if (callback != null) {
                            ApiResult pathResult = callback.onParse(pathJsonObject.toString().getBytes());
                            if (pathResult != null) {
                                result.put(request.getUrl(), pathResult);
                            }
                        }
                    }
                }
            }
            return result;
        }
        @Override
        public void complete(ApiResult result) {
            ApiRequest req;
            for (AppNetwork network : networks) {
                req = network.getRequest();
                ApiCallback callback = req.getCallback();
                if (callback != null) {
                    callback.onComplete((ApiResult) result.get(req.getUrl()));
                }
            }
            if (callback != null) {
                callback.complete(result);
            }
        }
        @Override
        public void failed(Throwable cause) {
            for (AppNetwork network : networks) {
                ApiCallback callback = network.getRequest().getCallback();
                if (callback != null) {
                    callback.onFailed(cause);
                }
            }
            if (callback != null) {
                callback.failed(cause);
            }
        }
    }

}
