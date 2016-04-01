package y2w.service;

/**
 * 错误码类
 * Created by yangrongfang on 2016/1/23.
 */
public class ErrorCode {

    //网络错误
    public static final int EC_NETWORK_ERROR = 101;
    //帐号已被注册
    public static final int EC_ACCOUNT_ALREADY_EXIT = 103;
    //登录帐号或者密码错误
    public static final int EC_ACCOUNT_OR_PASSWORD_ERROR = 105;
    //参数错误
    public static final int EC_PARAMETER_ERROR = 107;
    //token错误
    public static final int EC_TOKEN_ERROR = 109;
    //服务器错误提示
    public static final int EC_SERVER_TIP_TO_USER_ERROR = 111;
    //服务器错误
    public static final int EC_HTTP_ERROR_401 = 111;
    public static final int EC_HTTP_ERROR_403 = 113;
    public static final int EC_HTTP_ERROR_405 = 115;
    public static final int EC_HTTP_ERROR_407 = 117;
    public static final int EC_HTTP_ERROR_409 = 119;
    public static final int EC_HTTP_ERROR_429 = 129;
    public static final int EC_HTTP_ERROR_500 = 131;

    //未知
    public static final int EC_UNKNOWN = 133;



    public static int errorCodeParse(int status){
        int value = 0;
        switch (status){
            case 500:
                value = EC_HTTP_ERROR_500 ;
                break;
            default:
                break;
        }
        return value;
    }
}
