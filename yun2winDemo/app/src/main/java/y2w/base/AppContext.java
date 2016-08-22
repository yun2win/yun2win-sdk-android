package y2w.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.tencent.bugly.crashreport.CrashReport;
import com.y2w.av.lib.AVClient;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.imlib.IMClient;

import y2w.common.UserInfo;
import y2w.manage.Users;
import y2w.ui.activity.LoginActivity;
import y2w.ui.activity.SplashActivity;

/**
 * Created by yangrongfang on 2016/1/8.
 */
public class AppContext extends Application{

    private final String TAG = "AppContext";
    private static AppContext appContext;
    private String appKey = "";
    public static AppContext getAppContext(){
        return  appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        //SDK初始化
        IMClient.init(this);
        AVClient.init(this);
        CrashReport.initCrashReport(getApplicationContext());
        appKeyInit();
        reloginCallBack();
    }

    public String getAppKey(){
        return appKey;
    }

    private void appKeyInit(){
        ApplicationInfo appInfo = null;
        try {
            appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                appKey = (appInfo.metaData.get("YUN2WIN_APP_KEY") != null) ? appInfo.metaData.get("YUN2WIN_APP_KEY").toString() : "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
    private int activityCount=0;//activity的count数
    private boolean isForeground=true;//是否在前台
    private void reloginCallBack() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
            @Override
            public void onActivityStarted(Activity activity) {
                    activityCount++;
                    String classname= activity.getClass().getName();
                    String account = Users.getInstance().getCurrentUser().getEntity().getAccount();
                    if(StringUtil.isEmpty(account)){
                        if(!isForeground || classname.equals("y2w.ui.activity.MainActivity")){
                        try {
                            Users.getInstance().getCurrentUser().getImBridges().disConnect();
                        }catch (Exception e){
                        }
                        Intent intent = new Intent(AppData.getInstance().getMainActivity(), SplashActivity.class);
                        AppData.getInstance().getMainActivity().startActivity(intent);
                        AppData.getInstance().getMainActivity().finish();
                    }
                }
                isForeground=true;
            }
            @Override
            public void onActivityResumed(Activity activity) {
            }
            @Override
            public void onActivityPaused(Activity activity) {
            }
            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (0 == activityCount) {
                    isForeground = false;
                }
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }



    public void logout(){
        UserInfo.clearPassWord();
        Intent intent = new Intent(AppData.getInstance().getMainActivity(), LoginActivity.class);
        AppData.getInstance().getMainActivity().startActivity(intent);
    }

}
