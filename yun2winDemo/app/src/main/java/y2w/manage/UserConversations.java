package y2w.manage;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import y2w.common.Constants;
import y2w.db.TimeStampDb;
import y2w.db.UserConversationDb;
import y2w.entities.TimeStampEntity;
import y2w.entities.UserConversationEntity;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.UserConversationSrv;

/**
 * 用户会话管理类
 * 用户会话即主界面会话列表显示的子项
 * Created by yangrongfang on 2016/1/16.
 */
public class UserConversations implements Serializable {
    private String TAG = UserConversations.class.getSimpleName();
    private CurrentUser user;
    private String updatedAt;
    private Remote remote;

    /**
     * 获取某个用户会话
     * @param targetId 用户会话目标id
     * @return 回调
     */
    public UserConversation get(String targetId){
        UserConversationEntity entity = UserConversationDb.queryByTargetId(user.getEntity().getId(),targetId);
        return new UserConversation(this,entity);
    }
    /**
     * 删除某个用户会话
     * @param targetId 用户会话目标id
     * @return
     */
    public void delete(String targetId){
        UserConversationEntity entity = UserConversationDb.queryByTargetId(user.getEntity().getId(), targetId);
        if(entity!=null)
         UserConversationDb.delete(entity);
    }

    /**
     * 获取本地用户会话列表
     * @return 返回结果
     */
    public List<UserConversationEntity> getUserConversations(){
        return UserConversationDb.query(user.getEntity().getId());
    }

    /**
     * 将用户会话列表保存到本地数据库
     * @param userConversationList 会话列表
     */
    public void add(List<UserConversation> userConversationList){
        for(UserConversation userConversation:userConversationList){
            addUserConversation(userConversation);
        }
    }

    /**
     * 将单个用户会话信息保存到数据库
     * @param userConversation 用户会话实体
     */
    public void addUserConversation(UserConversation userConversation){
        userConversation.getEntity().setMyId(user.getEntity().getId());
        refreshTimeStamp(userConversation);
        UserConversationDb.addUserConversation(userConversation.getEntity());
    }

    /**
     * 保存同步更新会话时间戳，用户会话列表的所有会话的updateAt最大值，
     * 作为用户会话的时间戳保存到本地数据库时间戳表
     * @param userConversation 用户会话
     */
    private void refreshTimeStamp(UserConversation userConversation){
        TimeStampEntity entity= TimeStampDb.queryByType(user.getEntity().getId(), TimeStampEntity.TimeStampType.userConversation.toString());
        if(entity != null){
            if(StringUtil.timeCompare(entity.getTime(),userConversation.getEntity().getUpdatedAt()) > 0){
                entity.setTime(userConversation.getEntity().getUpdatedAt());
                TimeStampDb.addTimeStampEntity(entity);
            }
        }else{
            entity = TimeStampEntity.parse(userConversation.getEntity().getUpdatedAt(),TimeStampEntity.TimeStampType.userConversation.toString(),"");
            entity.setMyId(user.getEntity().getId());
            TimeStampDb.addTimeStampEntity(entity);
        }
    }
    /**
     * 有参构造
     * @param user 当前用户
     */
    public UserConversations(CurrentUser user){
        this.user=user;
    }

    /**
     * 获取当前登录用户
     * @return 返回结果
     */
    public CurrentUser getUser() {
        return user;
    }

    /**
     * 获取同步更新时间戳
     * @return 返回结果
     */
    public String getUpdatedAt() {
        TimeStampEntity entity= TimeStampDb.queryByType(user.getEntity().getId(), TimeStampEntity.TimeStampType.userConversation.toString());
        if(entity != null){
            updatedAt = entity.getTime();
        }else{
            updatedAt = Constants.TIME_ORIGIN;
        }
        LogUtil.getInstance().log(TAG, "TimeStamp :"+updatedAt, null);
        return updatedAt;
    }

    /**
     * 获取远程访问实例
     * @return 返回结果
     */
    public Remote getRemote(){
        if(remote == null){
            remote = new Remote(this);
        }
        return remote;
    }

    /*****************************remote*****************************/
    /**
     * 远程访问类
     */
    public class Remote implements Serializable{

        private UserConversations userConversations;
        public Remote(UserConversations userConversations){
            this.userConversations = userConversations;
        }

        /**
         * 同步更新用户会话列表
         * @param result 回调
         */
        public void sync(final Back.Result<List<UserConversation>> result){
            UserConversationSrv.getInstance().getUserConversations(user.getToken(), getUpdatedAt(), user.getEntity().getId(), new Back.Result<List<UserConversationEntity>>() {
                @Override
                public void onSuccess(List<UserConversationEntity> entities) {
                    List<UserConversation> userConversationList = new ArrayList<UserConversation>();
                    for(UserConversationEntity entity:entities){
                        userConversationList.add(new UserConversation(userConversations,entity));
                    }
                    add(userConversationList);
                    result.onSuccess(userConversationList);
                }

                @Override
                public void onError(int errorCode,String error) {

                }
            });
        }

        /**
         * 获取某个用户会话信息
         * @param userId 用户唯一标识码
         * @param userConversationId 用户会话唯一标识码
         * @param result 回调
         */
        public void getUserConversation(String userId,String userConversationId,Back.Result<UserConversation> result){
            UserConversationSrv.getInstance().getUserConversation(user.getToken(), userId,userConversationId, new Back.Result<UserConversationEntity>() {
                @Override
                public void onSuccess(UserConversationEntity entity) {

                }

                @Override
                public void onError(int errorCode,String error) {

                }
            });
        }
        /**
         * 删除某个用户会话
         * @param userConversationId 用户会话唯一标识码
         * @param result 回调
         */
        public void delete(String userConversationId,Back.Callback result){
            UserConversationSrv.getInstance().deleteUserConversation(user.getToken(), user.getEntity().getId(), userConversationId, result);
        }
    }





}
