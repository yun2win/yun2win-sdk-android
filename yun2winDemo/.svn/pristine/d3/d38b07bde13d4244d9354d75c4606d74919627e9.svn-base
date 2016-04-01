package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;

import y2w.base.AppContext;
import y2w.entities.TimeStampEntity;

/**
 * 同步时间戳管理类
 * Created by maa2 on 2016/3/8.
 */
public class TimeStampDb {

    public static void delete(TimeStampEntity entity){
        if(entity != null){
            try{
                DeleteBuilder<TimeStampEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_timeStamp.deleteBuilder();
                deleteBuilder.where().eq("type", entity.getType()).and().eq("myId", entity.getMyId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_timeStamp.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }
    public static void addTimeStampEntity(TimeStampEntity entity){
        if(entity != null){
            try{
                delete(entity);
                DaoManager.getInstance(AppContext.getAppContext()).dao_timeStamp.create(entity);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static TimeStampEntity queryByType(String myId,String type){
        TimeStampEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_timeStamp.queryBuilder()
                    .where()
                    .eq("type", type).and().eq("myId", myId).queryForFirst();

        } catch (Exception e) {
        }
        return entity;
    }
}
