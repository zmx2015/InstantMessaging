package com.zmx.instantmessaging.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.zmx.instantmessaging.BaseActivity;
import com.zmx.instantmessaging.MainActivity;
import com.zmx.instantmessaging.MySharedPreferences;
import com.zmx.instantmessaging.R;

public class WelcomeActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initViews() {


        if (MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.pwd,"").equals("")) {


            Intent intent = new Intent();
            intent.setClass(mActivity, LoginActivity.class);
            startActivity(intent);
            finish();

        }else{


            NIMClient.getService(AuthService.class).login(new LoginInfo(MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.username,""), MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.pwd,"")))
                    .setCallback(new RequestCallback() {

                        @Override
                        public void onSuccess(Object param) {

                            NimUIKit.loginSuccess(MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.username,""));
                            Toast.makeText(mActivity, "", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent();
                            intent.setClass(mActivity, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onFailed(int code) {
                            Log.e("失败水水水水水水水水水水水水", code + "---");
                            Intent intent = new Intent();
                            intent.setClass(mActivity, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onException(Throwable exception) {
                            Log.e("失败水水水水水水", exception.toString());
                            Intent intent = new Intent();
                            intent.setClass(mActivity, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });


        }

    }
}
