package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.y2w.uikit.utils.StringUtil;

import y2w.base.AppContext;
import y2w.entities.WebValueEntity;

/**
 * 用户表管理类
 * Created by maa2 on 2016/3/15.
 */
public class WebValueDb {

    public static void addWebValueEntity(WebValueEntity entity){
        if(entity != null){
            try{
                delete(entity.getKey(),entity.getMyId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_webValue.create(entity);
            }catch(Exception e){

            }
        }
    }
    public static void delete(String key,String myId){
        if(!StringUtil.isEmpty(key)){
            try{
                DeleteBuilder<WebValueEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_webValue.deleteBuilder();
                deleteBuilder.where().eq("key", key).and().eq("myId", myId);;
                DaoManager.getInstance(AppContext.getAppContext()).dao_webValue.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }

    public static WebValueEntity queryBykey(String key,String myId){
        WebValueEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_webValue.queryBuilder()
                    .where()
                    .eq("key",key).and().eq("myId", myId).queryForFirst();
        } catch (Exception e) {
            entity = new WebValueEntity();
        }
        return entity;
    }

}
