package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.entities.ContactEntity;
import com.y2w.uikit.utils.StringUtil;

/**
 * 通讯录表管理类
 * Created by yangrongfang on 2016/1/18.
 */
public class ContactDb {

    /**
     * 用户唯一标识码 删除
     * @param myId
     * @param userId 用户唯一标识码
     */
    public static void delete(String myId, String userId){
        if(!StringUtil.isEmpty(myId) && !StringUtil.isEmpty(myId)){
            try{
                DeleteBuilder<ContactEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_contact.deleteBuilder();
                deleteBuilder.where().eq("myId", myId).and().eq("userId", userId);
                DaoManager.getInstance(AppContext.getAppContext()).dao_contact.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }

    /**
     * 联系人唯一标识码 删除
     * @param myId
     * @param id 联系人唯一标识码
     */
    public static void deleteById(String myId, String id){
        if(!StringUtil.isEmpty(myId) && !StringUtil.isEmpty(myId)){
            try{
                DeleteBuilder<ContactEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_contact.deleteBuilder();
                deleteBuilder.where().eq("myId", myId).and().eq("userId", id);
                DaoManager.getInstance(AppContext.getAppContext()).dao_contact.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }

    public static void addContactEntity(ContactEntity entity){
        if(entity != null){
            try{
                delete(entity.getMyId(),entity.getUserId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_contact.create(entity);
            }catch(Exception e){

            }
        }
    }

    public static void add(List<ContactEntity> entities){
        if(entities != null){
            try{
                for(ContactEntity entity:entities) {
                    addContactEntity(entity);
                }

            }catch(Exception e){
            }
        }
    }

    public static ContactEntity queryByUserId(String myId,String userId){
        ContactEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_contact.queryBuilder()
                    .where()
                    .eq("userId",userId).and().eq("myId", myId).queryForFirst();
        } catch (Exception e) {
            entity = new ContactEntity();
        }
        return entity;
    }

    /**
     * 当前操作用户Id
     * @param myId
     * @return
     */

    public static List<ContactEntity> getAll(String myId){
        List<ContactEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_contact.queryBuilder()
                    .where()
                    .eq("isDelete",false)
                    .and()
                    .eq("myId", myId).query();
        } catch (Exception e) {
            entities = new ArrayList<ContactEntity>();
        }
        return entities;
    }


}
