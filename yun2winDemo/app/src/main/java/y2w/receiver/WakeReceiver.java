package y2w.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import y2w.ui.activity.ScreenActivity;

public class WakeReceiver extends BroadcastReceiver {

    private final static String TAG = WakeReceiver.class.getSimpleName();
    private final static int WAKE_SERVICE_ID = -1111;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_OFF)){
            //启动activity
            context.startActivity(new Intent(context.getApplicationContext(), ScreenActivity.class));
        }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            //停止activity
            if(ScreenActivity.activity != null){
                ScreenActivity.activity.finish();
            }
        }else{//系统广播，拉活APP
//            context.startService(new Intent(context, AotoLoginService.class));
        }
    }

}
