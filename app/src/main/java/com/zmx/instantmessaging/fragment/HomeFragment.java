package com.zmx.instantmessaging.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.zmx.instantmessaging.MySharedPreferences;
import com.zmx.instantmessaging.R;
import com.zmx.instantmessaging.activity.GroupChatActivity;
import com.zmx.instantmessaging.activity.SearchFriendsActivity;
import com.zmx.instantmessaging.activity.view.CustomToast;
import com.zmx.instantmessaging.activity.view.LoadingDialog;
import com.zmx.instantmessaging.activity.view.PopWinShare;
import com.zmx.instantmessaging.okhttp.OkHttp3ClientManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends SimpleImmersionFragment{

    private PopWinShare popWinShare;

    private ImageView add;
    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        showLoadingView("网页加载中...");

        webView = view.findViewById(R.id.webview);
        add = view.findViewById(R.id.sousuo);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popWinShare == null) {
                    //自定义的单击事件
                    HomeFragment.OnClickLintener paramOnClickListener = new HomeFragment.OnClickLintener();
                    popWinShare = new PopWinShare(HomeFragment.this.getActivity(), paramOnClickListener, 400, 300);
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

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", MySharedPreferences.getInstance(this.getActivity()).getString(MySharedPreferences.username,""));
        params.put("token",MySharedPreferences.getInstance(this.getActivity()).getString(MySharedPreferences.token,""));
        OkHttp3ClientManager.getInstance().NetworkRequestMode("http://154.83.13.210:801/ConfigMethod/Get_Web_Url", params, handler, 1, 404);

        return view;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case 1:

                    try {
                        JSONObject object = new JSONObject(msg.obj.toString());

                        if (object.getInt("code") == 200){



                            //声明WebSettings子类
                            WebSettings webSettings = webView.getSettings();

//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
                            webSettings.setJavaScriptEnabled(true);

//设置自适应屏幕，两者合用
                            webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
                            webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

//缩放操作
                            webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
                            webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
                            webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

//其他细节操作
                            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //关闭webview中缓存
                            webSettings.setAllowFileAccess(true); //设置可以访问文件
                            webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
                            webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
                            webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
                            Log.e("路径","路径"+object.getString("url"));
                            webView.loadUrl(object.getString("url"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                            }

                            // 允许所有SSL证书
                            webView.setWebViewClient(new WebViewClient() {
                                /**
                                 * Description: handle https
                                 * Created by Michael Lee on 12/6/16 08:38
                                 */
                                @Override
                                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                                    handler.proceed();
                                }

                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    String decoded_url = url;
                                    try {
                                        decoded_url = URLDecoder.decode(url, "UTF-8");
                                    } catch (Exception e) {
                                    }

                                    if (!checkUrlValid(decoded_url)) {
                                        super.shouldOverrideUrlLoading(view,url);
                                        return false;
                                    } else {
                                        // Do your special things
                                        return true;
                                    }
                                }

                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    Log.d("WebView", "onPageStarted : " + url);
                                    if (!checkUrlValid(url)) {
                                        super.onPageStarted(view,url,favicon);
                                        Log.d("WebView","Handle url with system~~");
                                        return;
                                    } else {
                                        // Do your special things
                                    }
                                }

                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    Log.d("WebView","onPageFinished : " + url);
                                    if (!checkUrlValid(url)) {
                                        super.onPageFinished(view,url);
                                        Log.d("WebView","Handle url with system~~");

                                        dismissLoadingView();
                                        return;
                                    } else {
                                        // Do your special things

                                        dismissLoadingView();
                                    }
                                }


                            });

                        }else{

                            CustomToast.getInstance().showToastCustom(HomeFragment.this.getContext(), object.getString("info"), Gravity.CENTER);
                            dismissLoadingView();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

            }

        }
    };

    private boolean checkUrlValid(String aUrl) {
        boolean result = true;
        if (aUrl == null || aUrl.equals("") || !aUrl.contains("http")) {
            return false;
        }
        if (aUrl.contains("s.click")) {
            result = false;
        }
        return result;
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

                        if (MySharedPreferences.getInstance(HomeFragment.this.getContext()).getString(MySharedPreferences.isgroupchat,"").equals("0")){
                            CustomToast.getInstance().showToastCustom(HomeFragment.this.getContext(), "没有该权限", Gravity.CENTER);

                        }else{

                            List<String> fs = NIMClient.getService(FriendService.class).getFriendAccounts();
                            friends = NIMClient.getService(UserService.class).getUserInfoList(fs);

                            if (friends == null){

                                CustomToast.getInstance().showToastCustom(HomeFragment.this.getContext(), "没有好友，无法发起群聊", Gravity.CENTER);
                                popWinShare.dismiss();
                                return;

                            }

                            intent.setClass(HomeFragment.this.getContext(), GroupChatActivity.class);
                            startActivity(intent);

                        }

                        popWinShare.dismiss();

                        break;
                    case R.id.add_friend:

                        if (MySharedPreferences.getInstance(HomeFragment.this.getContext()).getString(MySharedPreferences.isaddfriend,"").equals("0")){

                            CustomToast.getInstance().showToastCustom(HomeFragment.this.getContext(), "没有该权限", Gravity.CENTER);

                        }else {

                            intent.setClass(HomeFragment.this.getContext(), SearchFriendsActivity.class);
                            startActivity(intent);

                        }
                        popWinShare.dismiss();
                        break;

                    default:
                        break;
            }


        }
    }


    protected LoadingDialog mLoadingDialog; //显示正在加载的对话框
    /**
     * 设置加载提示框
     */
    protected void showLoadingView(String message) {

        if (mLoadingDialog == null) {

            mLoadingDialog = new LoadingDialog(HomeFragment.this.getContext(), message, false);

        }
        mLoadingDialog.show();

    }

    /**
     * 数据加载完成
     */
    /**
     * 数据加载完成
     */
    public void dismissLoadingView(){

        if (mLoadingDialog != null) {

            HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });

        }

    }

}
