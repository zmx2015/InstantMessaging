package com.zmx.instantmessaging;

import android.app.Application;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.util.NIMUtil;

public class MyApplication extends Application {

    private static MyApplication myApplication;

    public static MyApplication getInstance() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        myApplication = this;
        getInstance();

        NIMClient.init(this,null, null);

        if (NIMUtil.isMainProcess(this)) {

            // 初始化 // 初始化
                    NimUIKit.init(this);

        }
        }

}
