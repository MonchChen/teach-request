package com.monch.teach_request;

import android.app.Application;
import android.content.Context;

import com.monch.teach_request.api.NetworkProxy;

/**
 * @author 陈磊.
 */

public class App extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        NetworkProxy.init();
    }

    public static Context getApplication() {
        return application;
    }
}
