package y2w.manage;

import java.io.Serializable;

import y2w.model.MToken;
import y2w.model.User;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.UserSrv;

/**
 * 当前登录用户相关信息
 *
 * Created by yangrongfang on 2016/1/16.
 */
public class CurrentUser extends User implements Serializable {

    private String TAG = CurrentUser.class.getSimpleName();
    private String appKey;
    private String secret;
    private String token;
    private MToken imToken;
    private Contacts contacts;
    private Sessions sessions;
    private UserConversations userConversations;
    private UserSessions userSessions;
    private IMBridges imBridges;
    private Remote remote;
    private CurrentUser user;

    /**
     * 无参构造
     */
    public CurrentUser(){
        user = this;
    }

    /**
     * 设置开发者key
     * @param appKey 开发者key
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    /**
     * 获取appKey
     * @return  返回结果
     */
    public String getAppKey(){
        return appKey;
    }

    /**
     * 设置开发者密钥
     * @param secret 开发者密钥
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 获取开发者密钥
     * @return
     */
    public String getSecret() {
        return secret;
    }

    /**
     * 获取token
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置token
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 设置消息通道token
     * @param imToken
     */
    public void setImToken(MToken imToken) {
        this.imToken = imToken;
    }

    /**
     * 异步获取消息通道token
     * @param result
     */
    public void getImToken(Back.Result<MToken> result) {
        if(imToken != null){
            result.onSuccess(imToken);
        }else{
            getRemote().syncIMToken(result);
        }
    }

    /**
     * 获取消息通道token
     * @return
     */
    public MToken getImToken() {
        return imToken;
    }

    /**
     * 获取通讯录管理类
     * @return 返回结果
     */
    public Contacts getContacts(){
        if(contacts == null){
            contacts = new Contacts(this);
        }
        return contacts;
    }

    /**
     * 获取会话管理类
     * @return 返回结果
     */
    public Sessions getSessions(){
        if(sessions == null){
            sessions = new Sessions(this);
        }
        return sessions;
    }
    /**
     * 获取用户会话管理类
     * @return 返回结果
     */
    public UserConversations getUserConversations(){
        if(userConversations == null){
            userConversations = new UserConversations(this);
        }
        return userConversations;
    }
    /**
     * 获取群聊管理类
     * @return 返回结果
     */
    public UserSessions getUserSessions() {
        if(userSessions == null){
            userSessions = new UserSessions(this);
        }
        return userSessions;
    }

    /**
     * 获取服务器连接管理类
     * @return 返回结果
     */
    public IMBridges getImBridges() {
        if(imBridges == null){
            imBridges = new IMBridges(this);
        }
        return imBridges;
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

    /*****************************remote*****************************/
    /**
     * 远程访问类
     */
    public class  Remote implements Serializable{

        public Remote(){

        }
        /**
         * 获取token
         * @param result 回调
         */
        public void syncIMToken(final Back.Result<MToken> result){
            UserSrv.getInstance().getIMToken(appKey, secret, new Back.Result<MToken>() {
                @Override
                public void onSuccess(MToken imToken) {
                    setImToken(imToken);
                    result.onSuccess(imToken);
                }
                @Override
                public void onError(int errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });

        }
        /**
         * 同步通讯录与用户会话列表
         * @param callback 回调
         */
        public void sync(final Back.Callback callback){
            UserSrv.getInstance().sync(callback);
        }

    }



}
