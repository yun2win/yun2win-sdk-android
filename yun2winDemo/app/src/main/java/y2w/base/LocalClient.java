package y2w.base;

/**
 * Created by maa2 on 2016/1/4.
 */
public class LocalClient extends APIClient{

    private static LocalClient localClient;
    public static LocalClient getInstance(){
        if(localClient == null){
            localClient = new LocalClient();
        }
        return localClient;
    }

}
