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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.y2w.uikit.customcontrols.view.ClearableEditTextWithIcon;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import y2w.manage.Users;
import y2w.model.User;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.common.UserInfo;

import static com.yun2win.utils.LogUtil.*;


/**
 * Created by hejie on 2016/3/14.
 * 注册界面
 */
public class RegisterActivity extends BaseActivity {
	private static final String TAG = "RegisterActivity";
   //http://112.74.210.208:8080/images/default.jpg 默认头像地址
    private ClearableEditTextWithIcon registerAccountEdit;
	private ClearableEditTextWithIcon registerNickNameEdit;
	private ClearableEditTextWithIcon registerPasswordEdit;
	private Button bt_register;
	private Context context;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			ToastUtil.showToast(RegisterActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		context = this;
		registerInit();
	}

	/**
	 * 初始化
	 */
	public void registerInit() {
		registerAccountEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_register_account);
		registerNickNameEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_register_nickname);
		registerPasswordEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_register_password);
		bt_register = (Button) findViewById(R.id.bt_register);
		registerAccountEdit.setIconResource(R.drawable.user_account_icon);
		registerNickNameEdit.setIconResource(R.drawable.nick_name_icon);
		registerPasswordEdit.setIconResource(R.drawable.user_pwd_lock_icon);

		registerAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
		registerNickNameEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		registerPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

		registerAccountEdit.addTextChangedListener(textWatcher);
		registerNickNameEdit.addTextChangedListener(textWatcher);
		registerPasswordEdit.addTextChangedListener(textWatcher);
		bt_register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});

	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	private void register(){
		String account = registerAccountEdit.getText().toString().trim();
		String username = registerNickNameEdit.getText().toString().trim();
		String pwd = registerPasswordEdit.getText().toString().trim();
		if (TextUtils.isEmpty(account)) {
			Toast.makeText(this, getResources().getString(R.string.Account_cannot_be_empty), Toast.LENGTH_SHORT).show();
			registerAccountEdit.requestFocus();
			return;
		} else if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
			registerAccountEdit.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			registerNickNameEdit.requestFocus();
			return;
		}
		Users.getInstance().getRemote().register(account, pwd, username,new Back.Result<User>() {
			@Override
			public void onSuccess(User user) {
				UserInfo.setUserInfo(user.getEntity().getAccount(),"","","","","","");
				getInstance().log(TAG, "Id=" + user.getEntity().getId(), null);
				getInstance().log(TAG, "name=" + user.getEntity().getName(), null);
				getInstance().log(TAG, "email=" + user.getEntity().getAccount(), null);
				Intent intent = new Intent(context,LoginActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onError(int errorCode,String error) {
				Message msg = new Message();
				msg.obj = error;
				if(errorCode == ErrorCode.EC_NETWORK_ERROR){
					msg.what =1;
					handler.sendMessage(msg);
				}else if(errorCode == ErrorCode.EC_HTTP_ERROR_500){
					msg.what = 2;
					handler.sendMessage(msg);
				}
			}
		});

	}

	public void back(View view) {
		finish();
	}

}
