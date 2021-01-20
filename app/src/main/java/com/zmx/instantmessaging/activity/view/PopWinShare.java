package com.zmx.instantmessaging.activity.view;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zmx.instantmessaging.R;

/**
 * 程序猿：胖胖祥
 * 时 间：2020/12/29  14:30
 * 功 能：
 */
public class PopWinShare extends PopupWindow {

    private View mainView;
    private TextView layoutShare, layoutCopy;

    public PopWinShare(Activity paramActivity, View.OnClickListener paramOnClickListener, int paramInt1, int paramInt2) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(R.layout.popwin_share, null);
        //分享布局
        layoutShare = (mainView.findViewById(R.id.group_chat));
        //复制布局
        layoutCopy = mainView.findViewById(R.id.add_friend);
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
            layoutShare.setOnClickListener(paramOnClickListener);
            layoutCopy.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //设置显示隐藏动画
        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));
    }

}