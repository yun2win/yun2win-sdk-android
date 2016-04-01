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


}
