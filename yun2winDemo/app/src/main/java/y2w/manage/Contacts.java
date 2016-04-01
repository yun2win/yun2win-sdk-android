package y2w.manage;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import y2w.common.Constants;
import y2w.db.ContactDb;
import y2w.db.TimeStampDb;
import y2w.db.UserDb;
import y2w.entities.ContactEntity;
import y2w.entities.TimeStampEntity;
import y2w.entities.UserEntity;
import y2w.model.Contact;
import y2w.model.User;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.service.ContactSrv;
import y2w.service.ErrorCode;

/**
 * 通讯录管理
 *
 * Created by yangrongfang on 2016/1/16.
 */
public class Contacts implements Serializable {

    private CurrentUser user;
    private String updateAt;
    private String TAG = Contacts.class.getSimpleName();
    private Remote remote;

    /**
     * 获取某个联系人
     * @param userId 用户唯一标识码
     * @return
     */
    public Contact getContact(String userId){
        ContactEntity entity = ContactDb.queryByUserId(user.getEntity().getId(),userId);
        return new Contact(user,this,entity);
    }

    /**
     * 有参构造
     * @param user 当前登录用户
     */
    public Contacts(CurrentUser user){
        this.user = user;
    }

    /**
     * 获取当前登录用户实例
     * @return 返回结果
     */
    public CurrentUser getUser(){
        return user;
    }

    /**
     * 获取通讯录更新时间戳
     * @return 返回结果
     */
    public String getUpdateAt() {
        TimeStampEntity entity= TimeStampDb.queryByType(user.getEntity().getId(),TimeStampEntity.TimeStampType.contact.toString());
        if(entity != null){
            updateAt = entity.getTime();
        }else{
            updateAt = Constants.TIME_ORIGIN;
        }
        LogUtil.getInstance().log(TAG, "TimeStamp :"+updateAt, null);
        return updateAt;
    }


    /**
     * 获取远程访问实例
     * @return 返回结果
     */
    public Remote getRemote(){
        if(remote == null){
            remote=new Remote(this);
        }
        return remote;
    }

    /**
     * 获取通讯录列表本地数据
     * @return 返回结果
     */
    public List<Contact> getContacts(){
        List<Contact> contacts = new ArrayList<Contact>();
        List<ContactEntity> entities = ContactDb.getAll(user.getEntity().getId());
        for(ContactEntity entity:entities){
            contacts.add(new Contact(user,this,entity));
        }
        return  contacts;
    }

    /**
     * 将某个联系人信息保存到数据库
     * @param contact 联系人
     */
    public void addContact(Contact contact){
        contact.getEntity().setMyId(user.getEntity().getId());
        refreshTimeStamp(contact);
        ContactDb.addContactEntity(contact.getEntity());
    }

    /**
     * 将多个联系人信息保存到数据库
     * @param contacts 联系人列表
     */
    public void add(List<Contact> contacts){
        for(Contact contact:contacts){
            addContact(contact);
        }
    }

    /**
     * 更新通讯录同步时间戳
     * 所有联系人的最大值，作为时间戳
     * @param contact 联系人
     */
    private void refreshTimeStamp(Contact contact){
        TimeStampEntity entity= TimeStampDb.queryByType(user.getEntity().getId(), TimeStampEntity.TimeStampType.contact.toString());
        if(entity != null){
            if(StringUtil.timeCompare(entity.getTime(), contact.getEntity().getUpdatedAt()) > 0){
                entity.setTime(contact.getEntity().getUpdatedAt());
                TimeStampDb.addTimeStampEntity(entity);
                LogUtil.getInstance().log(TAG, "bigger :" + contact.getEntity().getUpdatedAt(), null);
            }
        }else{
            entity = TimeStampEntity.parse(contact.getEntity().getUpdatedAt(),TimeStampEntity.TimeStampType.contact.toString(),"");
            entity.setMyId(user.getEntity().getId());
            TimeStampDb.addTimeStampEntity(entity);
        }

    }

    /**
     * user表数据初始化
     * @param contact 联系人
     */
    private void userInit(Contact contact){
        UserEntity entity = UserDb.queryById(user.getEntity().getId(), contact.getEntity().getUserId());
        if(entity == null){
            User user = Users.getInstance().createUser(contact.getEntity().getUserId(),contact.getEntity().getName(),contact.getEntity().getAvatarUrl());
            Users.getInstance().addUser(user);
        }

    }

    /*****************************remote*****************************/

    /**
     * 远程访问类
     */
    public class Remote implements Serializable{
        private Contacts _contacts;
        public Remote(Contacts contacts){
            _contacts=contacts;
        }

        /**
         * 添加好友
         * @param otherId 对方唯一标识码
         * @param email 对方帐号
         * @param name 对方用户名
         * @param avatarUrl 对方头像
         * @param result 回调
         */
        public void contactAdd(String otherId,final String email,final String name,final String avatarUrl, final Back.Result<Contact> result){

            ContactSrv.getInstance().contactAdd(user.getToken(),user.getEntity().getId(),otherId,email,name,avatarUrl,new Back.Result<ContactEntity>() {
                @Override
                public void onSuccess(ContactEntity entity) {
                    Contact contact = new Contact(user,_contacts,entity);
                    result.onSuccess(contact);
                }

                @Override
                public void onError(int errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 同步联系人
         * @param result 回调
         */
        public void sync(final Back.Result<List<Contact>> result){
            ContactSrv.getInstance().getContacts(user.getToken(),getUpdateAt(), Constants.CONTACTS_SYNC_LIMIT, user.getEntity().getId(), new Back.Result<List<ContactEntity>>() {
                @Override
                public void onSuccess(List<ContactEntity> entities) {
                    List<Contact> contacts = new ArrayList<Contact>();
                    for (ContactEntity entity : entities) {
                        contacts.add(new Contact(user, _contacts, entity));
                    }
                    add(contacts);
                    result.onSuccess(contacts);
                }

                @Override
                public void onError(int errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 删除某个联系人
         * @param id 联系人唯一标识码
         * @param callback 回调
         */
        public void contactDelete(final String id, final Back.Callback callback){
            ContactSrv.getInstance().contactDelete(user.getToken(),user.getEntity().getId(), id, new Back.Callback() {
                @Override
                public void onSuccess() {
                    callback.onSuccess();
                }

                @Override
                public void onError(int errorCode,String error) {
                    callback.onError(errorCode,error);
                }
            });
        }

        /**
         * 更新保存某个联系人信息
         * @param contact 联系人
         * @param callback 回调
         */
        public void contactUpdate(final Contact contact, final Back.Callback callback){
            ContactSrv.getInstance().contactUpdate(user.getToken(),contact, new Back.Callback() {
                @Override
                public void onSuccess() {
                    addContact(contact);
                    callback.onSuccess();
                }

                @Override
                public void onError(int errorCode,String error) {
                    callback.onError(errorCode,error);
                }
            });
        }
    }
}
