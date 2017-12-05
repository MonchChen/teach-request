package com.monch.teach_request.network;

import android.util.Log;

import com.monch.remote.api.ApiRequest;
import com.monch.remote.api.ApiResult;
import com.monch.remote.api.RemoteException;
import com.monch.teach_request.api.AppNetwork;
import com.monch.teach_request.api.Network;

import org.json.JSONObject;

/**
 * @author 陈磊.
 */
@Network(url = "http://192.168.1.101:8094/teach/api/login", type = ApiRequest.POST)
public class LoginNetwork extends AppNetwork {

    private static final String TAG = "LoginNetwork";

    public LoginNetwork(ApiRequest.Builder builder) {
        super(builder);
    }

    @Override
    public ApiResult parse(JSONObject jsonObject) throws RemoteException {
        ApiResult result = new ApiResult();
        result.setCode(jsonObject.optInt("code"));
        result.setMessage(jsonObject.optString("message"));
        result.put("result", jsonObject.optString("data"));
        Log.e(TAG, "onLogin onParse");
        return result;
    }
}
