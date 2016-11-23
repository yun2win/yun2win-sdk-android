package y2w.base;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import taobe.tec.jcc.JChineseConvertor;
import y2w.common.AsyncMultiPartPost;
import y2w.common.CallBackUpdate;
import y2w.db.LooperExecutorDb;
import y2w.model.NewDataModel;
import y2w.ui.widget.storeage.files.FileItem;

/**
 * Created by maa2 on 2016/2/29.
 */
public class AppData {
    private static AppData appData = null;
    private JChineseConvertor jChineseConvertor;
    private boolean isactivityrun = false;
    public static AppData getInstance(){
        if(appData == null){
            appData = new AppData();
        }
        return appData;
    }

    public boolean isactivityrun() {
        return isactivityrun;
    }

    public void setIsactivityrun(boolean isactivityrun) {
        this.isactivityrun = isactivityrun;
    }

    /***图片消息，图片上传队列**/
    private HashMap<String, AsyncMultiPartPost> messagePosts = new HashMap<String, AsyncMultiPartPost>();

    public void addPost(String id, AsyncMultiPartPost post) {
        removePost(id);
        messagePosts.put(id, post);
    }

    public void removePost(String id) {
        if (messagePosts.containsKey(id))
            messagePosts.remove(id);
    }

    public AsyncMultiPartPost getPost(String id) {
        if (messagePosts.containsKey(id))
            return messagePosts.get(id);
        else
            return null;
    }

    /**
     * uiHnadler注册刷新列表
     */
    private HashMap<String, CallBackUpdate> updateHashMap;


    public HashMap<String, CallBackUpdate> getUpdateHashMap(){
        if(updateHashMap == null){
            updateHashMap = new HashMap<String, CallBackUpdate>();
        }
        return updateHashMap;
    }

    /**
     * 获取主界面Activity
     */
    private Activity mActivity;

    public Activity getMainActivity() {
        return mActivity;
    }

    public void setMainActivity(Activity activity) {
        this.mActivity = activity;
    }


    private ClipboardManager clipboardManager;

    public ClipboardManager getClipboardManager(Context context) {
        if (null == clipboardManager) {
            clipboardManager = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
        }
        return clipboardManager;
    }

    private static List<FileItem> fileItems;
    public List<FileItem> getFileItems() {
        if (fileItems == null) {
            fileItems = new ArrayList<FileItem>();
        }
        return fileItems;
    }
     private LooperExecutorDb looperExecutorDb;
    public LooperExecutorDb getLooperExecutorDb(){
         if(looperExecutorDb==null){
             looperExecutorDb = new LooperExecutorDb();
             looperExecutorDb.requestStart();
         }
        return  looperExecutorDb;
    }

    public static boolean isRefreshConversation;
    public static boolean isRefreshContact;
    public String getsampchina(String str){
        if(jChineseConvertor==null){
            try {
                jChineseConvertor = JChineseConvertor.getInstance();
            } catch (IOException e) {
                return str;
            }
        }
        return jChineseConvertor.t2s(str);
    }
    List<NewDataModel> moreResults =new ArrayList<NewDataModel>();
    public List<NewDataModel> getSearchResults(){
        return moreResults;
    }

    List<Activity> webViewActivitys = new ArrayList<Activity>();
    public List<Activity> getWebViewActivitys(){
        return webViewActivitys;
    }
    boolean isshowWebViewClose = false;
    public boolean getShowWebViewClose(){
        return  isshowWebViewClose;
    }
    public void setShowWebViewClose(boolean isshowWebViewClose){
        this.isshowWebViewClose = isshowWebViewClose;
    }
    List<Activity> ChooseSessionActivitys = new ArrayList<Activity>();
    public List<Activity> getChooseSessionActivitys(){
        return ChooseSessionActivitys;
    }

    String logs="";
    public void setLogDate(String log){
        logs = logs+"\n" +log;
    }
   public String getLogDate(){
     return logs;
   }

}
