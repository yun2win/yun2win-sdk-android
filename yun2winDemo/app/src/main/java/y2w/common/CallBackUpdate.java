package y2w.common;

import android.os.Handler;
import android.os.Message;

/**
 * ui界面通知类
 * Created by maa46 on 2016/3/10.
 */
public class CallBackUpdate {
    private Handler handler;
    private String  id;

    public CallBackUpdate(Handler handler){
        this.handler = handler;
    }
    public CallBackUpdate(Handler handler,String id){
        this.handler = handler;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void updateUI(){
        Message msg = new Message();
        msg.what = 1;
        msg.obj = id;
        handler.sendMessage(msg);
    }
    public void addDateUI(Object o){
        Message msg = new Message();
        msg.what = 2;
        msg.obj = o;
        handler.sendMessage(msg);
    }
    public void responseAVMessage(Object o){
        Message msg = new Message();
        msg.what = 3;
        msg.obj = o;
        handler.sendMessage(msg);
    }
    public void syncDate(){
        Message msg = new Message();
        msg.what = 4;
        handler.sendMessage(msg);
    }
    public void callReject(Object o){
        Message msg = new Message();
        msg.what = 5;
        msg.obj = o;
        handler.sendMessage(msg);
    }
    public void callBusy(Object o){
        Message msg = new Message();
        msg.what = 6;
        msg.obj = o;
        handler.sendMessage(msg);
    }
    public void callCancel(Object o){
        Message msg = new Message();
        msg.what = 7;
        msg.obj = o;
        handler.sendMessage(msg);
    }
	public void SyncSession(Object o){
        Message msg = new Message();
        msg.what = 9;
        msg.obj = o;
        handler.sendMessage(msg);
    }
    public static enum updateType{
        userConversation,contact,chatting,avcall,
    }

}
