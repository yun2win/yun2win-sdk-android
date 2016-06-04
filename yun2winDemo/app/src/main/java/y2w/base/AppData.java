package y2w.base;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import y2w.common.AsyncMultiPartPost;
import y2w.common.CallBackUpdate;
import y2w.ui.widget.storeage.files.FileItem;

/**
 * Created by maa2 on 2016/2/29.
 */
public class AppData {
    private static AppData appData = null;
    public static AppData getInstance(){
        if(appData == null){
            appData = new AppData();
        }
        return appData;
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
}
