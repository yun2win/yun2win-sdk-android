package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.Json;

import java.io.Serializable;

/**
 * Created by yangrongfang on 2016/4/8.
 */
@DatabaseTable(tableName = "EmojiEntity")
public class EmojiEntity implements Serializable{

    @DatabaseField
    private String id;
    @DatabaseField
    private String ePackage;
    @DatabaseField
    private String type;
    @DatabaseField
    private String name;
    @DatabaseField
    private String createdAt;
    @DatabaseField
    private String updatedAt;
    @DatabaseField
    private int width;
    @DatabaseField
    private int height;
    @DatabaseField
    private boolean isDelete;
    @DatabaseField
    private String url;
    @DatabaseField
    private int total;
    @DatabaseField
    private String myId;

    public static EmojiEntity parse(Json json){
        EmojiEntity entity = new EmojiEntity();
        entity.setId(json.getStr("id"));
        entity.setePackage(json.getStr("package"));
        entity.setType(json.getStr("type"));
        entity.setName(json.getStr("name"));
        entity.setCreatedAt(json.getStr("createdAt"));
        entity.setUpdatedAt(json.getStr("updatedAt"));
        entity.setWidth(json.getInt("width"));
        entity.setHeight(json.getInt("height"));
        entity.setIsDelete(json.getBool("isDelete"));
        entity.setUrl(json.getStr("url"));

        return entity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getePackage() {
        return ePackage;
    }

    public void setePackage(String ePackage) {
        this.ePackage = ePackage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt =  StringUtil.getOPerTime(createdAt);
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = StringUtil.getOPerTime(updatedAt);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
