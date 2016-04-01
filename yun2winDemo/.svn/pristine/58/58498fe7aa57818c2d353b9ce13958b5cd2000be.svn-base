package y2w.service;

import java.util.List;

import y2w.base.ClientFactory;
import y2w.entities.UserSessionEntity;

/**
 * 群聊远程访问
 * Created by yangrongfang on 2016/3/8.
 */
public class UserSessionSrv {

    private static UserSessionSrv userSessionSrv = null;
    public static UserSessionSrv getInstance(){
        if(userSessionSrv == null){
            userSessionSrv = new UserSessionSrv();
        }
        return userSessionSrv;
    }

    /**
     * 将会话添加到自己的会话列表
     * @param token
     * @param userId
     * @param sessionId
     * @param name
     * @param avatarUrl
     * @param result
     */
    public void sessionStore(String token, String userId,  String sessionId, String name, String avatarUrl, Back.Result<UserSessionEntity> result){
        ClientFactory.getInstance().sessionStore(token, userId, sessionId, name, avatarUrl, result);
    }

    /**
     * 将会话从自己的会话列表中删除
     * @param token
     * @param userId
     * @param id
     * @param callback
     */
    public void sessionDelete(String token, String userId,  String id, Back.Callback callback){
        ClientFactory.getInstance().userSessionDelete(token, userId, id, callback);
    }

    /**
     * 同步群聊列表
     * @param token
     * @param userId
     * @param result
     */
    public void sync(String token,String syncTime, String userId, Back.Result<List<UserSessionEntity>> result){
        ClientFactory.getInstance().getUserSessionsList(token,syncTime, userId, result);
    }
}
