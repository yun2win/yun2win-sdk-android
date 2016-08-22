package y2w.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import y2w.common.UserInfo;
import y2w.db.DaoManager;
import y2w.manage.CurrentUser;
import y2w.manage.Users;
import y2w.service.Back;
import y2w.service.ErrorCode;


/**
 * Created by hejie on 2016/3/14.
 * 导航界面
 */
public class SplashActivity extends BaseActivity {
	private RelativeLayout rootLayout;
	private TextView versionText;
	
	private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);

		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		versionText = (TextView) findViewById(R.id.tv_version);

		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				/*if (DemoHXSDKHelper.getInstance().isLogined()) {
					// ** 免登陆情况 加载所有本地群和会话
					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					//加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					long costTime = System.currentTimeMillis() - start;
					//等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//进入主页面
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}else {*/
				//如果本地帐号密码都存在，自动登录
					if(!StringUtil.isEmpty(UserInfo.getAccount())&& !StringUtil.isEmpty(UserInfo.getPassWord())){
						login(UserInfo.getAccount(), UserInfo.getPassWord());
						//创建数据库
						DaoManager.getInstance(SplashActivity.this);
						return;
					}
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				//}
			}
		}).start();

	}
	private void login(final String account, final String password){
		Users.getInstance().getRemote().login(account, password, new Back.Result<CurrentUser>() {
			@Override
			public void onSuccess(CurrentUser currentUser) {
				//同步会话联系人
				Users.getInstance().getCurrentUser().getRemote().sync(new Back.Callback() {
					@Override
					public void onSuccess() {
						startActivity(new Intent(SplashActivity.this, MainActivity.class));
						finish();
					}

					@Override
					public void onError(int errorCode, String error) {
						startActivity(new Intent(SplashActivity.this, LoginActivity.class));
						finish();
					}
				});
			}

			@Override
			public void onError(int errorCode, String error) {
				startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				finish();
			}
		});
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
