package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.yun2win.utils.Json;

import java.io.Serializable;

/**
 * 用户基本信息表
 * Created by yangrongfang on 2016/3/14.
 */
@DatabaseTable(tableName = "UserEntity")
public class UserEntity implements Serializable{

    @DatabaseField
    private String id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String account;
    @DatabaseField
    private String createdAt;
    @DatabaseField
    private String updatedAt;
    @DatabaseField
    private String avatarUrl;
    //此条数据所有者
    @DatabaseField
    private String myId;

    public UserEntity(){

    }

    public static UserEntity parse(Json json){
        UserEntity user = new UserEntity();
        user.setId(json.getStr("id"));
        user.setName(json.getStr("name"));
        user.setAccount(json.getStr("email"));
        user.setCreatedAt(json.getStr("createdAt"));
        user.setUpdatedAt(json.getStr("updatedAt"));
        user.setAvatarUrl(json.getStr("avatarUrl"));
        return user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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
