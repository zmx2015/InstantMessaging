package com.zmx.instantmessaging.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.CreateTeamResult;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.zmx.instantmessaging.BaseActivity;
import com.zmx.instantmessaging.R;
import com.zmx.instantmessaging.adapter.GroupChatAdapter;
import com.zmx.instantmessaging.pojo.GroupChat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发起群聊
 */
public class GroupChatActivity extends BaseActivity implements GroupChatAdapter.CheckBoxListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.submit)
    TextView submit;

    private Integer num=0;
    private String groupId;
    private GroupChatAdapter adapter;
    private List<GroupChat> lists;
    private List<NimUserInfo> friends;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_chat;
    }

    @Override
    protected void initViews() {

        setToolbar(R.id.tool_bar);
        ButterKnife.bind(this);

        List<String> fs = NIMClient.getService(FriendService.class).getFriendAccounts();

        friends = NIMClient.getService(UserService.class).getUserInfoList(fs);
        lists = new ArrayList<>();

        for (int i=0;i<friends.size();i++){
            GroupChat gc = new GroupChat();
            gc.setState(false);
            gc.setInfo(friends.get(i));
            lists.add(gc);
        }

        adapter = new GroupChatAdapter(mActivity);
        adapter.setCheckBoxListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setListAndNotifyDataSetChanged(lists);

    }

    @OnClick(R.id.submit)
    public void onViewClicked() {

        if (num<=0){
            Toast("请选择");
            return;
        }

        List<String> sd = new ArrayList<String>();

        for (int i=0;i<lists.size();i++){

            if (lists.get(i).getState()){
                sd.add(lists.get(i).getInfo().getAccount());
            }

        }

        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        fields.put(TeamFieldEnum.Name, "zmx博客");
//                VerifyType:申请入群的验证方式
//                InviteMode 群邀请模式：谁可以邀请他人入群
        fields.put(TeamFieldEnum.VerifyType, VerifyTypeEnum.Free);
        NIMClient.getService(TeamService.class)
                //  TeamTypeEnum  Normal(0), 普通群
                //    Advanced(1);高级群
                .createTeam(fields, TeamTypeEnum.Normal, "", sd)
                .setCallback(new RequestCallback<CreateTeamResult>() {
                    @Override
                    public void onSuccess(CreateTeamResult createTeamResult) {

                        Toast("成功创建群组");
                        groupId = createTeamResult.getTeam().getId();
                        // 打开群聊界面
                        NimUIKit.startTeamSession(mActivity, groupId);
                    }

                    @Override
                    public void onFailed(int i) {
                        Log.e("TGA", i + "");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Log.e("TGA", throwable + "");
                    }
                });


    }

    @Override
    public void setCheckBoxListener() {


        for (int i=0;i<lists.size();i++){

            if (lists.get(i).getState()){
                num=num+1;
            }

        }

        if (num>0){

            submit.setBackgroundColor(getResources().getColor(R.color.there));
            submit.setText("创建("+num+")");

        }else{

            submit.setBackgroundColor(getResources().getColor(R.color.hui));
            submit.setText("创建");
        }

    }
}
