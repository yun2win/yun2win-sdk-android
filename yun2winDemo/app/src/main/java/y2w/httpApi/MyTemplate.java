package y2w.httpApi;

import y2w.httpApi.request.CollectData;
import y2w.httpApi.request.CreateChannel;
import y2w.httpApi.response.CollectDataResult;
import y2w.httpApi.response.CreateChannelResult;

/**
 * Created by SongJie on 09/12 0012.
 */
public class MyTemplate extends BaseTemplate{

    public MyTemplate(VolleyClient client) {
        super(client);
    }

    /**
     * 创建频道接口
     */
    public Object createChannel(CreateChannel param, MyCallback<CreateChannelResult> callback) {
        return postRequest(new MyRequest<>(
                getUrl("v1/meetrooms/room"), param,
                CreateChannelResult.class, new MyVolleyListener<>(callback)));
    }

    /**
     *发送统计数据
     */
    public Object sendCollectData(CollectData param, MyCallback<CollectDataResult> callback) {
        return postRequest(new MyRequest<>(
                getUrl(""), param,
                CollectDataResult.class, new MyVolleyListener<>(callback)));
    }
}
