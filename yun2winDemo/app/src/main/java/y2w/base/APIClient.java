package y2w.base;

import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ThreadPool;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import y2w.common.Config;
import y2w.common.HttpUtil;

import com.yun2win.push.IMConfig;
import com.yun2win.utils.Json;
import com.yun2win.utils.LogUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import y2w.db.SessionDb;
import y2w.entities.ContactEntity;
import y2w.entities.EmojiEntity;
import y2w.entities.MessageEntity;
import y2w.entities.SessionEntity;
import y2w.entities.SessionMemberEntity;
import y2w.entities.UserConversationEntity;
import y2w.entities.UserEntity;
import y2w.entities.UserSessionEntity;
import y2w.manage.Users;
import y2w.model.MToken;
import y2w.model.SyncMessagesModel;
import y2w.model.Update;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.common.UserInfo;

/**
 * 远程服务器访问类
 * Created by yangrongfang on 2015/12/28.
 */
public class APIClient {

    private String TAG = APIClient.class.getSimpleName();
    private AppContext appContext = AppContext.getAppContext();
    private String ec_para_error = appContext.getResources().getString(R.string.ec_parameter_error);
    private String ec_network_error = appContext.getResources().getString(R.string.ec_newwork_error);

    /***********************************************************token过期处理**********************************************************/
    /**
     * 判断token是否过期
     * @param result 服务器返回数据
     * @return 返回结果
     */
    private boolean isTokenExpired(String result){
        Json json = new Json(result);
        String status = json.getStr("status");
        String message = json.getStr("message");
        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
            int key = Integer.parseInt(status);
            LogUtil.getInstance().log(TAG,"result:"+status+" -|- "+message,null);
            if(key == 401||key==400){
                LogUtil.getInstance().log(TAG,message,null);
                return true;
            }
            return false;
        }else{
            return false;
        }
    }

    /**
     * 更新token
     * @return refreshToken
     */
    private UserInfo.LoginInfo refreshToken(){
       String result = "";
       String token = "";
        String key = "";
        String secret ="";
        UserInfo.LoginInfo loginInfo=null;
       try {
           Map<String, String> params = new HashMap<String, String>();
           params.put("appKey", Users.getInstance().getCurrentUser().getAppKey());
           params.put("email", Users.getInstance().getCurrentUser().getEntity().getAccount());
           params.put("password", StringUtil.get32MD5(UserInfo.getPassWord()));
           result = HttpUtil.post(Urls.User_Login, params);
       }catch (Exception e){
           return loginInfo;
       }
       Json json = new Json(result);
       String status = json.getStr("status");
       String message = json.getStr("message");
       if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
           ToastUtil.ToastMessage(AppContext.getAppContext(),"用户信息验证失败,请重新登录");
            AppContext.getAppContext().logout();
       } else {
           loginInfo = UserInfo.LoginInfo.parseJson(json);
           token = loginInfo.getToken();
           key = loginInfo.getKey();
           secret = loginInfo.getSecret();
           LogUtil.getInstance().log(TAG,"token refresh:"+token,null);
           if(!StringUtil.isEmpty(token)&&!StringUtil.isEmpty(key)&&!StringUtil.isEmpty(secret)){
               Users.getInstance().getCurrentUser().setToken(token);
               Users.getInstance().getCurrentUser().setAppKey(key);
               Users.getInstance().getCurrentUser().setSecret(secret);
               Users.getInstance().getCurrentUser().getEntity().setUpdatedAt(loginInfo.getEntity().getUpdatedAt());
               Users.getInstance().getCurrentUser().getEntity().setAvatarUrl(loginInfo.getEntity().getAvatarUrl());
               Users.getInstance().getCurrentUser().getEntity().setName(loginInfo.getEntity().getName());
               Users.getInstance().getCurrentUser().setRole(loginInfo.getEntity().getRole());
               UserInfo.setCurrentInfo(Users.getInstance().getCurrentUser(),UserInfo.getPassWord());//存到本地
           }
       }
       return loginInfo;
    }
    /***********************************************************注册,登录**********************************************************/

    /**
     * 注册
     * @param appKey 开发者key
     * @param email 帐号
     * @param name 用户名
     * @param password  密码
     * @param resultCallback 回调
     */
    public void register(final String appKey ,final String email,final String name, final String password, final Back.Result<UserEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                try {
                    if (StringUtil.isEmpty(appKey) || StringUtil.isEmpty(email) || StringUtil.isEmpty(name) || StringUtil.isEmpty(password)) {
                        resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR, ec_para_error);
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("appKey", appKey);
                        params.put("email", email);
                        params.put("name", name);
                        params.put("password", password);
                        params.put("avatarUrl", Urls.User_Avatar_Def);
                        result = HttpUtil.post(Urls.User_Register, params);
                    }
                } catch (Exception e) {
                    resultCallback.onError(ErrorCode.EC_NETWORK_ERROR, ec_network_error);
                    return;
                }
                Json json = new Json(result);
                String status = json.getStr("status");
                String message = json.getStr("message");
                if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                    int key = Integer.parseInt(status);
                    if(key > 0){
                        resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                    }else{
                        resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                    }
                } else {
                    resultCallback.onSuccess(UserEntity.parse(json));
                }
            }
        });
    }

    /**
     * 登录
     * @param appKey 开发者key
     * @param email 帐号
     * @param password 密码
     * @param resultCallback 回调
     */
    public void login(final String appKey ,final String email,final String password, final Back.Result<UserInfo.LoginInfo> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                try {
                    if (StringUtil.isEmpty(appKey) || StringUtil.isEmpty(email) || StringUtil.isEmpty(password)) {
                        resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("appKey", appKey);
                        params.put("email", email);
                        params.put("password", password);
                        result = HttpUtil.post(Urls.User_Login, params);
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.EC_NETWORK_ERROR,ec_network_error);
                    return;
                }
                Json json = new Json(result);
                String status = json.getStr("status");
                String message = json.getStr("message");
                if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                    int key = Integer.parseInt(status);
                    if(key > 0){
                        resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                    }else{
                        resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                    }
                } else {
                    resultCallback.onSuccess(UserInfo.LoginInfo.parseJson(json));
                }
            }
        });
    }

    /***********************************************************Token**********************************************************/

    /**
     * 获取连接消息通道服务器token
     * @param grantType 固定值"client_credentials"
     * @param appKey 开发者key
     * @param appSecret 开发者密钥
     * @param resultCallback 回调
     */
    public void getToken(final String grantType, final String appKey, final String appSecret, final Back.Result<MToken> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh = "";
                try {
                    if (StringUtil.isEmpty(grantType) || StringUtil.isEmpty(appKey) || StringUtil.isEmpty(appSecret)) {
                        resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("grant_type", grantType);
                        params.put("client_id", appKey);
                        params.put("client_secret", appSecret);
                        result = HttpUtil.post(Config.Token_Get, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                Map<String, String> newparams = new HashMap<String, String>();
                                newparams.put("grant_type", grantType);
                                newparams.put("client_id", loginInfo.getKey());
                                newparams.put("client_secret", loginInfo.getSecret());
                                result = HttpUtil.post(Config.Token_Get, newparams);
                            }
                        }
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.EC_NETWORK_ERROR,ec_network_error);
                    return;
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                        }
                    } else {
                        resultCallback.onSuccess(MToken.parse(json));
                    }
                }catch (Exception e){
                   String ee = e.getMessage();
                }

            }
        });
    }

    /***********************************************************检测是否有新版本**********************************************************/
    public static Update checkVersion(AppContext appContext){
        try {
            Map<String, String> params = new HashMap<String, String>();
            long currenttime=System.currentTimeMillis();
            String result = HttpUtil.get("", Urls.User_App_Version_Get+"?date="+currenttime, params);
            Json json = new Json(result);

            return Update.parse(json);
        } catch (Exception e) {

        }
        return null;
    }

    /***********************************************************通讯录**********************************************************/
    /**
     * 获取某个用户信息
     * @param userId 用户id
     * @param resultCallback 返回结果
     */
    public void contactGet(final String token, final String userId ,String id, final Back.Result<ContactEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh = "";
                if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)) {
                    resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_para_error);
                } else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, Urls.User_Contact_Get + userId + Urls.User_Contacts_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, Urls.User_Contact_Get + userId + Urls.User_Contacts_Last, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_NETWORK_ERROR,ec_network_error);
                        return;
                    }
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                        }
                    } else {
                        resultCallback.onSuccess(ContactEntity.parse(json));
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /**
     * 获取本用户的通讯录列表.
     * @param userId 用户唯一标识码
     * @param resultCallback 回调
     */
    public void getContacts(final String token, final String updateAt, final int limit, final String userId,final Back.Result<List<ContactEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                try {
                    if (StringUtil.isEmpty(token)|| StringUtil.isEmpty(updateAt) || StringUtil.isEmpty(userId)) {
                        resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("limit", ""+limit);
                        result = HttpUtil.get(token,updateAt,Urls.User_Contacts_Get+userId+Urls.User_Contacts_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh,updateAt,Urls.User_Contacts_Get+userId+Urls.User_Contacts_Last, params);
                            }
                        }
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.EC_NETWORK_ERROR,ec_network_error);
                    return;
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                        }
                    } else {
                        List<ContactEntity> contacts = new ArrayList<ContactEntity>();
                        int total = json.getInt("total_count");
                        List<Json> jSons = json.get("entries").toList();
                        for(Json j:jSons){
                            contacts.add(ContactEntity.parseSync(j,total));
                        }
                        resultCallback.onSuccess(contacts);
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /**
     * 搜索用户
     * @param keyword 关键字
     * @param resultCallback 回调
     */
    public void contactSearch(final String token,final String keyword , final Back.Result<List<ContactEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isEmpty(token) || StringUtil.isEmpty(keyword)) {
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                } else {
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, Urls.User_Contact_Search+keyword, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh,Urls.User_Contact_Search+keyword, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }
                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            List<ContactEntity> entities = new ArrayList<ContactEntity>();
                            List<Json> jSons = json.get("entries").toList();
                            for(Json j:jSons){
                                entities.add(ContactEntity.parse(j));
                            }
                            resultCallback.onSuccess(entities);
                        }
                    }catch (Exception e){

                    }
                }
            }
        });

    }

    /**
     * 添加用户到通讯录
     * @param userId 用户唯一标识码
     * @param resultCallback 回调
     */
    public void contactAdd(final String token,final String userId , final String otherId, final String email,final String name,final String avatarUrl, final Back.Result<ContactEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(otherId) || StringUtil.isEmpty(email) || StringUtil.isEmpty(name)) {
                        resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userId", otherId);
                        params.put("email", email);
                        params.put("name", name);
                        params.put("avatarUrl", avatarUrl);
                        result = HttpUtil.post(token, Urls.User_Contact_Add + userId + Urls.User_Contacts_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.post(tokenRefresh, Urls.User_Contact_Add + userId + Urls.User_Contacts_Last, params);
                            }
                        }
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                    return;
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                        }
                    } else {
                        resultCallback.onSuccess(ContactEntity.parse(json));
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /**
     * 更新用户的某个联系人
     * @param userId 用户唯一标识码
     * @param otherId 对方唯一标识码
     * @param id 联系人唯一标识码
     * @param name 用户名
     * @param title 备注
     * @param remark 标志
     * @param avatarUrl 头像
     * @param callback 回调
     */
    public void contactUpdate(final String token,final String userId,final String otherId,final String id,final String name,final String title,final String remark,final String avatarUrl, final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(otherId) || StringUtil.isEmpty(id) || StringUtil.isEmpty(name) || StringUtil.isEmpty(title) || StringUtil.isEmpty(remark)) {
                        callback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userId", otherId);
                        params.put("name", name);
                        params.put("title", title);
                        params.put("remark",remark);
                        params.put("avatarUrl", avatarUrl);
                        result = HttpUtil.put(token, Urls.User_Contact_Update + userId + Urls.User_Contact_Update_Last + id, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.put(tokenRefresh, Urls.User_Contact_Update + userId + Urls.User_Contact_Update_Last + id, params);
                            }
                        }
                    }
                }catch (Exception e){
                    callback.onError(ErrorCode.EC_TOKEN_ERROR,ec_network_error);
                    return;
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            callback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            callback.onError(ErrorCode.EC_UNKNOWN,"");
                        }
                    } else {
                        callback.onSuccess();
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /**
     * 更新用户信息
     * @param userId 用户唯一标识码
     * @param email 帐号
     * @param name 用户名
     * @param role 角色
     * @param jobTitle 职位
     * @param phone 手机
     * @param address 地址
     * @param status 状态
     * @param avatarUrl 头像
     * @param resultCallback 回调
     */
    public void userUpdate(final String token,final String userId,final String email,final String name,final String role,final String jobTitle,final String phone,final String address,final String status,final String avatarUrl, final Back.Result<ContactEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(name) || StringUtil.isEmpty(role) || StringUtil.isEmpty(status)) {
                        resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                        return;
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userId", userId);
                        params.put("email", email);
                        params.put("name", name);
                        params.put("role",role);
                        params.put("jobTitle", jobTitle);
                        params.put("phone", phone);
                        params.put("address", address);
                        params.put("status", status);
                        params.put("avatarUrl", avatarUrl);
                        result = HttpUtil.put(token, Urls.User_Update + userId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.put(tokenRefresh, Urls.User_Update + userId, params);
                            }
                        }
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                    return;
                }

                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                        }
                    } else {
                        resultCallback.onSuccess(ContactEntity.parse(json));
                    }
                }catch (Exception e){

                }

            }
        });

    }


    /**
     * 获取某个用户信息
     * @param token 访问令牌
     * @param userId 用户唯一标识码
     * @param resultCallback
     */
    public void userGet(final String token, final String userId, final Back.Result<UserEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)) {
                        resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                        return;
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, Urls.User_Get + userId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, Urls.User_Get + userId, params);
                            }
                        }
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                    return;
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                        }
                    } else {
                        resultCallback.onSuccess(UserEntity.parse(json));
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /**
     * 个人信息密码修改
     * @param token
     * @param oldPassword
     * @param password
     * @param callback
     */
    public void userSetPassword(final String token, final String oldPassword,final String password, final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(oldPassword) || StringUtil.isEmpty(password)) {
                        callback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                        return;
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("oldPassword", oldPassword);
                        params.put("password", password);
                        result = HttpUtil.post(token, Urls.User_SetPassword, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, Urls.User_SetPassword, params);
                            }
                        }
                    }
                }catch (Exception e){
                    callback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                    return;
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            callback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            callback.onError(ErrorCode.EC_UNKNOWN, "");
                        }
                    } else {
                        callback.onSuccess();
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /**
     * 从通讯录中删除某个用户
     * @param userId 自己用户唯一标识码
     * @param id 被删除联系人唯一标识码
     * @param callback 回调
     */
    public void contactDelete(final String token,final String userId,final String id , final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(id)) {
                        callback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.delete(token, Urls.User_Contact_Delete + userId + Urls.User_Contact_Delete_Last + id, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.delete(tokenRefresh, Urls.User_Contact_Delete + userId + Urls.User_Contact_Delete_Last + id, params);
                            }
                        }
                    }
                }catch (Exception e){
                    callback.onError(ErrorCode.EC_TOKEN_ERROR,ec_network_error);
                    return;
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            callback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            callback.onError(ErrorCode.EC_UNKNOWN,"");
                        }
                    } else {
                        callback.onSuccess();
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /***********************************************Session***************************************************/



    /**
     * 获取自己的群组列表
     * @param userId 用户唯一标识码
     * @param resultCallback 回调
     */
    public void getUserSessionsList(final String token,final String syncTime,final String userId,final Back.Result<List<UserSessionEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {

                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else{
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, syncTime, Urls.User_Sessions_Get + userId + Urls.User_Sessions_Get_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, syncTime, Urls.User_Sessions_Get + userId + Urls.User_Sessions_Get_Last, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }

                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            List<UserSessionEntity> userSessionEntities = new ArrayList<UserSessionEntity>();
                            List<Json> jSons = new Json(result).get("entries").toList();
                            for(Json j:jSons){
                                userSessionEntities.add(UserSessionEntity.parse(j));
                            }
                            resultCallback.onSuccess(userSessionEntities);
                        }
                    }catch (Exception e){

                    }
                }
            }
        });

    }

    /**
     * 创建会话
     * @param name 会话名称
     * @param secureType 会话安全类型
     * @param type 会话类型
     * @param avatarUrl 会话头像
     * @param resultCallback 回调
     */

    public void sessionCreate(final String token,final String name,final String secureType, final String type, final String avatarUrl,
                              final Back.Result<SessionEntity> resultCallback){

        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(name)|| StringUtil.isEmpty(secureType)
                        || StringUtil.isEmpty(type)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else{
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name",name);
                        params.put("secureType",secureType);
                        params.put("type",type);
                        params.put("avatarUrl",avatarUrl);
                        result = HttpUtil.post(token, Urls.User_Session_Create, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.post(tokenRefresh, Urls.User_Session_Create, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }
                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(SessionEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }

            }
        });
    }


    public void sessionUpdate(final boolean sendnameChanged, final String token, final String sessionId, final String name, final String secureType, final boolean nameChanged, final String type, final String avatarUrl,
                              final Back.Result<SessionEntity> resultCallback){

        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)|| StringUtil.isEmpty(name)|| StringUtil.isEmpty(secureType)
                        || StringUtil.isEmpty(type)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                    return;
                }else{
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name",name);
                       if(sendnameChanged) {
                           params.put("nameChanged", nameChanged + "");
                       }
                        params.put("secureType",secureType);
                        params.put("type",type);
                        params.put("avatarUrl",avatarUrl);
                        result = HttpUtil.put(token, Urls.User_Session_Update + sessionId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.post(tokenRefresh,  Urls.User_Session_Update + sessionId, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }
                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(SessionEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }

            }
        });
    }

    /**
     * 将会话添加到自己的会话列表
     * @param userId 用户唯一标识码
     * @param name 会话名称
     * @param sessionId 会话Id
     * @param avatarUrl 会话头像
     * @param resultCallback 回调
     */
    public void sessionStore(final String token,final String userId, final String sessionId, final String name,final String avatarUrl,
                           final Back.Result<UserSessionEntity> resultCallback){

        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)|| StringUtil.isEmpty(sessionId)|| StringUtil.isEmpty(name)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else{
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("sessionId",sessionId);
                        params.put("name",name);
                        params.put("avatarUrl",avatarUrl);
                        result = HttpUtil.post(token, Urls.User_Sessions_Store + userId + Urls.User_Sessions_Store_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.post(tokenRefresh, Urls.User_Sessions_Store + userId + Urls.User_Sessions_Store_Last, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }

                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(UserSessionEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }

            }
        });
    }

    /**
     * 将会话从自己的会话列表中删除
     * @param userId 用户唯一标识码
     * @param id 群组唯一标识码
     * @param callback 回调
     */
    public void userSessionDelete(final String token,final String userId,final String id , final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(id)){
                    callback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else{
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.delete(token, Urls.User_Sessions_Delete + userId + Urls.User_Sessions_Delete_Last + id, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.delete(tokenRefresh, Urls.User_Sessions_Delete + userId + Urls.User_Sessions_Delete_Last + id, params);
                            }
                        }
                    }catch (Exception e){
                        callback.onError(ErrorCode.EC_TOKEN_ERROR,ec_network_error);
                        return;
                    }
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            callback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            callback.onError(ErrorCode.EC_UNKNOWN,"");
                        }
                    } else {
                        callback.onSuccess();
                    }
                }catch (Exception e){

                }

            }
        });
    }

    /**
     * 会话添加成员
     * @param sessionId 会话Id
     * @param userId 成员Id
     * @param name 成员名称
     * @param role 成员角色
     * @param avatarUrl 成员头像
     * @param status 成员状态
     * @param resultCallback 回调
     */
    public void sessionMemberAdd(final String token,final String sessionId,final String userId,final String name,final String role,final String avatarUrl,final String status,final Back.Result<SessionMemberEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else{
                    try{
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userId",userId);
                        params.put("name",name);
                        params.put("role",role);
                        params.put("avatarUrl",avatarUrl);
                        params.put("status",status);
                        result = HttpUtil.post(token, Urls.User_SessionMember_Add + sessionId + Urls.User_SessionMember_Add_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.post(tokenRefresh, Urls.User_SessionMember_Add + sessionId + Urls.User_SessionMember_Add_Last, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }
                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(SessionMemberEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }
            }
        });

    }

    /**
     * 删除本会话某个成员
     * @param sessionId 会话Id
     * @param id 会话成员唯一标识码
     * @param callback 回调
     */
    public void sessionMemberDelete(final String token,final String sessionId , final String id, final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId) || StringUtil.isEmpty(id)){
                    callback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else{
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.delete(token, Urls.User_SessionMember_Delete + sessionId + Urls.User_SessionMember_Delete_Last + id, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.delete(tokenRefresh, Urls.User_SessionMember_Delete + sessionId + Urls.User_SessionMember_Delete_Last + id, params);
                            }
                        }
                    }catch (Exception e){
                        callback.onError(ErrorCode.EC_TOKEN_ERROR,ec_network_error);
                        return;
                    }

                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                callback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                callback.onError(ErrorCode.EC_UNKNOWN,"");
                            }
                        } else {
                            callback.onSuccess();
                        }
                    }catch (Exception e){

                    }

                }

            }
        });

    }

    /**
     * 更新成员状态信息
     */
   public void sessionMemberUpdate(final String token,final String sessionId,final String id,final String userId,final String name,final String role,final String avatarUrl,final String status,final Back.Result<SessionMemberEntity> resultCallback){
       ThreadPool.getThreadPool().executNet(new Runnable() {
           @Override
           public void run() {
               String result = "";
               String tokenRefresh;
               if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId) || StringUtil.isEmpty(id)){
                   resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
               }else{
                   try {
                       Map<String, String> params = new HashMap<String, String>();
                       params.put("userId",userId);
                       params.put("name",name);
                       params.put("role",role);
                       params.put("avatarUrl",avatarUrl);
                       params.put("status",status);
                       result = HttpUtil.put(token, Urls.User_SessionMembers_update + sessionId + Urls.User_SessionMembers_update_Last + id, params);
                       if(isTokenExpired(result)){
                           UserInfo.LoginInfo loginInfo=refreshToken();
                           if(loginInfo!=null){
                               tokenRefresh = loginInfo.getToken();
                               result = HttpUtil.put(tokenRefresh, Urls.User_SessionMembers_update + sessionId + Urls.User_SessionMembers_update_Last + id, params);
                           }
                       }
                   }catch (Exception e){
                       resultCallback.onError(ErrorCode.EC_TOKEN_ERROR,ec_network_error);
                       return;
                   }

                   try {
                       Json json = new Json(result);
                       String status = json.getStr("status");
                       String message = json.getStr("message");
                       if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                           int key = Integer.parseInt(status);
                           if(key > 0){
                               resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                           }else{
                               resultCallback.onError(ErrorCode.EC_UNKNOWN,"");
                           }
                       } else {
                           resultCallback.onSuccess(SessionMemberEntity.parse(new Json(result)));
                       }
                   }catch (Exception e){

                   }

               }

           }
       });

   }
    /**
     * 获取本会话成员列表.
     * @param sessionId 会话Id
     * @param resultCallback 回调
     */
    public void sessionMembersGet(final String token,String updateAt, final String sessionId , final Back.Result<List<SessionMemberEntity>> resultCallback){

        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else{
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, Urls.User_SessionMembers_Get + sessionId + Urls.User_SessionMembers_Get_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh,Urls.User_SessionMembers_Get+sessionId+Urls.User_SessionMembers_Get_Last, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }

                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            List<SessionMemberEntity> sessionMembers = new ArrayList<SessionMemberEntity>();
                            List<Json> jSons = new Json(result).get("entries").toList();
                            for(Json j:jSons){
                                sessionMembers.add(SessionMemberEntity.parse(j));
                            }
                            resultCallback.onSuccess(sessionMembers);
                        }
                    }catch (Exception e){
                       LogUtil.getInstance().log("dd","ddd",e);
                    }
                }
            }
        });
    }
    /***********************************************UserSession***************************************************/
    public void updateUserSession(final String token, final String userId,final String userSessionId, final String sessionId,  final String name, final String avatarUrl, final Back.Result<UserSessionEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(userSessionId) || StringUtil.isEmpty(name)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("sessionId",sessionId);
                        params.put("name",name);
                        params.put("avatarUrl",avatarUrl);
                        result = HttpUtil.put(token, Urls.User_Sessions_Update + userId + Urls.User_Sessions_Update_Last + userSessionId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.put(tokenRefresh, Urls.User_Sessions_Update + userId + Urls.User_Sessions_Update_Last + userSessionId, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            resultCallback.onError(ErrorCode.EC_UNKNOWN,"");
                        }
                    } else {
                        resultCallback.onSuccess(UserSessionEntity.parse(json));
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /************************************************用户会话****************************************************/


    /**
     * 获取某个用户会话信息
     * @param userId 用户唯一标识码
     * @param userConversationId 用户会话唯一标识码
     * @param resultCallback 回调
     */
    public void getUserConversation(final String token,final String userId,final String userConversationId,final Back.Result<UserConversationEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userConversationId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, Urls.User_UserConversation_Get + userId + Urls.User_UserConversation_Last + userConversationId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, Urls.User_UserConversation_Get + userId + Urls.User_UserConversation_Last + userConversationId, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }

                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(UserConversationEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }
            }
        });

    }


    /**
     * 获取自己的用户会话列表
     * @param updateAt 更新时间戳
     * @param userId 自己的唯一标识码
     * @param resultCallback 回调
     */
    public void getUserConversations(final String token, final String updateAt,final String userId,final Back.Result<List<UserConversationEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, updateAt, Urls.User_UserConversations_Get + userId + Urls.User_UserConversations_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh,updateAt,Urls.User_UserConversations_Get+userId+Urls.User_UserConversations_Last, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }
                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            List<UserConversationEntity> userConversations = new ArrayList<UserConversationEntity>();
                            List<Json> jsons = new Json(result).get("entries").toList();
                            for(Json j:jsons){
                                userConversations.add(UserConversationEntity.parse(j));
                            }
                            resultCallback.onSuccess(userConversations);
                        }
                    }catch (Exception e){

                    }
                }
            }
        });

    }

    /**
     * 删除用户会话列表某个会话
     * @param userId 用户唯一标识码
     * @param userConversationId 用户会话唯一标识码
     * @param callback 回调
     */
    public void deleteUserConversation(final String token,final String userId,final String userConversationId,final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(userConversationId)){
                    callback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.delete(token, Urls.User_UserConversations_Delete + userId + Urls.User_UserConversation_Last + userConversationId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.delete(tokenRefresh, Urls.User_UserConversations_Delete + userId + Urls.User_UserConversation_Last + userConversationId, params);
                            }
                        }
                    }catch (Exception e){
                        callback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            callback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            callback.onError(ErrorCode.EC_UNKNOWN,"");
                        }
                    } else {
                        callback.onSuccess();
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /**
     * 更新用户会话
     * @param token
     * @param userId
     * @param userConversationId
     * @param targetId
     * @param name
     * @param top
     * @param type
     * @param avatarUrl
     * @param resultCallback
     */
    public void updateUserConversation(final String token, final String userId, final String userConversationId, final String targetId, final String name, final boolean top, final String type, final String avatarUrl, final Back.Result<UserConversationEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String tokenRefresh;
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(userConversationId) || StringUtil.isEmpty(targetId) || StringUtil.isEmpty(name) || StringUtil.isEmpty(type)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("targetId",targetId);
                        params.put("name",name);
                        params.put("top",top+"");
                        params.put("type",type);
                        params.put("avatarUrl",avatarUrl);
                        result = HttpUtil.put(token, Urls.User_UserConversations_Update + userId + Urls.User_UserConversation_Last + userConversationId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.delete(tokenRefresh, Urls.User_UserConversations_Update + userId + Urls.User_UserConversation_Last + userConversationId, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }
                }
                try {
                    Json json = new Json(result);
                    String status = json.getStr("status");
                    String message = json.getStr("message");
                    if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                        int key = Integer.parseInt(status);
                        if(key > 0){
                            resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                        }else{
                            resultCallback.onError(ErrorCode.EC_UNKNOWN,"");
                        }
                    } else {
                        resultCallback.onSuccess(UserConversationEntity.parse(json));
                    }
                }catch (Exception e){

                }
            }
        });

    }

    /************************************************会话****************************************************/
    /**
     * 获取自己的会话
     * @param token
     * @param userId
     * @param resultCallback
     */
    public void getSessionSingle(final String token,final String userId,final Back.Result<SessionEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, Urls.User_Session_Single_Get + userId , params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, Urls.User_Session_Single_Get + userId , params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }

                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(SessionEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }
            }
        });
    }

    /**
     * 获取一对一会话
     * @param userId 自己的唯一标识码
     * @param otherId 对方的唯一标识码
     * @param resultCallback 回调
     */
    public void getSessionP2p(final String token,final String userId, final String otherId, final Back.Result<SessionEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(otherId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, Urls.User_SessionP2p_Get + userId +"/" + otherId , params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, Urls.User_SessionP2p_Get + userId +"/" + otherId , params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }

                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(SessionEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }
            }
        });
    }


    /**
     * 获取某个会话
     * @param token 访问令牌
     * @param sessionId 会话唯一标识码
     * @param resultCallback 回调
     */
    public void getSession(final String token,final String sessionId, final Back.Result<SessionEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.get(token, Urls.User_Session_Get + sessionId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, Urls.User_Session_Get + sessionId, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }

                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(SessionEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }
            }
        });
    }

    /************************************************消息****************************************************/

    /**
     * 发送消息
     * @param sessionId 会话唯一标识码
     * @param sender 发送者唯一标识码
     * @param content 消息内容
     * @param type 消息类型
     * @param resultCallback 回调
     */
    public void sendMessage(final String token,final String sessionId,final String sender, final String content, final String type, final Back.Result<MessageEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId) || StringUtil.isEmpty(sender) || StringUtil.isEmpty(content) || StringUtil.isEmpty(type)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    String result ="";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("sender", sender);
                        params.put("content", content);
                        params.put("type", type);
                        result = HttpUtil.post(token, Urls.User_Messages_Send + sessionId + Urls.User_Messages_Send_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.post(tokenRefresh, Urls.User_Messages_Send + sessionId + Urls.User_Messages_Send_Last, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return;
                    }
                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        String error = json.getStr("error");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(key, error);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(MessageEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }
            }
        });

    }

    /**
     * 同步会话消息
     * @param sessionId 会话Id
     * @param syncTime 时间戳
     *  @param limit 消息条数极限
     * @param resultCallback 回调
     */
    public void getMessage(final String token,final String sessionId,final String syncTime, final int limit, final Back.Result<SyncMessagesModel> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    String result = "";
                    String tokenRefresh;
                    List<MessageEntity> models = new ArrayList<MessageEntity>();
                    SyncMessagesModel syncMessagesModel = new SyncMessagesModel();
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("limit", ""+limit);
                        result = HttpUtil.get(token, syncTime, Urls.User_Messages_Get + sessionId + Urls.User_Messages_Get_Last, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, syncTime, Urls.User_Messages_Get + sessionId + Urls.User_Messages_Get_Last, params);
                            }
                        }

                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        String error = json.getStr("error");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(key, error);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            List<Json> jSons = new Json(result).get("entries").toList();
                            for(Json j:jSons){
                                models.add(MessageEntity.parse(j));
                            }
                        }
                        String sessionUpdatedAt = json.getStr("sessionUpdatedAt");
                        syncMessagesModel.setSessionUpdatedAt(sessionUpdatedAt);
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return ;
                    }
                    syncMessagesModel.setMessageEntities(models);
                    resultCallback.onSuccess(syncMessagesModel);
                }
            }
        });

    }
    /**
     * 同步历史会话消息
     * @param sessionId 会话Id
     * @param syncTime 时间戳
     *  @param limit 消息条数极限
     * @param resultCallback 回调
     */
    public void getMessageHistory(final String token,final String sessionId,final String syncTime, final int limit, final Back.Result<List<MessageEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    String result = "";
                    String tokenRefresh;
                    List<MessageEntity> models = new ArrayList<MessageEntity>();
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("limit", ""+limit);
                        result = HttpUtil.get(token, syncTime, Urls.User_Messages_Get + sessionId + Urls.User_Messages_Get_Hostory, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, syncTime, Urls.User_Messages_Get + sessionId + Urls.User_Messages_Get_Hostory, params);
                            }
                        }
                        JSONArray jsonArray =  new JSONArray(result);

                        for(int i = 0;i<jsonArray.length();i++){
                            Json json = new Json(jsonArray.get(i));
                            models.add(MessageEntity.parse(json));
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return ;
                    }
                    resultCallback.onSuccess(models);
                }
            }
        });

    }

    public void messageUpdate(final String token,final String sessionId,final String messageId, final String sender, final String content, final String type,final Back.Result<MessageEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId) || StringUtil.isEmpty(messageId) || StringUtil.isEmpty(sender) || StringUtil.isEmpty(content)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                    return;
                }else {
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("sender", sender);
                        params.put("content", content);
                        params.put("type", type);
                        result = HttpUtil.put(token, Urls.User_Message_Update + sessionId + Urls.User_Message_Update_Last + messageId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.put(tokenRefresh, Urls.User_Message_Update + sessionId + Urls.User_Message_Update_Last + messageId, params);
                            }
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return ;
                    }
                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            resultCallback.onSuccess(MessageEntity.parse(json));
                        }
                    }catch (Exception e){

                    }
                }
            }
        });

    }

    public void messageDelete(final String token,final String sessionId,final String messageId,final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId) || StringUtil.isEmpty(messageId) ){
                    callback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                    return;
                }else {
                    String result = "";
                    String tokenRefresh;
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        result = HttpUtil.delete(token, Urls.User_Message_Delete + sessionId + Urls.User_Message_Delete_Last + messageId, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.delete(tokenRefresh, Urls.User_Message_Delete + sessionId + Urls.User_Message_Delete_Last + messageId, params);
                            }
                        }
                    }catch (Exception e){
                        callback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return ;
                    }
                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                callback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                callback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            callback.onSuccess();
                        }
                    }catch (Exception e){

                    }
                }
            }
        });

    }

    /*************************************************emoji************************************************/
    public void getEmojiList(final String token,final String syncTime, final int limit, final Back.Result<List<EmojiEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token)){
                    resultCallback.onError(ErrorCode.EC_PARAMETER_ERROR,ec_para_error);
                }else {
                    String result = "";
                    String tokenRefresh;
                    List<EmojiEntity> entityList = new ArrayList<EmojiEntity>();
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("limit", ""+limit);
                        result = HttpUtil.get(token, syncTime, Urls.User_Messages_Emojis_Get, params);
                        if(isTokenExpired(result)){
                            UserInfo.LoginInfo loginInfo=refreshToken();
                            if(loginInfo!=null){
                                tokenRefresh = loginInfo.getToken();
                                result = HttpUtil.get(tokenRefresh, syncTime, Urls.User_Messages_Emojis_Get, params);
                            }
                        }

                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.EC_UNKNOWN,ec_network_error);
                        return ;
                    }
                    try {
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        String message = json.getStr("message");
                        if (!StringUtil.isEmpty(status) && !StringUtil.isEmpty(message)) {
                            int key = Integer.parseInt(status);
                            if(key > 0){
                                resultCallback.onError(ErrorCode.errorCodeParse(key), message);
                            }else{
                                resultCallback.onError(ErrorCode.EC_UNKNOWN, "");
                            }
                        } else {
                            List<Json> jSons = json.get("entries").toList();
                            for (Json j : jSons) {
                                entityList.add(EmojiEntity.parse(j));
                            }
                            resultCallback.onSuccess(entityList);
                        }
                    }catch (Exception e){
                    }
                }
            }
        });

    }
}
