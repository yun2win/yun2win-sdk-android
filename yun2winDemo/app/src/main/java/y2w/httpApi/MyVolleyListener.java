package y2w.httpApi;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by SongJie on 09/12 0012.
 */
public class MyVolleyListener<T> implements Response.Listener<T>, Response.ErrorListener {

    private MyCallback<T> mCallback;

    public MyVolleyListener(MyCallback<T> callback) {
        mCallback = callback;
    }

    @Override
    public void onErrorResponse(VolleyError e) {
        MyErrorMessage errorMsg = new MyErrorMessage(e);
        if (null != mCallback) {
            mCallback.onError(errorMsg);
            mCallback.onFinish();
        }
    }

    @Override
    public void onResponse(T response) {
        if (null != mCallback) {
            mCallback.onSuccess(response);
            mCallback.onFinish();
        }
    }

}