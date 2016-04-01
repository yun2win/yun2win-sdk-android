package y2w.manage;

import android.os.Handler;
import android.os.Message;

import com.yun2win.imlib.ConnectionReturnCode;
import com.yun2win.imlib.ConnectionStatus;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;

import y2w.Bridge.CmdBuilder;
import y2w.base.AppContext;
import y2w.Bridge.IMBridge;
import y2w.Bridge.ReceiveUtil;
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
    /**
     * 有参构造
     * @param user 当前登录用户
     */
    public IMBridges(CurrentUser user){
        this.user = user;
        this.receiveUtil = new ReceiveUtil(user);
    }

    public IMBridge getImBridge() {
        return imBridge;
    }

    /**
     * 消息通道服务器连接
     * 先获取imToken，再用token与userId，连接服务器
     */
    public void connect(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    MToken mToken = (MToken) msg.obj;
                    user.setImToken(mToken);
                    imBridge = new IMBridge(user);
                    imBridge.getImClient().connect(new IMClient.onConnectionStatusChanged() {
                        @Override
                        public void onChanged(int status, int error) {
                            if (status == ConnectionStatus.CS_DISCONNECTED) {
                                switch (error) {
                                    case ConnectionReturnCode.CRC_KICKED:
                                        AppContext.getAppContext().logout();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }, new IMClient.OnMessageReceiveListener() {
                        @Override
                        public void onMessage(String message, IMSession imSession, String sendMsg, String data) {
                            receiveUtil.receiveMessage(message, imSession, sendMsg, data);
                        }
                    });
                }else{
                    LogUtil.getInstance().log(TAG,"get imToken failure",null);
                }
            }
        };
        //获取IMToken
        user.getImToken(new Back.Result<MToken>() {
            @Override
            public void onSuccess(MToken mToken) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = mToken;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(int errorCode, String error) {
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
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
     * @param message 消息体
     * @param callback 回调
     */
    public void sendMessage(Session session,String message,IMClient.SendCallback callback){
        IMSession imSession = new IMSession();
        String sessionId = session.getEntity().getType() + "_" + session.getEntity().getId();
        imSession.setId(sessionId);
        imSession.setMts(session.getMessages().getTimeStamp());
        if(imBridge != null)
        imBridge.getImClient().sendMessage(imSession, CmdBuilder.buildMessage(sessionId), callback);
    }
    /**
     * 更新会话
     * @param imSession 消息通道会话
     * @param members 会话成员
     * @param callback 回调
     */
    public void updateSession(IMSession imSession,String members,IMClient.SendCallback callback){
        if(imBridge != null)
        imBridge.getImClient().updateSession(imSession, members, callback);
    }

}
