package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.common.Constants;
import y2w.entities.MessageEntity;

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
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_message.queryBuilder().limit(maxRow).orderBy("updatedAt", false)
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("myId", myId)
                    .and()
                    .between("updatedAt", Constants.TIME_QUERY_AFTER, beforeTime).query();

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
}
