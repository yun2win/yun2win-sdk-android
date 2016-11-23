package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.y2w.uikit.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.entities.EmojiEntity;

/**
 * Created by maa2 on 2016/4/8.
 */
public class EmojiDb {

    public static void delete(EmojiEntity entity){
        if(entity != null){
            try{
                DeleteBuilder<EmojiEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_emoji.deleteBuilder();
                deleteBuilder.where().eq("myId", entity.getMyId()).and().eq("id", entity.getId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_emoji.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }
    public static void addEmojiEntity(EmojiEntity entity){
        if(entity != null){
            try{
                delete(entity);
                DaoManager.getInstance(AppContext.getAppContext()).dao_emoji.create(entity);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static List<EmojiEntity> query(String myId){
        List<EmojiEntity> entityList = null;
        try {
            entityList = DaoManager.getInstance(AppContext.getAppContext()).dao_emoji.queryBuilder().distinct()
                    .where()
                    .eq("myId", myId).query();

        } catch (Exception e) {
            entityList = new ArrayList<EmojiEntity>();
        }
        return entityList;
    }

    public static List<EmojiEntity> queryByPackage(String myId, String ePackage){
        List<EmojiEntity> entityList = null;
        try {
            entityList = DaoManager.getInstance(AppContext.getAppContext()).dao_emoji.queryBuilder().distinct()
                    .where()
                    .eq("ePackage", ePackage)
                    .and()
                    .eq("myId", myId).query();

        } catch (Exception e) {
            entityList = new ArrayList<EmojiEntity>();
        }
        return entityList;
    }

    public static EmojiEntity queryByName(String myId,String name){
        EmojiEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_emoji.queryBuilder()
                    .where()
                    .eq("name", name)
                    .and()
                    .eq("myId", myId).queryForFirst();

        } catch (Exception e) {
        }
        return entity;
    }

    public static long queryCountByPackage(String myId,String ePackage){
        long count = 0;
        try {
            count = DaoManager.getInstance(AppContext.getAppContext()).dao_emoji.queryBuilder().distinct()
                    .where()
                    .eq("ePackage",ePackage)
                    .and()
                    .eq("myId", myId).countOf();

        } catch (Exception e) {

        }
        return count;
    }
}
