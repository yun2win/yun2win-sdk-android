package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.entities.UserConversationEntity;

/**
 * 用户会话表管理类
 * Created by maa2 on 2016/1/19.
 */
public class UserConversationDb {

    public static void delete(UserConversationEntity entity){
        if(entity != null){
            try{
                DeleteBuilder<UserConversationEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.deleteBuilder();
                deleteBuilder.where().eq("targetId", entity.getTargetId()).and().eq("myId", entity.getMyId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }
    public static void addUserConversation(UserConversationEntity entity){
        if(entity != null){
            try{
                delete(entity);
                DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.create(entity);
            }catch(Exception e){

            }
        }
    }

    public static void add(List<UserConversationEntity> entities){
        if(entities != null){
            try{
                for(UserConversationEntity entity:entities) {
                    addUserConversation(entity);
                }
            }catch(Exception e){
            }
        }
    }

    public static UserConversationEntity queryByTargetId(String myId,String targetId){
        UserConversationEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.queryBuilder()
                    .where()
                    .eq("targetId", targetId).and().eq("myId", myId).queryForFirst();
        } catch (Exception e) {
        }
        return entity;
    }

    public static List<UserConversationEntity> query(String myId){
        List<UserConversationEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.queryBuilder().orderBy("updatedAt",false)
                    .where()
                    .eq("isDelete",false)
                    .and()
                    .eq("myId", myId).query();
        } catch (Exception e) {
            entities = new ArrayList<UserConversationEntity>();
        }
        return entities;
    }

}
