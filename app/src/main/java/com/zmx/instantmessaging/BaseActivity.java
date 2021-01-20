package com.zmx.instantmessaging;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.business.preference.UserPreferences;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.zmx.instantmessaging.activity.view.CustomToast;
import com.zmx.instantmessaging.activity.view.LoadingDialog;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {


    protected LoadingDialog mLoadingDialog; //显示正在加载的对话框

    protected Activity mActivity;
    private Toolbar toolbar;

    /**
     * 跳转到下一个activity
     **/
    protected static final int REQUEST_ACTIVITY = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);
        mActivity = this;

        //找到资源文件的XML
        if (getLayoutId() != 0) {

            View vContent = LayoutInflater.from(mActivity).inflate(getLayoutId(), null);
            ((FrameLayout) findViewById(R.id.frame_content)).addView(vContent);

        }

        initViews();

        // 更新消息提醒配置 StatusBarNotificationConfig，以设置不响铃为例。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.ring = false;
        NIMClient.updateStatusBarNotificationConfig(config);
        NIMClient.toggleNotification(true);

    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.fontScale != 1) {
            //非默认值
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    /**
     * 初始化Activity的常用变量 举例:
     * <b>mLayoutResID=页面XML资源ID 必须存在的</b>
     */
    protected abstract int getLayoutId();

    /**
     * 初始化视图
     **/
    protected abstract void initViews();

    /**
     * 通过Class跳转界面
     *
     * @param cls
     */
    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     *
     * @param cls
     * @param bundle
     */
    protected void startActivity(Class<?> cls, Bundle bundle) {
        startActivity(cls, bundle, REQUEST_ACTIVITY);
    }

    /**
     * 通过Class跳转界面
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    protected void startActivity(Class<?> cls, Bundle bundle, int requestCode) {

        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


    /**
     * 设置加载提示框
     */
    protected void showLoadingView(String message) {

        if (mLoadingDialog == null) {

            mLoadingDialog = new LoadingDialog(this, message, false);

        }
        mLoadingDialog.show();

    }

    /**
     * 数据加载完成
     */
    protected void dismissLoadingView() {

        if (mLoadingDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });

        }

    }

    @Override
    protected void onDestroy() {

        if (mLoadingDialog != null) {

            mLoadingDialog.dismiss();

        }

        super.onDestroy();
    }


    //获取某个时间后的时间
    public static String addDateMinut(String day, int hour) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null)
            return "";
        System.out.println("front:" + format.format(date)); //显示输入的日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);// 24小时制
        date = cal.getTime();
        System.out.println("after:" + format.format(date));  //显示更新后的日期
        cal = null;
        return format.format(date);

    }

    //获取某个时间几个小时后的时间
    public static Date addDateMinut(Date date, int hour) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        System.out.println("front:" + format.format(date)); //显示输入的日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);// 24小时制
        date = cal.getTime();
        System.out.println("after:" + format.format(date));  //显示更新后的日期
        return date;

    }


    /**
     * 字符Base64加密
     *
     * @param str
     * @return
     */
    public static String encodeToString(String str) {
        try {
            return Base64.encodeToString(str.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void setToolbar(int id) {

        ImmersionBar.with(this).titleBar(id).keyboardEnable(true).init();
        toolbar = findViewById(id);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//关闭页面
            }
        });
    }

    public void Toast(String msg) {

        CustomToast.getInstance().showToastCustom(this, msg, Gravity.CENTER);

    }

    /**
     * 权限申请
     */
    public void authorityManage() {


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            try {
                //检测是否有写的权限
                int permission = ActivityCompat.checkSelfPermission(this,
                        "android.permission.WRITE_EXTERNAL_STORAGE");
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    Toast("请允许读写存储权限，否则APP无法正常运行！");
                    // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_CHECKIN_PROPERTIES,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            android.Manifest.permission.RECORD_AUDIO},1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
