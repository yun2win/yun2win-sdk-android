package y2w.model;

import java.io.Serializable;

import y2w.manage.UserSessions;
import y2w.entities.UserSessionEntity;

/**
 * 群聊
 * Created by yangrongfang on 2016/3/7.
 */
public class UserSession implements Serializable {
    private UserSessionEntity entity;
    private UserSessions userSessions;


    public UserSession(UserSessions userSessions,UserSessionEntity entity){
        this.userSessions = userSessions;
        this.entity = entity;
    }

    public UserSessions getUserSessions() {
        return userSessions;
    }

    public UserSessionEntity getEntity() {
        return entity;
    }
}
