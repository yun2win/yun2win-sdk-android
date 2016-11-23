package y2w.httpApi.request;

/**
 * Created by SongJie on 09/13 0013.
 */
public class CollectDataItem {
//  数据类型audioOpen、audioClose、videoOpen、videoClose、connectSuccess、connectFailed、reConnectSuccess、reConnectFailed、enterChannel、leaveChannel
    private String eventType;
//统计时间，格式2011-03-08 23:22:11
    private String time;
//    频道id
    private String channelID;
//    发送的字节数
    private String sendBytes;
//  接受的字节数
    private String recvBytes;
    //通话时长，单位为秒
    private String callDuration;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getSendBytes() {
        return sendBytes;
    }

    public void setSendBytes(String sendBytes) {
        this.sendBytes = sendBytes;
    }

    public String getRecvBytes() {
        return recvBytes;
    }

    public void setRecvBytes(String recvBytes) {
        this.recvBytes = recvBytes;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }
}
