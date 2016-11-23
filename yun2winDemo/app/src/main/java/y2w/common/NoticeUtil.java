 package y2w.common;

 import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

 import com.alibaba.fastjson.JSONObject;
 import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import y2w.base.AppContext;
 import y2w.base.AppData;
 import y2w.base.Tool;
 import y2w.db.DaoManager;
 import y2w.manage.Users;
 import y2w.model.Session;
import y2w.ui.activity.ChatActivity;
import y2w.ui.activity.MainActivity;
 import y2w.ui.activity.SplashActivity;


 public class NoticeUtil {

     public static final int NOTIFICATION_ID = 140506002;
     public static String WHOSE="NONE";//谁的通知栏
     public static final int CHAT = 1;
     private static Context context = AppContext.getAppContext();

     /**
      * 消息通知栏
      * @param title   标题
      * @param msgType 消息类型
      */
     public static void notice(String title,String content, int msgType,String from,
             final Session session, final String chatType) {
         try{
             // 定义NotificationManager
             final NotificationManager mNotificationManager = (NotificationManager) context
                     .getSystemService(Context.NOTIFICATION_SERVICE);
             // 定义通知栏展现的内容信息
             int icon = R.drawable.lyy_icon;
             Bitmap bitmap = BitmapFactory.decodeResource(AppContext.getAppContext().getResources(),
                     icon);
             CharSequence tickerText = "yun2win";
             long when = System.currentTimeMillis();
             CharSequence contentTitle = title;
             CharSequence contentText = content;

             Notification notification;
             Intent notificationIntent = null;
             WHOSE = session.getEntity().getId();
             if (msgType == CHAT) {
                 String account = Users.getInstance().getCurrentUser().getEntity().getAccount();
                 if(StringUtil.isEmpty(account)){
                     if(AppData.getInstance().getMainActivity()!=null) {
                         try {
                             Users.getInstance().getCurrentUser().getImBridges().disConnect();
                             DaoManager.getInstance(AppContext.getAppContext()).close();
                             AppData.getInstance().getMainActivity().finish();
                         } catch (Exception e) {
                         }
                     }
                     notificationIntent = new Intent(context, SplashActivity.class);
                     JSONObject jsonObject = new JSONObject();
                     jsonObject.put("activity","chatActivity");
                     JSONObject jsonresult = new JSONObject();
                     jsonresult.put("sessionid", session.getEntity().getId());
                     jsonresult.put("sessiontype", session.getEntity().getType());
                     jsonresult.put("otheruserId", session.getEntity().getOtherSideId());
                     if (!StringUtil.isEmpty(session.getEntity().getName())) {
                         jsonresult.put("name", session.getEntity().getName());
                     } else {
                         jsonresult.put("name", title);
                     }
                     jsonObject.put("result",jsonresult);
                     notificationIntent.putExtra("skipActivity",jsonObject.toJSONString());
                 }else{
                     notificationIntent = new Intent(context, ChatActivity.class);
                     Bundle bundle = new Bundle();
                     bundle.putString("sessionid", session.getEntity().getId());
                     bundle.putString("sessiontype", session.getEntity().getType());
                     bundle.putString("otheruserId", session.getEntity().getOtherSideId());
                     if (!StringUtil.isEmpty(session.getEntity().getName())) {
                         bundle.putString("name", session.getEntity().getName());
                     } else {
                         bundle.putString("name", title);
                     }
                     notificationIntent.putExtras(bundle);
                 }
             }else{

             }

             PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                     notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

             if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                 notification = (new NotificationCompat.Builder(context)
                         .setContentText(contentText).setContentTitle(contentTitle)
                         .setContentIntent(contentIntent).setLargeIcon(bitmap)).build()
                 ;
                 notification.icon = icon;
                 notification.when = when;
                 notification.tickerText = tickerText;
                 notification.flags = Notification.FLAG_AUTO_CANCEL;
                 notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;
                 notification.defaults = Notification.DEFAULT_SOUND;

             } else {
                 notification = new Notification(icon, tickerText, when);
                 notification.flags = Notification.FLAG_AUTO_CANCEL;
                 // 定义下拉通知栏时要展现的内容信息
               /*  notification.setLatestEventInfo(appContext, contentTitle, contentText,
                         contentIntent);*/
                 notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;
                 notification.defaults = Notification.DEFAULT_SOUND;

             }
             mNotificationManager.notify(NOTIFICATION_ID, notification);
             if (bitmap != null && bitmap.isRecycled()) {
                 bitmap.recycle();
                 bitmap = null;
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

 }
