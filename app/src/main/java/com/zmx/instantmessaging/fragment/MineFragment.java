package com.zmx.instantmessaging.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.zmx.instantmessaging.MySharedPreferences;
import com.zmx.instantmessaging.R;
import com.zmx.instantmessaging.activity.LoginActivity;
import com.zmx.instantmessaging.activity.MyProfileActivity;
import com.zmx.instantmessaging.activity.SetUpActivity;

/**
 * 程序猿：胖胖祥
 * 时 间：2020/12/30  16:40
 * 功 能：
 */
public class MineFragment extends SimpleImmersionFragment{

    private ImageView head;
    private TextView name,username,user_message,set_up_view;
    private Button out_button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        head = view.findViewById(R.id.head);
        name = view.findViewById(R.id.name);
        username = view.findViewById(R.id.username);
        username.setText("账号："+MySharedPreferences.getInstance(this.getActivity()).getString(MySharedPreferences.username, ""));

        user_message = view.findViewById(R.id.user_message);
        user_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(MineFragment.this.getActivity(), MyProfileActivity.class);
                startActivity(intent);

            }
        });

        set_up_view = view.findViewById(R.id.set_up_view);
        set_up_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(MineFragment.this.getActivity(), SetUpActivity.class);
                startActivity(intent);

            }
        });
        out_button = view.findViewById(R.id.out_button);
        out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MySharedPreferences.getInstance(MineFragment.this.getActivity()).clear();//清空本地数据
                Intent intent = new Intent();
                intent.setClass(MineFragment.this.getActivity(), LoginActivity.class);
                startActivity(intent);
                MineFragment.this.getActivity().finish();
            }
        });



        return view;
    }
    @Override
    public void onResume() {
        super.onResume();


        Glide.with(getActivity()).load(MySharedPreferences.getInstance(this.getActivity()).getString(MySharedPreferences.icon, "")).into(head);
        name.setText(MySharedPreferences.getInstance(this.getActivity()).getString(MySharedPreferences.name, ""));


    }
    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).titleBarMarginTop(R.id.tool_bar) .statusBarDarkFont(true).init();
    }
}
