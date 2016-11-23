package y2w.manage;

import y2w.db.WebValueDb;
import y2w.entities.WebValueEntity;
import y2w.model.WebValue;

/**
 * Created by maa2 on 2016/4/8.
 */
public class WebValues {

    private String TAG = WebValues.class.getSimpleName();
    private CurrentUser user;

    public WebValues(CurrentUser user){
        this.user = user;
    }

    /**
     * 获取当前登录用户
     * @return 返回结果
     */
    public CurrentUser getUser(){
        return user;
    }

     public void updateWebValues(WebValueEntity entity){
         WebValueDb.addWebValueEntity(entity);
     }
     public WebValueEntity getWebValues(String key,String myId){
         return WebValueDb.queryBykey(key,myId);
     }
}
