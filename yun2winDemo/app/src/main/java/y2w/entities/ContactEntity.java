package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.Json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录表
 * Created by yangrongfang on 2016/1/18.
 */
@DatabaseTable(tableName = "ContactEntity")
public class ContactEntity implements Serializable {

    //联系人唯一标识码
    @DatabaseField
    private String id;
    //用户唯一标识码
    @DatabaseField
    private String userId;
    @DatabaseField
    private String name;
    @DatabaseField
    private String email;
    @DatabaseField
    private String createdAt;
    @DatabaseField
    private String updatedAt;
    @DatabaseField
    private String avatarUrl;
    @DatabaseField
    private String role;
    @DatabaseField
    private String status;
    @DatabaseField
    private String jobTitle;
    @DatabaseField
    private boolean isDelete;
    @DatabaseField
    private String phone;
    @DatabaseField
    private String address;
    //此条数据所有者
    @DatabaseField
    private String myId;

    private int total;

    public static ContactEntity parse(Json json){
        ContactEntity entity = new ContactEntity();
        entity.setId(json.getStr("id"));
        entity.setUserId(json.getStr("userId"));
        entity.setName(json.getStr("name"));
        entity.setEmail(json.getStr("email"));
        entity.setCreatedAt(json.getStr("createdAt"));
        entity.setUpdatedAt(json.getStr("updatedAt"));
        entity.setAvatarUrl(json.getStr("avatarUrl"));
        entity.setRole(json.getStr("role"));
        entity.setStatus(json.getStr("status"));
        entity.setJobTitle(json.getStr("jobTitle"));
        entity.setIsDelete(json.getBool("isDelete"));
        entity.setPhone(json.getStr("phone"));
        entity.setAddress(json.getStr("address"));
        return  entity;
    }
    public static ContactEntity parseSync(Json json,int total){
        ContactEntity entity = parse(json);
        entity.setTotal(total);
        return entity;
    }

    public static List<ContactEntity> parseList(List<Json> jsons){
        List<ContactEntity> contactEntities = new ArrayList<ContactEntity>();
        for(Json json:jsons){
            contactEntities.add(parse(json));
        }
        return  contactEntities;
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


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
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


    public String getAvatarUrl() {
        return avatarUrl;
    }


    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
