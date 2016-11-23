package y2w.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.tencent.bugly.crashreport.CrashReport;
import com.xsj.crasheye.Crasheye;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.imlib.IMClient;
import com.yun2win.utils.LogFileUtil;

import y2w.common.UserInfo;
import y2w.db.DaoManager;
import y2w.httpApi.ApiTemplate;
import y2w.httpApi.httpConfig;
import y2w.manage.Users;
import y2w.ui.activity.LoginActivity;
import y2w.ui.activity.MainActivity;
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
    private RequestQueue mRequestQueue;
    private ApiTemplate mApi;
    public AMapLocationClientOption mLocationOption = null;
    AMapLocationClient mlocationClient;
    public AMapLocation locaMapLocation;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        //SDK初始化
        IMClient.init(appContext);
//        AVClient.init(this);

        LogFileUtil.isWrite=true;
        CrashReport.initCrashReport(appContext);
        appKeyInit();
        reloginCallBack();
        //agora
        Crasheye.initWithNativeHandle(this, "06798b00");
        //volley
        mRequestQueue = Volley.newRequestQueue(appContext);
        mApi = new ApiTemplate(mRequestQueue, httpConfig.URL);
        initlocation();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
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
    public AMapLocation  getLocation(){
        return locaMapLocation;
    }

    private int activityCount=0;//activity的count数
    private boolean isForeground=true;//是否在前台
    private String currentActivityname;
    private void reloginCallBack() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
            @Override
            public void onActivityStarted(Activity activity) {
                    activityCount++;
                    String classname= activity.getClass().getName();
                    currentActivityname = classname;
                    String account = Users.getInstance().getCurrentUser().getEntity().getAccount();
                    if(StringUtil.isEmpty(account)){
                        if(!isForeground || classname.equals(MainActivity.class.getName())) {
                            if(AppData.getInstance().getMainActivity()!=null) {
                                try {
                                    Users.getInstance().getCurrentUser().getImBridges().disConnect();
                                } catch (Exception e) {
                                }
                                Intent intent = new Intent(AppData.getInstance().getMainActivity(), SplashActivity.class);
                                AppData.getInstance().getMainActivity().startActivity(intent);
                                DaoManager.getInstance(appContext).close();
                                AppData.getInstance().getMainActivity().finish();
                            }
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

    public String getCurrentActivityname(){
        if(Tool.getAppSatus(this,getPackageName())!=1){
            return  "backstage";
        }
        if(isForeground){
            return currentActivityname;
        }else{
            return  "backstage";
        }
    }
    public void logout(){
        UserInfo.clearPassWord();
        Intent intent = new Intent(AppData.getInstance().getMainActivity(), LoginActivity.class);
        AppData.getInstance().getMainActivity().startActivity(intent);
        DaoManager.getInstance(appContext).close();
        try {
            Users.getInstance().getCurrentUser().getImBridges().disConnect();
            AppData.getInstance().getMainActivity().finish();
        } catch (Exception e) {
        }
    }
    public void restartApp(){
        Intent intent = new Intent(AppData.getInstance().getMainActivity(), SplashActivity.class);
        AppData.getInstance().getMainActivity().startActivity(intent);
        DaoManager.getInstance(appContext).close();
        try {
            Users.getInstance().getCurrentUser().getImBridges().disConnect();
            AppData.getInstance().getMainActivity().finish();
        } catch (Exception e) {
        }

    }

    /**
     *  获得位置
     */
    private void initlocation(){
        mlocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                locaMapLocation =aMapLocation;
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        aMapLocation.getLatitude();//获取纬度
                        aMapLocation.getLongitude();//获取经度
                        aMapLocation.getAccuracy();//获取精度信息
                    } else {
                    }
                }
            }
        });
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setInterval(30000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    /**
     * 得到http访问的接口类
     */
    public ApiTemplate getmApi(){
        return mApi;
    }


    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }
}
