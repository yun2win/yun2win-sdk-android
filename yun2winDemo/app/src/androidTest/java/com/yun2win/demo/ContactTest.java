package com.yun2win.demo;

import android.content.Context;
import android.test.InstrumentationTestCase;


import com.y2w.uikit.utils.StringUtil;

import y2w.manage.CurrentUser;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.model.User;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.service.ErrorCode;

import java.util.List;
import java.util.concurrent.CountDownLatch;
/**
 * Created by maa2 on 2016/3/4.
 */
public class ContactTest extends InstrumentationTestCase {

    Context context;
    private String TAG = P2pChatTest.class.getSimpleName();
    private User userA;
    private User userB;
    private CurrentUser curA;
    private CurrentUser curB;
    private Contact contactB;
    private Session sessionA;
    private Session sessionB;

    private MessageModel msgA;
    private MessageModel msgB;

    private UserConversation userConA;
    private UserConversation userConB;
    private CountDownLatch signal;
    private String passWord = "123456";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testContact() throws Exception {


        //注册 A
        //String emailA = StringUtil.getRandomString(6);
        String emailA = "hejie";
        String name = emailA + "乖";
        signal = new CountDownLatch(1);
        Users.getInstance().getRemote().register(emailA, passWord, name, new Back.Result<User>() {
            @Override
            public void onSuccess(User user) {
                userA = user;
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

        //A 登录
        signal = new CountDownLatch(1);
        Users.getInstance().getRemote().login(userA.getEntity().getAccount(), passWord, new Back.Result<CurrentUser>() {
            @Override
            public void onSuccess(CurrentUser currentUser) {
                curA = currentUser;
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
       /* //B 注册
        String emailB = StringUtil.getRandomString(6);
        signal = new CountDownLatch(1);
        Users.getInstance().getRemote().register(emailB, passWord, emailB + "杨", new Back.Result<User>() {
            @Override
            public void onSuccess(User user) {
                userB = user;
                signal.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();*/

        //B 登录
        signal = new CountDownLatch(1);
        Users.getInstance().getRemote().login(userB.getEntity().getAccount(), passWord, new Back.Result<CurrentUser>() {
            @Override
            public void onSuccess(CurrentUser currentUser) {
                curB = currentUser;
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

        //A + B 成功后将B写入数据库，根据id,再从数据库获取B
        signal = new CountDownLatch(1);
        curA.getContacts().getRemote().contactAdd(userB.getEntity().getId(), userB.getEntity().getAccount(), userB.getEntity().getName(), userB.getEntity().getAvatarUrl(), new Back.Result<Contact>() {
            @Override
            public void onSuccess(Contact contact) {
                contactB = contact;
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

       /* //A 对 B修改备注
       contactB.getEntity().setName("土豆");
        signal = new CountDownLatch(1);
        curA.getContacts().getRemote().contactUpdate(contactB, new Back.Callback() {
            @Override
            public void onSuccess() {
                signal.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
        //A 同步联系人
        signal = new CountDownLatch(1);
        curA.getContacts().getRemote().sync(new Back.Result<List<Contact>>() {
            @Override
            public void onSuccess(List<Contact> contacts) {
                contactB = curA.getContacts().getContact(contactB.getEntity().getUserId());
                assertNotNull(contactB.getEntity());
                signal.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

        //A 从自己通讯录中删除 B用户
        signal = new CountDownLatch(1);
        curA.getContacts().getRemote().contactDelete(contactB.getEntity().getId(), new Back.Callback() {
            @Override
            public void onSuccess() {
                contactB = curA.getContacts().getContact(userB.getEntity().getId());
                assertNull(contactB.getEntity());
                signal.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
        //A 同步联系人
        signal = new CountDownLatch(1);
        curA.getContacts().getRemote().sync(new Back.Result<List<Contact>>() {
            @Override
            public void onSuccess(List<Contact> contacts) {
                contactB = curA.getContacts().getContact(userB.getEntity().getId());
                assertNotNull(contactB.getEntity());
                signal.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

        //A 再次添加 B

        signal = new CountDownLatch(1);
        curA.getContacts().getRemote().contactAdd(userB.getEntity().getId(), userB.getEntity().getAccount(), userB.getEntity().getName(), userB.getEntity().getAvatarUrl(), new Back.Result<Contact>() {
            @Override
            public void onSuccess(Contact contact) {
                contactB = contact;
                signal.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();*/
    }

}
