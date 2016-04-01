package com.y2w.uikit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SharedPreferUtil {

    public static void setMsgFirstSyncKey(String targetId,Context context){
        SharedPreferences preferences = context.getSharedPreferences("messageFirstSync" + targetId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1=preferences.edit();
        editor1.putInt("targetId", 1);
        editor1.commit();
    }

    public static int getMsgFirstSyncKey(String targetId,Context context){
        SharedPreferences preferences = context.getSharedPreferences("messageFirstSync"+targetId,Context.MODE_PRIVATE);
        return preferences.getInt("targetId", 0);
    }

}
