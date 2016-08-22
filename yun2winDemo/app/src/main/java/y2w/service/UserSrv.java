package y2w.service;



import java.util.List;

import y2w.manage.Users;
import y2w.base.ClientFactory;
import y2w.common.UserInfo;
import y2w.entities.ContactEntity;
import y2w.entities.UserEntity;
import y2w.model.Contact;
import y2w.model.MToken;
import y2w.model.UserConversation;

/**
 * 所有用户远程访问
 * Created by yangrongfang on 2016/1/23.
 */
public class UserSrv {
    private String grantType = "client_credentials";
    private static UserSrv userSrv = null;
    public static UserSrv getInstance(){
        if(userSrv == null){
            userSrv = new UserSrv();
        }
        return userSrv;
    }

    /**
     * 注册
     * @param appKey
     * @param email
     * @param name
     * @param password
     * @param result
     */
    public void register(String appKey, String email, String name, String password, Back.Result<UserEntity> result){
        ClientFactory.getInstance().register(appKey, email, name, password,result);
    }

    /**
     * 登录
     * @param appKey
     * @param email
     * @param password
     * @param result
     */
    public void login(String appKey, String email, String password, Back.Result<UserInfo.LoginInfo> result){
        ClientFactory.getInstance().login(appKey, email, password, result);
    }

    /**
     * 本应用用户搜索
     * @param keyword 姓名或是邮箱
     * @param result 回调
     */
    public void search(String token, String keyword, Back.Result<List<ContactEntity>> result){
        ClientFactory.getInstance().contactSearch(token, keyword, result);
    }

    /**
     * 更新用户信息
     * @param userId
     * @param email
     * @param name
     * @param role
     * @param jobTitle
     * @param phone
     * @param address
     * @param status
     * @param avatarUrl
     * @param result
     */
    public void userUpdate(String token, String userId, String email, String name, String role, String jobTitle, String phone, String address, String status, String avatarUrl, Back.Result<ContactEntity> result) {
        ClientFactory.getInstance().userUpdate(token, userId, email, name, role, jobTitle, phone, address, status, avatarUrl, result);
    }

    /**
     * 获取某个用户信息
     * @param token
     * @param userId
     * @param result
     */
    public void userGet(String token, String userId, Back.Result<UserEntity> result) {
        ClientFactory.getInstance().userGet(token, userId, result);
    }

    /**
     * 获取token
     * @param appKey
     * @param appSecret
     * @param result
     */
    public void getIMToken(String appKey, String appSecret, Back.Result<MToken> result) {
        ClientFactory.getInstance().getToken(grantType, appKey, appSecret, result);
    }

    /**
     * 登录成功后，同步更新通讯录与会话
     * @param callback
     */

    public void sync(final Back.Callback callback){
        Users.getInstance().getCurrentUser().getContacts().getRemote().sync(new Back.Result<List<Contact>>() {
            @Override
            public void onSuccess(List<Contact> contacts) {
                Users.getInstance().getCurrentUser().getContacts().add(contacts);
                Users.getInstance().getCurrentUser().getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
                    @Override
                    public void onSuccess(List<UserConversation> userConversationList) {
                        Users.getInstance().getCurrentUser().getUserConversations().add(userConversationList);
                        Users.getInstance().getCurrentUser().getEmojis().getRemote().sync();
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(int errorCode,String error) {
                        callback.onError(errorCode,error);
                    }
                });
            }

            @Override
            public void onError(int errorCode,String error) {
                callback.onError(errorCode,error);
            }
        });
    }


}
