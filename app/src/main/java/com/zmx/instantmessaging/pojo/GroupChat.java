package com.zmx.instantmessaging.pojo;

import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

/**
 * 程序猿：胖胖祥
 * 时 间：2020/12/30  15:09
 * 功 能：
 */
public class GroupChat {

    private Boolean state;
    private NimUserInfo info;

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public NimUserInfo getInfo() {
        return info;
    }

    public void setInfo(NimUserInfo info) {
        this.info = info;
    }
}
