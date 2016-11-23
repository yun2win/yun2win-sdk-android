package y2w.common;


import com.y2w.uikit.utils.StringUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Created by yangrongfang on 2016/1/20.
 */
public class HttpUtil {

    private static final String charset = "utf-8";

    public static String post(String url,Map<String,String> map) throws IOException {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;

        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
        }
        if(list.size() > 0){
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
            httpPost.setEntity(entity);
        }
        HttpResponse response = httpClient.execute(httpPost);
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity, charset);
            }
        }

        return result;

    }

    public static String post(String token,String url,Map<String,String> map) throws IOException {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(url);
        httpPost.addHeader("Authorization", Config.Token_Prefix+token);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
        }
        if(list.size() > 0){
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
            httpPost.setEntity(entity);
        }
        HttpResponse response = httpClient.execute(httpPost);
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity, charset);
            }
        }
        return result;
    }

    public static String get(String token,String url,Map<String,String> map) throws IOException {
        HttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        httpClient = new DefaultHttpClient();

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = map.entrySet().iterator();
        String paramet="";
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            if(paramet.equals("")){
                paramet = paramet +"?";
            }else{
                paramet = paramet +"&";
            }
            paramet = paramet+elem.getKey()+"="+elem.getValue();
        }
        if(paramet.length()>0){
            url = url+ paramet;
        }

        httpGet = new HttpGet(url);
        if(!StringUtil.isEmpty(token)) {
            httpGet.addHeader("Authorization", Config.Token_Prefix + token);
        }

        HttpResponse response = httpClient.execute(httpGet);
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity, charset);
            }
        }

        return result;

    }

    public static String get(String token,String updateAt,String url,Map<String,String> map) {
        HttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        try{
            httpClient = new DefaultHttpClient();

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            String paramet="";
            while(iterator.hasNext()){
                Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
                if(paramet.equals("")){
                    paramet = paramet +"?";
                }else{
                    paramet = paramet +"&";
                }
                paramet = paramet+elem.getKey()+"="+elem.getValue();
            }
            if(paramet.length()>0){
                url = url+ paramet;
            }

            httpGet = new HttpGet(url);
            httpGet.addHeader("Authorization", Config.Token_Prefix+token);
            httpGet.addHeader("Client-Sync-Time",updateAt);

            HttpResponse response = httpClient.execute(httpGet);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public static String delete(String token,String url,Map<String,String> map) throws IOException {
        HttpClient httpClient = null;
        HttpDelete httpDelete = null;
        String result = null;
        httpClient = new DefaultHttpClient();

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = map.entrySet().iterator();
        String paramet="";
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            if(paramet.equals("")){
                paramet = paramet +"?";
            }else{
                paramet = paramet +"&";
            }
            paramet = paramet+elem.getKey()+"="+elem.getValue();
        }
        if(paramet.length()>0){
            url = url+ paramet;
        }

        httpDelete = new HttpDelete(url);
        httpDelete.addHeader("Authorization", Config.Token_Prefix+token);

        HttpResponse response = httpClient.execute(httpDelete);
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity, charset);
            }
        }
        return result;

    }

    public static String put(String token,String url,Map<String,String> map) throws IOException {
        HttpClient httpClient = null;
        HttpPut httpPut = null;
        String result = null;
        httpClient = new DefaultHttpClient();
        httpPut = new HttpPut(url);
        httpPut.addHeader("Authorization", Config.Token_Prefix+token);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
        }
        if(list.size() > 0){
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
            httpPut.setEntity(entity);
        }
        HttpResponse response = httpClient.execute(httpPut);
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity, charset);
            }
        }
        return result;

    }


    /**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     *
     * @param url Service net address
     * @param params text content
     * @param files pictures
     * @return String result of Service response
     * @throws IOException
     */
    public static String post(String url, Map<String, String> params, Map<String, File> files)
            throws IOException {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";


        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(10 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);


        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }


        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());
        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getValue().getName() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());


                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }


                is.close();
                outStream.write(LINEND.getBytes());
            }


        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
        // 得到响应码
        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        StringBuilder sb2 = new StringBuilder();
        if (res == 200) {
            int ch;
            while ((ch = in.read()) != -1) {
                sb2.append((char) ch);
            }
        }
        outStream.close();
        conn.disconnect();
        return sb2.toString();
    }
}
