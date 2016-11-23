package y2w.httpApi.messages;

/**
 * Created by SongJie on 09/27 0027.
 */
public class AvSystemMsg {
    //["视频通话未接听","音频通话未接听","多人视频通话未接听","多人音频通话未接听","通话时长00:10"]
    String text;

    //["p2p","group"]
    String type;

    //["A","AV"]
    String mode;

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
