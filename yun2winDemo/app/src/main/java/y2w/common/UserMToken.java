package y2w.common;

import android.content.Context;
import android.content.SharedPreferences;

import y2w.manage.CurrentUser;
import y2w.model.MToken;
import y2w.service.Back;

import static y2w.base.AppContext.getAppContext;

/**
 * Created by maa46 on 2016/10/7.
 */
public class UserMToken {
    /**
     * 保存当前ImToken
     */
    public static void setImToken(String userid,String tokenType,String accessToken,String expiresIn,String refreshToken){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserMToken, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1=preferences.edit();
        editor1.putString("userid", userid);
        editor1.putString("tokentype", tokenType);
        editor1.putString("accesstoken", accessToken);
        editor1.putString("expiresin", expiresIn);
        editor1.putString("refreshtoken", refreshToken);
        editor1.commit();
    }
    public static String getUserId(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserMToken, Context.MODE_PRIVATE);
        return preferences.getString("userid", "");
    }

    public static String getTokenType(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserMToken, Context.MODE_PRIVATE);
        return preferences.getString("tokentype", "");
    }

    public static String getAccessToken(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserMToken, Context.MODE_PRIVATE);
        return preferences.getString("accesstoken", "");
    }

    public static String getExpiresin(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserMToken, Context.MODE_PRIVATE);
        return preferences.getString("expiresin", "");
    }

    public static String getRefreshToken(){
        SharedPreferences preferences = getAppContext().getSharedPreferences(Config.UserMToken, Context.MODE_PRIVATE);
        return preferences.getString("refreshtoken", "");
    }
    //重新刷新Token
    public static void reFreshToken(final CurrentUser user, final Back.Result<MToken> result){
        user.getImToken(new Back.Result<MToken>() {
            @Override
            public void onSuccess(MToken mToken) {
                UserMToken.setImToken(user.getEntity().getId(),mToken.getTokenType(),mToken.getAccessToken(),mToken.getExpiresIn(),mToken.getRefreshToken());
                user.setImToken(mToken);
                result.onSuccess(mToken);
            }
            @Override
            public void onError(int errorCode, String error) {
                result.onError(errorCode,error);
            }
        });
    }
}
