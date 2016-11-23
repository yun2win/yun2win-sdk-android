package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.y2w.uikit.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.entities.ContactEntity;
import y2w.entities.SessionMemberEntity;
import y2w.entities.UserEntity;
import y2w.manage.Users;
import y2w.model.User;
import y2w.service.Back;

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
        }
        return entity;
    }

    public static List<SessionMemberEntity> queryByUserId(String myId,String userId){
        List<SessionMemberEntity> entities;
        try {

            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.queryBuilder().distinct()
                    .where()
                    .eq("userId", userId).and().eq("isDelete", false).and().eq("myId", myId).query();
            if(entities!=null&&entities.size()>0){
                for(int i =0;i<entities.size();i++){
                    SessionMemberEntity entity =entities.get(i);
                    UserEntity userentity = UserDb.queryById(myId, entity.getUserId());
                    if(userentity!=null && !StringUtil.isEmpty(userentity.getId())){
                        entity.setName(userentity.getName());
                        entity.setAvatarUrl(userentity.getAvatarUrl());
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
            entities = new ArrayList<SessionMemberEntity>();
        }
        return entities;
    }

    public static List<SessionMemberEntity> query(String myId,String sessionId){
        List<SessionMemberEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.queryBuilder().distinct()
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("isDelete", false)
                    .and()
                    .eq("myId", myId).query();
            if(entities!=null&&entities.size()>0){
                for(int i =0;i<entities.size();i++){
                    SessionMemberEntity entity =entities.get(i);
                    UserEntity userentity = UserDb.queryById(myId, entity.getUserId());
                    if(userentity!=null && !StringUtil.isEmpty(userentity.getId())){
                        entity.setName(userentity.getName());
                        entity.setAvatarUrl(userentity.getAvatarUrl());
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
            entities = new ArrayList<SessionMemberEntity>();
        }
        return entities;
    }
    public static List<SessionMemberEntity> localquery(String myId,String sessionId){
        List<SessionMemberEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.queryBuilder().distinct()
                    .where()
                    .eq("sessionId", sessionId)
                    .and()
                    .eq("isDelete", false)
                    .and()
                    .eq("status", "active")
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
                    .eq("isDelete", false)
                    .and()
                    .eq("myId", myId).countOf();
        } catch (Exception e) {
        }
        return count;
    }

    public static List<SessionMemberEntity> searchByName(String myId,String nameKey){
        List<SessionMemberEntity> entities;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_sessionMember.queryBuilder().distinct()
                    .where()
                    .like("name", "%"+nameKey+"%")
                    .and().eq("isDelete", false)
                    .and()
                    .eq("myId", myId).query();
            if(entities!=null&&entities.size()>0){
                for(int i =0;i<entities.size();i++){
                    SessionMemberEntity entity =entities.get(i);
                    UserEntity userentity = UserDb.queryById(myId, entity.getUserId());
                    if(userentity!=null && !StringUtil.isEmpty(userentity.getId())){
                        entity.setName(userentity.getName());
                        entity.setAvatarUrl(userentity.getAvatarUrl());
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
            entities = new ArrayList<SessionMemberEntity>();
        }
        return entities;
    }


}
