package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.Json;

import java.io.Serializable;

/**
 * Created by hejie on 2016/4/8.
 */
@DatabaseTable(tableName = "NetImageEntity")
public class NetImageEntity implements Serializable{

    @DatabaseField
    private String imageUri;
    @DatabaseField
    private String accesstoken;
    public NetImageEntity(){}
    public NetImageEntity(String imageUri,String accesstoken){
        setImageUri(imageUri);
        setAccesstoken(accesstoken);
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}
