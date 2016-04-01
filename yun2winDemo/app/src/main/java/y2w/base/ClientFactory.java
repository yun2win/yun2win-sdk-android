package y2w.base;


import y2w.common.Config;

/**
 * Created by maa2 on 2016/1/11.
 */
public class ClientFactory {

    public static APIClient _client;

    public static APIClient getInstance(){
        if(_client==null){
            if(Config.Client_Mock)
                _client = new APIClient();
            else
                _client = new LocalClient();
        }

        return _client;
    }


}
