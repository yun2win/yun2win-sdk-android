package y2w.model;

import com.yun2win.utils.Json;

import java.io.Serializable;

/**
 * 连接消息通道服务器的token,连接时，用accessToken
 * Created by yangrongfang on 2016/3/4.
 */
public class MToken implements Serializable{

    String tokenType;
    String accessToken;
    String expiresIn;
    String refreshToken;

    public static MToken parse(Json json){
        MToken token = new MToken();
        token.setTokenType(json.getStr("token_type"));
        token.setAccessToken(json.getStr("access_token"));
        token.setExpiresIn(json.getStr("expires_in"));
        token.setRefreshToken(json.getStr("refresh_token"));
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
