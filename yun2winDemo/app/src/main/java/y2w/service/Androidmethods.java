package y2w.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONObject;
import com.y2w.uikit.customcontrols.view.ObservableScrollWebView;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import y2w.entities.WebValueEntity;
import y2w.manage.CurrentUser;
import y2w.manage.Users;

/**
 * Created by maa46 on 2016/9/12.
 */

@TargetApi(Build.VERSION_CODES.KITKAT)
public class Androidmethods {
    ObservableScrollWebView mWebView;
    MakeWebmethods makeWebmothods;
    Handler handlermsgsend;
    private String myId = Users.getInstance().getCurrentUser().getEntity().getId();
    public Androidmethods(Handler handlermsgsend,MakeWebmethods makeWebmothods, ObservableScrollWebView mWebView) {
        this.handlermsgsend = handlermsgsend;
        this.mWebView = mWebView;
        this.makeWebmothods = makeWebmothods;
        makeWebmothods.setAndroidmethods(this);
    }
    @JavascriptInterface
    public void postMessage(String receiveMsg,String data){
       try {
           if (!StringUtil.isEmpty(receiveMsg)) {
              Json json = new Json(receiveMsg);
               String type = json.getStr("type");
               String index  = json.getStr("index");
               if ("callback".equals(type)){
                   Back.Result<String> backresult = callbacks.get(index);
                  if(backresult!=null) {
                      String error = json.getStr("error");
                      String result = json.getStr("result");
                      if (StringUtil.isEmpty(error)) {
                          backresult.onSuccess(result);
                      } else {
                          backresult.onError(100, error);
                      }
                      callbacks.remove(index);
                  }
               }else{
                   String action = json.getStr("action");
                   List<Json> strparams =  json.getList("params");
                   if(!StringUtil.isEmpty(action)){
                       if(action.equals("_genAllMethod")){
                           JSONObject jsonObject = new JSONObject();
                           jsonObject.put("index",index);
                           jsonObject.put("type","callback");
                           jsonObject.put("error","");
                           jsonObject.put("result",getAllmethod());
                           sendwebMessage(jsonObject.toJSONString(),null,"");
                       }else if(action.equals("changeTitle")){
                           if(strparams.size()>0) {
                               changeTitle(strparams.get(0).toStr());
                           }
                           JSONObject jsonObject = new JSONObject();
                           jsonObject.put("index",index);
                           jsonObject.put("type","callback");
                           jsonObject.put("error","");
                           jsonObject.put("result","success");
                           sendwebMessage(jsonObject.toJSONString(),null,"");
                       }else if(action.equals("changeToolbarColor")){
                           if(strparams.size()>=3) {
                               changeToolbarColor(strparams.get(0).toStr(),strparams.get(1).toStr(),strparams.get(2).toStr());
                           }
                           JSONObject jsonObject = new JSONObject();
                           jsonObject.put("index",index);
                           jsonObject.put("type","callback");
                           jsonObject.put("error","");
                           jsonObject.put("result","success");
                           sendwebMessage(jsonObject.toJSONString(),null,"");
                       }else if(action.equals("setMenus")){
                           List<Json>  paramJsonList =  strparams.get(0).toList();
                           if(strparams.size()>=0) {
                               setMenus(paramJsonList);
                           }else{
                               setMenus(null);
                           }
                           JSONObject jsonObject = new JSONObject();
                           jsonObject.put("index",index);
                           jsonObject.put("type","callback");
                           jsonObject.put("error","");
                           jsonObject.put("result","success");
                           sendwebMessage(jsonObject.toJSONString(),null,"");
                       }else if(action.equals("getCurrentUser")){
                           JSONObject jsonObject = new JSONObject();
                           jsonObject.put("index",index);
                           jsonObject.put("type","callback");
                           jsonObject.put("error","");
                           jsonObject.put("result",getCurrentUser());
                           sendwebMessage(jsonObject.toJSONString(),null,"");
                       }else if(action.equals("didShow")){
                           didShow();
                           JSONObject jsonObject = new JSONObject();
                           jsonObject.put("index",index);
                           jsonObject.put("type","callback");
                           jsonObject.put("error","");
                           jsonObject.put("result","success");
                           sendwebMessage(jsonObject.toJSONString(),null,"");
                       }else if(action.equals("chooseFile")){
                           chooseFile(index);
                       }else if(action.equals("getCamera")){
                           getCamera(index);
                       }else if(action.equals("getLocation")){
                           getLocation(index);
                       }else if(action.equals("chooseDate")){
                           if(strparams.size()>=2) {
                               chooseDate(strparams.get(0).toBool(),strparams.get(1).toStr(),index);
                           }
                       }else if(action.equals("setData")){
                           if(strparams.size()>=2) {
                               setData(strparams.get(0).toStr(),strparams.get(1).toStr(),index);
                               JSONObject jsonObject = new JSONObject();
                               jsonObject.put("index",index);
                               jsonObject.put("type","callback");
                               jsonObject.put("error","");
                               jsonObject.put("result","success");
                               sendwebMessage(jsonObject.toJSONString(),null,"");
                           }
                       }else if(action.equals("getData")){
                           if(strparams.size()>=1) {
                               getData(strparams.get(0).toStr(),index);
                           }
                       }else if(action.equals("syncData")){
                           if(strparams.size()>=2) {
                               syncData(strparams.get(0).toList(),strparams.get(1).toInt());
                           }
                           JSONObject jsonObject = new JSONObject();
                           jsonObject.put("index",index);
                           jsonObject.put("type","callback");
                           jsonObject.put("error","");
                           jsonObject.put("result","success");
                           sendwebMessage(jsonObject.toJSONString(),null,"");

                       }else if(action.equals("openNew")){
                           if(strparams.size()>0) {
                               openNew(strparams.get(0).toStr());
                               JSONObject jsonObject = new JSONObject();
                               jsonObject.put("index",index);
                               jsonObject.put("type","callback");
                               jsonObject.put("error","");
                               jsonObject.put("result","success");
                               sendwebMessage(jsonObject.toJSONString(),null,"");
                           }
                       }else if(action.equals("chooseImage")){
                           chooseImage(index);
                       }else if(action.equals("openImage")){
                           if(strparams.size()>0) {
                               int currentpage = strparams.get(0).getInt("index");
                               ArrayList<String> imgurls = new ArrayList<String>();
                               List<Json> imglist = strparams.get(0).getList("list");
                               if(imglist!=null&&imglist.size()>0){
                                   for(int i =0;i<imglist.size();i++){
                                       String imgurl = imglist.get(i).getStr("url");
                                       if(imgurl.startsWith("http")){
                                           imgurls.add(imgurl);
                                       }
                                   }
                                   if(imgurls.size()>0)
                                   openImage(currentpage,imgurls,index);
                               }
                               JSONObject jsonObject = new JSONObject();
                               jsonObject.put("index",index);
                               jsonObject.put("type","callback");
                               jsonObject.put("error","");
                               jsonObject.put("result","success");
                               sendwebMessage(jsonObject.toJSONString(),null,"");
                           }
                       }else if(action.equals("changeUrl")){
                           if(strparams.size()>0) {
                               changeUrl(strparams.get(0).toStr(),index);
                               JSONObject jsonObject = new JSONObject();
                               jsonObject.put("index",index);
                               jsonObject.put("type","callback");
                               jsonObject.put("error","");
                               jsonObject.put("result","success");
                               sendwebMessage(jsonObject.toJSONString(),null,"");
                           }
                       }else if(action.equals("selectContact")) {
                           if (strparams.size() > 0){
                               selectContact(strparams.get(0),index);
                           }
                       }else if(action.equals("select")) {
                           if (strparams.size() > 0){
                               select(strparams.get(0),index);
                             }
                       }else if(action.equals("downloadFile")) {
                           if (strparams.size() > 0){
                               if(strparams.size()>=3) {
                                   downloadFile(strparams.get(0).toStr(),strparams.get(1).toStr(),strparams.get(2).toStr());
                                   JSONObject jsonObject = new JSONObject();
                                   jsonObject.put("index",index);
                                   jsonObject.put("type","callback");
                                   jsonObject.put("error","");
                                   jsonObject.put("result","success");
                                   sendwebMessage(jsonObject.toJSONString(),null,"");
                               }
                           }
                       }else if(action.equals("back")) {
                           back(index);
                           JSONObject jsonObject = new JSONObject();
                           jsonObject.put("index",index);
                           jsonObject.put("type","callback");
                           jsonObject.put("error","");
                           jsonObject.put("result","success");
                           sendwebMessage(jsonObject.toJSONString(),null,"");
                       }else if(action.equals("webLog")){
                           if(strparams.size()>=1) {
                               //webLog(strparams.get(0).toStr());
                           }
                       }else if(action.equals("talkTo")){
                           if(strparams.size()>=1) {
                               talkTo(strparams.get(0).toStr(),index);
                           }
                       }else if(action.equals("confirm")){
                           if(strparams.size()>=2) {
                               confirm(strparams.get(0).toStr(),strparams.get(1).toStr(),index);
                           }
                       }else if(action.equals("upOtherCoversation")){
                           if(strparams.size()>=2) {
                               upOtherCoversation(strparams.get(0).toList(),strparams.get(1).toInt(),index);
                               JSONObject jsonObject = new JSONObject();
                               jsonObject.put("index",index);
                               jsonObject.put("type","callback");
                               jsonObject.put("error","");
                               jsonObject.put("result","success");
                               sendwebMessage(jsonObject.toJSONString(),null,"");
                           }
                       }else if(action.equals("callPhoneNumber")){
                           if(strparams.size()>=2) {
                               callPhoneNumber(strparams.get(0).toStr(),strparams.get(1).toBool(),index);
                               JSONObject jsonObject = new JSONObject();
                               jsonObject.put("index",index);
                               jsonObject.put("type","callback");
                               jsonObject.put("error","");
                               jsonObject.put("result","success");
                               sendwebMessage(jsonObject.toJSONString(),null,"");
                           }
                       }
                   }
               }

           }
       }catch (Exception e){
           Log.i("hejie",e.getMessage());
       }
    }
    private Map<String, Back.Result> callbacks = new ArrayMap<String, Back.Result>();
   public void sendwebMessage(String sendmsg, Back.Result<String> result, String key){
      if(result!=null){
          callbacks.put(key,result);
      }
       Message msg = new Message();
       msg.what =1;
       msg.obj = sendmsg;
       handlermsgsend.sendMessage(msg);
   }
/**********************************************html调用本地方法***********************************************************/
   private String[] getAllmethod(){
       String allMethod[] ={"didShow","changeTitle","changeToolbarColor","setMenus","getData","setData","getCurrentUser",
               "chooseFile","getCamera","getLocation","chooseDate","syncData","openNew","chooseImage","openImage","changeUrl","selectContact","select","downloadFile","back","talkTo","confirm","upOtherCoversation","callPhoneNumber","webLog"};
       return allMethod;
    }
    //显示本页
    public void didShow(){
        makeWebmothods.didShow();
    }
    //设置标题
   public void changeTitle(String title){
       makeWebmothods.setactionbarTitle(title);
   }
    //改变toolbar颜色
    public void changeToolbarColor(String bgColor,String moveColor,String textColor){
        makeWebmothods.changeToolbarColor(bgColor,moveColor,textColor);
    }
    //设置菜单
    public void setMenus(List<Json> jsonmenus){
        makeWebmothods.setMenus(jsonmenus);
    }
    //获取当前用户信息
    public JSONObject getCurrentUser(){
        CurrentUser currentUser = Users.getInstance().getCurrentUser();
        JSONObject userinfo = new JSONObject();
        userinfo.put("account",currentUser.getEntity().getAccount());
        userinfo.put("avatarUrl",currentUser.getEntity().getAvatarUrl());
        userinfo.put("id",currentUser.getEntity().getId());
        userinfo.put("name",currentUser.getEntity().getName());
         return  userinfo;
    }
    //选择本地文件
    public void chooseFile(String index){
        makeWebmothods.openfile(index);
    }
    //获取拍照
    public void getCamera(String index){
        makeWebmothods.openCamera(index);
    }
    //获取当前位置
    public void getLocation(String index){
        makeWebmothods.getLocation(index);
    }
    //选择日期
    public void chooseDate(boolean time,String defaultTime,String index){
        makeWebmothods.chooseDate(time,defaultTime,index);
    }
    //调用本地存储存数据
    public void setData(String key,String value,String index){
        WebValueEntity webValue = new WebValueEntity(key,value,myId);
        Users.getInstance().getCurrentUser().getWebValues().updateWebValues(webValue);
    }
    //调用本地存储取数据
    public void getData(String key,String index){
        WebValueEntity webValueEntity = Users.getInstance().getCurrentUser().getWebValues().getWebValues(key,myId);
         String result ="";
         if(webValueEntity!=null){
             result = webValueEntity.getValue();
         }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("index",index);
        jsonObject.put("type","callback");
        jsonObject.put("error","");
        jsonObject.put("result",result);
        sendwebMessage(jsonObject.toJSONString(),null,"");
    }
    //同步联系人 会话等
    public void syncData(List<Json> parms, int timeout){
        makeWebmothods.syncData(parms,timeout);

    }
    //同一个webview打开新页面
    public void openNew(String url){
        makeWebmothods.openNew(url);
    }
    //选择图片
    public void chooseImage(String index){
        makeWebmothods.chooseImage(index);
    }
    //打开图片
    public void openImage(int currentpage,ArrayList<String> imgurls,String index){
        makeWebmothods.openimg(currentpage,imgurls);
    }
    //新页面打开新的webview
    public void changeUrl(String url,String index){
        makeWebmothods.openUrl(url);
    }
    //选择联系人
    public void selectContact(Json json, String index){//联系人选择
        makeWebmothods.selectContact(json,index);
    }
    //选择有数据源的人员
    public void select(Json json, String index){//人员选择
        makeWebmothods.selectPerson(json,index);
    }
    //下载文件
    public void downloadFile(String url,String name,String ext){
        makeWebmothods.downloadFile(url,name,ext);
    }
   //webview内回退
   public void back(String index){
       makeWebmothods.back();
   }
   //打印网页日志
    public void webLog(String log){
        makeWebmothods.webLog(log);
    }
   //p2p打开交流
   public void talkTo(String userId,String index){
       makeWebmothods.talkTo(userId,index);
   }
   //弹窗选择
    public void confirm(String title,String context,String index){
        makeWebmothods.confirm(title,context,index);
    }
    //更新对方的用户会话
    public void upOtherCoversation(List<Json> userIds, int timeout, String index){
        makeWebmothods.upOtherCoversation(userIds,timeout,index);
    }
    //拨打电话
    public void callPhoneNumber(String phonenum,boolean needCallback,String index){
        makeWebmothods.callPhoneNumber(phonenum,needCallback);
    }

}
