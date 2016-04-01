package y2w.model;

import com.yun2win.utils.Json;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;

import y2w.entities.ContactEntity;
import y2w.entities.UserEntity;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.UserSrv;

/**
 * 用户类
 * Created by yangrongfang on 2016/1/16.
 */
public class User implements Serializable{

    private UserEntity entity;
    public User(){
    }
    public User(UserEntity entity){
        this.entity = entity;
    }

    public UserEntity getEntity() {
        if(entity == null){
            entity = new UserEntity();
        }
        return entity;
    }

}
