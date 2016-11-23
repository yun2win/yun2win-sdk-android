package y2w.Bridge;

import android.content.Intent;
import android.os.Bundle;

import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.List;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.base.Tool;
import y2w.common.CallBackUpdate;
import y2w.httpApi.messages.AcCallQueue;
import y2w.manage.CurrentUser;
import y2w.manage.EnumManage;
import y2w.model.Contact;
import y2w.model.SyncQueue;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.ui.activity.ChatActivity;
import y2w.ui.activity.MainActivity;

/**
 * Created by maa2 on 2016/3/22.
 */
public class SyncManager implements Serializable{
    private static SyncManager syncManager;
    private String TAG = ReceiveUtil.class.getSimpleName();
    private CurrentUser user;
    private boolean syncConversation =false;//是否正在同步会话
    private boolean hasSyncConversation= false;//是否有新的同步会话消息
    private int repeatSyncconversationCount = 0;

    private boolean syncContact =false;//是否正在同步会话
    private boolean hasSyncContact= false;//是否有新的同步会话消息
    private int repeatSynccontactCount = 0;

    public SyncManager(CurrentUser user){
        this.user = user;
    }

    public void addMessage(SyncQueue sq){
        sync(sq);
    }

    private void sync(SyncQueue sq){
        String type = sq.getType();
        String content = sq.getContent();
        if("0".equals(type)){
            if(syncConversation){
                hasSyncConversation = true;
            }else{
                syncUserConversation();
            }
        }else if("1".equals(type)){
            String currentActivityname =AppContext.getAppContext().getCurrentActivityname();
             if(!("backstage".equals(AppContext.getAppContext().getCurrentActivityname()))&&currentActivityname.equals(ChatActivity.class.getName())){
                 CallBackUpdate update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.chatting.toString());
                 if(update!=null) {
                     update.addDateUI(sq.getSessionId());
                 }
            }
        }else if("2".equals(type)){
            if(syncContact){
                hasSyncContact = true;
            }else{
                syncUserContact();
            }
        }else if("3".equals(type)){

        }else if("4".equals(type)){//同步会话和会话成员
            String currentActivityname =AppContext.getAppContext().getCurrentActivityname();
            if(currentActivityname.equals(ChatActivity.class.getName())){
                CallBackUpdate update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.chatting.toString());
                if(update!=null&&sq!=null) {
                    update.SyncSession(sq.getSessionId());
                }
            }
        } 
		
    }
	public void addCallMessage(AcCallQueue sq){
        if(Tool.isShowAvActivity(AppContext.getAppContext())){//正在视频界面，AV消息由视频界面处理
            CallBackUpdate update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.avcall.toString());
            if(update!=null) {
                update.responseAVMessage(sq);
            }
        }
    }

    private void syncUserContact(){
        try {
            syncContact = true;
            hasSyncContact = false;
            repeatSynccontactCount++;
            if(repeatSynccontactCount==5){
                repeatSynccontactCount = 0;
                syncContact = false;
                return;
            }
            user.getContacts().getRemote().sync(new Back.Result<List<Contact>>() {
                @Override
                public void onSuccess(List<Contact> userContactList) {
                    AppData.isRefreshContact=true;
                    String currentActivityname =AppContext.getAppContext().getCurrentActivityname();
                    if(currentActivityname.equals("backstage")){//後台

                    }else if(currentActivityname.equals(MainActivity.class.getName())){
                        CallBackUpdate update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.contact.toString());
                        if(update != null){
                            update.updateUI();
                        }
                    }
                    syncContact = false;
                    repeatSynccontactCount =0;
                    if(hasSyncContact){
                        syncUserContact();
                    }
                }

                @Override
                public void onError(int errorCode,String error) {
                    syncUserContact();
                    LogUtil.getInstance().log(TAG, "update:" + errorCode, null);
                }
            });
        }catch (Exception e){
            syncUserContact();
        }

    }
    private void syncUserConversation(){
        try {
            syncConversation = true;
            hasSyncConversation = false;
            repeatSyncconversationCount++;
            if(repeatSyncconversationCount==5){
                repeatSyncconversationCount = 0;
                syncConversation = false;
                return;
            }
            user.getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
                @Override
                public void onSuccess(List<UserConversation> userConversationList) {
                    AppData.isRefreshConversation=true;
                    String currentActivityname =AppContext.getAppContext().getCurrentActivityname();
                    if(currentActivityname.equals("backstage")){//後台

                    }else if(currentActivityname.equals(MainActivity.class.getName())){
                        CallBackUpdate update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.userConversation.toString());
                        if(update != null){
                            update.updateUI();
                        }
                    }
                    syncConversation = false;
                    repeatSyncconversationCount =0;
                    if(hasSyncConversation){
                        syncUserConversation();
                    }
                }

                @Override
                public void onError(int errorCode,String error) {
                    syncUserConversation();
                    LogUtil.getInstance().log(TAG, "update:" + errorCode, null);
                }
            });
        }catch (Exception e){
            syncUserConversation();
        }

    }
}
