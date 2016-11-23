package y2w.manage;

import java.io.Serializable;
import java.util.List;

import y2w.db.SessionMemberDb;
import y2w.entities.SessionMemberEntity;
import y2w.model.MToken;
import y2w.model.User;
import y2w.service.Back;
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
    private String role;
    private boolean isShowWork;
    private MToken imToken;
    private Contacts contacts;
    private Sessions sessions;
    private UserConversations userConversations;
    private UserSessions userSessions;
    private IMBridges imBridges;
    private Emojis emojis;
    private Remote remote;
    private CurrentUser user;
    private WebValues webValues;

    /**
     * 无参构造
     */
    public CurrentUser(){
        user = this;
    }
    public void initCurrentUser(){
        imToken = null;
        contacts=null;
        sessions=null;
        userConversations =null;
        userSessions = null;
        if(imBridges!=null&&imBridges.getImBridge()!=null&&imBridges.getImBridge().getImClient()!=null){
            imBridges.getImBridge().getImClient().disConnect();
        }
        imBridges =null;
        emojis = null;
        remote =null;
        webValues = null;
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
     * 获取当前用户角色
     * @return
     */
    public String getRole() {
        return role;
    }
    /**
     * 设置当前用户角色
     * @param showWork
     */
    public void setRole(String role) {
        this.role = role;
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
        getRemote().syncIMToken(result);
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
     * 获取缓存图片地址管理类
     * @return 返回结果
     */
    public WebValues getWebValues() {
        if(webValues == null){
            webValues = new WebValues(this);
        }
        return webValues;
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
     * 获取表情管理类实例
     * @return
     */
    public Emojis getEmojis() {
        if(emojis == null){
            emojis = new Emojis(this);
        }
        return emojis;
    }


    /*
    *
    关键字所有所有会话成员
    *
    */
    public List<SessionMemberEntity> getAllMembersBynameKey(String myuserId,String nameKey){
        List<SessionMemberEntity> entities = SessionMemberDb.searchByName(myuserId, nameKey);
        return entities;
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
