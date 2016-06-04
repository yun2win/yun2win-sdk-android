package y2w.service;

import java.util.List;

import y2w.base.APIClient;
import y2w.base.ClientFactory;
import y2w.entities.EmojiEntity;
import y2w.model.Emoji;

/**
 * Created by yangrongfang on 2016/4/8.
 */
public class EmojiSrv {

    private static EmojiSrv emojiSrv = null;
    public static EmojiSrv getInstance(){
        if(emojiSrv == null){
            emojiSrv = new EmojiSrv();
        }
        return emojiSrv;
    }

    public void getEmojiList(String token,String syncTime,int limit,Back.Result<List<EmojiEntity>> result){
        ClientFactory.getInstance().getEmojiList(token,syncTime,limit,result);
    }

}
