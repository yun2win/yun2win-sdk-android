package y2w.entities.searchEntities;

import java.util.ArrayList;
import java.util.List;

import y2w.entities.SessionMemberEntity;
import y2w.model.UserConversation;

/**
 * Created by maa46 on 2016/8/30.
 */
public class SearchUserConversation {
    private UserConversation userConversation;
    private List<SessionMemberEntity> sessionMembers= new ArrayList<SessionMemberEntity>();

    public UserConversation getUserConversation() {
        return userConversation;
    }

    public void setUserConversation(UserConversation userConversation) {
        this.userConversation = userConversation;
    }
    public void addSessionMeberEntity(SessionMemberEntity memberEntity){
        sessionMembers.add(memberEntity);
    }
    public void addAllSessionMeberEntity(List<SessionMemberEntity> tempSessionMembers){
        sessionMembers.addAll(tempSessionMembers);
    }
    public List<SessionMemberEntity> getSessionMembers(){
        return sessionMembers;
    }

}
