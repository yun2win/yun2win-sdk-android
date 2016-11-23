package y2w.model.messages;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import y2w.entities.SessionMemberEntity;
import y2w.model.SessionMember;

/**
 * 消息发送时，转json与json解析
 * Created by yangrongfang on 2016/3/18.
 */
public class MessageCrypto {
    private static MessageCrypto build;
    public static MessageCrypto getInstance(){
        if(build == null){
            build = new MessageCrypto();
        }
        return build;
    }
    /***************带成员的文本消息*************/
    public String encryTextMembers(String text,List<SessionMemberEntity> choosemembers){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("text",text);
            JSONArray jsonArray = new JSONArray();
            if(choosemembers!=null&&choosemembers.size()>0) {
                for(int i = 0;i<choosemembers.size();i++){
                    String reg=".*"+"@"+choosemembers.get(i).getName()+".*";
                    if(text.matches(reg)){
                        JSONObject jsonuser = new JSONObject();
                        jsonuser.put("name",choosemembers.get(i).getName());
                        jsonuser.put("id",choosemembers.get(i).getUserId());
                        jsonArray.put(jsonuser);
                    }
                }
            }
            jsonobject.put("timestamp",System.currentTimeMillis()+"");
            jsonobject.put("users",jsonArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }
    public List<Json> decryUsers(String content){
        Json json = new Json(content);
        return  json.getList("users");
    }
    /***************带任务的文本消息*************/
    public String encryTaskText(String text,String url,String type){
        try {
            JSONObject jsonObject = new JSONObject(text);
            jsonObject.put("url",url);
            jsonObject.put("type",type);
            jsonObject.put("timestamp",System.currentTimeMillis()+"");
            return  jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return text;
    }

    /***************文本消息*************/
    public String encryText(String text){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("text",text);
            jsonobject.put("timestamp",System.currentTimeMillis()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    public String decryText(String content){
        Json json = new Json(content);
        String showmessage = json.getStr(MessageType.Text);
        if(!StringUtil.isEmpty(showmessage)){
            content =  showmessage;
        }
        return content;
    }
    public String decryTimestamp(String content){
        Json json = new Json(content);
        String showmessage = json.getStr("timestamp");
        if(!StringUtil.isEmpty(showmessage)){
            content =  showmessage;
        }else{
            content ="";
        }
        return content;
    }

    public String decryWebUrl(String content){
        Json json = new Json(content);
        String showmessage = json.getStr("url");
        if(!StringUtil.isEmpty(showmessage)){
            content =  showmessage;
        }
        return content;
    }
    public boolean decryMyuserId(String content,String myid){
        Json json = new Json(content);
        String showmessage = json.getStr("users");
      if(!StringUtil.isEmpty(myid)&&!StringUtil.isEmpty(showmessage)){
          if(showmessage.indexOf("\"id\":\""+myid+"\"")>-1){
                return  true;
          }
      }
        return false;
    }

    /***************图片消息*************/
    public String encryImage(String src,String localsrc, String thumbnail,int width,int height,String timestamp){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("src",src);
            jsonobject.put("localsrc",localsrc);
            jsonobject.put("thumbnail",thumbnail);
            jsonobject.put("width",width);
            jsonobject.put("height",height);
            jsonobject.put("timestamp",timestamp+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    /***************语音消息*************/
    public String encryAudio(String src,String localsrc, int second, String name,String timestamp){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("src",src);
            jsonobject.put("localsrc",localsrc);
            jsonobject.put("second",second);
            jsonobject.put("name",name);
            jsonobject.put("timestamp",timestamp+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    /***************小视频消息*************/
    public String encryMovie(String src,String localsrc, String thumbnail,int width,int height,String name,String timestamp){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("src",src);
            jsonobject.put("localsrc",localsrc);
            jsonobject.put("thumbnail",thumbnail);
            jsonobject.put("width",width);
            jsonobject.put("height",height);
            jsonobject.put("name",name);
            jsonobject.put("timestamp",timestamp+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    /***************文件消息*************/
    public String encryFile(String src,String localsrc,String thumbnail,int width,int height,String name,String size,String timestamp){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("src",src);
            jsonobject.put("localsrc",localsrc);
            jsonobject.put("name",name);
            jsonobject.put("size",size);
            jsonobject.put("timestamp",timestamp+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    /***************位置消息*************/
    public String encryLocation(String thumbnail, String localsrc, int width, int height, String latitude, String longitude,String timestamp){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("thumbnail",thumbnail);
            jsonobject.put("localsrc",localsrc);
            jsonobject.put("width",width);
            jsonobject.put("height",height);
            jsonobject.put("latitude",latitude);
            jsonobject.put("longitude",longitude);
            jsonobject.put("timestamp",timestamp+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

}
