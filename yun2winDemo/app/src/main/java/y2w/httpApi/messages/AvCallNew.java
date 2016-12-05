package y2w.httpApi.messages;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongJie on 09/26 0026.
 * 音视频的信令
 */
public class AvCallNew {
    String[] syncs;
    PushItem push;
    AvItem av;
    OtherPush pns;

    public String[] getSyncs() { return syncs; }
    public void setSyncs(String[] syncs) { this.syncs = syncs; }
    public PushItem getPush() {
        if(push == null){
            push = new PushItem();
        }
        return push;
    }
    public void setPush(PushItem push) { this.push = push; }
    public AvItem getAv() {
        if(av == null){
            av = new AvItem();
        }
        return av;
    }
    public void setAv(AvItem av) { this.av = av; }

    public OtherPush getPns() {
        if(pns == null){
            pns = new OtherPush();
        }
        return pns;
    }

    public void setPns(OtherPush pns) {
        this.pns = pns;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /**
     * 通知信息
     */
    class PushItem{
        String title;
        String msg;

        public String getMsg() { return msg; }
        public void setMsg(String msg) { this.msg = msg; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    /**
     * 视频通信
     */
    public class AvItem{

        String type;//['p2p','group']
        String mode;//['A','AV']
        String action;//['call','reject','busy']
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
        public void setMembers(List<String> members) { this.members = members; }

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }

    }
    /**
     * 推送到其他推送
     */
    public class OtherPush{
        String msg;
        PayLoad payload;
        String sound;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public PayLoad getPayload() {
            if(payload==null)
                payload = new PayLoad();
            return payload;
        }

        public void setPayload(PayLoad payload) {
            this.payload = payload;
        }
    }
    public class PayLoad{
        AvItem av;

        public AvItem getAv() {
            return av;
        }

        public void setAv(AvItem av) {
            this.av = av;
        }
    }
}
