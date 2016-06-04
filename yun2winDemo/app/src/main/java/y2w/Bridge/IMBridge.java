package y2w.Bridge;

import com.yun2win.imlib.IMClient;

import com.yun2win.imlib.IMSession;
import com.yun2win.utils.LogUtil;


import java.io.Serializable;

import y2w.manage.CurrentUser;
import y2w.model.Session;

/**
 * 服务器连接类
 * Created by yangrongfang on 2016/3/10.
 */
public class IMBridge implements Serializable{
    private String TAG = IMBridge.class.getSimpleName();
    private IMClient imClient;
    private CurrentUser user;
    public IMBridge(CurrentUser user){
        this.user = user;
        imClient = new IMClient(user.getAppKey(),user.getImToken().getAccessToken(),user.getEntity().getId());
    }

    public IMClient getImClient() {
        return imClient;
    }


}
