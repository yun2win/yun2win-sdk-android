package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.common.Constants;
import y2w.entities.MessageEntity;
import y2w.manage.EnumManage;
import y2w.model.MessageModel;
import y2w.model.messages.MessageType;

/**
 * 消息表管理类
 * Created by yangrongfang on 2016/2/23.
 */
public class MessageDb {

    public static void delete(MessageEntity entity){
        if(entity != null){
            try{
                DeleteBuilder<MessageEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_message.deleteBuilder();
                deleteBuilder.where().eq("id", entity.getId()).and().eq("myId", entity.getMyId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_message.delete(deleteBuilder.prepare());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void addMessageEntity(MessageEntity entity){
        if(entity != null){
            try{
                delete(entity);
                if(!entity.isDelete())
                DaoManager.getInstance(AppContext.getAppContext()).dao_message.create(entity);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void add(List<MessageEntity> entities){
        if(entities != null){
            try{
                for(MessageEntity entity:entities) {
                    addMessageEntity(entity);
                }
            }catch(Exception e){
            }
        }
    }

    public static MessageEntity queryById(String myId,String id){
        MessageEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_message.queryBuilder()
                    .where()
                    .eq("id", id).and().eq("myId", myId).queryForFirst();
        } catch (Exception e) {
        }
        return entity;
    }

    /**
     * 查询某个时间点前面maxRow条消息
     * @param myId
     * @param sessionId
     * @param beforeTime
     * @param maxRow
     * @return
     */

    public static List<MessageEntity> query(String myId,String sessionId,String beforeTime,int maxRow){
        List<MessageEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_message.queryBuilder().distinct().limit(maxRow).orderBy("createdAt", false).orderBy("id",false)
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("myId", myId)
                    .and()
                    .eq("isDelete",false)
                    .and()
                    .between("createdAt", Constants.TIME_QUERY_AFTER, beforeTime).query();

        } catch (Exception e) {
            entities = new ArrayList<MessageEntity>();
        }
        List<MessageEntity> entityList = new ArrayList<MessageEntity>();
        for(MessageEntity entity:entities){
            entityList.add(0,entity);
        }
        return entityList;
    }
    /**
     * 查询某个时间点后面所有消息（包括自己）
     * @param myId
     * @param sessionId
     * @return
     */

    public static List<MessageEntity> queryafterTimeMessage(String myId,String sessionId,String afterTime){
        List<MessageEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_message.queryBuilder().distinct().orderBy("createdAt", false).orderBy("id",false)
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("myId", myId)
                    .and().eq("isDelete", false)
                    .and()
                    .ge("createdAt", afterTime).query();

        } catch (Exception e) {
            entities = new ArrayList<MessageEntity>();
        }
        List<MessageEntity> entityList = new ArrayList<MessageEntity>();
        for(MessageEntity entity:entities){
            entityList.add(0,entity);
        }
        return entityList;
    }

    public static MessageEntity queryLastMessage(String myId,String sessionId){
        MessageEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_message.queryBuilder().orderBy("updatedAt", false)
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("myId", myId)
                   .queryForFirst();

        } catch (Exception e) {
        }

        return entity;
    }
    public static List<MessageEntity> querySessionTextMessageByKey(String myId,String sessionId,String key){
        List<MessageEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_message.queryBuilder().distinct().orderBy("updatedAt", false)
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("type", "text")
                    .and().eq("isDelete", false)
                    .and()
                    .like("simpchinacontent", "%" + key + "%")
                    .and()
                    .eq("myId", myId)
                    .query();

        } catch (Exception e) {
            entities = new ArrayList<MessageEntity>();
        }

        return entities;
    }

    public static List<MessageEntity> queryImageAll(String myId,String sessionId){
        List<MessageEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_message.queryBuilder().distinct().orderBy("updatedAt", false)
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("myId", myId)
                    .and().eq("isDelete", false)
                    .and()
                    .eq("type", MessageType.Image.toString()).query();

        } catch (Exception e) {
            entities = new ArrayList<MessageEntity>();
        }
        List<MessageEntity> entityList = new ArrayList<MessageEntity>();
        for(MessageEntity entity:entities){
            entityList.add(0,entity);
        }
        return entityList;
    }

}
