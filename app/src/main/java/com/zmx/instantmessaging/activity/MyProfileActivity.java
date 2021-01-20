package com.zmx.instantmessaging.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.lcw.library.imagepicker.ImagePicker;
import com.zmx.instantmessaging.BaseActivity;
import com.zmx.instantmessaging.MySharedPreferences;
import com.zmx.instantmessaging.R;
import com.zmx.instantmessaging.activity.view.BitmapUtil;
import com.zmx.instantmessaging.activity.view.GlideLoader;
import com.zmx.instantmessaging.okhttp.OkHttp3ClientManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyProfileActivity extends BaseActivity {

    @BindView(R.id.user_image)
    CircleImageView userImage;
    @BindView(R.id.name_layout)
    RelativeLayout nameLayout;
    @BindView(R.id.sex_layout)
    RelativeLayout sexLayout;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.sex)
    TextView sex;

    private String new_name;
    private String gender;
    private Integer index = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_profile;
    }

    @Override
    protected void initViews() {

        ButterKnife.bind(this);
        setToolbar(R.id.tool_bar);
        Glide.with(this).load(MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.icon, "")).into(userImage);
        userName.setText(MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.name, ""));
        if (MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.gender,"").equals("0")){
            index = 0;
            sex.setText("未知");
        }else if(MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.gender,"").equals("1")){
            sex.setText("男");
            index = 1;
        }else{
            sex.setText("女");
            index = 2;
        }

    }


    private String imagePath = "";
    private int REQUEST_SELECT_IMAGES_CODE = 1;

    @OnClick({R.id.user_image, R.id.name_layout, R.id.sex_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.user_image:

                ImagePicker.getInstance()
                        .setTitle("选择图片")//设置标题
                        .showCamera(true)//设置是否显示拍照按钮
                        .showImage(true)//设置是否展示图片
                        .showVideo(false)//设置是否展示视频
                        .setSingleType(true)//设置图片视频不能同时选择
                        .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
                        .setImageLoader(new GlideLoader())//设置自定义图片加载器
                        .start(mActivity, REQUEST_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode

                break;
            case R.id.name_layout:
                alert_label();
                break;
            case R.id.sex_layout:
                showSexChooseDialog();
                break;
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 1:
                    upload();
                    break;

                case 2:

                    try {

                        JSONObject object = new JSONObject(msg.obj.toString());
                        if (object.getInt("code") == 200) {

                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.name, new_name);
                            userName.setText(new_name);

                            Toast("更新成功");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case 3:

                    try {

                        JSONObject object = new JSONObject(msg.obj.toString());
                        if (object.getInt("code") == 200) {

                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.gender, gender);
                            Toast("更新成功");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;

                case 4:

                    Glide.with(mActivity).load(MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.icon, "")).into(userImage);

                    break;


            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            List<String> imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);

            for (String s : imagePaths) {

                imagePath = s;
                handler.sendEmptyMessage(1);

            }

        }
    }


    //上传图片
    private void upload() {

        if (imagePath != null) {

            OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS) // 读取超时
                    .connectTimeout(60, TimeUnit.SECONDS) // 连接超时
                    .writeTimeout(60, TimeUnit.SECONDS) // 写入超时
                    .build();

            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM); //表单上传文件

            File file = new File(new BitmapUtil().compressImage(imagePath)); //根据路径创建file对象
            builder.addFormDataPart("files", //
                    file.getName(),
                    RequestBody.create(MediaType.parse("image/jpeg"), file));

//            builder.addFormDataPart("username", MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.username, ""));

            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url("http://154.221.26.110:8804/UserMethod/UploadFileStream?username="+MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.username, "")+"&token="+MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.token, ""))
                    .post(requestBody)
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("失败八八八八八八八onFailure: ", e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String Result = response.body().string();

                    Log.e("失败八: ",Result);
                    try {

                            String data = Result.replaceAll("\'","\"");
                       String str = data.substring(1,data.length()-1);

                        Log.e("转移: ",str);
                        JSONObject object = new JSONObject(str);

                        if (object.getInt("code") == 200) {

                            //更新头像
                            MySharedPreferences.getInstance(mActivity).saveKeyObjValue(MySharedPreferences.icon,object.getString("icon"));
                            handler.sendEmptyMessage(4);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("失败八八八八八八八onFailure: ",e.toString());
                    }

                }
            });
        }


    }

    public static String replaceSingleQuote(String s){
        Pattern p = Pattern.compile("(<[^>//]*>)");
        Matcher m = p.matcher(s);
        List<String> result = new ArrayList();
        while(m.find()){
            result.add(m.group());
        }
        for(String s1 : result){
            String s2 = s1.replace("\'", "\"");
            s = s.replace(s1, s2);
        }
        return s;
    }


    public void alert_label() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater factory = LayoutInflater.from(this);//提示框
        final View view = factory.inflate(R.layout.dialog_edit, null);//这里必须是final的
        final EditText et = view.findViewById(R.id.editText);
        et.setText(MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.name, ""));
        builder.setTitle("输入昵称");

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

                    new_name = et.getText().toString();
                    UpdateUinfo();
                    dialog.dismiss();

                } else {

                    Toast("请输入昵称!");

                }


            }
        });


    }


    private String[] sexArry = new String[]{"未知","女", "男"};// 性别选择

    private void showSexChooseDialog() {

        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框
        builder3.setSingleChoiceItems(sexArry, index, new DialogInterface.OnClickListener() {
            // 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                index = which;
                if (sexArry[which].equals("男")) {
                    sex.setText("男");
                    gender = "1";
                    UpdateUinfo_gender(1);
                } else if (sexArry[which].equals("女")){
                    sex.setText("女");
                    gender = "2";
                    UpdateUinfo_gender(2);
                }else{

                    sex.setText("未知");
                    gender = "0";
                    UpdateUinfo_gender(0);

                }

                dialog.dismiss();
            }
        });
        builder3.show();// 让弹出框显示
    }

    public void UpdateUinfo() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.username, ""));
        params.put("name", new_name);
        params.put("token", MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.token,""));
        OkHttp3ClientManager.getInstance().NetworkRequestMode("http://154.83.13.210:801/UserMethod/UpdateUinfo_name", params, handler, 2, 404);


    }

    public void UpdateUinfo_gender(Integer sex) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.username, ""));
        params.put("gender", sex + "");
        params.put("token", MySharedPreferences.getInstance(mActivity).getString(MySharedPreferences.token,""));
        OkHttp3ClientManager.getInstance().NetworkRequestMode("http://154.83.13.210:801/UserMethod/UpdateUinfo_gender", params, handler, 3, 404);

    }

}
