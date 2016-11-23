package y2w.httpApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by SongJie on 09/12 0012.
 */
public class BaseTemplate {
    protected VolleyClient mClient;

    public BaseTemplate(VolleyClient client) {
        mClient = client;
    }

    public String getUrl(String method) {
        return mClient.getServerUrl(method);
    }

    protected Object postRequest(MyRequest<?> request) {
        Object tag = System.currentTimeMillis();
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy());
        mClient.post(request);
        return tag;
    }

    protected Object getRequest(StringRequest request) {
        Object tag = String.valueOf(System.currentTimeMillis());
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy());
        mClient.get(request);
        return tag;
    }

    public void cancelRequest(Object tag) {
        mClient.cancel(tag);
    }

}
