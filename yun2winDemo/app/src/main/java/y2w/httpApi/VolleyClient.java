package y2w.httpApi;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by SongJie on 09/12 0012.
 */
public class VolleyClient {
    private RequestQueue mRequestQueue;
    private String mServer;

    public VolleyClient(RequestQueue queue, String server) {
        mRequestQueue = queue;
        setServer(server);
    }

    private void setServer(String server) {
        if (!server.endsWith("/"))
            mServer = server + "/";
        else
            mServer = server;
    }

    /**
     * 得到访问的具体url接口
     */
    public String getServerUrl(String method) {
        return mServer + method;
    }

    public void post(MyRequest<?> request) {
        mRequestQueue.add(request);
    }

    public void cancel(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public void get(StringRequest request) {
        mRequestQueue.add(request);
    }

}
