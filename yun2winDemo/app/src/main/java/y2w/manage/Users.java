package y2w.manage;

import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.List;

import y2w.base.AppContext;
import y2w.db.UserDb;
import y2w.entities.ContactEntity;
import y2w.entities.UserEntity;
import y2w.model.User;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.UserSrv;
import com.y2w.uikit.utils.StringUtil;
import y2w.common.UserInfo;

/**
 * 用户管理类
 * Created by yangrongfang on 2016/1/16.
 */
public class Users {
    private String TAG = Users.class.getSimpleName();
    private static Users users = null;
    private CurrentUser currentUser = null;
    private Remote remote;

    /**
     * 获取单例
     * @return
     */
    public static Users getInstance(){
        if(users == null){
            users = new Users();
        }
        return  users;
    }

    /**
     * 设置当前登录用户
     * @param currentUser
     */
    public void setCurrentUser(CurrentUser currentUser){
        this.currentUser = currentUser;
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public CurrentUser getCurrentUser(){
        if(currentUser == null){
            currentUser = new CurrentUser();
        }
        return currentUser;
    }

    /**
     * 获取远程访问实例
     * @return
     */
    public Remote getRemote(){
        if(remote == null){
            remote = new Remote();
        }
        return remote;
    }

    /**
     * 将用户基本信息写入数据库
     * @param user 用户基本信息实体
     */
    public void addUser(User user){
        user.getEntity().setMyId(getCurrentUser().getEntity().getId());
        UserDb.addUserEntity(user.getEntity());
    }

    /**
     * 获取用户基本信息
     * @param userId 用户唯一标识码
     * @param result 回调
     */
    public void getUser(String userId,Back.Result<User> result){
        UserEntity entity = UserDb.queryById(getCurrentUser().getEntity().getId(),userId);
        if(entity != null){
            result.onSuccess(new User(entity));
        }else{
            getRemote().userGet(userId,result);
        }
    }

    /**
     * 创建用户基本信息
     * @param id 用户唯一标识码
     * @param name 用户名
     * @param avatarUrl 头像
     * @return 返回结果
     */
    public User createUser(String id, String name, String avatarUrl){
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setAvatarUrl(avatarUrl);
        return new User(entity);
    }

    /**
     * 创建一个登录用户
     * @param loginInfo 登录用户信息实体
     * @return 返回结果
     */
    public CurrentUser createCurrentUser(UserInfo.LoginInfo loginInfo){
        CurrentUser currentUser = new CurrentUser();
        if(loginInfo != null){
            currentUser.setAppKey(loginInfo.getKey());
            currentUser.setSecret(loginInfo.getSecret());
            currentUser.setToken(loginInfo.getToken());
            currentUser.getEntity().setId(loginInfo.getEntity().getId());
            currentUser.getEntity().setName(loginInfo.getEntity().getName());
            currentUser.getEntity().setAccount(loginInfo.getEntity().getEmail());
            currentUser.getEntity().setAvatarUrl(loginInfo.getEntity().getAvatarUrl());
        }
        return currentUser;
    }


    /***************************************************remote********************************************************/

    /**
     * 远程访问类
     */
    public class Remote implements Serializable {

        public Remote(){

        }

        /**
         * 注册
         * @param account 帐号
         * @param password 密码
         * @param name 用户名
         * @param result 回调
         */
        public void register(String account,String password,String name, final Back.Result<User> result){
            UserSrv.getInstance().register(AppContext.getAppContext().getAppKey(), account, name, StringUtil.get32MD5(password), new Back.Result<UserEntity>() {
                @Override
                public void onSuccess(UserEntity entity) {
                    result.onSuccess(new User(entity));
                }

                @Override
                public void onError(int errorCode, String error) {
                    result.onError(errorCode, error);
                }
            });
        }

        /**
         * 登录
         * @param account 帐号
         * @param password 密码
         * @param result 回调
         */
        public void login(final String account, final String password, final Back.Result<CurrentUser> result){
            UserSrv.getInstance().login(AppContext.getAppContext().getAppKey(), account, StringUtil.get32MD5(password), new Back.Result<UserInfo.LoginInfo>() {
                @Override
                public void onSuccess(UserInfo.LoginInfo loginInfo) {
                    LogUtil.getInstance().log(TAG, "token=" + loginInfo.getToken(), null);
                    LogUtil.getInstance().log(TAG, "uerId=" + loginInfo.getEntity().getId(), null);
                    currentUser = createCurrentUser(loginInfo);
                    //保存当前用户登录信息
                    UserInfo.setCurrentInfo(currentUser, password);
                    result.onSuccess(currentUser);
                }

                @Override
                public void onError(int code, String error) {
                    result.onError(code, error);
                }
            });
        }

        /**
         * 搜索
         * @param key 关键字
         * @param result 回调
         */
        public void search(String key,Back.Result<List<ContactEntity>> result){
            UserSrv.getInstance().search(currentUser.getToken(), key, result);
        }

        /**
         * 保存更新登录用户信息
         * @param entity 用户信息实体
         * @param result 回调
         */
        public void store(ContactEntity entity,Back.Result<ContactEntity> result){
            UserSrv.getInstance().userUpdate(currentUser.getToken(), entity.getUserId(), entity.getEmail(), entity.getName(), entity.getRole(), entity.getJobTitle(), entity.getPhone(), entity.getAddress(), entity.getStatus(), entity.getAvatarUrl(), result);
        }

        /**
         * 获取某个用户信息
         * @param userId 用户唯一标识码
         * @param result 回调
         */
        public void userGet(String userId, final Back.Result<User> result){
            UserSrv.getInstance().userGet(currentUser.getToken(), userId, new Back.Result<UserEntity>() {
                @Override
                public void onSuccess(UserEntity entity) {
                    addUser(new User(entity));
                    UserEntity userEntity = UserDb.queryById(currentUser.getEntity().getId(),entity.getId());
                    result.onSuccess(new User(userEntity));
                }

                @Override
                public void onError(int Code, String error) {
                    result.onError(Code, error);

                }
            });
        }

    }

}
