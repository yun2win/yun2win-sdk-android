package y2w.service;

/**
 * 回调
 * Created by yangrongfang on 2016/1/30.
 */
public class Back {

    //成功不返回结果
    public abstract static class Callback{
        public  abstract void onSuccess();
        public  abstract void onError(int code,String error);
    }
    //成功返回结果
    public abstract static class Result<T> extends Object{
        public  abstract void onSuccess(T t);
        public  abstract void onError(int code,String error);
    }

}
