package y2w.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by SongJie on 09/22 0022.
 */
public class Tool {

    /**
     * 返回app运行状态
     * 1:程序在前台运行
     * 2:程序在后台运行
     * 3:程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static int getAppSatus(Context context, String pageName) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(50);

        //判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName)) {
            return 1;
        } else {
            //判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName)) {
                    return 2;
                }
            }
            return 3;//栈里找不到，返回3
        }
    }

    /**
     * 时间转换为00：00
     */
    public static String stringForTimeAuto(long timeS) {
        long seconds = timeS % 60;
        long minutes = (timeS / 60) % 60;
        long hours = timeS / 3600;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return String.format("%02d:%02d", minutes, seconds).toString();
        }
    }
    /**
     * 判断是否在音视频界面
     */
    public static boolean isShowAvActivity(Context context){

        return false;
    }

    /**
     * List<String>转换为str;str;str
     */
    public static String getListString(List<String> listStr,String mark){
        String strCount = null;
        for (String str : listStr) {
            if(strCount == null){
                strCount = str;
            }else{
                strCount = strCount + mark + str;
            }
        }
        return strCount;
    }
}
