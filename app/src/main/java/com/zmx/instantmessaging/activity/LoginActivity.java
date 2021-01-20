package com.zmx.instantmessaging.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.zmx.instantmessaging.BaseActivity;
import com.zmx.instantmessaging.MainActivity;
import com.zmx.instantmessaging.MySharedPreferences;
import com.zmx.instantmessaging.R;
import com.zmx.instantmessaging.okhttp.OkHttp3ClientManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.registered)
    Button registered;
    @BindView(R.id.btn_login)
    Button btn_login;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {

        ButterKnife.bind(this);
        setToolbar(R.id.tool_bar);

    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 1:

                    dismissLoadingView();

                    Log.e("加密值", "数据：" + msg.obj.toString());

                    try {

                        JSONObject object = new JSONObject(msg.obj.toString());

                        if (object.getInt("code") == 200) {

                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.username,etAccount.getText().toString());
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.name,object.getString("nickname"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.gender,object.getString("gender"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.pwd,object.getString("token").substring(0,128));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.icon,object.getString("icon"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.token,object.getString("token"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.isaddfriend,object.getString("isaddfriend"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.isgroupchat,object.getString("isgroupchat"));

                            NIMClient.getService(AuthService.class).login(new LoginInfo(etAccount.getText().toString(), MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.pwd,"")))
                                    .setCallback(new RequestCallback() {

                                        @Override
                                        public void onSuccess(Object param) {

                                            NimUIKit.loginSuccess(etAccount.getText().toString());
                                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();

                                            Log.e("进啦了", "进来了");
                                            Intent intent = new Intent();
                                            intent.setClass(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }

                                        @Override
                                        public void onFailed(int code) {
                                            Log.e("失败水水水水水水水水水水水水", code + "---");
                                        }

                                        @Override
                                        public void onException(Throwable exception) {
                                            Log.e("失败水水水水水水", exception.toString());
                                        }
                                    });

                        } else {
                            Toast(object.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("e","eee异常"+e.toString());
                    }

                    break;

            }

        }
    };


    @OnClick({R.id.registered, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.registered:
                startActivity(RegisteredActivity.class);
                break;
            case R.id.btn_login:

                if (etAccount.getText().toString().equals("")) {
                    Toast("请输入账号");
                    return;

                }
                if (etPassword.getText().toString().equals("")) {
                    Toast("请输入密码");
                    return;

                }

                showLoadingView("正在登录中...");
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("username", etAccount.getText().toString());
                params.put("pwd", etPassword.getText().toString());
                OkHttp3ClientManager.getInstance().NetworkRequestMode("http://154.83.13.210:801/UserMethod/Login", params, handler, 1, 404);
                break;
        }
    }
}