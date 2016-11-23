package y2w.Statistics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import y2w.httpApi.request.CollectData;
import y2w.httpApi.request.CollectDataItem;

/**
 * Created by SongJie on 09/13 0013.
 */
public class StatisticsChannel {
    private CollectData collectData;

    private CollectData getCollectData(){
        if(collectData == null){
            collectData = new CollectData();
        }
        return collectData;
    }

    private String getCurrenttime(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    public void audioOpen(String channelID){
        CollectDataItem dataItem = new CollectDataItem();
        dataItem.setEventType("audioOpen");
        dataItem.setTime(getCurrenttime());
        dataItem.setChannelID(channelID);
        getCollectData().addMessageContent(dataItem);
    }


    class eventType{
        String audioOpen = "audioOpen";
        String audioClose = "audioClose";
        String videoOpen = "videoOpen";
        String videoClose = "videoClose";
        String connectSuccess = "connectSuccess";
        String connectFailed = "connectFailed";
        String reConnectSuccess = "reConnectSuccess";
        String reConnectFailed = "reConnectFailed";
        String enterChannel = "enterChannel";
        String leaveChannel = "leaveChannel";
    }
}
