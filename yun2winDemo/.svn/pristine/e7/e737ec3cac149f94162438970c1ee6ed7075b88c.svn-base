package y2w.model;

import java.util.List;

import y2w.manage.UserConversations;
import y2w.entities.MessageEntity;
import y2w.entities.UserConversationEntity;
import y2w.service.Back;

/**
 * 用户会话
 * Created by yangrongfang on 2016/1/16.
 */
public class UserConversation {


    private UserConversationEntity entity;
    private UserConversations userConversations;

    public UserConversation(UserConversations userConversations,UserConversationEntity entity) {
        this.userConversations = userConversations;
        this.entity = entity;
    }

    public UserConversationEntity getEntity() {
        return entity;
    }

    public UserConversations getUserConversations() {
        return userConversations;
    }

    /**
     * 获取当前用户会话对应的session
     * @param result 返回结果
     */
    public void getSession(Back.Result<Session> result) {
        userConversations.getUser().getSessions().getSessionByTargetId(entity.getTargetId(),entity.getType(),result);
    }


}
