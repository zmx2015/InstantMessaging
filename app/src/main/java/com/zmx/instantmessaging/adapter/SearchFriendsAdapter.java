package com.zmx.instantmessaging.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.wang.adapters.adapter.BaseAdapterRvList;
import com.wang.adapters.base.BaseViewHolder;
import com.zmx.instantmessaging.BaseActivity;
import com.zmx.instantmessaging.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 程序猿：胖胖祥
 * 时 间：2020/12/29  20:18
 * 功 能：
 */
public class SearchFriendsAdapter extends BaseAdapterRvList<SearchFriendsAdapter.ViewHolder, NimUserInfo> {

    public SearchFriendsAdapter(Activity activity) {
        super(activity);
    }

    @Override
    protected void onBindVH(ViewHolder holder, int listPosition, NimUserInfo nimUserInfo) {

        holder.name.setText(nimUserInfo.getName());
        holder.add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                friendsListener.addFriends(nimUserInfo);

            }
        });

    }

    @NonNull
    @Override
    protected ViewHolder onCreateVH(ViewGroup parent, LayoutInflater inflater) {

        ViewHolder holder = new ViewHolder(inflater.inflate(R.layout.adapter_search_friends, parent, false));
        return holder;

    }

    static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.add_friends)
        TextView add_friends;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface FriendsListener{

        void addFriends(NimUserInfo nimUserInfo);

    }

    private FriendsListener friendsListener;

    public void setFriendsListener(FriendsListener friendsListener){
        this.friendsListener = friendsListener;
    }

}
