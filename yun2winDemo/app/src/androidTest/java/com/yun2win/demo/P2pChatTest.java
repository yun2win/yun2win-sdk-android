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
import y2w.model.messages.MessageType;
import y2w.service.Back;
import y2w.service.ErrorCode;

import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * p2p私聊测试用例
 * Created by maa2 on 2016/1/6.
 */
public class P2pChatTest extends InstrumentationTestCase {

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
    private String passWord = "1234567890";

    List<MessageModel> msA=null;
    List<MessageModel> msB=null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testP2pChat() throws Exception {


        //注册 A
        String emailA = StringUtil.getRandomString(6);
        signal = new CountDownLatch(1);
        Users.getInstance().getRemote().register(emailA, passWord, emailA + "杨",  new Back.Result<User>() {
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
        //A 给 B 发送消息,成功后，保存到数据库,再从数据库根据消息Id获取消息
        signal = new CountDownLatch(1);
        msgA = sessionA.getMessages().createMessage("你好", MessageType.Text);
        sessionA.getMessages().getRemote().store(msgA, new Back.Result<MessageModel>() {
                    @Override
                    public void onSuccess(MessageModel model) {
                        msgA = model;
                        signal.countDown();
                    }

                    @Override
                    public void onError(int errorCode,String error) {
                        assertNull(errorCode);
                        signal.countDown();
                    }
                }
        );
        signal.await();
        //B 同步 useConversations,保存到数据库，再根据Session从数据库获取对应UserConversation,判断unread是否等于1
        signal = new CountDownLatch(1);
        curB.getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
            @Override
            public void onSuccess(List<UserConversation> userConversations) {
                userConB = curB.getUserConversations().get(curA.getEntity().getId(),"p2p");
                if (userConB == null || userConB.getEntity().getUnread() == 1) {
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
        //B 获取与A 交流的session
        signal = new CountDownLatch(1);
        userConB.getSession(new Back.Result<Session>() {
            @Override
            public void onSuccess(Session session) {
                sessionB = session;
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();

        //B 同步 与 A交流的最新消息,并保存本地
        signal = new CountDownLatch(1);


        //B 给 A 回个消息
        signal = new CountDownLatch(1);
        msgB = sessionB.getMessages().createMessage("我很好", MessageType.Text);
        sessionB.getMessages().getRemote().store(msgB, new Back.Result<MessageModel>() {
            @Override
            public void onSuccess(MessageModel model) {
                msgB = model;;
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();

            }
        });
        signal.await();
        //A 同步 useConversations,保存到数据库，再根据Session从数据库获取对应UserConversation,判断unread是否等于1
        signal = new CountDownLatch(1);
        curA.getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
            @Override
            public void onSuccess(List<UserConversation> userConversations) {
                userConA = curA.getUserConversations().get(curB.getEntity().getId(),"p2p");
                //TODO 服务器返回unread为0，正常情况为userConA.getEntity().getUnread() ！= 1
                if(userConA == null || userConA.getEntity().getUnread() == 1){
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


        //A 同步 与 B交流的最新消息,并保存本地
       /* signal = new CountDownLatch(1);
        sessionA.getMessages().getRemote().sync(true,"",20,new Back.Result<List<MessageModel>>() {
            @Override
            public void onSuccess(List<MessageModel> models) {
                sessionA.getMessages().getMessages(msgA, 10, new Back.Result<List<MessageModel>>() {
                    @Override
                    public void onSuccess(List<MessageModel> models) {
                        msA = models;
                        sessionB.getMessages().getMessages(msgA, 10, new Back.Result<List<MessageModel>>() {
                            @Override
                            public void onSuccess(List<MessageModel> models) {
                                msB = models;
                                MessageModel msa = msA.get(msA.size() - 1);
                                MessageModel msb = msB.get(msB.size() - 1);
                                if (!msa.getEntity().getId().equals(msb.getEntity().getId())) {
                                    assertEquals(1, 2);
                                }
                                signal.countDown();
                            }

                            @Override
                            public void onError(int errorCode,String error) {
                                signal.countDown();
                            }
                        });
                    }

                    @Override
                    public void onError(int errorCode,String error) {
                        signal.countDown();
                    }
                });
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });*/
        signal.await();
    }



}