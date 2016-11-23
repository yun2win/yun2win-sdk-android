package y2w.httpApi.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongJie on 09/26 0026.
 */
public class AcCallQueue implements Serializable {
    String type;
    String mode;
    String action;
    String channel;
    String sender;
    List<String> members;
    String session;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public List<String> getMembers() {
        if(members == null){
            members = new ArrayList<String>();
        }
        return members;
    }
    public String getSession() {
        return session;
    }
    public void setSession(String session) {
        this.session = session;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
