package com.zmx.instantmessaging;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

public class MySharedPreferences {

    private final String SHAREDPRE_FILE_NAME = "UserMessage"; // 配置文件名
    public static final String pwd = "role";
    public static final String icon = "avatar";
    public static final String name = "name";
    public static final String gender = "gender";
    public static final String token = "token";
    public static final String username = "username";
    public static final String isaddfriend = "isaddfriend";
    public static final String isgroupchat = "isgroupchat";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    /**
     * 单例对象实例
     */
    private static MySharedPreferences instance = null;

    public static MySharedPreferences getInstance(Context mContext) {
        if (instance == null) {
            synchronized (MySharedPreferences.class) {
                if (instance == null) {
                    instance = new MySharedPreferences(mContext);
                }
            }
        }
        return instance;
    }

    /**
     * 构造函数
     * @param mContext：上下文环境
     */
    public MySharedPreferences(Context mContext) {
        sp = mContext.getSharedPreferences(SHAREDPRE_FILE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }
    /**
     * 清除本地保存的所有数据
     */
    public void clear(){
        editor.clear();
        editor.commit();
    }

    /**
     * 保存数据到本地
     * @param key 键
     * @param value 值 Object:目前只支持：String/Boolean/Float/Integer/Long
     */
    public void saveKeyObjValue(String key, Object value){
        if(value instanceof String){
            editor.putString(key, (String)value);
        }else if(value instanceof Boolean){
            editor.putBoolean(key, (Boolean) value);
        }else if(value instanceof Float){
            editor.putFloat(key, (Float) value);
        }else if(value instanceof Integer){
            editor.putInt(key, (Integer) value);
        }else if(value instanceof Long){
            editor.putLong(key, (Long) value);
        }
        editor.commit();
    }

    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        //获取SharePreference对象的编辑对象，才能进行数据的存储
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.putString(tag, strJson);
        editor.commit();

    }
//
//    /**
//     * 获取List
//     * @param tag
//     * @return
//     */
//    public  List<StoresMessage> getDataList(String tag) {
//        List<StoresMessage> datalist=new ArrayList<StoresMessage>();
//        String strJson = instance.getString(tag, null);
//        if (null == strJson) {
//            return datalist;
//        }
//        Gson gson = new Gson();
//        datalist = gson.fromJson(strJson, new TypeToken<List<StoresMessage>>() {
//        }.getType());
//        return datalist;
//
//    }


    /**
     * 获取保存在本地的数据 未加密的
     * @param key
     * @param defValue
     * @return String
     */
    public String getString(String key, String defValue){
        return sp.getString(key, defValue);
    }

    public int getIntType(String key, int defValue){
        return sp.getInt(key, defValue);
    }

    /**
     * 获取保存在本地的数据 未加密的
     * @param key
     * @param defValue
     * @return Boolean
     */
    public boolean getBoolean(String key, boolean defValue){
        return sp.getBoolean(key, defValue);
    }


}