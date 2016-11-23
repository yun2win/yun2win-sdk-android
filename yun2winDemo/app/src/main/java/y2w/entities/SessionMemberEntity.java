package y2w.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.Json;

import java.io.Serializable;

import y2w.base.AppData;

/**
 * 会话成员表
 * Created by yangrongfang on 2016/1/18.
 */
@DatabaseTable (tableName = "SessionMemberEntity")
public class SessionMemberEntity implements Serializable {

    //会话成员唯一标识码
    @DatabaseField
    private String id;
    //用户唯一标识码
    @DatabaseField
    private String userId;
    @DatabaseField
    private String name;
    @DatabaseField
    private String simpchinaname;
    @DatabaseField
    private String sessionId;
    @DatabaseField
    private String createdAt;
    @DatabaseField
    private String updatedAt;
    @DatabaseField
    private boolean isDelete;
    @DatabaseField
    private String role;
    @DatabaseField
    private String status;
    @DatabaseField
    private String avatarUrl;
    //此条数据所有者
    @DatabaseField
    private String myId;

    public static SessionMemberEntity parse(Json json){
        SessionMemberEntity entity = new SessionMemberEntity();
        entity.setId(json.getStr("id"));
        entity.setUserId(json.getStr("userId"));
        entity.setName(json.getStr("name"));
        entity.setCreatedAt(json.getStr("createdAt"));
        entity.setUpdatedAt(json.getStr("updatedAt"));
        entity.setIsDelete(json.getBool("isDelete"));
        entity.setRole(json.getStr("role"));
        entity.setStatus(json.getStr("status"));
        entity.setAvatarUrl(json.getStr("avatarUrl"));
        entity.setSimpchinaname(entity.getName());
        return entity;
    }

    public static SessionMemberEntity parseUser(ContactEntity contact){
        SessionMemberEntity entity = new SessionMemberEntity();
        entity.setId("");
        entity.setUserId(contact.getUserId());
        entity.setName(contact.getName());
        entity.setCreatedAt(contact.getCreatedAt());
        entity.setUpdatedAt(contact.getUpdatedAt());
        entity.setIsDelete(false);
        entity.setRole(contact.getRole());
        entity.setStatus(contact.getStatus());
        entity.setSimpchinaname(AppData.getInstance().getsampchina(entity.getName()));
        return entity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(String createdAt) {
        this.createdAt = StringUtil.getOPerTime(createdAt);
    }


    public String getUpdatedAt() {
        return updatedAt;
    }


    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = StringUtil.getOPerTime(updatedAt);
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getMyId() {
        return myId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getSimpchinaname() {
        return simpchinaname;
    }

    public void setSimpchinaname(String simpchinaname) {
        this.simpchinaname = simpchinaname;
    }
}
