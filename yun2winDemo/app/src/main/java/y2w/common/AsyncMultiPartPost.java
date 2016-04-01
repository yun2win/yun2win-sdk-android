package y2w.common;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.URLUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.y2w.uikit.utils.StringUtil;

/**
 * 文件上传
 * 
 * 
 */
public class AsyncMultiPartPost extends AsyncTask<HttpResponse, Integer, String> {
	private static final String TAG = AsyncMultiPartPost.class.getSimpleName();
	private String url;
	private long totalSize;
	private String filePath;
	private CallBack mCallBack;
	private List<CallBackMsg> CallBackMsgs=new ArrayList<CallBackMsg>();
	private Context context;
	private String encode = HTTP.UTF_8;
	private String token;

	public AsyncMultiPartPost(Context context,String token, String url, String filePath) {
		super();
		// TODO 处理可空格，其他url特殊字符没处理
		this.url = url.replace(" ", "");
		this.filePath = filePath;
		this.context = context;
		this.token = token;
	}

	@Override
	protected void onPreExecute() {
		if (!URLUtil.isNetworkUrl(url)) {
			throw new IllegalArgumentException("unvalid url for post!");
		}
		if(StringUtil.isEmpty(filePath)){
			throw new IllegalArgumentException("文件不存在");
		}
	}

	@Override
	protected String doInBackground(HttpResponse... arg0) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Authorization", Config.Token_Prefix+token);
		try {
			
			CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(
					 new CustomMultiPartEntity.ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});
			// add  file
			File file = new File(filePath);
			if(!file.exists()){
				return null;
			}
			multipartContent.addPart(file.getName(), new FileBody(file));

			// add other parts
			FormBodyPart formBodyPart = new FormBodyPart("fileName", new StringBody(file.getName(), Charset.forName("UTF-8")));
			multipartContent.addPart(formBodyPart);

			totalSize = multipartContent.getContentLength();

			httpPost.setEntity(multipartContent);
			httpPost.setHeader("Content-MD5", StringUtil.getFileMD5(file));
			InputStream in = null;
			String result = null;
			try {  
	            HttpResponse response = httpClient.execute(httpPost, httpContext);  
	            HttpEntity entity = response.getEntity();  
	            if (entity != null) {  
	                entity = new BufferedHttpEntity(entity);  
	                in = entity.getContent();  
	                byte[] read = new byte[1024];  
	                byte[] all = new byte[0];  
	                int num;  
	                while ((num = in.read(read)) > 0) {  
	                    byte[] temp = new byte[all.length + num];  
	                    System.arraycopy(all, 0, temp, 0, all.length);  
	                    System.arraycopy(read, 0, temp, all.length, num);  
	                    all = temp;  
	                }
	                result = new String(all, "UTF-8");  
	            }  
	        } finally {
				if (in != null) {
					in.close();
				}
	            try{
	            	httpPost.abort();  
	            }catch(Exception e1){
	            	e1.printStackTrace();
	            }
	        }
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (mCallBack != null) {
			mCallBack.update(progress[0]);
		}
	}

	@Override
	protected void onPostExecute(String param) {
		if(CallBackMsgs != null){
			for(CallBackMsg callBackMsg : CallBackMsgs){
				if(callBackMsg != null){
					callBackMsg.msg(param);
				}
			}
		}
	}

	public void setCallBack(CallBack mCallBack) {
		this.mCallBack = mCallBack;
	}

	public void setCallBackMsg(CallBackMsg mCallBackMsg) {

		if(mCallBackMsg != null){
			CallBackMsgs.add(mCallBackMsg);
		}
	}
	
	public interface CallBack {
		public void update(Integer i);
	}

	public interface CallBackMsg {
		public void msg(String param);
	}
	
}
