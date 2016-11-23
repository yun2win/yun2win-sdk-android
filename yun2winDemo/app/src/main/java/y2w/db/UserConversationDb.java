package y2w.db;

import com.alibaba.fastjson.JSONObject;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.entities.ContactEntity;
import y2w.entities.UserConversationEntity;
import y2w.entities.UserEntity;
import y2w.manage.Users;
import y2w.model.User;
import y2w.model.messages.MessageType;
import y2w.service.Back;

/**
 * 用户会话表管理类
 * Created by maa2 on 2016/1/19.
 */
public class UserConversationDb {

    public static void delete(UserConversationEntity entity){
        if(entity != null){
            try{
                DeleteBuilder<UserConversationEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.deleteBuilder();
                deleteBuilder.where().eq("targetId", entity.getTargetId()).and().eq("type", entity.getType()).and().eq("myId", entity.getMyId());
                DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }
    public static void addUserConversation(UserConversationEntity entity){
        if(entity != null){
            try{
                UserConversationEntity tempentity = DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.queryBuilder()
                        .where()
                        .eq("targetId", entity.getTargetId()).and().eq("type", entity.getType()).and().eq("myId", entity.getMyId()).queryForFirst();
                delete(entity);
                 if(tempentity!=null){
                     if(!entity.getLastType().equals(MessageType.Draft)){
                         if(entity.getExtraDate()!=null&&entity.getExtraDate().equals("update")) {//清空草稿
                             entity.setExtraDate("");
                         }else{
                             if(tempentity.getLastType().equals(MessageType.Draft)) {//同步会话时原来会话有草稿
                                 JSONObject jsonObject = new JSONObject();
                                 jsonObject.put("lastsender", entity.getLastSender());
                                 jsonObject.put("lastcontext", entity.getLastContext());
                                 jsonObject.put("lasttype", entity.getLastType());
                                 jsonObject.put("updateat", entity.getUpdatedAt());
                                 jsonObject.put("draftmsg", tempentity.getLastContext());
                                 entity.setExtraDate(jsonObject.toJSONString());
                                 entity.setLastSender(tempentity.getLastSender());
                                 entity.setLastContext(tempentity.getLastContext());
                                 entity.setLastType(MessageType.Draft);
                             }
                         }
                     }
                 }
                DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.create(entity);
            }catch(Exception e){
                LogUtil.getInstance().log("hejie","hejie",e);
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

    public static UserConversationEntity queryByTargetId(String myId,String targetId,String type){
        UserConversationEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.queryBuilder()
                    .where()
                    .eq("targetId", targetId).and().eq("type", type).and().eq("myId", myId).queryForFirst();
           if(entity!=null&&entity.getType().equals("p2p")){
               UserEntity userentity = UserDb.queryById(myId, targetId);
               if (userentity != null && !StringUtil.isEmpty(userentity.getId())) {
                   entity.setName(userentity.getName());
                   entity.setAvatarUrl(userentity.getAvatarUrl());
               } else {
                   Users.getInstance().getRemote().userGet(targetId, new Back.Result<User>() {
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
        }
        return entity;
    }

    public static List<UserConversationEntity> query(String myId){
        List<UserConversationEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.queryBuilder().distinct().orderBy("updatedAt", false)
                    .where()
                    .eq("isDelete",false)
                    .and()
                    .eq("myId", myId).query();
            if(entities!=null&&entities.size()>0) {
                for (int i = 0; i < entities.size(); i++) {
                    UserConversationEntity entity = entities.get(i);
                    if (entity != null && entity.getType().equals("p2p")) {
                        UserEntity userentity = UserDb.queryById(myId, entity.getTargetId());
                        if (userentity != null && !StringUtil.isEmpty(userentity.getId())) {
                            entity.setName(userentity.getName());
                            entity.setAvatarUrl(userentity.getAvatarUrl());
                        } else {
                            Users.getInstance().getRemote().userGet(entity.getTargetId(), new Back.Result<User>() {
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
            }
        } catch (Exception e) {
            entities = new ArrayList<UserConversationEntity>();
        }
        return entities;
    }
    public static List<UserConversationEntity> searchByName(String myId,String nameKey){
        List<UserConversationEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_userConversation.queryBuilder().distinct().orderBy("updatedAt",false)
                    .where()
                    .eq("isDelete",false)
                    .and()
                    .like("simpchinaname", "%"+nameKey+"%")
                    .and()
                    .eq("myId", myId).query();
            if(entities!=null&&entities.size()>0) {
                for (int i = 0; i < entities.size(); i++) {
                    UserConversationEntity entity = entities.get(i);
                    if (entity != null && entity.getType().equals("p2p")) {
                        UserEntity userentity = UserDb.queryById(myId, entity.getTargetId());
                        if (userentity != null && !StringUtil.isEmpty(userentity.getId())) {
                            entity.setName(userentity.getName());
                            entity.setAvatarUrl(userentity.getAvatarUrl());
                        } else {
                            Users.getInstance().getRemote().userGet(entity.getTargetId(), new Back.Result<User>() {
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
            }
        } catch (Exception e) {
            entities = new ArrayList<UserConversationEntity>();
        }
        return entities;
    }
}
