package y2w.httpApi;

import com.android.volley.RequestQueue;

/**
 * Created by SongJie on 09/12 0012.
 */
public class ApiTemplate {
    private VolleyClient mClient;

    private MyTemplate myOperations;

    public ApiTemplate(RequestQueue queue, String server) {
        mClient = new VolleyClient(queue, server);
        myOperations = new MyTemplate(mClient);
    }

    public MyTemplate getMyOperations() {
        return myOperations;
    }
}
