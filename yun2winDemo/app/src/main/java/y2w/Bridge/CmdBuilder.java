package y2w.Bridge;

import com.yun2win.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import y2w.model.SessionMember;

/**
 * Created by maa2 on 2016/3/11.
 */
public class CmdBuilder {
    private static String TAG = CmdBuilder.class.getSimpleName();
    public static String buildMessage(String sessionId){
       JSONObject object = null;
        try {
            JSONObject object1 = new JSONObject();
            object1.put("type",0);
            JSONObject object2 = new JSONObject();
            object2.put("type",1);
            object2.put("sessionId",sessionId);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0,object1);
            jsonArray.put(1,object2);
            object = new JSONObject();
            object.put("syncs",jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
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

    public static String AvCall(String type,String ChannelId,String sessionId,String senderId,String memberIds,String avCallType){
        String message = "";
        try {

            String[] ids = memberIds.split(";");
            JSONArray jsonArray1 = new JSONArray();
            for(int i = 0;i<ids.length;i++){
                jsonArray1.put(i,ids[i]);
            }
            JSONObject object1 = new JSONObject();
            object1.put("senderId",senderId);
            object1.put("receiversIds",jsonArray1);
            object1.put("avcalltype",avCallType);
            object1.put("channelId",ChannelId);
            object1.put("sessionId",sessionId);

            JSONObject object2 = new JSONObject();
            object2.put("type",type);
            object2.put("content",object1);


            JSONObject object3 = new JSONObject();
            object3.put("type",0);
            JSONObject object4 = new JSONObject();
            object4.put("type",1);
            object4.put("sessionId",sessionId);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0,object3);
            jsonArray.put(1,object4);
            jsonArray.put(2,object2);
            JSONObject object = new JSONObject();
            object.put("syncs",jsonArray);
            message = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }


}
