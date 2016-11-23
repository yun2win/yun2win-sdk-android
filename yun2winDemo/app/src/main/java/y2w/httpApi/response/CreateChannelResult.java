package y2w.httpApi.response;

/**
 * Created by SongJie on 09/12 0012.
 */
public class CreateChannelResult {
    String channelId;
    String meetRouter;
    String audioServerAddr;
    String audioServerPort;
    String audioServerPassword;
    String stunServerAddr;
    String stunServerPort;
    String turnServerAddr;
    String turnServerPort;
    String turnUserName;
    String turnPassword;

    public String getTurnServerPort() {
        return turnServerPort;
    }

    public void setTurnServerPort(String turnServerPort) {
        this.turnServerPort = turnServerPort;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getMeetRouter() {
        return meetRouter;
    }

    public void setMeetRouter(String meetRouter) {
        this.meetRouter = meetRouter;
    }

    public String getAudioServerAddr() {
        return audioServerAddr;
    }

    public void setAudioServerAddr(String audioServerAddr) {
        this.audioServerAddr = audioServerAddr;
    }

    public String getAudioServerPort() {
        return audioServerPort;
    }

    public void setAudioServerPort(String audioServerPort) {
        this.audioServerPort = audioServerPort;
    }

    public String getAudioServerPassword() {
        return audioServerPassword;
    }

    public void setAudioServerPassword(String audioServerPassword) {
        this.audioServerPassword = audioServerPassword;
    }

    public String getStunServerAddr() {
        return stunServerAddr;
    }

    public void setStunServerAddr(String stunServerAddr) {
        this.stunServerAddr = stunServerAddr;
    }

    public String getStunServerPort() {
        return stunServerPort;
    }

    public void setStunServerPort(String stunServerPort) {
        this.stunServerPort = stunServerPort;
    }

    public String getTurnServerAddr() {
        return turnServerAddr;
    }

    public void setTurnServerAddr(String turnServerAddr) {
        this.turnServerAddr = turnServerAddr;
    }

    public String getTurnUserName() {
        return turnUserName;
    }

    public void setTurnUserName(String turnUserName) {
        this.turnUserName = turnUserName;
    }

    public String getTurnPassword() {
        return turnPassword;
    }

    public void setTurnPassword(String turnPassword) {
        this.turnPassword = turnPassword;
    }
}
