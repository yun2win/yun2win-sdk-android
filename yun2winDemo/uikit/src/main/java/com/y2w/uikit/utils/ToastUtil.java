package com.y2w.uikit.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by maa2 on 2016/2/26.
 */
public class ToastUtil {

    private static Toast mToast;

    Handler handlerToast = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    public static void initToast(Context context){
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }
    public static void showToast(Context context, String text, int duration) {
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(context, text, duration);
        mToast.show();
    }

    public static void ToastMessage(Context context, String msg) {
        try{
            if(msg==null)
                msg="网络不给力";
            msg=msg.replace("java.lang.Exception:", "").replace("java.lang.NullPointerException", "");
            showToast(context, msg, Toast.LENGTH_LONG);
        }catch(Exception ex){
         String e =ex.toString();
        }
    }
}
