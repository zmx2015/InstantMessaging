package com.zmx.instantmessaging.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

public class RegisteredActivity extends BaseActivity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_password_a)
    EditText etPasswordA;
    @BindView(R.id.btn_login)
    Button btnLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_registered;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        setToolbar(R.id.tool_bar);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case 1:


                    Log.e("失败水水水水水水水水水水水水", msg.obj.toString());

                    try {

                        JSONObject object = new JSONObject(msg.obj.toString());

                        if (object.getInt("code") == 200){

                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.username,object.getString("username"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.name,object.getString("name"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.pwd,object.getString("token").substring(0,128));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.icon,object.getString("icon"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.token,object.getString("token"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.isaddfriend,object.getString("isaddfriend"));
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.isgroupchat,object.getString("isgroupchat"));

                            NIMClient.getService(AuthService.class).login(new LoginInfo(object.getString("username"), MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.pwd,"")))
                                    .setCallback(new RequestCallback() {

                                        @Override
                                        public void onSuccess(Object param) {

                                            NimUIKit.loginSuccess(etAccount.getText().toString());
                                            Toast.makeText(mActivity, "注册成功", Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent();
                                            intent.setClass(mActivity, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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


                        }else{
                            Toast("注册失败");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

            }

        }
    };

    @OnClick(R.id.btn_login)
    public void onViewClicked() {


        if (etAccount.getText().toString().equals("")){

            Toast("请输入账号");
            return;

        }

        if (etPassword.getText().toString().equals("")){

            Toast("请输入密码");
            return;

        }
        if (!etPasswordA.getText().toString().equals(etPassword.getText().toString())){

            Toast("两次密码不正确");
            return;

        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", etAccount.getText().toString());
        params.put("pwd", etPassword.getText().toString());
        params.put("name", "A"+(int)((Math.random()*9+1)*100000));
        params.put("gender", "1");
        params.put("ex", "");
        params.put("icon", "https://gd4.alicdn.com/imgextra/i4/478240469/O1CN01Ko0xsU1FKqN5kA5qO_!!478240469-0-lubanu-s.jpg");
        OkHttp3ClientManager.getInstance().NetworkRequestMode("http://154.83.13.210:801/UserMethod/Create", params, handler, 1, 404);

    }


}
