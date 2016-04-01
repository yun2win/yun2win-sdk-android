package y2w.service;

import java.util.List;

import y2w.base.ClientFactory;
import y2w.entities.ContactEntity;
import y2w.model.Contact;

/**
 * 通讯录远程访问类
 * Created by yangrongfang on 2016/1/19.
 */
public class ContactSrv {

    private static ContactSrv contactSrv = null;
    public static ContactSrv getInstance(){
        if(contactSrv == null){
            contactSrv = new ContactSrv();
        }
        return contactSrv;
    }

    /**
     * 获取通讯录联系人
     * @param token 访问令牌
     * @param updateAt 某时间之后的数据
     * @param limit 最大人数
     * @param userId 用户唯一标识码
     * @param result 回调
     */
    public void getContacts(String token, String updateAt,int limit,String userId,Back.Result<List<ContactEntity>> result){
        ClientFactory.getInstance().getContacts(token, updateAt,limit, userId, result);
    }

    /**
     * 添加联系人
     * @param token
     * @param userId
     * @param otherId
     * @param email
     * @param name
     * @param avatarUrl
     * @param result
     */
    public void contactAdd(String token, String userId ,String otherId, String email, String name, String avatarUrl, Back.Result<ContactEntity> result){
        ClientFactory.getInstance().contactAdd(token, userId, otherId, email, name, avatarUrl, result);
    }

    /**
     * 删除联系人
     * @param token
     * @param userId
     * @param id
     * @param callback
     */
    public void contactDelete(String token, String userId,String id, Back.Callback callback){
        ClientFactory.getInstance().contactDelete(token, userId, id, callback);
    }

    /**
     * 更新联系人
     * @param token
     * @param contact
     * @param callback
     */
    public void contactUpdate(String token, Contact contact, Back.Callback callback){
        ClientFactory.getInstance().contactUpdate(token, contact.getEntity().getMyId(),contact.getEntity().getUserId(),
                contact.getEntity().getId(), contact.getEntity().getName(),
                 contact.getEntity().getName(), contact.getEntity().getAvatarUrl(),
                contact.getEntity().getAvatarUrl(), callback);

    }

    /**
     * 查找用户
     * @param token
     * @param keyword
     * @param result
     */
    public void contactSearch(String token, String keyword,Back.Result<List<ContactEntity>> result){
        ClientFactory.getInstance().contactSearch(token, keyword,result);
    }

}
