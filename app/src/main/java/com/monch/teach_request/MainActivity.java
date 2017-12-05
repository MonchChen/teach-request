package com.monch.teach_request;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.monch.remote.api.ApiRequest;
import com.monch.remote.api.ApiResult;
import com.monch.teach_request.api.AppNetwork;
import com.monch.teach_request.api.BatchNetwork;
import com.monch.teach_request.network.LoginNetwork;
import com.monch.teach_request.network.RegisterNetwork;

public class MainActivity extends AppCompatActivity {

    // GitHub地址：https://github.com/MonchChen

    private static final String TAG = "MainActivity";

    private static final String URL = "http://192.168.1.101:8094/teach/api/";


    // 192.168.1.101
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLogin(View view) {
        // username password
        LoginNetwork network = new LoginNetwork(
                ApiRequest.create().addParameter("password", "password")) {
            @Override
            public void complete(ApiResult result) {
                Log.e(TAG, "onLogin onComplete");
                if (ApiResult.isSuccess(result)) {
                    Log.e(TAG, "onLogin result = " + result.getString("result"));
                } else {
                    Log.e(TAG, "onLogin failed.");
                }
            }
            @Override
            public void failed(Throwable cause) {
                Log.e(TAG, "onLogin onFailed");
            }
        };
        network.request();
    }

    public void onRegister(View view) {

    }

    public void onRegisterAndLogin(View view) {
        // 线程1
        final AppNetwork loginNetwork = new LoginNetwork(
                ApiRequest.create()
                        .addParameter("username", "admin")
                        .addParameter("password", "admin123"));
        final AppNetwork registerNetwork = new RegisterNetwork(
                ApiRequest.create()
                        .addParameter("username", "reg_admin")
                        .addParameter("gender", "1"));
        BatchNetwork batchNetwork = new BatchNetwork();
        batchNetwork.request(new BatchNetwork.BatchCallback() {
            @Override
            public void complete(ApiResult result) {
                if (result != null) {
                    Toast.makeText(MainActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
                    ApiResult loginResult = result.get(loginNetwork.getRequest().getUrl());
                    if (loginResult != null) {
                        Log.i(TAG, String.valueOf(loginResult));
                    }
                    ApiResult registerResult = result.get(registerNetwork.getRequest().getUrl());
                    if (registerResult != null) {
                        Log.i(TAG, String.valueOf(registerResult));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed(Throwable cause) {
                Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onRegisterAndLogin error.", cause);
            }
        }, loginNetwork, registerNetwork);
    }

}
