package com.yun2win.demo;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.y2w.uikit.utils.StringUtil;

import java.util.concurrent.CountDownLatch;

import y2w.manage.CurrentUser;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.MToken;
import y2w.model.Session;
import y2w.model.User;
import y2w.service.Back;

/**
 * Created by maa2 on 2016/3/4.
 */
public class MqttTest extends InstrumentationTestCase {

    Context context;
    private String TAG = P2pChatTest.class.getSimpleName();
    private User userA;
    private User userB;
    private CurrentUser curA;
    private CurrentUser curB;
    private Contact contactB;
    private Session sessionA;

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

    public void testMqtt() throws Exception {

        //注册 A
        String emailA = StringUtil.getRandomString(7);
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
        String emailB = StringUtil.getRandomString(7);
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

        //A 获取与B交流的Session，先从本地获取，本地没有服务器获取，获取成功后保存本地
        signal = new CountDownLatch(1);
        contactB.getSession(new Back.Result<Session>() {
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


        //获取连接的token
        signal = new CountDownLatch(1);
        curA.getImToken(new Back.Result<MToken>() {
            @Override
            public void onSuccess(MToken mToken) {
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
       /* //连接服务器
        signal = new CountDownLatch(1);
        IMClient.getInstance().init(AppContext.getAppContext());
        curA.getImBridges().connect(new IMClient.onConnectionStatusChanged() {
            @Override
            public void onChanged(int status, int error) {
                if (status == ConnectionStatus.CS_CONNECTED) {
                    signal.countDown();
                } else if (status == ConnectionStatus.CS_CONNECTING) {

                } else {
                    signal.countDown();
                }
            }
        }, new IMClient.OnMessageReceiveListener() {
            @Override
            public void onMessage(String message, IMSession imSession, String sendMsg, String data) {
                signal.countDown();
            }
        });
        signal.await();
        //发送命令
       signal = new CountDownLatch(1);
        String message = "{syncs:[{type:0},{type:1,sessionId:\"" + "p2p_" + sessionA.getEntity().getId() + "\"}]}";
        sessionA.getMessages().getRemote().sendMessage(message, new IMClient.SendCallback() {
            @Override
            public void onReturnCode(int code) {
                assertEquals(code,20);
                signal.countDown();
            }
        });
        signal.await();*/


    }
}
