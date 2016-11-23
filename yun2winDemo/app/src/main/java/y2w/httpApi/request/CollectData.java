package y2w.httpApi.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongJie on 09/12 0012.
 */
public class CollectData {
    // "channelStatistics",
    private String routeType;
    private String userID;
    private List<CollectDataItem> messageContent = new ArrayList<CollectDataItem>();

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<CollectDataItem> getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(List<CollectDataItem> messageContent) {
        this.messageContent = messageContent;
    }

    public void addMessageContent(CollectDataItem dataItem) {
        this.messageContent.add(dataItem);
    }
}
