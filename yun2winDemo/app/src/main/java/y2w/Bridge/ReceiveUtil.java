package y2w.Bridge;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.SendReturnCode;
import com.yun2win.imlib.IMSession;
import com.yun2win.utils.Json;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.List;

import y2w.base.AppContext;
import y2w.common.NoticeUtil;
import y2w.common.SendUtil;
import y2w.manage.CurrentUser;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.SyncQueue;
import y2w.model.User;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.ui.activity.ChatActivity;

/**
 * 接受消息处理类
 * Created by yangrongfang on 2016/3/16.
 */
public class ReceiveUtil implements Serializable{
    private String TAG = ReceiveUtil.class.getSimpleName();
    private CurrentUser user;
    private SyncManager syncManager;

    public ReceiveUtil(CurrentUser user){
        this.user = user;
        this.syncManager = new SyncManager(user);
    }

   /* {
        "message": {
        "syncs": [
        {
            "type": 0
        },
        {
            "type": 1,
                "sessionId": "p2p_85"
        }
        ]
    },
        "cmd": "sendMessage",
            "from": "18"
    }*/
    public void receiveMessage(String message,IMSession imSession, String sendMsg, String data){
        if(StringUtil.isEmpty(message)){
            return ;
        }
        Json json = new Json(message);
        String cmd = json.getStr("cmd");
        if("sendMessage".equals(cmd)){
            messageDeal(json.get("message"));
            noticeToast(message);
        }else if(message.contains("y2wMessageId")){
            int returnCode = Integer.parseInt(json.getStr("returnCode"));
            messageSendReturn(returnCode,imSession,sendMsg);
        }

    }

    /**
     * 收到消息回执处理
     * @param returnCode
     */
    private void messageSendReturn(final int returnCode, final IMSession imSession,String sendMsg){
        switch (returnCode){
            case SendReturnCode.SRC_SUCCESS :
                user.getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
                    @Override
                    public void onSuccess(List<UserConversation> userConversationList) {
                        LogUtil.getInstance().log(TAG, "userConversationList :"+userConversationList.size(), null);
                    }

                    @Override
                    public void onError(int errorCode,String error) {
                        LogUtil.getInstance().log(TAG, "update:"+errorCode, null);
                    }
                });
                break;
            case SendReturnCode.SRC_CMD_INVALID :
                LogUtil.getInstance().log(TAG, "returnCode:"+returnCode, null);
                break;

            case SendReturnCode.SRC_SESSION_ON_SERVER_NOT_EXIST :
                LogUtil.getInstance().log(TAG, "imSession.getId() ="+imSession.getId(), null);
                int  index = imSession.getId().indexOf("_")+1;
                if(index < 1){
                    return ;
                }
                LogUtil.getInstance().log(TAG, "SRC_SESSION_ON_SERVER_NOT_EXIST", null);
                String  sessionId = imSession.getId().substring(index,imSession.getId().length());
                user.getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
                    @Override
                    public void onSuccess(final Session session) {
                        session.getMembers().getMembers(new Back.Result<List<SessionMember>>() {
                            @Override
                            public void onSuccess(List<SessionMember> sessionMembers) {
                                user.getImBridges().getImBridge().getImClient().updateSession(imSession, CmdBuilder.buildMembersToJson(sessionMembers), new IMClient.SendCallback() {
                                    @Override
                                    public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                                        if (SendReturnCode.SRC_SESSION_MEMBERS_INVALID == code) {
                                            LogUtil.getInstance().log(TAG, "updateSession:" + code, null);
                                        } else {
                                            session.getMessages().getRemote().sendMessage("", new IMClient.SendCallback() {
                                                @Override
                                                public void onReturnCode(int i, IMSession imSession, String s) {
                                                    LogUtil.getInstance().log(TAG, "sendMessage :" + i, null);
                                                }
                                            });
                                            /*user.getImBridges().getImBridge().getImClient().sendMessage(imSession, sendMsg, new IMClient.SendCallback() {

                                                @Override
                                                public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                                                    LogUtil.getInstance().log(TAG, "sendMessage :" + code, null);
                                                }
                                            });*/
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(int errorCode, String error) {
                                LogUtil.getInstance().log(TAG, "ErrorCode:" + errorCode, null);
                            }
                        });

                    }
                    @Override
                    public void onError(int errorCode,String error) {

                    }
                });
                break;
            case SendReturnCode.SRC_SESSION_MTS_ON_CLIENT_HAS_EXPIRED :
                index = imSession.getId().indexOf("_")+1;
                if(index < 1){
                    return ;
                }
                sessionId = imSession.getId().substring(index,imSession.getId().length());
                user.getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
                    @Override
                    public void onSuccess(final Session session) {
                        session.getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
                            @Override
                            public void onSuccess(List<SessionMember> sessionMembers) {
                                imSession.setMts(session.getMessages().getTimeStamp());
                                LogUtil.getInstance().log(TAG, returnCode + " getMts:" + imSession.getMts(), null);
                                user.getImBridges().getImBridge().getImClient().sendMessage(imSession, CmdBuilder.buildMembersToJson(sessionMembers), new IMClient.SendCallback() {

                                    @Override
                                    public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                                        LogUtil.getInstance().log(TAG, "sendMessage :" + code, null);
                                    }
                                });
                            }

                            @Override
                            public void onError(int errorCode,String error) {

                            }
                        });
                    }

                    @Override
                    public void onError(int errorCode,String error) {

                    }
                });
                break;
            case SendReturnCode.SRC_SESSION_MTS_ON_SERVER_HAS_EXPIRED :
                index = imSession.getId().indexOf("_")+1;
                if(index < 1){
                    return ;
                }
                sessionId = imSession.getId().substring(index,imSession.getId().length());
                LogUtil.getInstance().log(TAG, "SRC_SESSION_MTS_ON_SERVER_HAS_EXPIRED:"+28 , null);
                user.getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
                    @Override
                    public void onSuccess(Session session) {
                        session.getMembers().getMembers(new Back.Result<List<SessionMember>>() {
                            @Override
                            public void onSuccess(List<SessionMember> sessionMembers) {
                                LogUtil.getInstance().log(TAG, "sessionMembers:"+sessionMembers.size() , null);
                                user.getImBridges().updateSession(imSession, CmdBuilder.buildMembersWithDelToJson(sessionMembers), new IMClient.SendCallback() {

                                    @Override
                                    public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                                        if (SendReturnCode.SRC_SESSION_MEMBERS_INVALID == code) {
                                            LogUtil.getInstance().log(TAG, "updateSession:" + code, null);
                                        }else{
                                            user.getImBridges().getImBridge().getImClient().sendMessage(imSession, sendMsg, new IMClient.SendCallback() {

                                                @Override
                                                public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                                                    LogUtil.getInstance().log(TAG, "sendMessage :" + code, null);
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(int errorCode,String error) {
                                LogUtil.getInstance().log(TAG, "ErrorCode:" + errorCode, null);
                            }
                        });

                    }

                    @Override
                    public void onError(int errorCode,String error) {

                    }
                });
                break;
            default:break;
        }
    }
    private void messageDeal(Json json){
        List<Json> syncs = json.getList("syncs");
        for(Json j : syncs){
            SyncQueue sq = new SyncQueue();
            sq.setType(j.getStr("type"));
            sq.setSessionId(j.getStr("sessionId"));
            sq.setContent(j.getStr("content"));
            sq.setStatus(EnumManage.ReceiveSyncStatusType.none.toString());
            sq.setMyId(user.getEntity().getId());
            syncManager.addSync(sq);
        }

    }
    private void noticeToast(String message){
        if(StringUtil.isEmpty(message)){
            return;
        }
        if(isTopChatActivity()){
            return;
        }
        final Json json = new Json(message);
        final String from = json.getStr("from");
        Users.getInstance().getUser(from, new Back.Result<User>() {
            @Override
            public void onSuccess(final User user1) {
                List<Json> syncs = json.get("message").getList("syncs");
                String sessionId = "";
                for(Json j : syncs){
                    sessionId = j.getStr("sessionId");
                    if(!StringUtil.isEmpty(sessionId)){
                        break;
                    }
                }
                if(!StringUtil.isEmpty(sessionId)){
                    int  index = sessionId.indexOf("_")+1;
                    if(index < 1){
                        return ;
                    }
                    sessionId = sessionId.substring(index, sessionId.length());
                    user.getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
                        @Override
                        public void onSuccess(Session session) {
                            if(EnumManage.SessionType.group.toString().equals(session.getEntity().getType())){
                                NoticeUtil.notice(session.getEntity().getName(),"有一条新消息",0,from,session.getEntity().getType());
                            }else{
                                NoticeUtil.notice(user1.getEntity().getName(),"有一条新消息",0,from,session.getEntity().getType());
                            }
                        }

                        @Override
                        public void onError(int code, String error) {

                        }
                    });
                }
            }

            @Override
            public void onError(int code, String error) {

            }
        });

    }

    /**
     * 判断当前activity是否是ChatActivity
     *
     * @return
     */
    private static boolean isTopChatActivity() {
        return ChatActivity.class.getCanonicalName().equals(
                StringUtil.getTopActivityName(AppContext.getAppContext()));
    }

}
