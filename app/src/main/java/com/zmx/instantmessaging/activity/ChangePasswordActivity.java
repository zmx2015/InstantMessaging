package com.zmx.instantmessaging.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.zmx.instantmessaging.BaseActivity;
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

public class ChangePasswordActivity extends BaseActivity {


    @BindView(R.id.edit_pwd_a)
    EditText editPwdA;
    @BindView(R.id.edit_pwd_b)
    EditText editPwdB;
    @BindView(R.id.edit_pwd_c)
    EditText editPwdC;
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void initViews() {

        ButterKnife.bind(this);
        setToolbar(R.id.tool_bar);

    }

    @OnClick(R.id.submit)
    public void onViewClicked() {

        if (editPwdA.getText().toString().equals("")){

            Toast("请输入原密码");
            return;
        }
        if (editPwdB.getText().toString().equals("")){

            Toast("请输入密码");
            return;
        }
        if (editPwdC.getText().toString().equals("")){

            Toast("请输入密码");
            return;
        }

        if (!editPwdC.getText().toString().equals(editPwdB.getText().toString())){

            Toast("两次密码不正确");
            return;
        }

        UpdateUinfoPwd();

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case 1:

                    try {

                        JSONObject object = new JSONObject(msg.obj.toString());

                        if (object.getInt("code") == 200){

                            Toast("修改成功");
                            finish();

                        }else {

                            Toast(object.getString("info"));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

            }

        }
    };

    public void UpdateUinfoPwd(){

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.username,""));
        params.put("pwd1",editPwdA.getText().toString());
        params.put("pwd2", editPwdC.getText().toString());
        params.put("token", MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.token,""));
        OkHttp3ClientManager.getInstance().NetworkRequestMode("http://154.83.13.210:801/UserMethod/UpdateUinfo_pwd", params, handler, 1, 404);

    }

}
