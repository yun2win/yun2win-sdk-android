package y2w.common;

import android.os.Environment;

/**
 * Created by maa2 on 2016/1/11.
 */
public class Config {
    public static String TOKEN = "token";
    public static String TestTpye = "local";
    public static final String CACHE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/y2w/temp/";
    public static final int LOAD_MESSAGE_COUNT = 20;//每页消息加载数目
    public final static boolean Client_Mock = true;
    public final static String Token_Prefix = "Bearer ";
}
