package y2w.model;

import java.io.Serializable;

import y2w.manage.Messages;
import y2w.manage.SessionMembers;
import y2w.manage.Sessions;
import y2w.entities.SessionEntity;

/**
 * 会话
 * Created by yangrongfang on 2016/1/16.
 */
public class Session implements Serializable{
    private SessionEntity entity;
    private SessionMembers members;
    private Messages messages;
    private Sessions sessions;


    public Session(Sessions sessions,SessionEntity entity){
        this.sessions = sessions;
        this.entity = entity;
        this.messages = new Messages(this);
        this.members = new SessionMembers(this);
    }

    public SessionMembers getMembers() {
        return members;
    }


    public Messages getMessages() {
        return messages;
    }

    public Sessions getSessions() {
        return sessions;
    }

    public SessionEntity getEntity() {
        return entity;
    }



}
