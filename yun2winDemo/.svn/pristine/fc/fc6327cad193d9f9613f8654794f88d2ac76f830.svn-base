package y2w.Bridge;

import com.yun2win.utils.LogUtil;

import org.json.JSONObject;

import java.util.List;

import y2w.model.SessionMember;

/**
 * Created by maa2 on 2016/3/11.
 */
public class CmdBuilder {
    private static String TAG = CmdBuilder.class.getSimpleName();
    public static String buildMessage(String sessionId){
        String message = "{\"syncs\":[{\"type\":0},{\"type\":1,\"sessionId\":\"" + sessionId + "\"}]}";
       /* JSONObject object = null;
        try {
            JSONObject object1 = new JSONObject();
            object1.put("type",0);
            JSONObject object2 = new JSONObject();
            object2.put("type",1);
            object2.put("sessionId",sessionId);
            object = new JSONObject();
            object.put("syncs","["+object1.toString() + "," +object2.toString()+"]");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        LogUtil.getInstance().log(TAG, "message:"+message, null);
        return message;
    }

    public static String buildMembersToJson(List<SessionMember> memberList){
        String members = "[";
        for(SessionMember member : memberList){
            members += buildMember(member.getEntity().getUserId())+",";
        }
        members = members.substring(0,members.length() -1) + "]";
        return members;
    }

    public static String buildMembersWithDelToJson(List<SessionMember> memberList){
        String members = "[";
        for(SessionMember member : memberList){
            members += buildMemberWithDel(member.getEntity().getUserId(), member.getEntity().isDelete())+",";
        }
        members = members.substring(0,members.length() -1) + "]";
        return members;
    }

    private static String buildMember(String uid){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("uid",uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    private static String buildMemberWithDel(String uid, boolean isDel){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("uid",uid);
            jsonobject.put("isDel",isDel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }


}
