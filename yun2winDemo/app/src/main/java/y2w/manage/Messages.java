package y2w.manage;

import com.y2w.uikit.utils.ThreadPool;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.SendReturnCode;
import com.yun2win.imlib.IMSession;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import y2w.common.Constants;
import y2w.db.MessageDb;
import y2w.db.SessionDb;
import y2w.entities.MessageEntity;
import y2w.entities.SessionEntity;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.service.Back;
import y2w.service.MessageSrv;
import com.y2w.uikit.utils.StringUtil;

/**
 * 消息管理类
 * Created by yangrongfang on 2016/1/16.
 */
public class Messages implements Serializable {
    private String TAG = Messages.class.getSimpleName();
    private Session session;
    private String updateAt;
    private Remote remote;

    /**
     * 有参构造
     * @param session
     */
    public Messages(Session session){
       this.session = session;
    }

    /**
     * 获取当前会话
     * @return 返回结果
     */
    public Session getSession() {
        return session;
    }

    /**
     * 获取会话CreateMTS
     * @return 返回结果
     */
    public String getCreateMTS() {
        SessionEntity entity = SessionDb.queryBySessionId(session.getEntity().getMyId(), session.getEntity().getId());
        if(entity != null){
            updateAt = entity.getCreateMTS();
            if(StringUtil.isEmpty(updateAt)){
                updateAt = entity.getCreatedAt();
            }
        }else {
            updateAt = Constants.TIME_ORIGIN;
        }
        return updateAt;
    }

    /**
     * 获取本地最后一条消息的时间
     * @return 返回结果
     */
    public String getMessageUpdateAt() {
        MessageEntity entity = MessageDb.queryLastMessage(session.getEntity().getMyId(), session.getEntity().getId());
        if(entity != null){
            updateAt = entity.getUpdatedAt();
        }else {
            updateAt = Constants.TIME_ORIGIN;
        }

        return updateAt;
    }

    /**
     * 获取发送消息时，会话时间戳
     * @return 返回结果
     */
    public String getTimeStamp(){
        return StringUtil.getTimeStamp(getCreateMTS());
    }

    /**
     * 根据消息Id，获取某个消息
     * @param mid 消息唯一标识码
     * @return 返回结果
     */
    public MessageModel getMessage(String mid){
        MessageEntity entity = MessageDb.queryById(session.getEntity().getMyId(),mid);
        return new MessageModel(this,entity);
    }

    /**
     * 获取某个会话 model消息 后面 maxRow 条消息
     * @param model 消息
     * @param maxRow 条数极限
     */
    public List<MessageModel> getMessages(MessageModel model,int maxRow){
        List<MessageModel> models = new ArrayList<MessageModel>();
        List<MessageEntity> entities = null;
        if(model != null){
            entities = MessageDb.query(session.getEntity().getMyId(), session.getEntity().getId(),model.getEntity().getUpdatedAt(),maxRow);
        }else{
            entities = MessageDb.query(session.getEntity().getMyId(), session.getEntity().getId(),Constants.TIME_QUERY_BEFORE,maxRow);
        }
        for(MessageEntity entity:entities){
            models.add(new MessageModel(this,entity));
        }
        return models;
    }
    /**
     * 发消息时，创建一条消息
     * @param content 消息类容
     * @param type 消息类型
     * @return 返回结果
     */
    public MessageModel createMessage(String content,String type){
        MessageEntity entity = new MessageEntity();
        entity.setSessionId(session.getEntity().getId());
        entity.setSender(session.getEntity().getMyId());
        entity.setContent(content);
        entity.setType(type);
        entity.setStatus(MessageEntity.MessageState.storing.toString());
        return new MessageModel(this,entity);
    }

    /**
     * 将多条消息保存到数据库
     * @param messageList 消息列表
     */
    public void add(List<MessageModel> messageList){
        for(MessageModel message:messageList){
            addMessage(message);
        }
    }

    /**
     * 将一条消息保存到数据库
     * @param message 消息
     */
    public void addMessage(MessageModel message){
        message.getEntity().setMyId(session.getEntity().getMyId());
        message.getEntity().setSessionId(session.getEntity().getId());
        MessageDb.addMessageEntity(message.getEntity());

    }

    /**
     * 获取远程访问实例
     * @return 返回结果
     */
    public Remote getRemote(){
        if(remote == null){
            remote = new Remote(this);
        }
        return remote;
    }

    /*****************************remote*****************************/
    /**
     * 远程访问类
     */
    public class Remote implements Serializable{

        private Messages messages;
        public Remote(Messages messages) {
            this.messages = messages;
        }
        /**
         * 获取最新的消息
         * @param count 消息最大条数
         * @param result 回调
         */
        public void getLastMessages(final int count,final Back.Result<List<MessageModel>> result){
           MessageSrv.getInstance().getLastMessage(session.getSessions().getUser().getToken(), session.getEntity().getId(), Constants.TIME_QUERY_BEFORE, count, new Back.Result<List<MessageEntity>>() {
               @Override
               public void onSuccess(List<MessageEntity> entities) {
                   List<MessageModel> messageList = new ArrayList<MessageModel>();
                   for (MessageEntity entity : entities) {
                       messageList.add(new MessageModel(messages, entity));
                   }
                   result.onSuccess(messageList);
               }

               @Override
               public void onError(int errorCode,String error) {
                   result.onError(errorCode,error);
               }
           });
        }

        /**
         * 同步某个时间戳后面消息
         * @param isStore 是否保存本地
         * @param syncTime 同步时间
         * @param limit 最大消息条数
         * @param result 回调
         */
        public void sync(final boolean isStore,String syncTime, int limit, final Back.Result<List<MessageModel>> result){
            MessageSrv.getInstance().sync(session.getSessions().getUser().getToken(), session.getEntity().getId(), syncTime, limit, new Back.Result<List<MessageEntity>>() {
                @Override
                public void onSuccess(List<MessageEntity> entities) {
                    List<MessageModel> messageList = new ArrayList<MessageModel>();
                    for (MessageEntity entity : entities) {
                        messageList.add(new MessageModel(messages, entity));
                    }
                    if (isStore) {
                        add(messageList);
                    }
                    result.onSuccess(messageList);
                }

                @Override
                public void onError(int errorCode, String error) {
                    result.onError(errorCode, error);
                }
            });
        }

        /**
         * 保存消息到业务服务器
         * @param message 消息体
         * @param result 回调
         */
        public void store(final MessageModel message, final Back.Result<MessageModel> result){
            MessageSrv.getInstance().store(session.getSessions().getUser().getToken(), message.getEntity(), new Back.Result<MessageEntity>() {
                @Override
                public void onSuccess(MessageEntity entity) {
                    MessageModel model = new MessageModel(messages, entity);
                    model.getEntity().setSessionId(message.getEntity().getSessionId());
                    model.getEntity().setType(message.getEntity().getType());
                    model.getEntity().setStatus(MessageEntity.MessageState.stored.toString());
                    result.onSuccess(model);
                    sendMessage(message.getEntity().getContent(), new IMClient.SendCallback() {

                        @Override
                        public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                            switch (code) {
                                case SendReturnCode.SRC_SUCCESS:
                                    LogUtil.getInstance().log(TAG, "returnCode:" + code, null);
                                    break;
                                case SendReturnCode.SRC_CMD_INVALID:
                                    LogUtil.getInstance().log(TAG, "returnCode:" + code, null);
                                    break;
                                case SendReturnCode.SRC_SESSION_INVALID:
                                    LogUtil.getInstance().log(TAG, "returnCode:" + code, null);
                                    break;
                                case SendReturnCode.SRC_SESSION_ID_INVALID:
                                    LogUtil.getInstance().log(TAG, "returnCode:" + code, null);
                                    break;
                                case SendReturnCode.SRC_SESSION_MTS_INVALID:
                                    LogUtil.getInstance().log(TAG, "returnCode:" + code, null);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

                }

                @Override
                public void onError(int errorCode, String error) {
                    result.onError(errorCode, error);
                }
            });
        }

        /**
         * 发送推送命令消息
         * @param message
         * @param callback
         */
        public void sendMessage(String message,IMClient.SendCallback callback){
            session.getSessions().getUser().getImBridges().sendMessage(session, callback);
           /* IMSession imSession = new IMSession();
            imSession.setId(session.getEntity().getType() + "_" + session.getEntity().getId());
            imSession.setMts(getTimeStamp());
            message = "{\"syncs\":[{\"type\":0},{\"type\":1,\"sessionId\":\"" + session.getEntity().getType()+"_" + session.getEntity().getId() + "\"}]}";
            LogUtil.getInstance().log(TAG, "updateAt:" + imSession.getMts(), null);
            LogUtil.getInstance().log(TAG, "message:"+message, null);
            Users.getInstance().getCurrentUser().getImBridges().getImBridge().getImClient().sendMessage(imSession, message, callback);*/
        }

        public void updateMessage(MessageModel model, final Back.Result<MessageModel> result){
            MessageSrv.getInstance().messageUpdate(session.getSessions().getUser().getToken(), model.getEntity().getSessionId(), model.getEntity().getId(), model.getEntity().getSender(), model.getEntity().getContent(), model.getEntity().getType(), new Back.Result<MessageEntity>() {
                @Override
                public void onSuccess(MessageEntity entity) {
                    result.onSuccess(new MessageModel(messages,entity));
                }

                @Override
                public void onError(int code, String error) {
                    result.onError(code, error);
                }
            });

        }

    }


}
