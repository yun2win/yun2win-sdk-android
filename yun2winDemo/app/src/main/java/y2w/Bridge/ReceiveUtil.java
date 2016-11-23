package y2w.Bridge;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;
import com.yun2win.imlib.SendReturnCode;
import com.yun2win.utils.Json;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.common.CallBackUpdate;
import y2w.common.NoticeUtil;
import y2w.httpApi.messages.AcCallQueue;
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
import y2w.ui.activity.MainActivity;

/**
 * 接受消息处理类
 * Created by yangrongfang on 2016/3/16.
 */
public class ReceiveUtil implements Serializable{
    private String TAG = ReceiveUtil.class.getSimpleName();
    private CurrentUser currentUser;
    private SyncManager syncManager;
    private HashMap<String,Integer> mtsMap = new HashMap<String,Integer>();
    private int mtsMaxCount = 3;

    private List<ReceiveMessageEntity> receivequeue = new ArrayList<ReceiveMessageEntity>();
    private ReceiveMessageManage messageMeangeThread ;

    public ReceiveUtil(CurrentUser user){
        this.currentUser = user;
        this.syncManager = new SyncManager(user);
       if(messageMeangeThread==null) {
           messageMeangeThread =new ReceiveMessageManage();
           messageMeangeThread.start();
       }
    }

    public void receiveMessage(String message,IMSession imSession, String sendMsg, String data){
        if(StringUtil.isEmpty(message)){
            return ;
        }
        receivequeue.add(new ReceiveMessageEntity(message, imSession, sendMsg, data));
    }
   private class ReceiveMessageManage extends Thread{
       @Override
       public void run() {
           super.run();
           while (true) {
               if(receivequeue.size()>0) {
                   ReceiveMessageEntity receiveMessage = receivequeue.get(0);
                   receivequeue.remove(0);
                   if (receiveMessage != null) {
                       Json json = new Json(receiveMessage.getMessage());
                       String cmd = json.getStr("cmd");
                       if ("sendMessage".equals(cmd)) {
                           messageDeal(json.get("message"),receiveMessage.getMessage());

                       } else if (receiveMessage.getMessage().contains("y2wMessageId")) {
                           int returnCode = Integer.parseInt(json.getStr("returnCode"));
                           messageSendReturn(returnCode, receiveMessage.getImSession(), receiveMessage.getSendMsg());
                       }
                   }
               }else{
                   try {
                       sleep(500);
                   } catch (Exception e) {
                   }
               }
           }
       }
   }

    private class ReceiveMessageEntity{
        private String message;
        private IMSession imSession;
        private String sendMsg;
        private String data;
        public ReceiveMessageEntity(String message,IMSession imSession, String sendMsg, String data){
            this.message = message;
            this.imSession = imSession;
            this.sendMsg = sendMsg;
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public IMSession getImSession() {
            return imSession;
        }

        public String getSendMsg() {
            return sendMsg;
        }

        public String getData() {
            return data;
        }
    }
    /**
     * 收到消息回执处理
     * @param returnCode
     */
    private void messageSendReturn(final int returnCode, final IMSession imSession, final String sendMsg){

      if(returnCode== SendReturnCode.UPDATE_MESSAGE){//更新会话
          if(currentUser!=null) {
              currentUser.getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
                  @Override
                  public void onSuccess(List<UserConversation> userConversationList) {
                      AppData.isRefreshConversation = true;
                      String currentActivityname = AppContext.getAppContext().getCurrentActivityname();
                      if (currentActivityname.equals(ChatActivity.class.getName())) {
                          CallBackUpdate update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.chatting.toString());
                          if (update != null)
                              update.addDateUI("updatemessage");
                      } else {
                          if (currentActivityname.equals("backstage")) {//後台

                          } else if (currentActivityname.equals(MainActivity.class.getName())) {
                              CallBackUpdate update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.userConversation.toString());
                              if (update != null) {
                                  update.updateUI();
                              }
                          }
                      }
                  }

                  @Override
                  public void onError(int errorCode, String error) {
                      LogUtil.getInstance().log(TAG, "update:" + errorCode, null);
                  }
              });
          }
          return;
      }
       if(imSession==null||imSession.getId()==null)
           return;
        String foo[] = imSession.getId().split("_");
        if(foo==null||foo.length<2){
            return;
        }
        String sessiontype = foo[0];
        String sessionId = foo[1];
        switch (returnCode){
            case SendReturnCode.SRC_SUCCESS :
                if(mtsMap.containsKey(sessionId) && imSession.isForce()){
                    mtsMap.remove(sessionId);
                }
                break;
            case SendReturnCode.SRC_CMD_INVALID :
                LogUtil.getInstance().log(TAG, "returnCode:"+returnCode, null);
                break;
            case SendReturnCode.SRC_SESSION_MTS_INVALID :
                LogUtil.getInstance().log(TAG, "returnCode:"+returnCode, null);
                break;
            case SendReturnCode.SRC_SESSION_ON_SERVER_NOT_EXIST :
                 if(StringUtil.isEmpty(sessiontype))
                     return;
                if(sessiontype.equals(currentUser.getImBridges().getY2wIMAppSessionPrefix())){
                    currentUser.getImBridges().getImBridge().getImClient().updateSession(imSession, "["+CmdBuilder.buildMemberWithDel(sessionId,false)+"]", sendMsg, new IMClient.SendCallback() {
                        @Override
                        public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                        }
                    });
                }else {
                    currentUser.getSessions().getRemote().getSession(sessionId, EnumManage.SessionType.group.toString(),new Back.Result<Session>() {//通过sessionID获得，走默认端口
                        @Override
                        public void onSuccess(final Session session) {
                            session.getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
                                @Override
                                public void onSuccess(List<SessionMember> sessionMembers) {
                                    imSession.setMts(session.getMessages().getTimeStamp());
                                    imSession.setForce(true);
                                    currentUser.getImBridges().getImBridge().getImClient().updateSession(imSession, CmdBuilder.buildMembersWithDelToJson(sessionMembers), sendMsg, new IMClient.SendCallback() {
                                        @Override
                                        public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, String error) {
                                }
                            });
                        }

                        @Override
                        public void onError(int code, String error) {
                        }
                    });
                }
                break;
            case SendReturnCode.SRC_SESSION_MTS_ON_CLIENT_HAS_EXPIRED :

                if(mtsMap.containsKey(sessionId)){
                    int count =  mtsMap.get(sessionId) + 1;
                    mtsMap.remove(sessionId);
                    mtsMap.put(sessionId, count);
                }else{
                    mtsMap.put(sessionId,0);
                }
                if(mtsMap.get(sessionId) < mtsMaxCount){
                    currentUser.getSessions().getRemote().getSession(sessionId, EnumManage.SessionType.group.toString(), new Back.Result<Session>() {
                        @Override
                        public void onSuccess(final Session session) {
                            session.getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
                                @Override
                                public void onSuccess(List<SessionMember> sessionMembers) {
                                    imSession.setMts( session.getMessages().getTimeStamp());
                                    imSession.setForce(true);
                                    currentUser.getImBridges().getImBridge().getImClient().updateSession(imSession,CmdBuilder.buildMembersWithDelToJson(sessionMembers),sendMsg, new IMClient.SendCallback() {
                                        @Override
                                        public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                                        }
                                    });
                                }
                                @Override
                                public void onError(int code, String error) {
                                }
                            });
                        }
                        @Override
                        public void onError(int code, String error) {
                        }
                    });
                }else{
                    mtsMap.remove(sessionId);
                }
                break;
            case SendReturnCode.SRC_SESSION_MTS_ON_SERVER_HAS_EXPIRED :

                currentUser.getSessions().getRemote().getSession(sessionId, EnumManage.SessionType.group.toString(), new Back.Result<Session>() {
                    @Override
                    public void onSuccess(final Session session) {
                        session.getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
                            @Override
                            public void onSuccess(List<SessionMember> sessionMembers) {
                                imSession.setMts( session.getMessages().getTimeStamp());
                                imSession.setForce(true);
                                currentUser.getImBridges().getImBridge().getImClient().updateSession(imSession,CmdBuilder.buildMembersWithDelToJson(sessionMembers),sendMsg, new IMClient.SendCallback() {
                                    @Override
                                    public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                                    }
                                });
                            }
                            @Override
                            public void onError(int code, String error) {
                            }
                        });
                    }
                    @Override
                    public void onError(int code, String error) {
                    }
                });
                break;

            default:break;
        }
    }

    private void messageDeal(Json json,String noticemessage){
        if(!json.getStr("syncs").equals("")) {
            List<Json> syncs = json.getList("syncs");
            for (Json j : syncs) {
                SyncQueue sq = new SyncQueue();
                sq.setType(j.getStr("type"));
                sq.setSessionId(j.getStr("sessionId"));
                sq.setContent(j.getStr("content"));
                sq.setStatus(EnumManage.ReceiveSyncStatusType.none.toString());
                sq.setMyId(currentUser.getEntity().getId());
                syncManager.addMessage(sq);
                if("1".equals(sq.getType())){//交流消息
                    if("backstage".equals(AppContext.getAppContext().getCurrentActivityname())) {
                        noticeToast(noticemessage);
                    }
                }
            }
        }
        if(!json.getStr("av").equals("")){
            Json avJson = new Json(json.getStr("av"));
            AcCallQueue sq = new AcCallQueue();
            sq.setType(avJson.getStr("type"));
            sq.setMode(avJson.getStr("mode"));
            sq.setAction(avJson.getStr("action"));
            sq.setChannel(avJson.getStr("channel"));
            sq.setSender(avJson.getStr("sender"));
            List<Json> nubJsons = avJson.getList("members");
            List<String> ids = new ArrayList<String>();
            for(Json j : nubJsons){
                ids.add(j.toString());
            }
            sq.setMembers(ids);
            sq.setSession(avJson.getStr("session"));
            syncManager.addCallMessage(sq);
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
        if(from.equals(currentUser.getEntity().getId())){
            return;
        }
        Users.getInstance().getUser(from, new Back.Result<User>() {
            @Override
            public void onSuccess(final User user) {
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
                    currentUser.getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
                        @Override
                        public void onSuccess(Session session) {
                            noticeShow(session,user);
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

    private void noticeShow(final Session session, final User user){
        if (EnumManage.SessionType.group.toString().equals(session.getEntity().getType())) {
            NoticeUtil.notice(session.getEntity().getName(), "您有收到一条新的消息!", NoticeUtil.CHAT, user.getEntity().getId(), session, session.getEntity().getType());
        } else {
            NoticeUtil.notice(user.getEntity().getName(), "您有收到一条新的消息!", NoticeUtil.CHAT, user.getEntity().getId(), session, session.getEntity().getType());
        }
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
