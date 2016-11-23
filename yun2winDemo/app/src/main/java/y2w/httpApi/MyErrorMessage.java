package y2w.httpApi;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

/**
 * Created by SongJie on 11/07 0007.
 ** @Description: S 系统抛出错误
 */
public class MyErrorMessage extends VolleyError {
    private static final String TAG = "MyErrorMessage";

    public static final String ERROR_NO_CONNECTION = "S001";
    public static final String ERROR_NETWORK = "S002";
    public static final String ERROR_SERVER = "S003";
    public static final String ERROR_TIMEOUT = "S004";
    public static final String ERROR_PARSE = "S005";
    public static final String ERROR_UNKNOWN = "S006";

    private String code;
    private String message;

    //用户自定义错误
    public MyErrorMessage(String code, String msg) {
        this.code = code;
        this.message = msg;
    }
    //volley错误
    public MyErrorMessage(VolleyError e) {
        if (e instanceof NoConnectionError) {
            code = MyErrorMessage.ERROR_NO_CONNECTION;
            message = "Network No Connection";
        } else if (e instanceof NetworkError) {
            code = MyErrorMessage.ERROR_NETWORK;
            message = "Network Error";
        } else if (e instanceof TimeoutError) {
            code = MyErrorMessage.ERROR_TIMEOUT;
            message = "Timeout Error";
        } else if (e instanceof ParseError) {
            code = MyErrorMessage.ERROR_PARSE;
            message = "Xml Parser Error";
        } else if (e instanceof ServerError) {
            code = MyErrorMessage.ERROR_SERVER;
            message = "Server Responded with an error response";
        } else if (e instanceof MyErrorMessage){
            MyErrorMessage msg = (MyErrorMessage) e;
            code = msg.code;
            message = msg.message;
        } else {
            code = MyErrorMessage.ERROR_UNKNOWN;
            message = e.getMessage();
            if(message.contains("INTERNET")) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
