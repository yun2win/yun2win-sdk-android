package y2w.model;

import java.util.ArrayList;
import java.util.List;

import y2w.entities.MessageEntity;

/**
 * Created by maa46 on 2016/9/23.
 */
public class SyncMessagesModel {
    private List<MessageModel>  messageModels = new ArrayList<MessageModel>();
    private List<MessageEntity> messageEntities = new ArrayList<MessageEntity>();
    private String sessionUpdatedAt;

    public List<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(List<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public String getSessionUpdatedAt() {
        return sessionUpdatedAt;
    }

    public void setSessionUpdatedAt(String sessionUpdatedAt) {
        this.sessionUpdatedAt = sessionUpdatedAt;
    }

    public List<MessageEntity> getMessageEntities() {
        return messageEntities;
    }

    public void setMessageEntities(List<MessageEntity> messageEntities) {
        this.messageEntities = messageEntities;
    }
}
