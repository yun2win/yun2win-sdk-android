package com.yun2win.demo;

import android.content.Context;
import android.test.InstrumentationTestCase;
import y2w.entities.SessionEntity;
import y2w.manage.CurrentUser;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends InstrumentationTestCase{

    Context context;
    private String TAG = "";
    private CurrentUser userA;
    private CurrentUser userB;
    private SessionEntity sessionEntity;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*public void test() throws Exception {
        String emailA = StringUtil.getRandomString(5);
        userA = new CurrentUser();
        //注册 A
        final CountDownLatch signal1 = new CountDownLatch(1);
        ClientFactory.getInstance().register(AppContext.getAppContext().getAppKey(), emailA, emailA + "杨", StringUtil.get32MD5("123456"), new Back.Result<User>() {
            @Override
            public void onSuccess(User user) {
                userA.setId(entity.getId());
                userA.setAccount(entity.getEmail());
                userA.setName(entity.getName());
                userA.setAvatarUrl(entity.getAvatarUrl());
                signal1.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal1.countDown();
            }
        });
        signal1.await();

        //登录 A
        final CountDownLatch signal2 = new CountDownLatch(1);
        ClientFactory.getInstance().login(AppContext.getAppContext().getAppKey(), userA.getAccount(), StringUtil.get32MD5("123456"), new Back.Result<UserInfo.LoginInfo>() {
            @Override
            public void onSuccess(UserInfo.LoginInfo loginInfo) {
                userA.setAppKey(loginInfo.getKey());
                userA.setSecret(loginInfo.getSecret());
                userA.setToken(loginInfo.getToken());
                ClientFactory.getInstance().setToken(loginInfo.getToken());//初始化SDK
                signal2.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal2.countDown();
            }
        });
        signal2.await();

        String emailB = StringUtil.getRandomString(5);
        userB = new CurrentUser();
        //注册 B
        final CountDownLatch signal3 = new CountDownLatch(1);
        ClientFactory.getInstance().register(AppContext.getAppContext().getAppKey(), emailB, emailB + "杨", StringUtil.get32MD5("123456"), new Back.Result<ContactEntity>() {
            @Override
            public void onSuccess(ContactEntity entity) {
                userB.setId(entity.getId());
                userB.setAccount(entity.getEmail());
                userB.setName(entity.getName());
                userB.setAvatarUrl(entity.getAvatarUrl());
                signal3.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal3.countDown();
            }
        });
        signal3.await();

        //A 加 B
        final CountDownLatch signal4 = new CountDownLatch(1);
        ClientFactory.getInstance().contactAdd(userA.getId(), userB.getAccount(), userB.getName(), userB.getAvatarUrl(), new Back.Result<ContactEntity>() {
            @Override
            public void onSuccess(ContactEntity entity) {
                assertNotNull(entity.getId());
                signal4.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal4.countDown();
            }
        });
        signal4.await();

        //A 给 B 发消息
        final CountDownLatch signal5 = new CountDownLatch(1);
        ClientFactory.getInstance().getP2PSession(userA.getId(), userB.getId(), new Back.Result<SessionEntity>() {
            @Override
            public void onSuccess(SessionEntity entity) {//得到session
                assertNotNull(entity.getId());
                sessionEntity = entity;
                signal5.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal5.countDown();
            }
        });
        signal5.await();

        final CountDownLatch signal6 = new CountDownLatch(1);
        ClientFactory.getInstance().sendMessage(sessionEntity.getId(), userA.getId(), "你好吗", SessionEntity.SessionType.p2p.toString(), new Back.Result<MessageEntity>() {
            @Override
            public void onSuccess(MessageEntity entity) {//发送文本消息
                assertNotNull(entity.getId());
                signal6.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal6.countDown();
            }
        });
        signal6.await();


        //登录 B
        final CountDownLatch signal7 = new CountDownLatch(1);
        ClientFactory.getInstance().login(AppContext.getAppContext().getAppKey(), userA.getAccount(), StringUtil.get32MD5("123456"), new Back.Result<UserInfo.LoginInfo>() {
            @Override
            public void onSuccess(UserInfo.LoginInfo loginInfo) {
                userB.setAppKey(loginInfo.getKey());
                userB.setSecret(loginInfo.getSecret());
                userB.setToken(loginInfo.getToken());
                ClientFactory.getInstance().setToken(loginInfo.getToken());//初始化SDK
                signal7.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal7.countDown();
            }
        });
        signal7.await();

        //获取用户B会话列表
        final CountDownLatch signal8 = new CountDownLatch(1);
        ClientFactory.getInstance().getUserConversations("", userB.getId(), new Back.Result<List<UserConversationEntity>>() {
            @Override
            public void onSuccess(List<UserConversationEntity> entities) {
                int count = entities.size();
                assertEquals(1, count);
                signal8.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal8.countDown();
            }
        });
        signal8.await();

        // B 回 A 一条消息
        final CountDownLatch signal9 = new CountDownLatch(1);
        ClientFactory.getInstance().sendMessage(sessionEntity.getId(), userB.getId(), "我很好", SessionEntity.SessionType.p2p.toString(), new Back.Result<MessageEntity>() {
            @Override
            public void onSuccess(MessageEntity entity) {//发送文本消息
                assertNotNull(entity.getId());
                signal9.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal9.countDown();
            }
        });
        signal9.await();

        //获取用户A会话列表
        final CountDownLatch signal10 = new CountDownLatch(1);
        ClientFactory.getInstance().setToken(userA.getToken());//初始化SDK
        ClientFactory.getInstance().getUserConversations("", userA.getId(), new Back.Result<List<UserConversationEntity>>() {
            @Override
            public void onSuccess(List<UserConversationEntity> entities) {
                int count = entities.size();
                assertEquals(1, count);
                signal10.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal10.countDown();
            }
        });
        signal10.await();

    }*/
}