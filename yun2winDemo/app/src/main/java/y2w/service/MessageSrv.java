package y2w.service;

import java.util.List;

import y2w.base.ClientFactory;
import y2w.entities.MessageEntity;
import y2w.model.SyncMessagesModel;

/**
 * 消息远程访问
 * Created by yangrongfang on 2016/1/28.
 */
public class MessageSrv {

    private static MessageSrv messageSrv = null;
    public static MessageSrv getInstance(){
        if(messageSrv == null){
            messageSrv = new MessageSrv();
        }
        return messageSrv;
    }

    /**
     * 保存消息
     * @param token
     * @param entity
     * @param result
     */
    public void store(String token, MessageEntity entity, Back.Result<MessageEntity> result){
        ClientFactory.getInstance().sendMessage(token, entity.getSessionId(), entity.getSender(), entity.getContent(), entity.getType(), result);
    }

    /**
     * 获取最新消息
     * @param token
     * @param sessionId
     * @param syncTime
     * @param limit
     * @param result
     */
    public void getLastMessage(String token, String sessionId, String syncTime,final int limit, Back.Result<List<MessageEntity>> result){
        ClientFactory.getInstance().getMessageHistory(token, sessionId, syncTime, limit, result);
    }

    /**
     * 消息同步
     * @param token
     * @param sessionId
     * @param syncTime
     * @param limit
     * @param result
     */
    public void sync(String token, String sessionId, String syncTime, final int limit, Back.Result<SyncMessagesModel> result){
        ClientFactory.getInstance().getMessage(token, sessionId, syncTime, limit, result);
    }

    /**
     * 消息更新
     * @param token
     * @param sessionId
     * @param messageId
     * @param sender
     * @param content
     * @param type
     * @param result
     */
    public void messageUpdate(String token, String sessionId, String messageId, String sender, String content, String type, Back.Result<MessageEntity> result) {
        ClientFactory.getInstance().messageUpdate(token, sessionId, messageId, sender, content, type, result);
    }

    /**
     * 删除消息
     * @param token
     * @param sessionId
     * @param messageId
     * @param callback
     */
    public void messageDelete(String token, String sessionId, String messageId,Back.Callback callback) {
        ClientFactory.getInstance().messageDelete(token, sessionId, messageId, callback);
    }

}
