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
    public static void setUserInfo(String account,String password,String id,String username,String token, String appKey, String secret){

        SharedPreferences preferences = getAppContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1=preferences.edit();
        editor1.putString("account", account);
        editor1.putString("password", password);
        editor1.putString("id", id);
        editor1.putString("username", username);
        editor1.putString("token", token);
        editor1.putString("appKey", appKey);
        editor1.putString("secret", secret);
        editor1.commit();
    }



    public static String getUserId(){
        SharedPreferences preferences = getAppContext().getSharedPreferences("user_info",Context.MODE_PRIVATE);
        return preferences.getString("id", "");
    }
    public static String getUserName(){
        SharedPreferences preferences = getAppContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("username", "");
    }


    public static String getAccount(){
        SharedPreferences preferences = getAppContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("account", "");
    }

    public static String getPassWord(){
        SharedPreferences preferences = getAppContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("password", "");
    }
    public static String getToken(){
        SharedPreferences preferences = getAppContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("token", "");
    }

    public static String getAppKey(){
        SharedPreferences preferences = getAppContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("appKey", "");
    }
    public static String getSecret(){
        SharedPreferences preferences = getAppContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("secret", "");
    }

    public static void setCurrentInfo(CurrentUser user,String passWord){
        setUserInfo(user.getEntity().getAccount(),passWord,user.getEntity().getId(),user.getEntity().getName(),user.getToken(),user.getAppKey(),user.getSecret());
    }

    public static class LoginInfo{
        String token;
        String key;
        String secret;
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
        setUserInfo(account, "", "", "", "","","");
    }


}
