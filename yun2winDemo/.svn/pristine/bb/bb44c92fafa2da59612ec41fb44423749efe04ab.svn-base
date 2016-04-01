package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.yun2win.utils.Json;

import java.io.Serializable;

/**
 * 群聊表
 * Created by yangrongfang on 2016/3/7.
 */
@DatabaseTable(tableName = "UserSessionEntity")
public class UserSessionEntity implements Serializable {

    //会话id
    @DatabaseField
    private String id;
    //会话类型为p2p时，对方Id
    @DatabaseField
    private String sessionId;
    //会话名称
    @DatabaseField
    private String name;
    //会话类型
    @DatabaseField
    private boolean isDelete;
    //会话创建时间
    @DatabaseField
    private String createdAt;
    //会话更新时间
    @DatabaseField
    private String updatedAt;
    //会话头像
    @DatabaseField
    private String avatarUrl;
    //此条数据所有者
    @DatabaseField
    private String myId;

    public static UserSessionEntity parse(Json json){
        UserSessionEntity Entity = new UserSessionEntity();
        Entity.setId(json.getStr("id"));
        Entity.setSessionId(json.getStr("sessionId"));
        Entity.setName(json.getStr("name"));
        Entity.setIsDelete(json.getBool("isDelete"));
        Entity.setCreatedAt(json.getStr("createdAt"));
        Entity.setUpdatedAt(json.getStr("updatedAt"));
        Entity.setAvatarUrl(json.getStr("avatarUrl"));
        return  Entity;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
