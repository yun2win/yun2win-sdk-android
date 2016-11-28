package y2w.common;

import android.os.Environment;

import y2w.base.AppContext;

/**
 * Created by yangrongfang on 2016/1/11.
 */
public class Config {
    public static final String Host_Port = "http://console.yun2win.com:8080";
    public static final String COMPAMY_NAME = "yun2win";
    public static final String Token_Get = "http://console.yun2win.com/oauth/token";
    public static final String File_Host="http://console.yun2win.com:443/#/imGroupFile/index.html";
    private static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    // private static String PATH = AppContext.getAppContext().getFilesDir().getAbsolutePath();
    public static final String CACHE_PATH_FILE = PATH + "/"+COMPAMY_NAME+"/file/";
    public static final String CACHE_PATH_IMAGE = PATH + "/"+COMPAMY_NAME+"/image/";
    public static final String CACHE_PATH_EMOJI =PATH + "/"+COMPAMY_NAME+"/emoji/";
    public static final String CACHE_PATH_MOVIE = PATH + "/"+COMPAMY_NAME+"/movie/";
    public static final String CACHE_PATH_LOG = PATH + "/"+COMPAMY_NAME+"/log/";

    public static final String DEFAULT_PATH_FILE = AppContext.getAppContext().getExternalFilesDir(null).getAbsolutePath()+"/";

    public static final int LOAD_MESSAGE_COUNT = 20;//每页消息加载数目
    public final static boolean Client_Mock = true;
    public final static String Token_Prefix = "Bearer ";

    public final static String UserInfo=COMPAMY_NAME+"_user_info";
    public final static String UserMToken=COMPAMY_NAME+"_user_imtoken";
}
