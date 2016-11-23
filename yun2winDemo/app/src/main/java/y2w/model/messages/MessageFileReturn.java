package y2w.model.messages;

import com.yun2win.utils.Json;

/**
 * Created by yangrongfang on 2016/4/5.
 */
public class MessageFileReturn {
    String id;
    String fileName;
    boolean isDelete;
    String path;
    String md5;
    String updateAt;
    String createAt;

    public static MessageFileReturn parse(Json json){
        MessageFileReturn image = new MessageFileReturn();
        image.setId(json.getStr("id"));
        image.setFileName(json.getStr("fileName"));
        image.setIsDelete(json.getBool("isDelete"));
        image.setPath(json.getStr("path"));
        image.setMd5(json.getStr("md5"));
        image.setUpdateAt(json.getStr("updatedAt"));
        image.setCreateAt(json.getStr("createdAt"));
        return  image;
    }

    public static String getFileUrl(String id){
       return "attachments/"+id+"/content";
    }
    public static String getMD5FileUrl(String id,String md5){
        return "attachments/"+id+"/"+md5;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
