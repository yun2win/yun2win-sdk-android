package y2w.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;
import com.yun2win.imlib.SendReturnCode;
import com.yun2win.utils.Json;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.base.Urls;
import y2w.common.AsyncMultiPartGet;
import y2w.common.AsyncMultiPartPost;
import y2w.common.CallBackUpdate;
import y2w.common.ChooseDateDialog;
import y2w.common.Config;
import y2w.common.Constants;
import y2w.common.FileUtil;
import y2w.common.IntentChecker;
import y2w.common.StorageManager;
import y2w.db.TimeStampDb;
import y2w.entities.TimeStampEntity;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.Session;
import y2w.model.User;
import y2w.model.UserConversation;
import y2w.model.messages.MessageFileReturn;
import y2w.ui.activity.ChatActivity;
import y2w.ui.activity.MoreImageBrowseActivity;
import y2w.ui.activity.SelectContactActivity;
import y2w.ui.activity.SelectSoucePersonActivity;
import y2w.ui.activity.StrongWebViewActivity;

/**
 * Created by maa46 on 2016/9/12.
 */

public class MakeWebmethods {
    Handler handlerui;
    Context context;
    Activity activity;
    Androidmethods androidmethods;
    Handler timeOuthandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what ==1){
                List<Json> userIds = (List<Json>) msg.obj;
                if(userIds!=null&&userIds.size()>0){
                    for(int i =0;i<userIds.size();i++){
                        String otherUserId = userIds.get(i).toStr();
                        if(otherUserId!=null&&!otherUserId.equals(myId)){
                            Users.getInstance().getCurrentUser().getImBridges().upOtherCoversation(otherUserId,new IMClient.SendCallback() {
                                @Override
                                public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                                    switch (code) {
                                        case SendReturnCode.SRC_SUCCESS:
                                            break;
                                        case SendReturnCode.SRC_CMD_INVALID:
                                            break;
                                        case SendReturnCode.SRC_SESSION_INVALID:
                                            break;
                                        case SendReturnCode.SRC_SESSION_ID_INVALID:
                                            break;
                                        case SendReturnCode.SRC_SESSION_MTS_INVALID:
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            });
                        }
                    }
                }

            }else if(msg.what==2){
                List<Json> parms = (List<Json>) msg.obj;
                for(int i =0;i<parms.size();i++){
                    int type = parms.get(i).getInt("type");
                    if(type==0){
                        Users.getInstance().getCurrentUser().getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
                            @Override
                            public void onSuccess(List<UserConversation> userConversations) {
                            }
                            @Override
                            public void onError(int code, String error) {
                            }
                        });
                    }else if(type==2){
                        Users.getInstance().getCurrentUser().getContacts().getRemote().sync(new Back.Result<List<Contact>>() {
                            @Override
                            public void onSuccess(List<Contact> contacts) {
                            }
                            @Override
                            public void onError(int code, String error) {
                            }
                        });
                    }
                }
            }
        }
    };
    private String myId = Users.getInstance().getCurrentUser().getEntity().getId();
     public  MakeWebmethods(Handler handlerui,Context context,Activity activity){
         this.handlerui = handlerui;
         this.context = context;
         this.activity = activity;
     }
     //web是否使用缓存
    public boolean isuseCache(){
        long currenttime = System.currentTimeMillis();
        TimeStampEntity timeStampEntity = TimeStampDb.queryByType(Users.getInstance().getCurrentUser().getEntity().getId(),"webcachetimestamp");
        if(timeStampEntity==null){
            timeStampEntity = new TimeStampEntity();
            timeStampEntity.setMyId(myId);
            timeStampEntity.setRemark("是否用缓存的时间戳");
            timeStampEntity.setTime(currenttime+"");
            timeStampEntity.setType("webcachetimestamp");
            TimeStampDb.addTimeStampEntity(timeStampEntity);
            return false;
        }
       try {
           long hostorytime = Long.parseLong(timeStampEntity.getTime());
           if ((currenttime - hostorytime) > (2 * 24 * 60 * 60 * 1000)) {
               timeStampEntity.setTime(currenttime + "");
               context.deleteDatabase("webview.db");
               context.deleteDatabase("webviewCache.db");
               return false;
           } else {
               return true;
           }
       }catch (Exception e){
           timeStampEntity.setTime(currenttime + "");
           return false;
       }
    }

    public void setAndroidmethods(Androidmethods androidmethods){
      this.androidmethods = androidmethods;
    }
    private String colorformat(String color){
        String format = color;
        if(!StringUtil.isEmpty(color)){
            if(color.length()==4 || color.length()==5){
                char [] stringArr = color.toCharArray();
                for(int i =0;i<stringArr.length;i++){
                    if(i ==0){
                        format ="";
                        format = format+stringArr[i];
                    }else{
                        format = format+stringArr[i]+stringArr[i];
                    }
                }
            }
        }
        return format;
    }
    public void sendDate(String dateTime){
        if(dateIndex!=null){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index",dateIndex);
            jsonObject.put("type","callback");
            jsonObject.put("error","");
            jsonObject.put("result",dateTime);
            androidmethods.sendwebMessage(jsonObject.toJSONString(),null,"");
            dateIndex =null;
        }
    }
    public void activityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==701 &&data!=null){
            String filePath = FileUtil.getPhotoPathFromContentUri(context,data.getData());
            uploadfile("file",fileIndex,filePath);
        }else if(requestCode==702&& resultCode == Activity.RESULT_OK){
            uploadfile("img",cameraIndex,camerapath);
        }else if(requestCode==703&& resultCode == Activity.RESULT_OK &&data!=null){
            String filePath = FileUtil.getPhotoPathFromContentUri(context,data.getData());
            uploadfile("img",imageIndex,filePath);
        }else if(requestCode==704&& resultCode == Activity.RESULT_OK &&data!=null){
            ArrayList<SortModel> choiceContacts = (ArrayList<SortModel>) data.getExtras().getSerializable("choiceperson");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index",SelectContactIndex);
            jsonObject.put("type","callback");
            jsonObject.put("error","");
            JSONArray jsonArray = new JSONArray();
            if(choiceContacts!=null&&choiceContacts.size()>0) {
                for(int i =0;i<choiceContacts.size();i++) {
                    JSONObject jsonObjectrt = new JSONObject();
                    jsonObjectrt.put("id", choiceContacts.get(i).getUserId());
                    jsonObjectrt.put("name", choiceContacts.get(i).getName());
                    jsonObjectrt.put("email", choiceContacts.get(i).getEmail());
                    String avatarUrl = choiceContacts.get(i).getAvatarUrl();
                    if(avatarUrl.contains("http")){
                        jsonObjectrt.put("avatarUrl", avatarUrl);
                    }else{
                        jsonObjectrt.put("avatarUrl", Urls.User_Messages_File_DownLoad+avatarUrl+"?access_token=" + Users.getInstance().getCurrentUser().getToken());
                    }
                    jsonArray.add(jsonObjectrt);
                }
            }
            jsonObject.put("result",jsonArray);

            androidmethods.sendwebMessage(jsonObject.toJSONString(),null,"");
        }else if(requestCode==705&& resultCode == Activity.RESULT_OK &&data!=null){
            ArrayList<SortModel> choiceContacts = (ArrayList<SortModel>) data.getExtras().getSerializable("choiceperson");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index",SelectPersonIndex);
            jsonObject.put("type","callback");
            jsonObject.put("error","");
            JSONArray jsonArray = new JSONArray();

            if(choiceContacts!=null&&choiceContacts.size()>0) {
               String personId[] = new String[choiceContacts.size()];
                for(int i =0;i<choiceContacts.size();i++){
                    personId[i] =choiceContacts.get(i).getId();
                }
                jsonObject.put("result",personId);
            }
            androidmethods.sendwebMessage(jsonObject.toJSONString(),null,"");
        }
    }
    private void uploadfile(final String type, final String fileindex, String filePath){
        final ProgressDialog downloadDialog = new ProgressDialog(context);
        final AsyncMultiPartPost post = new AsyncMultiPartPost(context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, filePath);
        // downloadDialog.setIcon(R.drawable.ic_launcher);
        downloadDialog.setTitle("正在上传中...");
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.setButton2("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                post.setIscancel(true);
            }
        });
        File file = new File(filePath);
        if(!file.exists()){
            ToastUtil.ToastMessage(context,"文件加载失败");
            return;
        }
        final long filesize = file.length();
        String name = file.getName();
        final String filetype = name.substring(name.lastIndexOf(".")+1);
        final String filename = name.substring(0,name.lastIndexOf("."));
        downloadDialog.show();

        post.executeOnExecutor((ExecutorService) Executors.newCachedThreadPool());
        post.setCallBack(new AsyncMultiPartPost.CallBack() {

            @Override
            public void update(Integer i) {
                if (downloadDialog != null) {
                    if (i == 100) {
                        downloadDialog.dismiss();
                    } else {
                        downloadDialog.setProgressNumberFormat("%1d /%2d ");
                        downloadDialog.setProgress(i);
                    }
                }
            }
        });
        post.setCallBackMsg(new AsyncMultiPartPost.CallBackMsg() {
            @Override
            public void msg(String param) {
                final MessageFileReturn fileReturn = MessageFileReturn.parse(new Json(param));
                if(StringUtil.isEmpty(fileReturn.getId())){
                    return;
                }
                String url = Urls.User_Messages_File_DownLoad+ MessageFileReturn.getMD5FileUrl(fileReturn.getId(),fileReturn.getMd5());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("index",fileindex);
                jsonObject.put("type","callback");
                jsonObject.put("error","");
                if(type!=null&&type.equals("file")){
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObjectrt = new JSONObject();
                    jsonObjectrt.put("url",url);
                    jsonObjectrt.put("name",filename);
                    jsonObjectrt.put("ext",filetype);
                    jsonObjectrt.put("size",filesize);
                    jsonObjectrt.put("time",fileReturn.getCreateAt());
                    jsonArray.add(jsonObjectrt);
                    jsonObject.put("result",jsonArray);
                }else{
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObjectrt = new JSONObject();
                    jsonObjectrt.put("url",url);
                    jsonObjectrt.put("thumbnailUrl",url);
                    jsonArray.add(jsonObjectrt);
                    jsonObject.put("result",jsonArray);
                }
                androidmethods.sendwebMessage(jsonObject.toJSONString(),null,"");
            }
        });
    }
    public void setactionbarTitle(String title){
        Message msg = new Message();
        msg.what =1;
        msg.obj = title;
        handlerui.sendMessage(msg);
    }

    public void changeToolbarColor(String bgColor,String moveColor,String textColor){
        Message msg = new Message();
        msg.what =2;
        JSONObject color = new JSONObject();
        color.put("bgColor",colorformat(bgColor));
        color.put("moveColor",colorformat(moveColor));
        color.put("textColor",colorformat(textColor));
        msg.obj = color;
        handlerui.sendMessage(msg);

    }
    public void setMenus(List<Json> menus){
        Message msg = new Message();
        msg.what =3;
        msg.obj = menus;
        handlerui.sendMessage(msg);
    }
    public void didShow(){
        Message msg = new Message();
        msg.what =5;
        handlerui.sendMessage(msg);
    }
    public void openUrl(String url){
        if(StringUtil.isEmpty(url) || !url.startsWith("http")) {
            ToastUtil.ToastMessage(AppContext.getAppContext(),"参数不可用");
        }else {
            Message msg = new Message();
            msg.what = 6;
            msg.obj = url;
            handlerui.sendMessage(msg);
        }
    }
    public void back(){
        Message msg = new Message();
        msg.what =7;
        handlerui.sendMessage(msg);
    }
    String fileIndex;
    public void openfile(String index){
        fileIndex = index;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent,701);
    }
    public void openimg(int currentpage,ArrayList<String> imgurls){
        Intent intent =new Intent(context,MoreImageBrowseActivity.class);
        intent.putStringArrayListExtra("imgurls",imgurls);
        intent.putExtra("currentpage",currentpage);
        activity.startActivity(intent);
    }

    String imageIndex;
    public void chooseImage(String index){
        imageIndex = index;
        try{
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            activity.startActivityForResult(intent, 703);
        }catch(Exception e){
            imageIndex =null;
            ToastUtil.ToastMessage(context, "打开相册失败！详情：" + e.getMessage().toString());
        }
    }
    String SelectContactIndex;
    public void selectContact(Json json, String index){
        SelectContactIndex = index;
        Intent intent = new Intent(context,SelectContactActivity.class);
        intent.putExtra("avatar",json.getBool("avatar"));
        intent.putExtra("mode",json.getStr("mode"));
        intent.putExtra("title",json.getStr("title"));

        List<Json> select = json.getList("selected");
        String userIds ="";
        if(select!=null&&select.size()>0){
            for(int i =0;i<select.size();i++){
                userIds = userIds+ select.get(i).toStr();
                if(i<(select.size()-1)){
                    userIds = userIds+";";
                }
            }
        }
        intent.putExtra("userIds",userIds);
        activity.startActivityForResult(intent, 704);
    }
    String SelectPersonIndex;
    String selectpesrsons[];
    ArrayList<SortModel> choicePersons = new ArrayList<SortModel>();
    public void selectPerson(Json json, String index){
        SelectPersonIndex = index;
        choicePersons.clear();
        Intent intent = new Intent(context,SelectSoucePersonActivity.class);
        intent.putExtra("avatar",json.getBool("avatar"));
        intent.putExtra("selectFolder",json.getBool("selectFolder"));
        intent.putExtra("mode",json.getStr("mode"));
        intent.putExtra("title",json.getStr("title"));

        List<Json> select = json.getList("selected");
        String userIds ="";
        if(select!=null&&select.size()>0){
            for(int i =0;i<select.size();i++){
                userIds = userIds+ select.get(i).toStr();
                if(i<(select.size()-1)){
                    userIds = userIds+";";
                }
            }
            selectpesrsons = userIds.split(";");
        }else {
            selectpesrsons = null;
        }
        intent.putExtra("userIds",userIds);
        ArrayList<SortModel> persons = new ArrayList<SortModel>();
        List<Json> dataSource =json.getList("dataSource");
        if(dataSource!=null&&dataSource.size()>0){
            for(int i =0;i<dataSource.size();i++){
                SortModel person = new SortModel();
                person.setId(dataSource.get(i).getStr("id"));
                person.setAvatarUrl(dataSource.get(i).getStr("avatarUrl"));
                person.setName(dataSource.get(i).getStr("name"));
                if(selectpesrsons!=null&&selectpesrsons.length>0){
                    for(int j =0;j<selectpesrsons.length;j++){
                        if(selectpesrsons[j].equals(person.getId())){
                            person.setIsChoice(true);
                            if(choicePersons.size()>0){
                                boolean find = false;
                                for(int x =0;x<choicePersons.size();x++){
                                    if(choicePersons.get(x).getId().equals(person.getId())){
                                        find = true;
                                        break;
                                    }
                                }
                                if(!find) {
                                    choicePersons.add(person);
                                }
                            }else{
                                choicePersons.add(person);
                            }
                            break;
                        }
                    }
                }
                if(dataSource.get(i).getBool("folder")){
                    addPersonchildren(person,dataSource.get(i).getList("children"));
                }
                persons.add(person);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("dataSource",persons);
            bundle.putSerializable("selectdataSource",choicePersons);
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, 705);

    }

    private void addPersonchildren(SortModel person, List<Json> children){
        if(children!=null&&children.size()>0) {
            for (int i = 0; i < children.size(); i++) {
                SortModel personchildren = new SortModel();
                personchildren.setId(children.get(i).getStr("id"));
                personchildren.setAvatarUrl(children.get(i).getStr("avatarUrl"));
                personchildren.setName(children.get(i).getStr("name"));
                personchildren.setHightSortModel(person);
                if(selectpesrsons!=null&&selectpesrsons.length>0){
                    for(int j =0;j<selectpesrsons.length;j++){
                        if(selectpesrsons[j].equals(personchildren.getId())){
                            personchildren.setIsChoice(true);

                            if(choicePersons.size()>0){
                                boolean find = false;
                                for(int x =0;x<choicePersons.size();x++){
                                    if(choicePersons.get(x).getId().equals(personchildren.getId())){
                                        find = true;
                                        break;
                                    }
                                }
                                if(!find) {
                                    choicePersons.add(personchildren);
                                }
                            }else{
                                choicePersons.add(personchildren);
                            }
                            break;
                        }
                    }
                }
                person.getChildrenPerson().add(personchildren);
                if (children.get(i).getBool("folder")) {
                    addPersonchildren(personchildren, children.get(i).getList("children"));
                }
            }
        }
    }
    public void downloadFile(String url, final String name, final String ext){
        if(StringUtil.isEmpty(url) || !url.startsWith("http")) {
            ToastUtil.ToastMessage(AppContext.getAppContext(),"参数不可用");
            return;
        }
        File file = new File(Config.CACHE_PATH_FILE + name+"."+ext);
        if (file.exists()) {
            com.y2w.uikit.utils.FileUtil.openFile(context, Config.CACHE_PATH_FILE + name+"."+ext);
            return;
        }
        final ProgressDialog downloadDialog = new ProgressDialog(context);
        // downloadDialog.setIcon(R.drawable.ic_launcher);
        final AsyncMultiPartGet get = new AsyncMultiPartGet("", url, Config.CACHE_PATH_FILE,name+"."+ext);
        downloadDialog.setTitle("正在下载中...");
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.setButton2("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                get.setIscancel(true);
            }
        });
        downloadDialog.show();
        get.executeOnExecutor((ExecutorService) Executors.newCachedThreadPool());
        get.setCallBack(new AsyncMultiPartGet.CallBack() {

            @Override
            public void update(Integer i) {
                if (downloadDialog != null) {
                    if (i == 100) {
                        downloadDialog.dismiss();
                    } else {
                        downloadDialog.setProgressNumberFormat("%1d /%2d ");
                        downloadDialog.setProgress(i);
                    }
                }
            }
        });
        get.setCallBackMsg(new AsyncMultiPartGet.CallBackMsg() {
            @Override
            public void msg(String param) {
                ToastUtil.ToastMessage(context, Config.CACHE_PATH_FILE+name+"."+ext);
            }
        });
    }

    String cameraIndex,camerapath;
    public void openCamera(String index){
        cameraIndex= index;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (!IntentChecker.isAvailable(context, intent,
                new String[]{PackageManager.FEATURE_CAMERA})) {
            ToastUtil.ToastMessage(
                    context,
                    context.getResources().getString(
                            R.string.feature_not_available_on_this_device));
            return;
        }
        File f = StorageManager.createNewAttachmentFile(context,
                Constants.MIME_TYPE_IMAGE_EXT);
        if (f == null) {
            ToastUtil.ToastMessage(context, context.getResources().getString(R.string.error));
            return;
        }
        if(!f.getParentFile().exists()){
            f.getParentFile().mkdirs();
        }
        camerapath = f.getPath();
        Uri uri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //}
        activity.startActivityForResult(intent, 702);
    }
    String locationIndex;
    public void getLocation(String index){
        locationIndex = index;
        AMapLocation locaMapLocation = AppContext.getAppContext().getLocation();
        if(locaMapLocation!=null){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index",locationIndex);
            jsonObject.put("type","callback");
            jsonObject.put("error","");
            JSONObject locationObject = new JSONObject();
            locationObject.put("longitude",locaMapLocation.getLongitude());
            locationObject.put("latitude",locaMapLocation.getLatitude());
            locationObject.put("address",locaMapLocation.getAddress());
            jsonObject.put("result",locationObject);
            androidmethods.sendwebMessage(jsonObject.toJSONString(),null,"");
            locationIndex =null;
        }
    }
    String dateIndex;
    public void chooseDate(boolean time,String defaultTime,String index){
        dateIndex = index;
        new ChooseDateDialog().showDialog(context,time,defaultTime,handlerui);
    }

    public void openNew(String url){
        if(StringUtil.isEmpty(url) || !url.startsWith("http")) {
            ToastUtil.ToastMessage(AppContext.getAppContext(),"参数不可用");
        }else {
            Intent intent = new Intent(context, StrongWebViewActivity.class);
            intent.putExtra("webUrl", url);
            //intent.putExtra("webUrl","file:///android_asset/test.html");
            activity.startActivity(intent);
        }
    }
   public void webLog(String log){
       AppData.getInstance().setLogDate(log);
   }
    public void talkTo(String userId,String index){
        if(!StringUtil.isEmpty(userId)){

            boolean isfriend = false;
            Contact contact = Users.getInstance().getCurrentUser().getContacts().getContact(userId);
            if(contact.getEntity()!=null){
                if(contact.getEntity().getUserId().equals(userId)&&!contact.getEntity().isDelete()) {
                    isfriend = true;
                }
            }
            final ProgressDialog pd = new ProgressDialog(context);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage(context.getString(R.string.loading));
           if(isfriend){
               Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(userId, EnumManage.SessionType.p2p.toString(), new Back.Result<Session>() {
                   @Override
                   public void onSuccess(final Session session) {
                       Intent intent = new Intent(context, ChatActivity.class);
                       Bundle bundle = new Bundle();
                       bundle.putString("sessionid", session.getEntity().getId());
                       bundle.putString("sessiontype", session.getEntity().getType());
                       bundle.putString("otheruserId", session.getEntity().getOtherSideId());
                       bundle.putString("name", session.getEntity().getName());
                       intent.putExtras(bundle);
                       activity.startActivity(intent);
                       pd.dismiss();
                   }
                   @Override
                   public void onError(int errorCode, String error) {
                       pd.dismiss();
                       ToastUtil.ToastMessage(context, "操作失败");
                   }
               });
           }else{
               Users.getInstance().getRemote().userGet(userId, new Back.Result<User>() {
                   @Override
                   public void onSuccess(User user) {

                       Users.getInstance().getCurrentUser().getContacts().getRemote().contactAdd(user.getEntity().getId(), user.getEntity().getAccount(), user.getEntity().getName(), user.getEntity().getAvatarUrl(), new Back.Result<Contact>() {
                           @Override
                           public void onSuccess(Contact contact) {
                               Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(contact.getEntity().getUserId(), EnumManage.SessionType.p2p.toString(), new Back.Result<Session>() {
                                   @Override
                                   public void onSuccess(final Session session) {
                                       Intent intent = new Intent(context, ChatActivity.class);
                                       Bundle bundle = new Bundle();
                                       bundle.putString("sessionid", session.getEntity().getId());
                                       bundle.putString("sessiontype", session.getEntity().getType());
                                       bundle.putString("otheruserId", session.getEntity().getOtherSideId());
                                       bundle.putString("name", session.getEntity().getName());
                                       intent.putExtras(bundle);
                                       activity.startActivity(intent);
                                       pd.dismiss();
                                   }
                                   @Override
                                   public void onError(int errorCode, String error) {
                                       pd.dismiss();
                                       ToastUtil.ToastMessage(context, "操作失败");
                                   }
                               });
                               AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.contact.toString()).syncDate();

                           }
                           @Override
                           public void onError(int errorCode, String error) {
                               pd.dismiss();
                               ToastUtil.ToastMessage(context, "操作失败");
                           }
                       });
                   }

                   @Override
                   public void onError(int Code, String error) {
                       pd.dismiss();
                       ToastUtil.ToastMessage(context, "操作失败");
                   }
               });
           }
        }
    }
  public  void confirm(String title, String content, final String index){
       new AlertDialog.Builder(context).setTitle(title).setMessage(content).setPositiveButton("确定", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               JSONObject jsonObject = new JSONObject();
               jsonObject.put("index",index);
               jsonObject.put("type","callback");
               jsonObject.put("error","");
               jsonObject.put("result",true);
               androidmethods.sendwebMessage(jsonObject.toJSONString(),null,"");
           }
       }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               JSONObject jsonObject = new JSONObject();
               jsonObject.put("index",index);
               jsonObject.put("type","callback");
               jsonObject.put("error","");
               jsonObject.put("result",false);
               androidmethods.sendwebMessage(jsonObject.toJSONString(),null,"");
           }
       }).create().show();
  }
public void upOtherCoversation(List<Json> userIds, int timeout, String index){
    Message msg = new Message();
    msg.what=1;
    msg.obj=userIds;
    timeOuthandler.sendMessageDelayed(msg,timeout);

}
    public void syncData(List<Json> parms, int timeout){
        Message msg = new Message();
        msg.what=2;
        msg.obj=parms;
        timeOuthandler.sendMessageDelayed(msg,timeout);
    }
   boolean phoneneedCallback = false;
    public void callPhoneNumber(String phonenum,boolean needCallback){
        phoneneedCallback = needCallback;
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phonenum);
        intent.setData(data);
        context.startActivity(intent);
    }
    public void onResumeActivity(){
        if(phoneneedCallback){
            final String index = System.currentTimeMillis() + "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index", index);
            jsonObject.put("action", "phoneCallBack");
            JSONArray params = new JSONArray();
            jsonObject.put("params", params);
            androidmethods.sendwebMessage(jsonObject.toJSONString(), new Back.Result<String>() {
                    @Override
                    public void onSuccess(String s) {
                    }
                    @Override
                    public void onError(int code, String error) {
                    }
                }, index);
        }
        phoneneedCallback = false;
    }
}
