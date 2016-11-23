package y2w.httpApi.request;

/**
 * Created by SongJie on 09/12 0012.
 */
public class CreateChannel {
    private String userId;
    private String deviceType;

    public CreateChannel(String userId,String deviceType){
        this.userId = userId;
        this.deviceType = deviceType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "userId=" + userId + "&" +
                "deviceType=" + deviceType;
    }
}
