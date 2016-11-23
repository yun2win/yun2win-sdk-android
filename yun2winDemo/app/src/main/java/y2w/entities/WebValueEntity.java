package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.Json;

import java.io.Serializable;

/**
 * Created by hejie on 2016/4/8.
 */
@DatabaseTable(tableName = "WebValueEntity")
public class WebValueEntity implements Serializable{

    @DatabaseField
    private String key;
    @DatabaseField
    private String value;
    @DatabaseField
    private String myId;
    public WebValueEntity(){}
    public WebValueEntity(String key, String value,String myId){
        setKey(key);
        setValue(value);
        setMyId(myId);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
