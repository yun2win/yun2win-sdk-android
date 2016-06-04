package y2w.model;

import com.y2w.uikit.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by maa2 on 2016/3/21.
 */

public class SyncQueue implements Serializable{
    private String type;
    private String content;
    private String sessionId;
    private String status;
    private String time;
    private String myId;

    public static SyncQueue parse(String type,String sessionId,String status){
        SyncQueue syncQueue = new SyncQueue();
        syncQueue.setType(type);
        syncQueue.setSessionId(sessionId);
        syncQueue.setStatus(status);
        syncQueue.setTime(StringUtil.getNowTime());
        return syncQueue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
