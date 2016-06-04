package y2w.service;

import java.util.List;

import y2w.base.ClientFactory;
import y2w.entities.UserConversationEntity;

/**
 * 用户会话
 * Created by yangrongfang on 2016/1/19.
 */
public class UserConversationSrv {


    private static UserConversationSrv userConversationSrv = null;
    public static UserConversationSrv getInstance(){
        if(userConversationSrv == null){
            userConversationSrv = new UserConversationSrv();
        }
        return userConversationSrv;
    }

    /**
     * 获取某个用户会话
     * @param userId
     * @param userConversationId
     * @param result
     */
    public void getUserConversation(String token, String userId,String userConversationId, Back.Result<UserConversationEntity> result){
        ClientFactory.getInstance().getUserConversation(token, userId, userConversationId, result);
    }

    /**
     * 获取用户会话列表
     * @param updateAt
     * @param userId
     * @param result
     */
    public void getUserConversations(String token, String updateAt,String userId, Back.Result<List<UserConversationEntity>> result){
        ClientFactory.getInstance().getUserConversations(token, updateAt, userId, result);
    }

    /**
     * 删除用户会话列表某个会话
     * @param userId
     * @param userConversationId
     * @param callback
     */
    public void deleteUserConversation(String token, String userId, String userConversationId, Back.Callback callback){
        ClientFactory.getInstance().deleteUserConversation(token, userId, userConversationId, callback);
    }

    public void updateUserConversation(String token, String userId, String userConversationId, String targetId, String name, boolean top, String type, String avatarUrl, Back.Result<UserConversationEntity> result){
        ClientFactory.getInstance().updateUserConversation(token, userId, userConversationId, targetId, name, top, type, avatarUrl, result);
    }

}
