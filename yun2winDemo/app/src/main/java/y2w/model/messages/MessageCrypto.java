package y2w.model.messages;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    /***************文本消息*************/
    public String encryText(String text){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("text",text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    public String decryText(String content){
        Json json = new Json(content);
        if(!StringUtil.isEmpty(json.getStr(MessageType.Text))){
            content =  json.getStr(MessageType.Text);
        }
        return content;
    }
    /***************图片消息*************/
    public String encryImage(String src, String thumbnail,int width,int height){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("src",src);
            jsonobject.put("thumbnail",thumbnail);
            jsonobject.put("width",width);
            jsonobject.put("height",height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    /***************语音消息*************/
    public String encryAudio(String src, int second, String name){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("src",src);
            jsonobject.put("second",second);
            jsonobject.put("name",name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    /***************小视频消息*************/
    public String encryMovie(String src, String thumbnail,int width,int height,String name){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("src",src);
            jsonobject.put("thumbnail",thumbnail);
            jsonobject.put("width",width);
            jsonobject.put("height",height);
            jsonobject.put("name",name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    /***************文件消息*************/
    public String encryFile(String src,String thumbnail,int width,int height,String name,String size){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("src",src);
            jsonobject.put("name",name);
            jsonobject.put("size",size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

    /***************位置消息*************/
    public String encryLocation(String thumbnail,int width,int height,String latitude,String longitude){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("thumbnail",thumbnail);
            jsonobject.put("width",width);
            jsonobject.put("height",height);
            jsonobject.put("latitude",latitude);
            jsonobject.put("longitude",longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject.toString();
    }

}
