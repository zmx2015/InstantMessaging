package com.zmx.instantmessaging.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.zmx.instantmessaging.BaseActivity;
import com.zmx.instantmessaging.R;
import com.zmx.instantmessaging.adapter.SearchFriendsAdapter;
import com.zmx.instantmessaging.fragment.SessionListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索好友
 */
public class SearchFriendsActivity extends BaseActivity implements SearchFriendsAdapter.FriendsListener {


    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.search_text)
    TextView searchText;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private SearchFriendsAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_friends;
    }

    @Override
    protected void initViews() {

        ButterKnife.bind(this);
        setToolbar(R.id.tool_bar);

        adapter = new SearchFriendsAdapter(mActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(adapter);
        adapter.setFriendsListener(this);
    }

    @OnClick(R.id.search_text)
    public void onViewClicked() {


        loadFriend(search.getText().toString());

    }


    private void loadFriend(final String account) {
        List<String> accounts = new ArrayList<>();
        accounts.add(account);
        NIMClient.getService(UserService.class).fetchUserInfo(accounts)
                .setCallback(new RequestCallback<List<NimUserInfo>>() {
                    @Override
                    public void onSuccess(List<NimUserInfo> param) {

                        if (param.size() > 0) {

                            adapter.setListAndNotifyDataSetChanged(param);

                        } else {

                            Toast("未搜索到用户");

                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        Toast("未知错误");
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Toast("未知错误");
                    }
                });
    }

    @Override
    public void addFriends(NimUserInfo nimUserInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater factory = LayoutInflater.from(mActivity);//提示框
        final View view = factory.inflate(R.layout.dialog_edit, null);//这里必须是final的
        final EditText et = view.findViewById(R.id.editText);
        et.setText("账号发送请求");
        builder.setTitle("添加好友");

        final AlertDialog dialog = builder.create();
        dialog.setView(view);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(et.getText().toString())) {

                    final VerifyType verifyType = VerifyType.DIRECT_ADD; // 发起好友验证请求

                    NIMClient.getService(FriendService.class).addFriend(new AddFriendData(nimUserInfo.getAccount(), verifyType, et.getText().toString()))
                            .setCallback(new RequestCallback<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast("申请成功");
                                }

                                @Override
                                public void onFailed(int i) {

                                    Toast("申请失败");
                                    Log.e("失败","失败"+i);
                                }

                                @Override
                                public void onException(Throwable throwable) {

                                    Toast("申请失败");
                                    Log.e("异常","失败"+throwable.toString());
                                }
                            });

                    dialog.dismiss();

                } else {

//                    CustomToast.getInstance().showToastCustom(InformationFragment.this.getContext(), "非法输入", Gravity.BOTTOM);

                }

            }
        });



    }
}
