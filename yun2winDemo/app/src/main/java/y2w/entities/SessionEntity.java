package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.Json;
import java.io.Serializable;


/**
 * 会话表
 * Created by yangrongfang on 2016/1/16.
 */
@DatabaseTable(tableName = "SessionEntity")
public class SessionEntity implements Serializable {

    //会话id
    @DatabaseField
    private String id;
    //会话类型为p2p时，对方Id
    @DatabaseField
    private String otherSideId;
    //会话名称
    @DatabaseField
    private String name;
    //会话类型
    @DatabaseField
    private String type;
    //安全类型
    @DatabaseField
    private String secureType;
    //会话简介
    @DatabaseField
    private String description;
    //成员变更时间戳
    @DatabaseField
    private String createMTS;
    //成员信息变更时间戳
    @DatabaseField
    private String updateMTS;
    //会话创建时间
    @DatabaseField
    private String createdAt;
    //会话更新时间
    @DatabaseField
    private String updatedAt;
    //会话头像
    @DatabaseField
    private String avatarUrl;
    @DatabaseField
    //此条数据所有者
    private String myId;

    public static SessionEntity parse(Json json){
        SessionEntity Entity = new SessionEntity();
        Entity.setId(json.getStr("id"));
        Entity.setName(json.getStr("name"));
        Entity.setType(json.getStr("type"));
        Entity.setSecureType(json.getStr("secureType"));
        Entity.setDescription(json.getStr("description"));
        Entity.setCreateMTS("");
        Entity.setUpdateMTS("");
        Entity.setCreatedAt(json.getStr("createdAt"));
        Entity.setUpdatedAt(json.getStr("updatedAt"));
        Entity.setAvatarUrl(json.getStr("avatarUrl"));
        return  Entity;
    }


    /**
     * 获取会话id
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * 设置会话id
     * @param id 会话id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * p2p时，获取对方Id
     * @return
     */
    public String getOtherSideId() {
        return otherSideId;
    }

    /**
     * p2p时，设置对方Id
     * @param otherSideId
     */
    public void setOtherSideId(String otherSideId) {
        this.otherSideId = otherSideId;
    }

    /**
     * 获取会话名称
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 设置会话名称
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取会话类型
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * 设置会话类型
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取会话安全类型
     * @return
     */
    public String getSecureType() {
        return secureType;
    }

    /**
     * 设置会话安全类型
     * @param secureType
     */
    public void setSecureType(String secureType) {
        this.secureType = secureType;
    }

    /**
     * 获取会话简介
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置会话简介
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateMTS() {
        return createMTS;
    }

    public void setCreateMTS(String createMTS) {
        this.createMTS = StringUtil.getOPerTime(createMTS);
    }

    public String getUpdateMTS() {
        return updateMTS;
    }

    public void setUpdateMTS(String updateMTS) {
        this.updateMTS = StringUtil.getOPerTime(updateMTS);
    }

    /**
     * 获取会话创建时间
     * @return
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置会话创建时间
     * @param createdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = StringUtil.getOPerTime(createdAt);
    }

    /**
     * 获取会话更新时间
     * @return
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置会话更新时间
     * @param updatedAt
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = StringUtil.getOPerTime(updatedAt);
    }

    /**
     * 获取会话头像
     * @return
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 设置会话头像
     * @param avatarUrl
     */
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

