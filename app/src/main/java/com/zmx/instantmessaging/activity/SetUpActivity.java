package com.zmx.instantmessaging.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;

import com.zmx.instantmessaging.BaseActivity;
import com.zmx.instantmessaging.MySharedPreferences;
import com.zmx.instantmessaging.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetUpActivity extends BaseActivity {


    @BindView(R.id.relative1)
    RelativeLayout relative1;
    @BindView(R.id.relative2)
    RelativeLayout relative2;
    @BindView(R.id.relative3)
    RelativeLayout relative3;
    @BindView(R.id.relative4)
    RelativeLayout relative4;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_up;
    }

    @Override
    protected void initViews() {

        ButterKnife.bind(this);
        setToolbar(R.id.tool_bar);

    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {

        Intent intent = new Intent();

        switch (view.getId()) {
            case R.id.relative1:

                Toast("最新版本了");

                break;
            case R.id.relative2:

                new AlertDialog.Builder(mActivity)
                        .setTitle("提示")
                        .setMessage("是否清理缓存？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast("清理成功");
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

                break;
            case R.id.relative3:

                //清空本地所有数据
                MySharedPreferences.getInstance(this).clear();//清空本地数据
                intent.setClass(mActivity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;

            case R.id.relative4:

                intent.setClass(mActivity, ChangePasswordActivity.class);
                startActivity(intent);

                break;

    }

    }
}
