package y2w.entities.searchEntities;

import java.util.ArrayList;
import java.util.List;

import y2w.entities.MessageEntity;
import y2w.model.UserConversation;

/**
 * Created by maa46 on 2016/8/30.
 */
public class SearchMessage {
    private UserConversation userConversation;
    private List<MessageEntity> messages= new ArrayList<MessageEntity>();
    public UserConversation getUserConversation() {
        return userConversation;
    }

    public void setUserConversation(UserConversation userConversation) {
        this.userConversation = userConversation;
    }

    public void addMessage(MessageEntity messageEntity){
        messages.add(messageEntity);
    }
    public void addAllMessages(List<MessageEntity> tempmessages){
        messages.addAll(tempmessages);
    }
    public List<MessageEntity> getMessages(){
        return messages;
    }
}
