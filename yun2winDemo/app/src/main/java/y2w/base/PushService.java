package y2w.base;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.y2w.uikit.utils.StringUtil;

import y2w.manage.CurrentUser;
import y2w.manage.Users;



public class PushService extends Service {
	// 推送服务标签
	public final String TAG = PushService.class.getSimpleName();
	public Context context;
	private static final String ACTION_START = "START";
	private static final String ACTION_STOP = "STOP";
	private CurrentUser currentUser = Users.getInstance().getCurrentUser();
	public static PushHandler pushHandler;

	public PushService() {
	}


	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		context = AppContext.getAppContext();
		if(!StringUtil.isEmpty(Users.getInstance().getCurrentUser().getEntity().getId())) {
			currentUser.getImBridges().connect();
		}
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	public class PushHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case PushHandlerCode.CODE_PUSH_CONNECT:

				break;
			default:
				break;

			}
		}
	}

	public class PushHandlerCode{
		private static final int CODE_PUSH_CONNECT = 1;
	}


}