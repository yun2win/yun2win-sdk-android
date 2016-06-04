package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.yun2win.utils.Json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.y2w.uikit.utils.StringUtil;
import y2w.common.UserInfo;
import y2w.model.messages.MessageCrypto;
import y2w.model.messages.MessageType;

/**
 * 用户会话表
 * Created by yangrongfang on 2016/1/18.
 */
@DatabaseTable (tableName = "UserConversationEntity")
public class UserConversationEntity implements Serializable {

    @DatabaseField
    private String id;
    @DatabaseField
    private String targetId;
    @DatabaseField
    private String name;
    @DatabaseField
    private String type;
    @DatabaseField
    private boolean top;
    @DatabaseField
    private boolean visiable;
    @DatabaseField
    private int unread;
    //最后一条消息发送者 唯一标识码
    @DatabaseField
    private String lastSender;
    //最后一条消息类型
    @DatabaseField
    private String lastType;
    //最后一条消息展示类容
    @DatabaseField
    private String lastContent;
    @DatabaseField
    private boolean isDelete;
    @DatabaseField
    private String createdAt;
    @DatabaseField
    private String updatedAt;
    @DatabaseField
    private String avatarUrl;
    //此条数据所有者
    @DatabaseField
    private String myId;



    public static UserConversationEntity parse(Json json){
        UserConversationEntity entity = new UserConversationEntity();
        entity.setId(json.getStr("id"));
        entity.setTargetId(json.getStr("targetId"));
        entity.setName(json.getStr("name"));
        entity.setType(json.getStr("type"));
        entity.setTop(json.getBool("top"));
        entity.setVisiable(json.getBool("visiable"));
        entity.setUnread(json.getInt("unread"));
        Json lastMessage = new Json(json.getStr("lastMessage"));
        entity.setLastSender(lastMessage.getStr("sender"));
        entity.setLastType(lastMessage.getStr("type"));
        if(MessageType.Text.equals(lastMessage.getStr("type")) || MessageType.System.equals(lastMessage.getStr("type"))){
            entity.setLastContext(MessageCrypto.getInstance().decryText(lastMessage.getStr("content")));
        }else{
            entity.setLastContext(getContent(lastMessage.getStr("type")));
        }
        entity.setIsDelete(json.getBool("isDelete"));
        entity.setCreatedAt(json.getStr("createdAt"));
        entity.setUpdatedAt(json.getStr("updatedAt"));
        entity.setAvatarUrl(json.getStr("avatarUrl"));
        entity.setMyId(UserInfo.getUserId());
        return entity;
    }

    private static String getContent(String type){
        if(MessageType.Image.equals(type)){
            type = "[图片]";
        }else if(MessageType.Audio.equals(type)){
            type = "[语音]";
        }else if(MessageType.Video.equals(type)){
            type = "[小视频]";
        }else if(MessageType.File.equals(type)){
            type = "[文件]";
        }else if(MessageType.Location.equals(type)){
            type = "[位置]";
        }
        return type;
    }

    public static List<UserConversationEntity> parseList(List<Json> jsons){
        List<UserConversationEntity> entities = new ArrayList<UserConversationEntity>();
        for(Json json:jsons){
            entities.add(parse(json));
        }
        return  entities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public boolean isTop() {
        return top;
    }


    public void setTop(boolean top) {
        this.top = top;
    }


    public boolean isVisiable() {
        return visiable;
    }

    public void setVisiable(boolean visiable) {
        this.visiable = visiable;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public String getLastSender() {
        return lastSender;
    }

    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
    }

    public String getLastType() {
        return lastType;
    }

    public void setLastType(String lastType) {
        this.lastType = lastType;
    }

    public String getLastContext() {
        return lastContent;
    }

    public void setLastContext(String lastContext) {
        this.lastContent = lastContext;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
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

    public String getFriendlyTime(){
        return StringUtil.getFriendlyTime(updatedAt);
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
