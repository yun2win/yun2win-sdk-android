package y2w.service;

import android.content.Context;
import y2w.base.AppData;
import y2w.common.AsyncMultiPartPost;

/**
 * 文件远程访问
 * Created by yangrongfang on 2016/2/29.
 */
public class FileSrv {

    private static FileSrv FileSrv = null;
    public static FileSrv getInstance(){
        if(FileSrv == null){
            FileSrv = new FileSrv();
        }
        return FileSrv;
    }

    /**
     * 文件上传
     * @param context 上下文
     * @param token 访问令牌
     * @param uploadFileURL 上传地址
     * @param filepath 本地文件路径
     * @param remark 备注
     */
    public void uploadMessagesFile(final Context context,String token, String uploadFileURL, String filepath,String remark){
        try {
            AsyncMultiPartPost post = new AsyncMultiPartPost(context,token, uploadFileURL, filepath, remark);
            //将请求加入到全局保存
            AppData.getInstance().addPost(filepath, post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 文件上传
     * @param context 上下文
     * @param token 访问令牌
     * @param uploadFileURL 上传地址
     * @param filepath 本地文件路径
     */
    public void uploadMessagesFile(final Context context,String token, String uploadFileURL, String filepath){
        try {
            AsyncMultiPartPost post = new AsyncMultiPartPost(context,token, uploadFileURL, filepath);
            //将请求加入到全局保存
            AppData.getInstance().addPost(filepath, post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
