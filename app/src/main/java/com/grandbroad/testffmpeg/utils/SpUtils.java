package com.grandbroad.testffmpeg.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

public class SpUtils
{
    /**
     * 分辨率
     * @param screensize
     * @param context
     */
    public static void saveScreenSize(String screensize,Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("screen",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("size",screensize);
        editor.apply();
    }

    public static String getScreenSize(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("screen",Context.MODE_PRIVATE);
       return preferences.getString("size",null);
    }

    /**
     * 保存url
     * @param url
     * @param context
     */
    public static void saveUrl(String url,Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("url",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("url",url);
        editor.apply();
    }

    public static String getUrl(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("url",Context.MODE_PRIVATE);
        return preferences.getString("url",null);
    }

    public static void setQultey(int qulality,Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("dd",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("dd",qulality);
        editor.apply();
    }

    public static int getQultey(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("dd",Context.MODE_PRIVATE);
        return preferences.getInt("dd",0);
    }

    /**
     * 设置帧率
     * @param fps
     * @param context
     */
    public static void saveFps(int fps,Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("fps",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("fps",fps);
        editor.apply();
    }

    public static int getFps(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("fps",Context.MODE_PRIVATE);
        return preferences.getInt("fps",30);
    }
    /**
     * 码率
     * @param speed
     * @param context
     */
    public static void saveSpeed(int speed,Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("speed",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("speed",speed);
        editor.apply();
    }

    public static int getSpeed(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("speed",Context.MODE_PRIVATE);
        return preferences.getInt("speed",800);
    }
}
