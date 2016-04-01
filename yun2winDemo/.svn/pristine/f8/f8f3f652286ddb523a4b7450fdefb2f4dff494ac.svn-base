package com.y2w.uikit.common;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by maa2 on 2016/1/21.
 */
public interface UserInfoProvider {
    UserInfoProvider.UserInfo getUserInfo(String var1);

    int getDefaultIconResId();

    Bitmap getTeamIcon(String var1);

    Bitmap getAvatarForMessageNotifier(String var1);

    String getDisplayNameForMessageNotifier(String var1, String var2, SessionTypeEnum var3);

    public interface UserInfo extends Serializable {
        String getAccount();

        String getName();

        String getAvatar();
    }
}
