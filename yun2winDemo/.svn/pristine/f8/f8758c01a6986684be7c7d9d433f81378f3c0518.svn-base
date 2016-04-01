package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.entities.SessionMemberEntity;

/**
 * 会话成员表管理类
 * Created by maa2 on 2016/3/3.
 */
public class SessionMemberDb {

    public static void delete(SessionMemberEntity entity){
        if(entity != null){
            try{
                DeleteBuilder<SessionMemberEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.deleteBuilder();
                deleteBuilder.where().eq("userId", entity.getUserId()).and().eq("sessionId", entity.getSessionId()).and().eq("myId", entity.getMyId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }
    public static void addSessionMemberEntity(SessionMemberEntity entity){
        if(entity != null){
            try{
                delete(entity);
                DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.create(entity);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static SessionMemberEntity queryByMemberId(String myId,String sessionId,String userId){
        SessionMemberEntity entity = null;
        try {

            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.queryBuilder()
                    .where()
                    .eq("userId", userId).and().eq("sessionId", sessionId).and().eq("myId", myId).queryForFirst();

        } catch (Exception e) {
        }
        return entity;
    }


    public static List<SessionMemberEntity> query(String myId,String sessionId){
        List<SessionMemberEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.queryBuilder()
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("myId", myId).query();
        } catch (Exception e) {
            entities = new ArrayList<SessionMemberEntity>();
        }
        return entities;
    }

    public static long queryCount(String myId,String sessionId){
        long count = 0;
        try {
            count = DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.queryBuilder()
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("myId", myId).countOf();
        } catch (Exception e) {
        }
        return count;
    }

}
