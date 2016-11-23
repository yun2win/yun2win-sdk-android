package y2w.httpApi;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.y2w.av.base.AVConfig;

import java.util.HashMap;
import java.util.Map;

import y2w.common.UserMToken;
import y2w.httpApi.response.ServerErrorResult;

/**
 * Created by SongJie on 09/12 0012.
 */
public class MyRequest<T> extends Request<T> {
    private final Gson mGson = new Gson();
    private final Class<T> mClazz;
    private final Response.Listener<T> mListener;
    private Object mParam;

    public MyRequest(String url, Object param, Class<T> clazz, MyVolleyListener<T> callback) {
        super(Method.POST, url, callback);
        mParam = param;
        mClazz = clazz;
        mListener = callback;
    }

    /**
     * 数据返回
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String strRue = new String(response.data, "UTF-8");
//            if (BuildConfig.DEBUG)
//                Log.d(TAG, xml);
            if (strRue.contains("error")) { //server返回错误
                ServerErrorResult serverError = mGson.fromJson(strRue,ServerErrorResult.class);
                Response.error(new MyErrorMessage(serverError.getStatus(),serverError.getMessage()));
            }
            return Response.success(mGson.fromJson(strRue,mClazz),HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 回调
     */
    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
    /**
     * 对像转成String字符串
     */
    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mParam != null) {
            return mParam.toString().getBytes();
        }
        return super.getBody();
    }

    /**
     * 设置类型
     */
    @Override
    public String getBodyContentType() {
        return super.getBodyContentType();
    }

    /**
     * 设置头文件的鉴权
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<String, String>();
        //Users.getInstance().getCurrentUser().getToken()
        headers.put("Authorization",AVConfig.Token_Prefix+ UserMToken.getAccessToken());
//        headers.put("Authorization", AVConfig.Token_Prefix+ "0a4c9f8946159f56b073cc76b84bdc96e5e2cdd3");
        return headers;
    }

}
