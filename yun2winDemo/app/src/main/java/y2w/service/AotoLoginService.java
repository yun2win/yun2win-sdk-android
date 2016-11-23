package y2w.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.y2w.uikit.utils.StringUtil;

import y2w.base.AppData;
import y2w.base.PushService;
import y2w.base.Tool;
import y2w.common.CallBackUpdate;
import y2w.common.UserInfo;
import y2w.manage.CurrentUser;
import y2w.manage.Users;

public class AotoLoginService extends Service {
    public static boolean isLogin = false;

    public AotoLoginService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if(!isLogin) {
            isLogin = true;
            if(AppData.getInstance().isactivityrun()){
                return super.onStartCommand(intent, flags, startId);
            }
            String account = Users.getInstance().getCurrentUser().getEntity().getAccount();
            if(StringUtil.isEmpty(account)){
                return super.onStartCommand(intent, flags, startId);
            }
            if(Users.getInstance().getCurrentUser().getImBridges().getConnectstatus()!=0){
                return super.onStartCommand(intent, flags, startId);
            }
            if(!StringUtil.isEmpty(UserInfo.getAccount())&& !StringUtil.isEmpty(UserInfo.getPassWord())) {
                Users.getInstance().createCurrentUser(UserInfo.getCurrentInfo());
                Users.getInstance().getCurrentUser().getImBridges().disConnect();
                Intent pushservice = new Intent(this, PushService.class);
                startService(pushservice);
                //同步会话联系人
                Users.getInstance().getCurrentUser().getRemote().sync(new Back.Callback() {
                    @Override
                    public void onSuccess() {
                        CallBackUpdate updateconversation = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.userConversation.toString());
                        if(updateconversation != null){
                            updateconversation.updateUI();
                        }
                        CallBackUpdate updatecatct = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.contact.toString());
                        if(updatecatct != null){
                            updatecatct.updateUI();
                        }
                        gotoActivity(intent,1);
                    }
                    @Override
                    public void onError(int errorCode, String error) {
                        if(errorCode==ErrorCode.EC_NETWORK_ERROR){
                            gotoActivity(intent,2);
                        }else{
                            gotoActivity(intent,3);
                        }
                    }
                });
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void gotoActivity(Intent intent,int type){
        if(intent.getExtras() != null) {
            Messenger mMessenger = (Messenger) intent.getExtras().get("messenger");
            if(mMessenger != null){
                try {
                    Message message = Message.obtain();
                    message.what = type;
                    mMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        stopSelf();
    }
}
