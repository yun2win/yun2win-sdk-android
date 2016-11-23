package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.entities.ContactEntity;
import y2w.entities.UserEntity;
import y2w.manage.Users;
import y2w.model.User;
import y2w.service.Back;

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
                ContactEntity entity =queryById(myId,id);
                if(entity!=null) {
                    entity.setIsDelete(true);
                    delete(entity.getMyId(), entity.getUserId());
                    DaoManager.getInstance(AppContext.getAppContext()).dao_contact.create(entity);
                }
            }catch(Exception e){

            }
        }
    }

    public static void addContactEntity(ContactEntity entity){
        if(entity != null){
            try{
                delete(entity.getMyId(), entity.getUserId());
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
    public static ContactEntity queryById(String myId,String id){
        ContactEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_contact.queryBuilder()
                    .where()
                    .eq("id",id).and().eq("myId", myId).queryForFirst();
        } catch (Exception e) {
            entity = new ContactEntity();
        }
        return entity;
    }

    public static ContactEntity queryByUserId(String myId,String userId){
        ContactEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_contact.queryBuilder()
                    .where()
                    .eq("userId",userId).and().eq("myId", myId).queryForFirst();
            if(entity!=null) {
                UserEntity userentity = UserDb.queryById(myId, userId);
                if (userentity != null && !StringUtil.isEmpty(userentity.getId())) {
                    entity.setName(userentity.getName());
                    entity.setAvatarUrl(userentity.getAvatarUrl());
                } else {
                    Users.getInstance().getRemote().userGet(userId, new Back.Result<User>() {
                        @Override
                        public void onSuccess(User user) {
                        }

                        @Override
                        public void onError(int Code, String error) {
                        }
                    });
                }
            }
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
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_contact.queryBuilder().distinct()
                    .where()
                    .eq("isDelete",false)
                    .and()
                    .ne("userId",myId)
                    .and()
                    .ne("userId","")
                    .and()
                    .eq("myId", myId).query();
          if(entities!=null&&entities.size()>0){
                for(int i =0;i<entities.size();i++){
                    ContactEntity entity =entities.get(i);
                    UserEntity userentity = UserDb.queryById(myId, entity.getUserId());
                    if(userentity!=null && !StringUtil.isEmpty(userentity.getId())){
                        entity.setName(userentity.getName());
                        entity.setAvatarUrl(userentity.getAvatarUrl());
                        entity.setEmail(userentity.getAccount());
                    }else{
                        Users.getInstance().getRemote().userGet(entity.getUserId(), new Back.Result<User>() {
                            @Override
                            public void onSuccess(User user) {
                            }
                            @Override
                            public void onError(int Code, String error) {
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            entities = new ArrayList<ContactEntity>();
        }
        return entities;
    }
   public static List<ContactEntity> searchByName(String myId,String nameKey){
        List<ContactEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_contact.queryBuilder().distinct()
                    .where()
                    .eq("isDelete", false)
                    .and()
                    .ne("userId",myId)
                    .and()
                    .ne("userId","")
                    .and()
                    .like("simpchinaname", "%" + nameKey+"%")
                    .and()
                    .eq("myId", myId).query();
        } catch (Exception e) {
            entities = new ArrayList<ContactEntity>();
        }
        return entities;
    }

}
