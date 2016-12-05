package y2w.manage;

import android.os.Handler;
import android.os.Message;

import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.imlib.ConnectionReturnCode;
import com.yun2win.imlib.ConnectionStatus;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;
import com.yun2win.imlib.SendReturnCode;
import com.yun2win.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import y2w.Bridge.CmdBuilder;
import y2w.Bridge.IMBridge;
import y2w.Bridge.ReceiveUtil;
import y2w.base.AppContext;
import y2w.common.UserMToken;
import y2w.model.MToken;
import y2w.model.Session;
import y2w.service.Back;

/**
 * 消息通道服务器连接管理类
 * Created by yangrongfang on 2016/3/16.
 */
public class IMBridges implements Serializable{

    private String TAG = IMBridges.class.getSimpleName();
    private CurrentUser user;
    private ReceiveUtil receiveUtil;
    private IMBridge imBridge;
    private Handler uihander;
    private int connectstatus = 0;//成功

    /**
     * 有参构造
     * @param user 当前登录用户
     */
    public IMBridges(CurrentUser user){
        this.user = user;
        this.receiveUtil = new ReceiveUtil(user);
    }

    public void setHandler(Handler uihander){
        this.uihander = uihander;
        uihander.sendEmptyMessage(connectstatus);
    }
    public int getConnectstatus(){
        return connectstatus;
    }
    public IMBridge getImBridge() {
        return imBridge;
    }
    public String getY2wIMAppSessionPrefix(){
        return "y2wIMApp";
    }
    public String getY2wIMAppMTS(){
        return "1264953600000";
    }
    /**
     * 消息通道服务器连接
     * 先获取imToken，再用token与userId，连接服务器
     */
    public void connect(){
        if(user == null || user.getEntity() == null)
            return;
        refreshMToken(false);
        return;
        /*String userid = UserMToken.getUserId();
        if(!StringUtil.isEmpty(userid)&&userid.equals(user.getEntity().getId())){
            String tokenType = UserMToken.getTokenType() ;
            String accessToken = UserMToken.getAccessToken();
            String expiresIn = UserMToken.getExpiresin();
            String refreshToken= UserMToken.getRefreshToken();
            if(!StringUtil.isEmpty(tokenType) && !StringUtil.isEmpty(accessToken) && !StringUtil.isEmpty(expiresIn) && !StringUtil.isEmpty(refreshToken)){
                MToken mToken = new MToken();
                mToken.setTokenType(tokenType);
                mToken.setAccessToken(accessToken);
                mToken.setExpiresIn(expiresIn);
                mToken.setRefreshToken(refreshToken);
                user.setImToken(mToken);
                pushConnect(mToken);
                return;
            }
        }
        refreshMToken();*/
    }
    Handler connctMqttHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what ==1){
                pushConnect(user.getImToken());
            }
        }
    };
    boolean isreconnct = false;
    private void refreshMToken(boolean isreconnct){
        this.isreconnct = isreconnct;
        UserMToken.reFreshToken(user, new Back.Result<MToken>() {
            @Override
            public void onSuccess(MToken mToken) {
                connctMqttHandler.sendEmptyMessage(1);
            }
            @Override
            public void onError(int code, String error) {
                String tokenType = UserMToken.getTokenType() ;
                String accessToken = UserMToken.getAccessToken();
                String expiresIn = UserMToken.getExpiresin();
                String refreshToken= UserMToken.getRefreshToken();
                if(!StringUtil.isEmpty(tokenType) && !StringUtil.isEmpty(accessToken) && !StringUtil.isEmpty(expiresIn) && !StringUtil.isEmpty(refreshToken)){
                    MToken mToken = new MToken();
                    mToken.setTokenType(tokenType);
                    mToken.setAccessToken(accessToken);
                    mToken.setExpiresIn(expiresIn);
                    mToken.setRefreshToken(refreshToken);
                    user.setImToken(mToken);
                    connctMqttHandler.sendEmptyMessage(1);
                }
            }
        });
    }
    private void pushConnect(MToken mToken){
        if(mToken == null)
            return;
        if(StringUtil.isEmpty(user.getEntity().getId())||StringUtil.isEmpty(mToken.getAccessToken()))
            return;
        if(isreconnct&&imBridge!=null){//重连
            imBridge.getImClient().reconnect(mToken.getAccessToken());
            return;
        }
        imBridge = new IMBridge(user);
        imBridge.getImClient().connect(new IMClient.onConnectionStatusChanged() {
            @Override
            public void onChanged(int status, int error) {
                if (status == ConnectionStatus.CS_DISCONNECTED) {
                    switch (error) {
                        case ConnectionReturnCode.CRC_KICKED://其他端登录
                            AppContext.getAppContext().logout();
                            ToastUtil.ToastMessage(AppContext.getAppContext(),"您的账号在其他端登入，请重新登入");
                            break;
                        case ConnectionReturnCode.CRC_IDENTIFIER_REJECTED://clientId不合法，sdk抛出异常到
                            ToastUtil.ToastMessage(AppContext.getAppContext(),"用户的ID设置不合法");
                            break;
                        case ConnectionReturnCode.CRC_UNACCEPTABLE_PROTOCOL_VERSION://协议版本不正确
                            ToastUtil.ToastMessage(AppContext.getAppContext(),"连接协议错误");
                            break;
                        case ConnectionReturnCode.CRC_UID_INVALID://username为空
                            ToastUtil.ToastMessage(AppContext.getAppContext(),"用户ID为空");
                            break;
                        case ConnectionReturnCode.CRC_TOKEN_INVALID: //服务器验证token出错
                        case ConnectionReturnCode.CRC_TOKEN_HAS_EXPIRED://服务器验证token过期
                            refreshMToken(true);
                        default:
                            break;
                    }
                }else if(status ==ConnectionStatus.CS_NETWORK_DISCONNECTED){//没有网络
                    if(uihander!=null){
                        uihander.sendEmptyMessage(1);
                    }
                    connectstatus = 1;
                }else if(status ==ConnectionStatus.CS_CONNECTING){//连接中
                    if(uihander!=null){
                        uihander.sendEmptyMessage(2);
                    }
                    connectstatus = 2;
                }else if(status == ConnectionStatus.CS_CONNECTED){//连接成功
                    if(uihander!=null){
                        uihander.sendEmptyMessage(3);
                    }
                    connectstatus = 3;
                    if(receiveUtil!=null) {//同步消息会话
                        try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject();
                        jsonObject.put("cmd","update");
                        jsonObject.put("y2wMessageId","update");
                        jsonObject.put("returnCode", SendReturnCode.UPDATE_MESSAGE);
                        receiveUtil.receiveMessage(jsonObject.toString(), new IMSession(), "", "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new IMClient.OnMessageReceiveListener() {
            @Override
            public void onMessage(String message, IMSession imSession, String sendMsg, String data) {
                if(receiveUtil!=null) {
                    receiveUtil.receiveMessage(message, imSession, sendMsg, data);
                }
            }
        });
    }

    /**
     * 断开服务器连接
     */
    public void disConnect(){
        if(imBridge != null)
        imBridge.getImClient().disConnect();
    }

    /**
     * 消息发送
     * @param session 会话
     * @param callback 回调
     */
    public void sendMessage(Session session, boolean ispns, IMClient.SendCallback callback){
        if(session != null) {
            IMSession imSession = new IMSession();
            String sessionId = session.getEntity().getType() + "_" + session.getEntity().getId();
            imSession.setId(sessionId);
            imSession.setForce(false);
            imSession.setMts( session.getMessages().getTimeStamp());

           JSONObject pns = new JSONObject();
            if(ispns) {
                JSONObject pnsContext = new JSONObject();
                try {
                    pns.put("msg", "您有一条新消息");
                    pnsContext.put("from", session.getEntity().getName());
                    pns.put("payload", pnsContext);
                    pns.put("sound","global.wav");
                } catch (JSONException e) {
                    pns = null;
                    e.printStackTrace();
                }
            }else{
                pns = null;
            }

            if (imBridge != null) {
                imBridge.getImClient().sendMessage(imSession, CmdBuilder.buildMessage(sessionId,pns), callback);
            }else{
                connect();
                if (imBridge != null)
                imBridge.getImClient().sendMessage(imSession, CmdBuilder.buildMessage(sessionId,pns), callback);
            }
        }
    }
    /**
     * 通知消息更新别人会话
     * @param callback 回调
     */
   public void upOtherCoversation(String otherUid,IMClient.SendCallback callback){
      if(!StringUtil.isEmpty(otherUid)) {
          IMSession imSession = new IMSession();
          String sessionId = getY2wIMAppSessionPrefix() + "_" + otherUid;
          imSession.setId(sessionId);
          imSession.setMts(getY2wIMAppMTS());
          imSession.setForce(false);
          if (imBridge != null)
              imBridge.getImClient().sendMessage(imSession, CmdBuilder.buildupdateCoversation(), callback);
      }
   }
    /**
     * 更新会话和成员 已读未读
     * @param session 会话
     * @param callback 回调
     */
    public void updateMembers(Session session,IMClient.SendCallback callback){
        if(session != null) {
            IMSession imSession = new IMSession();
            String sessionId = session.getEntity().getType() + "_" + session.getEntity().getId();
            imSession.setId(sessionId);

            imSession.setMts( session.getMessages().getTimeStamp());
            if (imBridge != null)
                imBridge.getImClient().sendMessage(imSession, CmdBuilder.buildupdateMembers(sessionId), callback);
        }
    }
    /**
     * 更新会话
     * @param imSession 消息通道会话
     * @param members 会话成员
     * @param callback 回调
     */
    public void updateSession(IMSession imSession,String members,String message,IMClient.SendCallback callback){
        if(imBridge != null)
        imBridge.getImClient().updateSession(imSession, members, message,callback);
    }

    /**
     * 音视频通知
     * @param session
     * @param message
     * @param callback
     */
    public void avCallMessage(Session session,String message,IMClient.SendCallback callback){
        if(session != null) {
            IMSession imSession = new IMSession();
            String sessionId = session.getEntity().getType() + "_" + session.getEntity().getId();
            imSession.setId(sessionId);
            imSession.setMts(session.getMessages().getTimeStamp());
            if (imBridge != null)
                imBridge.getImClient().sendMessage(imSession, message, callback);
        }
    }

}
