package com.zmx.instantmessaging.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.business.contact.ContactsFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.zmx.instantmessaging.MySharedPreferences;
import com.zmx.instantmessaging.R;
import com.zmx.instantmessaging.activity.GroupChatActivity;
import com.zmx.instantmessaging.activity.SearchFriendsActivity;
import com.zmx.instantmessaging.activity.view.CustomToast;
import com.zmx.instantmessaging.activity.view.PopWinShare;

import java.util.List;

/**
 * 程序猿：胖胖祥
 * 时 间：2020/12/30  11:44
 * 功 能：
 */
public class ContactListFragment extends SimpleImmersionFragment{

    private PopWinShare popWinShare;

    private ImageView add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        add = view.findViewById(R.id.sousuo);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popWinShare == null) {
                    //自定义的单击事件
                    ContactListFragment.OnClickLintener paramOnClickListener = new ContactListFragment.OnClickLintener();
                    popWinShare = new PopWinShare(ContactListFragment.this.getActivity(), paramOnClickListener, 400, 300);
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


                    if (MySharedPreferences.getInstance(ContactListFragment.this.getContext()).getString(MySharedPreferences.isgroupchat,"").equals("0")){
                        CustomToast.getInstance().showToastCustom(ContactListFragment.this.getContext(), "没有该权限", Gravity.CENTER);

                    }else{

                        List<String> fs = NIMClient.getService(FriendService.class).getFriendAccounts();
                        friends = NIMClient.getService(UserService.class).getUserInfoList(fs);

                        if (friends == null){

                            CustomToast.getInstance().showToastCustom(ContactListFragment.this.getContext(), "没有好友，无法发起群聊", Gravity.CENTER);
                            popWinShare.dismiss();
                            return;

                        }

                        intent.setClass(ContactListFragment.this.getContext(), GroupChatActivity.class);
                        startActivity(intent);

                    }

                    popWinShare.dismiss();

                    break;
                case R.id.add_friend:

                    if (MySharedPreferences.getInstance(ContactListFragment.this.getContext()).getString(MySharedPreferences.isaddfriend,"").equals("0")){

                        CustomToast.getInstance().showToastCustom(ContactListFragment.this.getContext(), "没有该权限", Gravity.CENTER);

                    }else {

                        intent.setClass(ContactListFragment.this.getContext(), SearchFriendsActivity.class);
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
