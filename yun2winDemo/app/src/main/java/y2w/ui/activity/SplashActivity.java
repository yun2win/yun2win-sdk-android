package y2w.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.base.PushService;
import y2w.common.UserInfo;
import y2w.db.DaoManager;
import y2w.manage.Users;


/**
 * Created by hejie on 2016/3/14.
 * 导航界面
 */
public class SplashActivity extends BaseActivity {
	private RelativeLayout rootLayout;
	private TextView versionText;
	private boolean gotomake=false;
	private Context mContext = this;
	private String skipActivity;

	private static final int sleepTime = 2000;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1://跳转到主页面
					Intent mIntentMain = new Intent(mContext, MainActivity.class);
					if(!StringUtil.isEmpty(skipActivity)){
						mIntentMain.putExtra("skipActivity",skipActivity);
					}
					mContext.startActivity(mIntentMain);
					skipActivity ="";
					finish();
					break;
				case 2://跳转到登录
					Intent mIntentLogin = new Intent(mContext, LoginActivity.class);
					mContext.startActivity(mIntentLogin);
					skipActivity ="";
					finish();
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);
		AppData.getInstance().setIsactivityrun(true);
		skipActivity = getIntent().getStringExtra("skipActivity");
		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		versionText = (TextView) findViewById(R.id.tv_version);

		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
		AppData.getInstance().getUpdateHashMap().clear();
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread(new Runnable() {
			public void run() {
				if(!gotomake){
					gotomake = true;
				if(!StringUtil.isEmpty(UserInfo.getAccount())&& !StringUtil.isEmpty(UserInfo.getPassWord())){
					Users.getInstance().getCurrentUser().initCurrentUser();
					Users.getInstance().createCurrentUser(UserInfo.getCurrentInfo());
					Intent pushservice = new Intent(SplashActivity.this, PushService.class);
					startService(pushservice);
					DaoManager.getInstance(AppContext.getAppContext());
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					Intent mIntentMain = new Intent(mContext, MainActivity.class);
					if(!StringUtil.isEmpty(skipActivity)){
						mIntentMain.putExtra("skipActivity",skipActivity);
					}
					mContext.startActivity(mIntentMain);
					skipActivity ="";
					//创建数据库
					finish();
					return;
					}
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					skipActivity ="";
					finish();
				}
			}
		}).start();

	}
	/**
	 * 获取当前应用程序的版本号
	 */
	private String getVersion() {
		String st = getResources().getString(R.string.Version_number_is_wrong);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}
}
