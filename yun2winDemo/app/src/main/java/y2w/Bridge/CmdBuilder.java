package y2w.Bridge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import y2w.httpApi.messages.AvCallNew;
import y2w.manage.Users;
import y2w.model.SessionMember;

/**
 * Created by maa2 on 2016/3/11.
 */
public class CmdBuilder {
    private static String TAG = CmdBuilder.class.getSimpleName();
    public static String buildMessage(String sessionId, JSONObject pns){
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
            if(pns !=null){
                object.put("pns",pns);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }
    public static String buildupdateCoversation(){
        JSONObject object = null;
        try {
            JSONObject object1 = new JSONObject();
            object1.put("type",0);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0,object1);
            object = new JSONObject();
            object.put("syncs",jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }
    public static String buildupdateMembers(String sessionId){
        JSONObject object = null;
        try {
            JSONObject object1 = new JSONObject();
            object1.put("type",4);
            object1.put("sessionId",sessionId);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0,object1);
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

    public static String buildMemberWithDel(String uid, boolean isDel){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("uid",uid);
            jsonobject.put("isDel",isDel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    public static String AvCallNew(String type,String mode,String action,String ChannelId,String session,String senderId,String memberIds){
        AvCallNew avCallNew = new AvCallNew();
        AvCallNew.AvItem avItem = avCallNew.getAv();
        avItem.setType(type);
        avItem.setMode(mode);
        avItem.setAction(action);
        avItem.setChannel(ChannelId);
        avItem.setSession(session);
        avItem.setSender(senderId);
        String[] members = memberIds.split(";");
        for(int i = 0;i<members.length;i++){
            avItem.getMembers().add(members[i]);
        }
        if(action.equals("call")){
            AvCallNew.OtherPush otherPush = avCallNew.getPns();
            String pushmsg = Users.getInstance().getCurrentUser().getEntity().getName()+"向您发起一个";
           if(type.equals("group")){
               pushmsg = pushmsg+"群";
           }
            if(mode.equals("AV")){
                pushmsg = pushmsg+"视频聊天";
            }else if(mode.equals("A")){
                pushmsg = pushmsg+"音频频聊天";
            }
            otherPush.setMsg(pushmsg);
            otherPush.getPayload().setAv(avItem);
        }

        return avCallNew.toString();
    }

//    public static String AvCall(String type,String ChannelId,String sessionId,String senderId,String memberIds,String avCallType){
//        String message = "";
//        try {
//
//            String[] ids = memberIds.split(";");
//            JSONArray jsonArray1 = new JSONArray();
//            for(int i = 0;i<ids.length;i++){
//                jsonArray1.put(i,ids[i]);
//            }
//            JSONObject object1 = new JSONObject();
//            object1.put("senderId",senderId);
//            object1.put("receiversIds",jsonArray1);
//            object1.put("avcalltype",avCallType);
//            object1.put("channelId",ChannelId);
//            object1.put("sessionId",sessionId);
//
//            JSONObject object2 = new JSONObject();
//            object2.put("type",type);
//            object2.put("content",object1);
//
//
//            JSONObject object3 = new JSONObject();
//            object3.put("type",0);
//            JSONObject object4 = new JSONObject();
//            object4.put("type",1);
//            object4.put("sessionId",sessionId);
//            JSONArray jsonArray = new JSONArray();
//            jsonArray.put(0,object3);
//            jsonArray.put(1,object4);
//            jsonArray.put(2,object2);
//            JSONObject object = new JSONObject();
//            object.put("syncs",jsonArray);
//            message = object.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return message;
//    }
//
//
//    public static String AvCallRefuse(String type,String ChannelId,String sessionId,String senderId,String memberIds,String avCallType){
//        String message = "";
//        try {
//
//            String[] ids = memberIds.split(";");
//            JSONArray jsonArray1 = new JSONArray();
//            for(int i = 0;i<ids.length;i++){
//                jsonArray1.put(i,ids[i]);
//            }
//            JSONObject object1 = new JSONObject();
//            object1.put("senderId",senderId);
//            object1.put("receiversIds",jsonArray1);
//            object1.put("avcalltype",avCallType);
//            object1.put("channelId",ChannelId);
//            object1.put("sessionId",sessionId);
//
//            JSONObject object2 = new JSONObject();
//            object2.put("type",type);
//            object2.put("content",object1);
//
//
//            JSONObject object3 = new JSONObject();
//            object3.put("type",0);
//            JSONObject object4 = new JSONObject();
//            object4.put("type",1);
//            object4.put("sessionId",sessionId);
//            JSONArray jsonArray = new JSONArray();
//            jsonArray.put(0,object3);
//            jsonArray.put(1,object4);
//            jsonArray.put(2,object2);
//            JSONObject object = new JSONObject();
//            object.put("syncs",jsonArray);
//            message = object.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return message;
//    }

}
