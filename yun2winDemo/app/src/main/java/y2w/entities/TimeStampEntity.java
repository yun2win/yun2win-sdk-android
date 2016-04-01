package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 同步时间戳表
 * Created by yangrongfang on 2016/3/8.
 */
@DatabaseTable(tableName = "TimeStampEntity")
public class TimeStampEntity implements Serializable {

    //时间
    @DatabaseField
    private String time;
    //类型
    @DatabaseField
    private String type;
    //备注
    @DatabaseField
    private String remark;
    //此条数据所有者
    @DatabaseField
    private String myId;

    public static TimeStampEntity parse(String time,String type,String remark){
        TimeStampEntity entity = new TimeStampEntity();
        entity.setTime(time);
        entity.setType(type);
        entity.setRemark(remark);
        return entity;
    }

    public static enum TimeStampType{
        contact,userConversation,userSession,
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

}
