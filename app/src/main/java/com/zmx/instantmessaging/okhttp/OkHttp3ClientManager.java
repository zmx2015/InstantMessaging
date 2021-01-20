package com.zmx.instantmessaging.okhttp;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 程序猿：胖胖祥
 * 时 间：2021/1/3  21:11
 * 功 能：
 */

public class OkHttp3ClientManager {

    private String TAG = this.getClass().getName();
    private static OkHttp3ClientManager mInstance;
    private OkHttpClient mOkHttpClient;

    private String data="";

    private OkHttp3ClientManager() {
        mOkHttpClient = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS) // 读取超时
                .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
                .writeTimeout(60, TimeUnit.SECONDS) // 写入超时
                .build();
    }

    public static OkHttp3ClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttp3ClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttp3ClientManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * post请求参数
     *
     * @param BodyParams
     * @return
     */
    private RequestBody SetRequestBody(Map<String, Object> BodyParams) {

        RequestBody body = null;
        // 创建请求的参数body
        FormBody.Builder builder = new FormBody.Builder();

        // 遍历key
        if (null != BodyParams) {
            for (Map.Entry<String, Object> entry : BodyParams.entrySet()) {

                Log.e("Key = "+ entry.getKey(),"Value = "+entry.getValue());
                builder.add(entry.getKey(), entry.getValue().toString());

            }
        }

        body = builder.build();
        return body;
    }

    /**
     * get方法连接拼加参数
     *
     * @param mapParams
     * @return
     */
    private String setUrlParams(Map<String, Object> mapParams) {
        String strParams = "";
        if (mapParams != null) {
            Iterator<String> iterator = mapParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                strParams += "&" + key + "=" + mapParams.get(key);
            }
        }

        return strParams;
    }

    /**
     * POST方法获取实体bean
     *
     * @param reqUrl
     * @param map
     * @param mHandler
     * @param rspClass
     */
    public void postBeanExecute(String reqUrl, Map<String, Object> map, final Handler mHandler,
                                final Class<?> rspClass) {
        RequestBody body = SetRequestBody(map);
        Request request = new Request.Builder().url(reqUrl).post(body).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String Result = arg1.body().string();
//          Result = PUB.decodeBase64(Result);
                Log.d(TAG, "[postBeanExecute]Call===" + arg1.code() + "Response===" + Result);

                Message mess = mHandler.obtainMessage();
                if (arg1.code() == 200) {
                    try {
                        // 转换返回结果信息给BEAN
                        mess.obj = new Gson().fromJson(Result, rspClass);
                    } catch (Exception e) {
                        mess.what = 404;
                        mess.obj = "HTTP数据异常-0002";
                    }
                } else {
                    mess.what = arg1.code();
                    mess.obj = "HTTP状态异常-" + arg1.code();
                }
                mHandler.sendMessage(mess);
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.d(TAG, "[onFailure]Call===" + arg0 + "IOException===" + arg1.toString());

                Message mess = mHandler.obtainMessage();
                mess.what = 404;
                mess.obj = "HTTP通讯错误-0001";
                mHandler.sendMessage(mess);
            }
        });
    }

    /**
     * Get方法获取实体bean
     *
     * @param reqUrl
     * @param map
     * @param mHandler
     * @param rspClass
     */
    public void getBeanExecute(String reqUrl, Map<String, Object> map, final Handler mHandler,
                               final Class<?> rspClass) {
        String UrlParams = setUrlParams(map);
        String URL = reqUrl + "?" + UrlParams;
        Log.d(TAG, "[getBeanExecute]URL===" + URL);

        Request request = new Request.Builder().url(URL).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String Result = arg1.body().string();
//          Result = PUB.decodeBase64(Result);
                Log.d(TAG, "[onResponse]Call===" + arg1.code() + "Response===" + Result);

                Message mess = mHandler.obtainMessage();
                if (arg1.code() == 200) {
                    try {
                        // 转换返回结果信息给BEAN
                        mess.obj = new Gson().fromJson(Result, rspClass);
                    } catch (Exception e) {
                        mess.what = 404;
                        mess.obj = "HTTP数据异常-0002";
                    }
                } else {
                    mess.what = arg1.code();
                    mess.obj = "HTTP状态异常-" + arg1.code();
                }
                mHandler.sendMessage(mess);
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.d(TAG, "[onFailure]Call===" + arg0 + "IOException===" + arg1.toString());

                Message mess = mHandler.obtainMessage();
                mess.what = 404;
                mess.obj = "HTTP通讯错误-0001";
                mHandler.sendMessage(mess);
            }
        });
    }


    /**
     * Post 获取String数据
     *
     * @param reqUrl
     * @param mHandler
     */
    public void postStringExecute(String reqUrl, Map<String, Object> map, final Handler mHandler, final int state, final int errorState) {

        RequestBody body = SetRequestBody(map);
        Request request = new Request.Builder().url(reqUrl).post(body).build();
        Log.e("路径", "[postStringExecute]URL===" + reqUrl);

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String Result = arg1.body().string();
//          Result = PUB.decodeBase64(Result);
                Log.e("路径", "[onResponse]Call===" + arg1.code() + "Response===" + Result);

                Message mess = mHandler.obtainMessage();
                if (arg1.code() == 200) {
                    try {
                        // 转换返回结果信息给BEAN
                        mess.what = state;
                        mess.obj = Result;
                    } catch (Exception e) {
                        mess.what = errorState;
                        mess.obj = "HTTP数据异常-0002";
                    }
                } else {
                    mess.what = errorState;
                    mess.obj = "HTTP状态异常-" + arg1.code();
                }
                mHandler.sendMessage(mess);
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.e("HTTP通讯错误-0001", "[onFailure]Call===" + arg0 + "IOException===" + arg1.toString());

                Message mess = mHandler.obtainMessage();
                mess.what = errorState;
                mess.obj = "HTTP通讯错误-0001";
                mHandler.sendMessage(mess);
            }
        });
    }

    /**
     * Get 获取String数据
     *
     * @param reqUrl
     * @param
     */
    public String getStringExecute(String reqUrl, Map<String, Object> map) {

        String UrlParams = setUrlParams(map);
        String URL = reqUrl + "?" + UrlParams;

        Log.e("URL",""+URL);

        Request request = new Request.Builder().url(URL).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {

                String Result = arg1.body().string();
                data = Result;

            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {

                data = "404";

            }
        });

        return data;
    }

    /**
     * Get 获取String数据
     *
     * @param reqUrl
     * @param mHandler
     */
    public void getStringExecute(String reqUrl, Map<String, Object> map, final Handler mHandler, final int state , final int errorState) {
        String UrlParams = setUrlParams(map);
        String URL = reqUrl + "?" + UrlParams;

        Log.e("URL",""+URL);
        Request request = new Request.Builder().url(URL).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {

                String Result = arg1.body().string();
                Message mess = mHandler.obtainMessage();
                if (arg1.code() == 200) {
                    try {
                        // 转换返回结果信息给BEAN
                        Log.e("请求结果","请求结果==="+ Result);
                        mess.what = state;
                        mess.obj = Result;



                    } catch (Exception e) {
                        mess.what = errorState;
                        mess.obj = "HTTP数据异常-0002";
                    }
                } else {
                    mess.what = errorState;
                    mess.obj = "HTTP状态异常-" + arg1.code();
                }
                mHandler.sendMessage(mess);
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.d(TAG, "[onFailure]Call===" + arg0 + "IOException===" + arg1.toString());

                Message mess = mHandler.obtainMessage();
                mess.what = errorState;
                mess.obj = "HTTP通讯错误-0001";
                mHandler.sendMessage(mess);
            }
        });
    }

    //网络请求方式

    /**
     *
     * @param reqUrl 请求的url
     * @param map    请求的参数
     * @param mHandler  处理返回数据的handler
     * @param state     在handler处理成功返回的数据
     * @param errorState 在handler处理失败返回的数据
     */
    public void NetworkRequestMode(String reqUrl, Map<String, Object> map, final Handler mHandler, final int state , final int errorState){

//        if(MyApplication.getNetworkRequestMode().equals("get")){
//
            getStringExecute(reqUrl,map,mHandler,state,errorState);
//
//        }else{

//        postStringExecute(reqUrl,map,mHandler,state,errorState);

//        }


    }

    /**
     * 取消网络请求
     */
    public void cancelHttp(){

        if(mOkHttpClient != null){

            mOkHttpClient.dispatcher().cancelAll();

        }

    }
}