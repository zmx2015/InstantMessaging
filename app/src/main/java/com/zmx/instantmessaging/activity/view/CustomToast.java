package com.zmx.instantmessaging.activity.view;


import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zmx.instantmessaging.R;

public class CustomToast {
    private static CustomToast instance;
    private Toast toast;
    private final int MARGIN_DP = 200;

    public CustomToast() {
    }
    public static CustomToast getInstance(){
        if (instance == null){
            synchronized (CustomToast.class){
                if (instance == null){
                    instance = new CustomToast();
                }
            }
        }
        return instance;
    }
    public void cancle(){
        if (toast != null){
            toast.cancel();
            toast = null;
        }
    }

    /**
     * 显示自定义布局
     * @param context 上下文对象
     * @param msg   显示内容
     * @param gravity   显示位置
     */
    public void showToastCustom(Context context,String msg,int gravity){
        cancle();
        if (TextUtils.isEmpty(msg)){
            return;
        }
        View layout = View.inflate(context,R.layout.toast_center_horizontal,null);//获取布局文件
        TextView txtMsg = layout.findViewById(R.id.toast_msg);  //获取需要显示的文字
        txtMsg.setText(msg);//设置文字

        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);  //显示时长
        //判断需要显示的位置
        //setGravity(参数1：显示的位置， 参数2:X方向偏移， 参数3：Y方向偏移);
        if (gravity == Gravity.TOP){
            toast.setGravity(gravity,0,MARGIN_DP);
        }else if (gravity == Gravity.BOTTOM){
            toast.setGravity(gravity,0,MARGIN_DP);
        }else {
            toast.setGravity(gravity,0,0);
        }
        toast.setView(layout);
        toast.show();   //记得要把Toast显示出来
    }
}


