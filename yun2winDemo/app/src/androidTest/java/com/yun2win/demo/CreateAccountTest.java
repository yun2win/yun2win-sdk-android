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
public class CreateAccountTest extends InstrumentationTestCase {

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

    public void testCreateAccount() throws Exception {


        //注册 A
        String emailA = "hj02";
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

        //B 登录
        signal = new CountDownLatch(1);
        Users.getInstance().getRemote().login("yrf", passWord, new Back.Result<CurrentUser>() {
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
        curA.getContacts().getRemote().contactAdd(curB.getEntity().getId(), curB.getEntity().getAccount(), curB.getEntity().getName(), curB.getEntity().getAvatarUrl(), new Back.Result<Contact>() {
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


    }

}
