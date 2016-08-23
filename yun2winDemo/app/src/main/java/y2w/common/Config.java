package y2w.common;

import android.os.Environment;

/**
 * Created by yangrongfang on 2016/1/11.
 */
public class Config {
    public static final String Host_Port = "http://112.74.210.208:8080";
    public static final String Token_Get = "http://console.yun2win.com/oauth/token";
    private static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String CACHE_PATH_FILE = PATH + "/y2w/file/";
    public static final String CACHE_PATH_IMAGE = PATH + "/y2w/image/";
    public static final String CACHE_PATH_EMOJI =PATH + "/y2w/emoji/";
    public static final String CACHE_PATH_MOVIE = PATH + "/y2w/movie/";
    public static final int LOAD_MESSAGE_COUNT = 20;//每页消息加载数目
    public final static boolean Client_Mock = true;
    public final static String Token_Prefix = "Bearer ";


}
