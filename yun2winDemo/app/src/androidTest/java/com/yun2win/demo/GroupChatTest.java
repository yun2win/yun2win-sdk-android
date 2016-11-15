package com.yun2win.demo;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.y2w.uikit.utils.StringUtil;


import y2w.base.Urls;
import y2w.manage.CurrentUser;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.User;
import y2w.model.UserConversation;
import y2w.model.messages.MessageType;
import y2w.service.Back;
import y2w.service.ErrorCode;

import java.util.List;
import java.util.concurrent.CountDownLatch;
/**
 * 群聊测试用例
 * Created by maa2 on 2016/3/2.
 */
public class GroupChatTest extends InstrumentationTestCase {

    Context context;
    private String TAG = GroupChatTest.class.getSimpleName();
    private User userA;
    private User userB;
    private CurrentUser curA;
    private CurrentUser curB;
    private Contact contactB;

    private MessageModel msgA;
    private MessageModel msgB;

    private UserConversation userConA;
    private UserConversation userConB;
    private CountDownLatch signal;

    private String passWord = "1234567890";
    private Session sessionA;
    private Session sessionB;
    private SessionMember memberB;

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

    public void testGroupChat() throws Exception {


        //注册 A
        String emailA = StringUtil.getRandomString(5);
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
        String emailB = StringUtil.getRandomString(5);
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
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
        //更新session成员，看B是否为其成员
        signal = new CountDownLatch(1);
        sessionA.getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
            @Override
            public void onSuccess(List<SessionMember> sessionMembers) {
                memberB = sessionA.getMembers().getLocalMember(contactB.getEntity().getUserId());
                assertNotNull(memberB.getEntity());
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
        //A往session中发送消息，并保存本地
        signal = new CountDownLatch(1);
        msgA = sessionA.getMessages().createMessage("大家好", MessageType.Text);
        sessionA.getMessages().getRemote().store(msgA, new Back.Result<MessageModel>() {
            @Override
            public void onSuccess(MessageModel model) {
                assertNotNull(model.getEntity());
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();
        //B 同步 useConversations,保存到数据库，再根据Session从数据库获取对应UserConversation,判断unread是否等于1
        signal = new CountDownLatch(1);
        curB.getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
            @Override
            public void onSuccess(List<UserConversation> userConversations) {
                userConB = curB.getUserConversations().get(sessionA.getEntity().getId(),sessionA.getEntity().getType());
                if (userConB.getEntity() == null || userConB.getEntity().getUnread() == 1) {
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

        //A 更新UserConversation，查看unread是否为1
       /* signal = new CountDownLatch(1);
        curA.getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
            @Override
            public void onSuccess(List<UserConversation> userConversations) {
                userConA = curA.getUserConversations().get(sessionA.getEntity().getId());
                if (userConA.getEntity() == null || userConA.getEntity().getUnread() == 1) {
                    assertEquals(1, 2);
                }
                signal.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode) {
                assertNull(errorCode);
                signal.countDown();
            }
        });
        signal.await();*/

        //A B 各自的userConversation的targetId应该相同
       /* if(!userConA.getEntity().getTargetId().equals(userConB.getEntity().getTargetId())){
            assertEquals(1, 2);
        }*/

        //B根据userConB，获取session，往session中发送消息，并保存本地
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
        msgB = sessionB.getMessages().createMessage("我很好", MessageType.Text);
        sessionB.getMessages().getRemote().store(msgB, new Back.Result<MessageModel>() {
            @Override
            public void onSuccess(MessageModel model) {
                assertNotNull(model.getEntity());
                signal.countDown();
            }

            @Override
            public void onError(int errorCode,String error) {
                assertNull(errorCode);
                signal.countDown();
            }
        });

        //A 根据session同步更新消息


        //B 根据session同步更新消息,查看A B最后一条消息id是否相同

       /* sessionB.getMessages().getRemote().sync(true,"",20,new Back.Result<List<MessageModel>>() {
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
        });
*/

    }



}
