package com.zmx.instantmessaging.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.business.recent.RecentContactsFragment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.zmx.instantmessaging.MySharedPreferences;
import com.zmx.instantmessaging.R;
import com.zmx.instantmessaging.activity.GroupChatActivity;
import com.zmx.instantmessaging.activity.SearchFriendsActivity;
import com.zmx.instantmessaging.activity.view.CustomToast;
import com.zmx.instantmessaging.activity.view.PopWinShare;

import java.util.ArrayList;
import java.util.List;

public class SessionListFragment extends SimpleImmersionFragment {

    private PopWinShare popWinShare;

    private ImageView add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.messages_fragment, container, false);

        add = view.findViewById(R.id.sousuo);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popWinShare == null) {
                    //自定义的单击事件
                    OnClickLintener paramOnClickListener = new OnClickLintener();
                    popWinShare = new PopWinShare(SessionListFragment.this.getActivity(), paramOnClickListener, 400, 300);
                    //监听窗口的焦点事件，点击窗口外面则取消显示
                    popWinShare.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                popWinShare.dismiss();
                            }
                        }
                    });
                }
//设置默认获取焦点
                popWinShare.setFocusable(true);
//以某个控件的x和y的偏移量位置开始显示窗口
                popWinShare.showAsDropDown(add, 0, 0);
//如果窗口存在，则更新
                popWinShare.update();

            }
        });


        return view;
    }


    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).titleBarMarginTop(R.id.tool_bar) .statusBarDarkFont(true).init();
    }


    private List<NimUserInfo> friends;

    class OnClickLintener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {

                case R.id.group_chat:


                    if (MySharedPreferences.getInstance(SessionListFragment.this.getContext()).getString(MySharedPreferences.isgroupchat,"").equals("0")){
                        CustomToast.getInstance().showToastCustom(SessionListFragment.this.getContext(), "没有该权限", Gravity.CENTER);

                    }else{

                        List<String> fs = NIMClient.getService(FriendService.class).getFriendAccounts();
                        friends = NIMClient.getService(UserService.class).getUserInfoList(fs);

                        if (friends == null){

                            CustomToast.getInstance().showToastCustom(SessionListFragment.this.getContext(), "没有好友，无法发起群聊", Gravity.CENTER);
                            popWinShare.dismiss();
                            return;

                        }

                        intent.setClass(SessionListFragment.this.getContext(), GroupChatActivity.class);
                        startActivity(intent);

                    }

                    popWinShare.dismiss();

                    break;
                case R.id.add_friend:

                    if (MySharedPreferences.getInstance(SessionListFragment.this.getContext()).getString(MySharedPreferences.isaddfriend,"").equals("0")){

                        CustomToast.getInstance().showToastCustom(SessionListFragment.this.getContext(), "没有该权限", Gravity.CENTER);

                    }else {

                        intent.setClass(SessionListFragment.this.getContext(), SearchFriendsActivity.class);
                        startActivity(intent);

                    }
                    popWinShare.dismiss();
                    break;

                default:
                    break;
            }


        }
    }

}
