package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.entities.UserSessionEntity;

/**
 * 群聊表管理类
 * Created by maa2 on 2016/3/7.
 */
public class UserSessionDb {
    public static void delete(UserSessionEntity entity){
        if(entity != null){
            try{
                DeleteBuilder<UserSessionEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_userSession.deleteBuilder();
                deleteBuilder.where().eq("myId", entity.getMyId()).and().eq("id", entity.getId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_userSession.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }
    public static void addUserSessionEntity(UserSessionEntity entity){
        if(entity != null){
            try{
                delete(entity);
                DaoManager.getInstance(AppContext.getAppContext()).dao_userSession.create(entity);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static UserSessionEntity queryBySessionId(String myId, String sessionId){
        UserSessionEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_userSession.queryBuilder()
                    .where()
                    .eq("sessionId", sessionId).and().eq("myId", myId).queryForFirst();
        } catch (Exception e) {
        }
        return entity;
    }


    public static List<UserSessionEntity> query(String myId){
        List<UserSessionEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_userSession.queryBuilder().orderBy("updatedAt", false)
                    .where()
                    .eq("isDelete",false)
                    .and()
                    .eq("myId", myId).query();
        } catch (Exception e) {
            entities = new ArrayList<UserSessionEntity>();
        }
        return entities;
    }
}
