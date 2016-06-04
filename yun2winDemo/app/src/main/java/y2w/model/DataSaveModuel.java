package y2w.model;

import android.provider.Telephony;

import java.util.List;

import y2w.manage.UserConversations;

/**
 * Created by Administrator on 2016/4/25.
 */
public class DataSaveModuel {
    public static DataSaveModuel instance = null;
    public static DataSaveModuel getInstance(){
        if(instance==null){
            instance = new DataSaveModuel();
        }
        return instance;
    }
    public List<UserSession> listgroups;
    public List<UserConversation> conversations;
}
