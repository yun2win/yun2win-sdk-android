package y2w.common;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.y2w.uikit.utils.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import y2w.model.TimeStamp;

/**
 * Created by yangrongfang on 2016/4/7.
 */
public class AsyncMultiPartGet extends AsyncTask<Void, Integer, String> {
    private String token;
    private String url;
    private int fileSize;
    private int downLoadFileSize;
    private String fileName,filePath;
    private CallBack mCallBack;
    private CallBackMsg mCallBackMsg;
    public AsyncMultiPartGet(String token,String url,String filePath,String fileName){
        this.token = token;
        this.url = url;
        this.filePath = filePath;
        this.fileName = fileName;
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mCallBack != null)
                mCallBack.update(msg.arg1);//更新进度
        }
    };
    @Override
    protected String doInBackground(Void... params) {
        //获取文件名
        URL myURL = null;
        try {
            myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.addRequestProperty("Authorization", Config.Token_Prefix + token);
            conn.connect();
            InputStream is = conn.getInputStream();
            this.fileSize = conn.getContentLength();//根据响应获取文件大小
            if (this.fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            File file1 = new File(filePath);
            File file2 = new File(filePath+fileName);
            if(!file1.exists()){
                file1.mkdirs();
            }
            if(!file2.exists()){
                file2.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePath+fileName);
            //把数据存入路径+文件名
            byte buf[] = new byte[1024];
            downLoadFileSize = 0;
            do{
                //循环读取
                int numRead = is.read(buf);
                if (numRead == -1)
                {
                    break;
                }
                fos.write(buf, 0, numRead);
                downLoadFileSize += numRead;
                int result = downLoadFileSize * 100 / fileSize;
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = result;
                handler.sendMessage(msg);
            } while (true);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "success";
    }

    @Override
    protected void onPostExecute(String s) {
        if(mCallBackMsg != null)
        mCallBackMsg.msg(s);//通知下载完成
    }

    @Override
    protected void onPreExecute() {
        if (!URLUtil.isNetworkUrl(url)) {
            throw new IllegalArgumentException("unValid url for get!");
        }
        if(StringUtil.isEmpty(filePath)){
            throw new IllegalArgumentException("路径不存在");
        }
    }

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public void setCallBackMsg(CallBackMsg mCallBackMsg) {
        this.mCallBackMsg = mCallBackMsg;
    }

    public interface CallBack {
        public void update(Integer i);
    }

    public interface CallBackMsg {
        public void msg(String result);
    }

}
