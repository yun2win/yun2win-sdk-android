/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package y2w.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import y2w.Account.SyncAdapter;
import y2w.base.AppContext;
import y2w.base.PushService;
import y2w.common.UserInfo;
import y2w.db.DaoManager;
import y2w.manage.CurrentUser;
import y2w.manage.Users;
import y2w.service.Back;
import y2w.service.ErrorCode;


/**
 * Created by hejie on 2016/3/14.
 * 登录界面
 */
public class LoginActivity extends AccountAuthenticatorActivity {
	private static final String TAG = LoginActivity.class.getSimpleName();
	private EditText usernameEditText;
	private EditText passwordEditText;
	private AccountManager mAccountManager;
	private boolean progressShow = false;

	private String currentUsername;
	private String currentPassword;
	private Context context;

	private String mUsername;
	protected boolean mRequestNewAccount = false;
	private Boolean mConfirmCredentials = false;
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			ToastUtil.showToast(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mUsername = intent.getStringExtra(PARAM_USERNAME);
		mRequestNewAccount = mUsername == null;
		mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);

		setContentView(R.layout.activity_login);
		mAccountManager = AccountManager.get(this);
		context = this;
		usernameEditText = (EditText) findViewById(R.id.et_login_account);
		passwordEditText = (EditText) findViewById(R.id.et_login_secret);

		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		//如果本地帐号密码都存在，自动登录
		if(!StringUtil.isEmpty(UserInfo.getAccount())){
			usernameEditText.setText(UserInfo.getAccount());
			if(!StringUtil.isEmpty(UserInfo.getPassWord())){
				passwordEditText.setText(UserInfo.getPassWord());
				login(UserInfo.getAccount(),UserInfo.getPassWord());
			}else{
				passwordEditText.requestFocus();
			}
		}
		//创建数据库
		DaoManager.getInstance(AppContext.getAppContext());

	}

	/**
	 * 注册
	 * @param view
	 */
	public void registerClick(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}

	/**
	 * 登录
	 * @param view
	 */
	public void loginClick(View view) {

		if (mRequestNewAccount) {
			currentUsername = usernameEditText.getText().toString().trim();
		}
		currentPassword = passwordEditText.getText().toString().trim();
		//登录帐号检测
		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.Account_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		//登录密码检测
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		login(currentUsername, currentPassword);
	}

	// 如果开发者直接使用这个demo，只需更改appKey，然后就登入自己的账户体系。
	// 登录成功后，开发者可以获取到appKey与appSecret,userId，通过appKey与appSecret可以获取连接理约云消息通道服务器的token。
	// 主界面MainActivity里面，连接理约云消息通道服务器
	private void login(final String account, final String password){
		final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();
		progressShow = true;
		Users.getInstance().getCurrentUser().initCurrentUser();
		Users.getInstance().getRemote().login(account, password, new Back.Result<CurrentUser>() {
			@Override
			public void onSuccess(CurrentUser currentUser) {
				if (progressShow == false) {
					return;
				}
				//同步会话联系人
				Users.getInstance().getCurrentUser().getRemote().sync(new Back.Callback() {
					@Override
					public void onSuccess() {
						if (!mConfirmCredentials) {
							Account mAccount = new Account(account, SyncAdapter.ACCOUNT_TYPE);
							if (mRequestNewAccount) {
								mAccountManager.addAccountExplicitly(mAccount, password, null);
								// Set contacts sync for this mAccount.
								ContentResolver.setSyncAutomatically(mAccount, ContactsContract.AUTHORITY, true);
								ContentResolver.setIsSyncable(mAccount, ContactsContract.AUTHORITY, 1);
								ContentResolver.setMasterSyncAutomatically(true);
								ContentResolver.addPeriodicSync(mAccount, ContactsContract.AUTHORITY, new Bundle(), 90);
							} else {
								mAccountManager.setPassword(mAccount, password);
							}
						} else {
							Account account = new Account(mUsername, SyncAdapter.ACCOUNT_TYPE);
							mAccountManager.setPassword(account, password);
							final Intent intent = new Intent();
							intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, "OK");
							setAccountAuthenticatorResult(intent.getExtras());
							setResult(RESULT_OK, intent);
						}
						pd.dismiss();
						Intent pushservice = new Intent(LoginActivity.this, PushService.class);
						startService(pushservice);
						startActivity(new Intent(LoginActivity.this, MainActivity.class));
						finish();
					}

					@Override
					public void onError(int errorCode, String error) {
						pd.dismiss();
					}
				});
			}

			@Override
			public void onError(int errorCode, String error) {
				pd.dismiss();
				Message msg = new Message();
				msg.obj = error;
				if (errorCode == ErrorCode.EC_NETWORK_ERROR) {
					msg.what = 1;
					handler.sendMessage(msg);
				} else if (errorCode == ErrorCode.EC_HTTP_ERROR_500) {
					msg.what = 2;
					handler.sendMessage(msg);
				}
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

}
