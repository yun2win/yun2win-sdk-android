package y2w.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.yun2win.utils.Json;
import com.yun2win.utils.LogUtil;

import y2w.manage.CurrentUser;
import y2w.entities.ContactEntity;

import static y2w.base.AppContext.*;

/**
 * 用户信息
 * Created by maa2 on 2016/1/23.
 */
public class UserInfo {

    /**
     * 保存当前登录用户基本信息
     * @param account
     * @param password
     */
    public static void setUserInfo(String account,String password,String id,String username,String avatarUrl,String token, String appKey, String secret,String role){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1=preferences.edit();
        editor1.putString("account", account);
        editor1.putString("password", password);
        editor1.putString("id", id);
        editor1.putString("username", username);
        editor1.putString("token", token);
        editor1.putString("appKey", appKey);
        editor1.putString("secret", secret);
        editor1.putString("avatarUrl", avatarUrl);
        editor1.putString("role",role);
        editor1.commit();
    }

    public static String getUserId(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo,Context.MODE_PRIVATE);
        return preferences.getString("id", "");
    }
    public static String getUserName(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo, Context.MODE_PRIVATE);
        return preferences.getString("username", "");
    }


    public static String getAccount(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo, Context.MODE_PRIVATE);
        return preferences.getString("account", "");
    }

    public static String getPassWord(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo, Context.MODE_PRIVATE);
        return preferences.getString("password", "");
    }
    public static String getToken(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo, Context.MODE_PRIVATE);
        return preferences.getString("token", "");
    }

    public static String getAppKey(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo, Context.MODE_PRIVATE);
        return preferences.getString("appKey", "");
    }
    public static String getSecret(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo, Context.MODE_PRIVATE);
        return preferences.getString("secret", "");
    }
    public static String getRole(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo, Context.MODE_PRIVATE);
        return preferences.getString("role", "");
    }
    public static String getAvatarUrl(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserInfo, Context.MODE_PRIVATE);
        return preferences.getString("avatarUrl", "");
    }
    public static void setCurrentInfo(CurrentUser user,String passWord){
        setUserInfo(user.getEntity().getAccount(),passWord,user.getEntity().getId(),user.getEntity().getName(),user.getEntity().getAvatarUrl(),user.getToken(),user.getAppKey(),user.getSecret(),user.getRole());
    }
    public static LoginInfo getCurrentInfo(){
        LoginInfo info = new LoginInfo();
        info.setToken(getToken());
        info.setKey(getAppKey());
        info.setSecret(getSecret());
        info.setRole(getRole());
        ContactEntity entity = new ContactEntity();
        entity.setId(getUserId());
        entity.setName(getUserName());
        entity.setEmail(getAccount());
        entity.setAvatarUrl(getAvatarUrl());
        info.setEntity(entity);
        return  info;
    }

    public static class LoginInfo{
        String token;
        String key;
        String secret;
        String role = "";
        ContactEntity entity;

        public static LoginInfo parseJson(Json json){
            LoginInfo info = new LoginInfo();
            info.setToken(json.getStr("token"));
            info.setKey(json.getStr("key"));
            info.setSecret(json.getStr("secret"));
            info.setEntity(ContactEntity.parse(json));
            return info;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public ContactEntity getEntity() {
            return entity;
        }

        public void setEntity(ContactEntity entity) {
            this.entity = entity;
        }
    }


    /**
     * 退出登录时，清空密码等相关信息
     */
    public static void clearPassWord(){
        String account = getAccount();
        LogUtil.getInstance().log("userInfo",account,null);
        setUserInfo(account, "", "", "", "","","","","");
    }


}
