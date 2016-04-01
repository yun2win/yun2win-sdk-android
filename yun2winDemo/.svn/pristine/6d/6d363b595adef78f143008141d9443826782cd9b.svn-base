package y2w.Bridge;

import android.os.Handler;
import android.os.Message;

import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.common.CallBackUpdate;
import y2w.common.NoticeUtil;
import y2w.model.SyncQueue;
import y2w.manage.CurrentUser;
import y2w.manage.EnumManage;
import y2w.model.UserConversation;
import y2w.service.Back;

/**
 * Created by maa2 on 2016/3/22.
 */
public class SyncManager implements Serializable{
    private static SyncManager syncManager;
    private String TAG = ReceiveUtil.class.getSimpleName();
    //定时器
    private Timer syncTimer;
    private TimerTask syncTask;
    private SyncHandler syncHandler;
    private CurrentUser user;
    private Map<Integer,SyncQueue> syncQueues = new HashMap<Integer,SyncQueue>();
    private List<SyncQueue> entityList = new ArrayList<SyncQueue>();

    public SyncManager(CurrentUser user){
        this.user = user;
        this.syncHandler = new SyncHandler();
    }

    public void addSync(SyncQueue sq){
        if(syncQueues.containsKey(sq.getType())){
            if(EnumManage.ReceiveSyncStatusType.syncing.toString().equals(sq.getStatus())){
                syncQueues.remove(sq.getType());
                sq.setStatus(EnumManage.ReceiveSyncStatusType.repeat.toString());
                syncQueues.put(sq.getType(),sq);
            }
        }else{
            syncQueues.put(sq.getType(),sq);
        }
        syncHandlerSend(1);
    }

    public class SyncHandler extends Handler implements Serializable{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:// 消息发送，若定时器已关闭，则重新开启
                    syncQueueDeal();
                    break;
                default:
                    break;
            }
        }
    }

    // 消息发送队列处理
    private void syncQueueDeal() {
        try {
            if (entityList.size() == 0) {// 消息发送队列处理
                for(Map.Entry<Integer,SyncQueue> entry : syncQueues.entrySet()){
                    SyncQueue sq = entry.getValue();
                    if(sq.getStatus().equals(EnumManage.ReceiveSyncStatusType.none.toString()) || sq.getStatus().equals(EnumManage.ReceiveSyncStatusType.repeat.toString())){
                        sq.setStatus(EnumManage.ReceiveSyncStatusType.syncing.toString());
                        entityList.add(sq);
                        sync(sq.getType());
                        break;
                    }else{
                        sq.setStatus(EnumManage.ReceiveSyncStatusType.none.toString());
                    }
                }

            }

        } catch (Exception e) {
        }
    }
    private void syncHandlerSend(int what){
        if (syncHandler == null){
            syncHandler = new SyncHandler();
        }
        syncHandler.sendEmptyMessage(what);
    }

    private void sync(int type){
        switch (type){
            case 0 :syncUserConversation();
                break;
            default:
                syncComplete(true);
                break;
        }
    }

    private void syncUserConversation(){
        try {
            NoticeUtil.notice(AppContext.getAppContext(), "receive", "有一条消息", 0, "", "");
            user.getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
                @Override
                public void onSuccess(List<UserConversation> userConversationList) {
                    syncComplete(true);
                    CallBackUpdate update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.chatting.toString());
                    if(update != null){
                        update.addDateUI("");
                    }else{
                        update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.userConversation.toString());
                        if(update != null){
                            update.updateUI();
                        }
                    }
                }

                @Override
                public void onError(int errorCode,String error) {
                    syncComplete(false);
                    LogUtil.getInstance().log(TAG, "update:" + errorCode, null);
                }
            });
        }catch (Exception e){
            syncComplete(false);
        }

    }

    private void syncComplete(boolean isOk){
        if(isOk){
            if(entityList.size() > 0){
                SyncQueue entity = syncQueues.get(entityList.get(0));
                if(entity == null || !entity.getStatus().equals(EnumManage.ReceiveSyncStatusType.repeat)){
                    syncQueues.remove(entityList.get(0));
                }
            }
        }
        entityList.clear();
        syncHandlerSend(1);
    }

}
