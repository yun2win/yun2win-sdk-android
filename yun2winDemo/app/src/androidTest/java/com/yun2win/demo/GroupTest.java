package com.yun2win.demo;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.y2w.uikit.utils.StringUtil;


import y2w.base.Urls;
import y2w.entities.SessionEntity;
import y2w.manage.CurrentUser;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.User;
import y2w.model.UserConversation;
import y2w.model.UserSession;
import y2w.service.Back;
import y2w.service.ErrorCode;

import java.util.List;
import java.util.concurrent.CountDownLatch;
/**
 * Created by maa2 on 2016/3/4.
 */
public class GroupTest extends InstrumentationTestCase {

    Context context;
    private String TAG = P2pChatTest.class.getSimpleName();
    private User userA;
    private User userB;
    private CurrentUser curA;
    private CurrentUser curB;
    private Contact contactB;
    private Session sessionA;
    private Session sessionB;

    private UserSession userSessionA;

    private MessageModel msgA;
    private MessageModel msgB;

    private SessionMember sessionMemberB;


    private CountDownLatch signal;
    private String passWord = "1234567890";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGroup() throws Exception {


        //注册 A
        String emailA = StringUtil.getRandomString(6);
        signal = new CountDownLatch(1);
        Users.getInstance().getRemote().register(emailA, passWord, emailA + "杨", new Back.Result<User>() {
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
        //B 注册
        String emailB = StringUtil.getRandomString(6);
        signal = new CountDownLatch(1);
        Users.getInstance().getRemote().register(emailB, passWord, emailB + "杨", new Back.Result<User>() {
            @Override
            public void onSuccess(User user) {
                userB = user;
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

        //A 创建session，将B加入session
        signal = new CountDownLatch(1);
        curA.getSessions().getRemote().sessionCreate("测试群", "public", "group", Urls.User_Avatar_Def, new Back.Result<Session>() {
            @Override
            public void onSuccess(Session s) {
                sessionA = s;
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

        signal = new CountDownLatch(1);
        sessionA.getMembers().getRemote().sessionMemberAdd(contactB.getEntity().getUserId(), contactB.getEntity().getName(), "user", contactB.getEntity().getAvatarUrl(), "active", new Back.Result<SessionMember>() {
            @Override
            public void onSuccess(SessionMember sessionMember) {
                sessionMemberB = sessionMember;
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

        //同步群聊成员
        signal = new CountDownLatch(1);
        sessionA.getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
            @Override
            public void onSuccess(List<SessionMember> sessionMembers) {
                SessionMember temp = sessionA.getMembers().getLocalMember(contactB.getEntity().getUserId());
                assertNotNull(temp.getEntity());
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

        //删除成员B
        signal = new CountDownLatch(1);
        sessionA.getMembers().getRemote().sessionMemberDelete(sessionMemberB, new Back.Callback() {
            @Override
            public void onSuccess() {
                SessionMember temp = sessionA.getMembers().getLocalMember(contactB.getEntity().getUserId());
                assertNull(temp.getEntity());
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
        //重新添加B
        signal = new CountDownLatch(1);
        sessionA.getMembers().getRemote().sessionMemberAdd(contactB.getEntity().getUserId(), contactB.getEntity().getName(), "user", contactB.getEntity().getAvatarUrl(), "active", new Back.Result<SessionMember>() {
            @Override
            public void onSuccess(SessionMember sessionMember) {
                sessionMemberB = sessionMember;
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

        //保存该群聊  到我的  群聊列表
        signal = new CountDownLatch(1);
        curA.getUserSessions().getRemote().sessionStore(sessionA.getEntity().getId(), sessionA.getEntity().getName(), sessionA.getEntity().getAvatarUrl(), new Back.Result<UserSession>() {
            @Override
            public void onSuccess(UserSession userSession) {
                userSessionA = userSession;
                assertNotNull(userSessionA.getEntity());
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
        //同步我的群聊列表
        signal = new CountDownLatch(1);
        curA.getUserSessions().getRemote().sync(new Back.Result<List<UserSession>>() {
            @Override
            public void onSuccess(List<UserSession> userSessions) {
                if (userSessions.size() == 0) {
                    assertEquals(1, 2);
                }
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
        //将该群聊从我的   群聊列表中删除
        signal = new CountDownLatch(1);
        curA.getUserSessions().getRemote().userSessionDelete(userSessionA.getEntity().getId(), new Back.Callback() {
            @Override
            public void onSuccess() {
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
        //同步我的群聊列表
        signal = new CountDownLatch(1);
        curA.getUserSessions().getRemote().sync(new Back.Result<List<UserSession>>() {
            @Override
            public void onSuccess(List<UserSession> userSessions) {
                if (userSessions.size() == 0) {
                    assertEquals(1, 2);
                }
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
