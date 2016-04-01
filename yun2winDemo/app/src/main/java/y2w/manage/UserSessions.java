package y2w.manage;

import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import y2w.common.Constants;
import y2w.db.TimeStampDb;
import y2w.db.UserSessionDb;
import y2w.entities.TimeStampEntity;
import y2w.entities.UserSessionEntity;
import y2w.model.UserSession;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.UserSessionSrv;

/**
 * 群聊管理类
 * Created by maa2 on 2016/2/18.
 */
public class UserSessions implements Serializable{

    private CurrentUser user;
    private Remote remote;
    private String updatedAt;
    /**
     * 有参构造
     * @param user 当前用户
     */
    public UserSessions(CurrentUser user){
        this.user=user;
    }

    /**
     * 获取当前登录用户信息
     * @return
     */
    public CurrentUser getUser(){
        return user;
    }
    /**
     * 获取远程访问实例
     * @return 返回结果
     */
    public Remote getRemote(){
        if(remote == null){
            remote = new Remote(this);
        }
        return remote;
    }
    /**
     * 获取本地所有群聊
     * @return 返回结果
     */
    public List<UserSession> getUserSessions(){
        List<UserSessionEntity> entities = UserSessionDb.query(user.getEntity().getId());
        return  new ArrayList<UserSession>();
    }

    /**
     * 某个群聊信息保存到数据库
     * @param userSession 群聊
     */
    public void addUserSession(UserSession userSession){
        userSession.getEntity().setMyId(user.getEntity().getId());
        UserSessionDb.addUserSessionEntity(userSession.getEntity());
    }

    /**
     * 群聊列表保存到数据库
     * @param  userSessionList 群聊列表
     */
    public void add(List<UserSession> userSessionList){
        for(UserSession userSession : userSessionList){
            addUserSession(userSession);
        }
    }
    /**
     * 获取同步更新时间戳
     * @return 返回结果
     */
    public String getUpdatedAt() {
        TimeStampEntity entity= TimeStampDb.queryByType(user.getEntity().getId(), TimeStampEntity.TimeStampType.userSession.toString());
        if(entity != null){
            updatedAt = entity.getTime();
        }else{
            updatedAt = Constants.TIME_ORIGIN;
        }
        return updatedAt;
    }
    /*****************************remote*****************************/
    /**
     * 远程访问类
     */
    public class Remote implements Serializable{
        private UserSessions userSessions;
        public Remote(UserSessions userSessions){
            this.userSessions = userSessions;
        }

        /**
         * 将某个session添加到自己的群聊列表
         * @param sessionId 会话唯一标识码
         * @param name 会话名称
         * @param avatarUrl 会话头像
         * @param result 回调
         */
        public void sessionStore(String sessionId, String name, String avatarUrl, final Back.Result<UserSession> result){
             UserSessionSrv.getInstance().sessionStore(user.getToken(), user.getEntity().getId(), sessionId, name, avatarUrl, new Back.Result<UserSessionEntity>() {
                @Override
                public void onSuccess(UserSessionEntity entity) {
                    addUserSession(new UserSession(userSessions, entity));
                    UserSessionEntity temp = UserSessionDb.queryBySessionId(user.getEntity().getId(), entity.getSessionId());
                    result.onSuccess(new UserSession(userSessions, temp));
                }

                @Override
                public void onError(int errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 同步自己的群聊列表
         * @param result 回调
         */
        public void sync(final Back.Result<List<UserSession>> result){
            UserSessionSrv.getInstance().sync(user.getToken(), getUpdatedAt(),user.getEntity().getId(), new Back.Result<List<UserSessionEntity>>() {
                @Override
                public void onSuccess(List<UserSessionEntity> entities) {
                    List<UserSession> sessionList = new ArrayList<UserSession>();
                    for(UserSessionEntity entity:entities){
                        sessionList.add(new UserSession(userSessions, entity));
                    }
                    add(sessionList);
                    result.onSuccess(sessionList);
                }

                @Override
                public void onError(int errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 删除某个群聊
         * @param id 成员唯一标识码
         * @param callback 回调
         */
        public void userSessionDelete(String id, final Back.Callback callback){
            UserSessionSrv.getInstance().sessionDelete(user.getToken(), user.getEntity().getId(), id, new Back.Callback() {
                @Override
                public void onSuccess() {
                    callback.onSuccess();
                }

                @Override
                public void onError(int errorCode,String error) {
                    callback.onError(errorCode,error);
                }
            });
        }

    }


}
