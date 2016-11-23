package y2w.manage;

import com.y2w.uikit.utils.ThreadPool;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import y2w.db.SessionDb;
import y2w.db.SessionMemberDb;
import y2w.entities.SessionEntity;
import y2w.entities.SessionMemberEntity;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.SessionSrv;

/**
 * 会话管理
 * Created by yangrongfang on 2016/1/16.
 */
public class Sessions implements Serializable {
    private String TAG = Sessions.class.getSimpleName();
    private CurrentUser user;
    private Remote remote;
    private HashMap<String,Session> sessionHashMap;

    /**
     * 有参构造
     * @param user 当前登录用户
     */
    public Sessions(CurrentUser user){
        this.user=user;
        this.sessionHashMap = new HashMap<String,Session>();
    }

    /**
     * 获取当前登录用户
     * @return 返回结果
     */
    public CurrentUser getUser(){
        return user;
    }

    /**
     * 获取session哈希列表
     * @return 返回结果
     */
    public HashMap<String, Session> getSessionHashMap() {
        return sessionHashMap;
    }

    /**
     * 更新ession哈希列表
     */
    public void refreshSessionHashMap(Session session){
        if(sessionHashMap.containsKey(session.getEntity().getId())){
            sessionHashMap.remove(session.getEntity().getId());
            sessionHashMap.put(session.getEntity().getId(),session);
        }else {
            sessionHashMap.put(session.getEntity().getId(),session);
        }
    }

    /**
     * 获取远程访问实例
     * @return 返回结果
     */
    public Remote getRemote(){
        if(remote == null){
            remote = new Remote();
        }
        return remote;
    }

    /**
     * 某个session信息保存到数据库
     * @param session 会话
     */
    public void addSession(Session session){
        session.getEntity().setMyId(user.getEntity().getId());
        refreshSessionHashMap(session);
        SessionDb.addSessionEntity(session.getEntity());
    }

    /**
     * session列表保存到数据库
     * @param sessionList 会话列表
     */
    public void add(List<Session> sessionList){
        for(Session session : sessionList){
            addSession(session);
        }
    }

    /**
     * 根据目标Id获取session
     * session类型为p2p时，targetId为对方userId,为group时,targetId为sessionId，会话唯一标识码
     * @param targetId 目标id
     * @param type 会话类型
     * @param result 回调
     */
    public void getSessionByTargetId(final String targetId, final String type, final Back.Result<Session> result){
       ThreadPool.getThreadPool().executUI(new Runnable() {
           @Override
           public void run() {
               if(EnumManage.SessionType.p2p.toString().equals(type)){
                   getSession(targetId, type, result);
               }else{
                   if(sessionHashMap.containsKey(targetId)){
                       result.onSuccess(sessionHashMap.get(targetId));
                   }else {
                       getSession(targetId, type, result);
                   }
               }
           }
       });
    }

    /**
     * 根据会话类型获取session
     * session类型为p2p时，targetId为对方userId,为group时,targetId为sessionId，会话唯一标识码
     * @param targetId 目标id
     * @param type 会话类型
     * @param result 回调
     */
    private void getSession(final String targetId, final String type, final Back.Result<Session> result){
        SessionEntity entity = SessionDb.queryByTargetId(user.getEntity().getId(),targetId,type);
        if(entity != null){
            Session session = new Session(this, entity);
            if(!sessionHashMap.containsKey(session.getEntity().getId())) {
                sessionHashMap.put(session.getEntity().getId(), session);
            }
            result.onSuccess(session);
        }else{
            getRemote().getSession(targetId, type, result);
        }
    }

    /**
     * 根据sessionId获取session
     * @param sessionId 会话唯一标识码
     * @param result 回调
     */
    public void getSessionBySessionId(final String sessionId,final Back.Result<Session> result){
        if(sessionHashMap.containsKey(sessionId)){
            result.onSuccess(sessionHashMap.get(sessionId));
        }else {
           final Sessions that = this;
            ThreadPool.getThreadPool().executUI(new Runnable() {
                @Override
                public void run() {
                    SessionEntity entity = SessionDb.queryBySessionId(user.getEntity().getId(),sessionId);
                    if(entity != null){
                        if(EnumManage.SessionType.p2p.toString().equals(entity.getType())){
                            if(sessionHashMap.containsKey(entity.getOtherSideId())){
                                result.onSuccess(sessionHashMap.get(entity.getOtherSideId()));
                            }else{
                                Session session = new Session(that, entity);
                                if(!sessionHashMap.containsKey(sessionId)){
                                    sessionHashMap.put(entity.getOtherSideId(),session);
                                }
                                result.onSuccess(session);
                            }
                        }else{
                            Session session = new Session(that, entity);
                            if(!sessionHashMap.containsKey(sessionId)) {
                                sessionHashMap.put(sessionId, session);
                            }
                            result.onSuccess(session);
                        }

                    }else{
                        getRemote().getSession(sessionId, EnumManage.SessionType.group.toString(), result);
                    }
                }
            });
        }
    }


    /*****************************remote*****************************/
    /**
     * 远程访问类
     */
    public class Remote implements Serializable{
        public Remote(){

        }

        /**
         * 根据session类型获取某个session
         * session类型为p2p时，targetId为对方userId,为group时,为sessionId，会话唯一标识码
         * @param targetId  目标id
         * @param type 会话类型
         * @param result 回调
         */
        public void getSession(final String targetId, final String type, final Back.Result<Session> result){
            if(EnumManage.SessionType.p2p.toString().equals(type)){
                SessionSrv.getInstance().getSessionP2p(user.getToken(), user.getEntity().getId(), targetId, new Back.Result<SessionEntity>() {
                    @Override
                    public void onSuccess(SessionEntity entity) {
                         if(SessionMemberDb.queryCount(user.getEntity().getId(),entity.getId()) == 0){
                             //若本地没有一个session成员，则同步所有成员
                             new Session(Sessions.this, entity).getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
                                 @Override
                                 public void onSuccess(List<SessionMember> sessionMemberList) {
                                     LogUtil.getInstance().log(TAG, "sessionMemberList:" + sessionMemberList.size(), null);
                                 }

                                 @Override
                                 public void onError(int errorCode,String error) {

                                 }
                             });
                         }
                        entity.setOtherSideId(targetId);
                        addSession(new Session(Sessions.this, entity));
                        SessionEntity temp = SessionDb.queryByTargetId(user.getEntity().getId(), targetId, type);
                        Session session = new Session(Sessions.this, temp);
                        if(!sessionHashMap.containsKey(targetId)) {
                            sessionHashMap.put(targetId, session);
                        }
                        result.onSuccess(session);
                    }

                    @Override
                    public void onError(int errorCode,String error) {
                        result.onError(errorCode,error);
                    }
                });
            }else if(EnumManage.SessionType.group.toString().equals(type)){
                SessionSrv.getInstance().getSession(user.getToken(), targetId, new Back.Result<SessionEntity>() {
                    @Override
                    public void onSuccess(SessionEntity entity) {
                        //若本地没有一个session成员，则同步所有成员
                        if(SessionMemberDb.queryCount(user.getEntity().getId(),entity.getId()) == 0){
                            new Session(Sessions.this, entity).getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
                                @Override
                                public void onSuccess(List<SessionMember> sessionMemberList) {
                                    LogUtil.getInstance().log(TAG, "sessionMemberList:" + sessionMemberList.size(), null);
                                }

                                @Override
                                public void onError(int errorCode,String error) {

                                }
                            });
                        }
                        addSession(new Session(Sessions.this, entity));
                        SessionEntity temp = SessionDb.queryByTargetId(user.getEntity().getId(), targetId, type);
                        Session session = new Session(Sessions.this, temp);
                        if(!sessionHashMap.containsKey(targetId)) {
                            sessionHashMap.put(targetId, session);
                        }
                        result.onSuccess(new Session(Sessions.this, temp));
                    }

                    @Override
                    public void onError(int errorCode,String error) {
                        result.onError(errorCode,error);
                    }
                });
            }
        }

        /**
         * 创建session
         * @param name 会话名称
         * @param secureType 会话安全类型 "private"为私密，"public"为公开
         * @param type 会话类型
         * @param avatarUrl 会话头像
         * @param result 回调
         */
        public void sessionCreate(String name, String secureType, final String type, String avatarUrl, final Back.Result<Session> result){
            SessionSrv.getInstance().sessionCreate(user.getToken(), name, secureType, type, avatarUrl, new Back.Result<SessionEntity>() {
                @Override
                public void onSuccess(SessionEntity entity) {
                    addSession(new Session(Sessions.this, entity));
                    SessionEntity temp = SessionDb.queryByTargetId(user.getEntity().getId(), entity.getId(), entity.getType());
                    result.onSuccess(new Session(Sessions.this, temp));
                }
                @Override
                public void onError(int errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 更新session
         * @param result
         */
        public void sessionUpdate(Session session,boolean sendnameChanged, final Back.Result<Session> result){
            SessionSrv.getInstance().sessionUpdate(sendnameChanged,user.getToken(),session.getEntity().getId(), session.getEntity().getName(), session.getEntity().getSecureType(),session.getEntity().isNameChanged(), session.getEntity().getType(), session.getEntity().getAvatarUrl(), new Back.Result<SessionEntity>() {
                @Override
                public void onSuccess(SessionEntity entity) {
                    addSession(new Session(Sessions.this, entity));
                    SessionEntity temp = SessionDb.queryByTargetId(user.getEntity().getId(), entity.getId(), entity.getType());
                    result.onSuccess(new Session(Sessions.this, temp));
                }

                @Override
                public void onError(int errorCode, String error) {
                    result.onError(errorCode, error);
                }
            });
        }

    }


}
