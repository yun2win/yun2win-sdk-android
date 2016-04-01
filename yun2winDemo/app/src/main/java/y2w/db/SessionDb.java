package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.y2w.uikit.utils.StringUtil;

import y2w.base.AppContext;
import y2w.entities.SessionEntity;
import y2w.manage.EnumManage;

/**
 * 会话表管理类
 * Created by maa2 on 2016/2/22.
 */
public class SessionDb {

    public static void delete(SessionEntity entity){
        if(entity != null){
            try{
                DeleteBuilder<SessionEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_session.deleteBuilder();
                deleteBuilder.where().eq("myId", entity.getMyId()).and().eq("id", entity.getId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_session.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }
    public static void addSessionEntity(SessionEntity entity){
        if(entity != null){
            try{
                if(StringUtil.isEmpty(entity.getCreateMTS())){
                    SessionEntity temp = queryBySessionId(entity.getMyId(),entity.getId());
                    if(temp != null && !StringUtil.isEmpty(temp.getCreateMTS())){
                        entity.setCreateMTS(temp.getCreateMTS());
                        entity.setUpdateMTS(temp.getUpdateMTS());
                    }
                }
                delete(entity);
                DaoManager.getInstance(AppContext.getAppContext()).dao_session.create(entity);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static SessionEntity queryByTargetId(String myId,String targetId,String type){
        SessionEntity entity = null;
        try {
            if(EnumManage.SessionType.p2p.toString().equals(type)){
                entity = DaoManager.getInstance(AppContext.getAppContext()).dao_session.queryBuilder()
                        .where()
                        .eq("otherSideId",targetId).and().eq("myId", myId).queryForFirst();
            }else{
                entity = DaoManager.getInstance(AppContext.getAppContext()).dao_session.queryBuilder()
                        .where()
                        .eq("id",targetId).and().eq("myId", myId).queryForFirst();
            }

        } catch (Exception e) {
        }
        return entity;
    }

    public static SessionEntity queryBySessionId(String myId,String sessionId){
        SessionEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_session.queryBuilder()
                    .where()
                    .eq("id", sessionId).and().eq("myId", myId).queryForFirst();

        } catch (Exception e) {
        }
        return entity;
    }

}
