package y2w.service;

import java.util.List;

import y2w.base.ClientFactory;
import y2w.entities.SessionEntity;
import y2w.entities.SessionMemberEntity;

/**
 * 会话远程访问
 * Created by yangrongfang on 2016/1/16.
 */
public class SessionSrv {

    private static SessionSrv sessionSrv = null;
    public static SessionSrv getInstance(){
        if(sessionSrv == null){
            sessionSrv = new SessionSrv();
        }
        return sessionSrv;
    }


    /**
     * 获取自己的会话
     * @param token
     * @param userId
     * @param result
     */
    public void getSessionSingle(String token, String userId,final Back.Result<SessionEntity> result){
        ClientFactory.getInstance().getSessionSingle(token, userId, result);
    }

    /**
     * 获取p2p交流会话
     * @param userId
     * @param otherId
     * @param result
     */
    public void getSessionP2p(String token, String userId,String otherId, final Back.Result<SessionEntity> result){
        ClientFactory.getInstance().getSessionP2p(token, userId, otherId, result);
    }

    /**
     * 获取交流会话(不含p2p)
     * @param sessionId
     * @param result
     */
    public void getSession(String token, String sessionId, final Back.Result<SessionEntity> result){
        ClientFactory.getInstance().getSession(token, sessionId, result);
    }


    /**
     * 创建群交流会话
     * @param name
     * @param secureType
     * @param type
     * @param avatarUrl
     * @param result
     */
    public void sessionCreate(String token, String name, String secureType, String type, String avatarUrl, Back.Result<SessionEntity> result){
        ClientFactory.getInstance().sessionCreate(token, name, secureType, type, avatarUrl, result);
    }

    /**
     * 群交流会话更新
     * @param token
     * @param sessionId
     * @param name
     * @param secureType
     * @param nameChanged
     * @param type
     * @param avatarUrl
     * @param result
     */
    public void sessionUpdate(boolean sendnameChanged,String token,String sessionId, String name, String secureType,boolean nameChanged, String type, String avatarUrl, Back.Result<SessionEntity> result){
        ClientFactory.getInstance().sessionUpdate(sendnameChanged,token, sessionId, name, secureType, nameChanged, type, avatarUrl, result);
    }

    /**
     * 群交流会话添加成员
     * @param sessionId
     * @param userId
     * @param name
     * @param role
     * @param avatarUrl
     * @param status
     * @param result
     */
    public void sessionMemberAdd(String token, String sessionId, String userId, String name, String role, String avatarUrl, String status, Back.Result<SessionMemberEntity> result) {
        ClientFactory.getInstance().sessionMemberAdd(token, sessionId, userId, name, role, avatarUrl, status, result);
    }

    /**
     * 获取群聊成员
     * @param updateAt
     * @param sessionId
     * @param result
     */
    public void sessionMembersGet(String token, String updateAt, String sessionId,Back.Result<List<SessionMemberEntity>> result){
        ClientFactory.getInstance().sessionMembersGet(token, updateAt, sessionId, result);
    }

    /**
     * 删除某个群聊成员
     * @param sessionId
     * @param id
     * @param callback
     */

    public void sessionMembersDelete(String token, String sessionId, String id,Back.Callback callback){
        ClientFactory.getInstance().sessionMemberDelete(token, sessionId, id, callback);
    }
    public void sessionMemberUpdate(String token, String sessionId, String id,String userId,String name,String role,String avatarUrl,String status,Back.Result<SessionMemberEntity> resultCallback){
        ClientFactory.getInstance().sessionMemberUpdate(token, sessionId, id, userId, name, role, avatarUrl, status, resultCallback);
    }

}
