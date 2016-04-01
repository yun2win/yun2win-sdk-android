package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.List;

import y2w.base.AppContext;
import y2w.entities.UserEntity;
import com.y2w.uikit.utils.StringUtil;

/**
 * 用户表管理类
 * Created by maa2 on 2016/3/15.
 */
public class UserDb {

    public static void delete(String myId, String id){
        if(!StringUtil.isEmpty(myId) && !StringUtil.isEmpty(id)){
            try{
                DeleteBuilder<UserEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_user.deleteBuilder();
                deleteBuilder.where().eq("id", id).and().eq("myId", myId);
                DaoManager.getInstance(AppContext.getAppContext()).dao_user.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }
    public static void addUserEntity(UserEntity entity){
        if(entity != null){
            try{
                delete(entity.getMyId(),entity.getId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_user.create(entity);
            }catch(Exception e){

            }
        }
    }
    public static void add(List<UserEntity> entities){
        if(entities != null){
            try{
                for(UserEntity entity:entities) {
                    addUserEntity(entity);
                }
            }catch(Exception e){
            }
        }
    }
    public static UserEntity queryById(String myId,String id){
        UserEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_user.queryBuilder()
                    .where()
                    .eq("id",id).and().eq("myId", myId).queryForFirst();
        } catch (Exception e) {
            entity = new UserEntity();
        }
        return entity;
    }

}
