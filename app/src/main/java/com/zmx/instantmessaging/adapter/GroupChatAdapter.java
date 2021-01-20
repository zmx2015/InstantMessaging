package com.zmx.instantmessaging.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.wang.adapters.adapter.BaseAdapterRvList;
import com.wang.adapters.base.BaseViewHolder;
import com.zmx.instantmessaging.R;
import com.zmx.instantmessaging.pojo.GroupChat;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 程序猿：胖胖祥
 * 时 间：2020/12/30  14:36
 * 功 能：
 */
public class GroupChatAdapter extends BaseAdapterRvList<GroupChatAdapter.ViewHolder, GroupChat> {

    public GroupChatAdapter(Activity activity) {
        super(activity);
    }

    @Override
    protected void onBindVH(ViewHolder holder, int listPosition, GroupChat gc) {

        holder.name.setText(gc.getInfo().getName());


        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {

                    gc.setState(true);

                } else {

                    gc.setState(false);

                }

                checkBoxListener.setCheckBoxListener();

            }
        });

    }

    @NonNull
    @Override
    protected ViewHolder onCreateVH(ViewGroup parent, LayoutInflater inflater) {

        GroupChatAdapter.ViewHolder holder = new GroupChatAdapter.ViewHolder(inflater.inflate(R.layout.adapter_group_chat, parent, false));
        return holder;
    }

    static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.mCheckBox)
        CheckBox mCheckBox;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface CheckBoxListener{

        void setCheckBoxListener();

    }

    private CheckBoxListener checkBoxListener;

    public void setCheckBoxListener(CheckBoxListener checkBoxListener) {
        this.checkBoxListener = checkBoxListener;
    }
}
