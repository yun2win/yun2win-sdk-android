package y2w.model;

import com.y2w.uikit.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by maa2 on 2016/3/21.
 */

public class SyncQueue implements Serializable{
    private int type;
    private String sessionId;
    private String status;
    private String time;
    private String myId;

    public static SyncQueue parse(int type,String sessionId,String status){
        SyncQueue syncQueue = new SyncQueue();
        syncQueue.setType(type);
        syncQueue.setSessionId(sessionId);
        syncQueue.setStatus(status);
        syncQueue.setTime(StringUtil.getNowTime());
        return syncQueue;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
