package y2w.manage;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import y2w.base.Urls;
import y2w.common.AsyncMultiPartGet;
import y2w.common.Config;
import y2w.common.Constants;
import y2w.db.ContactDb;
import y2w.db.EmojiDb;
import y2w.db.TimeStampDb;
import y2w.entities.EmojiEntity;
import y2w.entities.TimeStampEntity;
import y2w.model.Emoji;
import y2w.model.TimeStamp;
import y2w.service.Back;
import y2w.service.EmojiSrv;

/**
 * Created by maa2 on 2016/4/8.
 */
public class Emojis {

    private String TAG = Emojis.class.getSimpleName();
    private CurrentUser user;
    private Remote remote;
    private String updatedAt;
    public Emojis(CurrentUser user){
        this.user = user;
    }

    /**
     * 获取当前登录用户
     * @return 返回结果
     */
    public CurrentUser getUser(){
        return user;
    }

    /**
     * 获取远程访问实例
     * @return 返回结果
     */
    public Remote getRemote(){
        if(remote == null){
            remote = new Remote();
        }
        return remote;
    }

    /**
     * 获取同步更新时间戳
     * @return 返回结果
     */
    public String getUpdatedAt() {
        TimeStampEntity entity= TimeStampDb.queryByType(user.getEntity().getId(), TimeStampEntity.TimeStampType.emoji.toString());
        if(entity != null){
            updatedAt = entity.getTime();
        }else{
            updatedAt = Constants.TIME_ORIGIN;
        }
        return updatedAt;
    }

    public List<Emoji> getEmojiList(){
        List<Emoji> emojiList = new ArrayList<Emoji>();
        List<EmojiEntity>  entities = EmojiDb.query(user.getEntity().getId());
        for(EmojiEntity entity : entities){
            emojiList.add(new Emoji(entity));
        }
        return  emojiList;
    }

    public List<Emoji> getEmojiByPackage(String ePackage){
        List<Emoji> emojiList = new ArrayList<Emoji>();
        List<EmojiEntity>  entities = EmojiDb.query(user.getEntity().getId());
        for(EmojiEntity entity : entities){
            emojiList.add(new Emoji(entity));
        }
        return  emojiList;
    }

    public Emoji getEmojiByName(String name){
        EmojiEntity entity = EmojiDb.queryByName(user.getEntity().getId(), name);
        return new Emoji(entity) ;
    }

    public long getEmojiCountByPackage(String ePackage){
        return EmojiDb.queryCountByPackage(user.getEntity().getId(), ePackage);
    }


    public void addSession(Emoji emoji){
        emoji.getEntity().setMyId(user.getEntity().getId());
        EmojiDb.addEmojiEntity(emoji.getEntity());
    }


    public void add(List<Emoji> emojiList){
        for(Emoji emoji : emojiList){
            addSession(emoji);
        }
    }

    /*****************************remote*****************************/
    /**
     * 远程访问类
     */
    public class Remote{
        public Remote() {

        }

        public void sync(){

            EmojiSrv.getInstance().getEmojiList(user.getToken(), getUpdatedAt(), Constants.EMOJIS_SYNC_LIMIT, new Back.Result<List<EmojiEntity>>() {
                @Override
                public void onSuccess(List<EmojiEntity> emojiEntities) {
                    List<Emoji> emojiList = new ArrayList<Emoji>();
                    for(EmojiEntity entity:emojiEntities){
                        emojiList.add(new Emoji(entity));
                    }
                    add(emojiList);
                }

                @Override
                public void onError(int code, String error) {

                }
            });
        }

        public void getEmojiList(final Back.Result<List<Emoji>> result){
            EmojiSrv.getInstance().getEmojiList(user.getToken(), getUpdatedAt(), Constants.EMOJIS_SYNC_LIMIT, new Back.Result<List<EmojiEntity>>() {
                @Override
                public void onSuccess(List<EmojiEntity> emojiEntities) {
                    List<Emoji> emojiList = new ArrayList<Emoji>();
                    for(EmojiEntity entity:emojiEntities){
                        emojiList.add(new Emoji(entity));
                    }
                    add(emojiList);
                    if(emojiEntities != null && emojiEntities.size() > 0){
                        TimeStampEntity entity = TimeStampEntity.parse(emojiEntities.get(0).getUpdatedAt(), TimeStampEntity.TimeStampType.emoji.toString() , "");
                        TimeStampDb.addTimeStampEntity(entity);
                    }
                    result.onSuccess(emojiList);
                }

                @Override
                public void onError(int code, String error) {
                    result.onError(code,error);
                }
            });
        }


    }
}
