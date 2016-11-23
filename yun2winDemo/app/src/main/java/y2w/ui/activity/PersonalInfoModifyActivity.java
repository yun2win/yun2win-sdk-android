package y2w.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.y2w.av.lib.AVBack;
import com.y2w.uikit.utils.NetUtil;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import y2w.base.AppContext;
import y2w.entities.ContactEntity;
import y2w.manage.CurrentUser;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.User;
import y2w.service.Back;

public class PersonalInfoModifyActivity extends Activity{

	public final static int FLAG_MODIFY_USERNAME = 100;
	public final static int FLAG_MODIFY_PASSWORD = 101;

	private final int MSG_OK = 0;
	private final int MSG_FAIL = 1;
	private final int MSG_SAVE_OK = 2;
	private final int MSG_SAVE_FAIL = 3;
		
	private ProgressDialog dialog = null;
	private Context context = null;
	private int flag ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		try {
			initActionBar();
			setContentView(R.layout.activity_modify_personal_info);
			context = this;
			setTitle("个人信息修改");
			Bundle bundle = getIntent().getExtras();
			if(bundle == null)
				return;
			flag = bundle.getInt("flag");
			initView();
		} catch (Exception e) {
		}
	}

	TextView tv_title,tv_oper;
	private void initActionBar(){
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setCustomView(R.layout.actionbar_chat);
		tv_title = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
		tv_oper = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);
		ImageButton imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
		tv_oper.setText("保存");
		tv_oper.setVisibility(View.VISIBLE);
		imageButtonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_oper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(NetUtil.isNetworkAvailable(context)){
					if(checkInfo()){
						saveInfo();
					}
				}else{
					ToastUtil.ToastMessage(context,"请检查网络连接");
				}
			}
		});

	}

	
	private void initView() {

		switch (flag) {
		case FLAG_MODIFY_USERNAME:
			tv_title.setText("修改姓名");
			((EditText) findViewById(R.id.edt_modify_personalinfo_name)).setVisibility(View.VISIBLE);
			((EditText) findViewById(R.id.edt_modify_personalinfo_name)).setText(Users.getInstance().getCurrentUser().getEntity().getName());
			break;
		case FLAG_MODIFY_PASSWORD:
			tv_title.setText("修改密码");
			((EditText) findViewById(R.id.edt_modify_personalinfo_old_pwd)).setVisibility(View.VISIBLE);
			((EditText) findViewById(R.id.edt_modify_personalinfo_new_pwd)).setVisibility(View.VISIBLE);
			((EditText) findViewById(R.id.edt_modify_personalinfo_ensure_pwd)).setVisibility(View.VISIBLE);
			break;
		}
	}

	private boolean checkInfo(){
		try{
			switch(flag){
			case FLAG_MODIFY_USERNAME:
				String name = ((EditText) findViewById(R.id.edt_modify_personalinfo_name)).getText().toString();
				if(name == null || "".equalsIgnoreCase(name.trim())){
					((EditText) findViewById(R.id.edt_modify_personalinfo_name)).setError("姓名不能为空，请重新修改！");
					return false;
				}
				break;
			case FLAG_MODIFY_PASSWORD:
				String oldPwd = ((EditText) findViewById(R.id.edt_modify_personalinfo_old_pwd)).getText().toString();
				String newPwd = ((EditText) findViewById(R.id.edt_modify_personalinfo_new_pwd)).getText().toString();
				String ensurePwd = ((EditText) findViewById(R.id.edt_modify_personalinfo_ensure_pwd)).getText().toString();
				if(null == oldPwd || oldPwd.length() <= 0){
					((EditText) findViewById(R.id.edt_modify_personalinfo_old_pwd)).setError("旧密码不能为空！");
					return false;
				}
				if(null == newPwd || newPwd.length() <= 0){
					((EditText) findViewById(R.id.edt_modify_personalinfo_new_pwd)).setError("请输入新密码！");
					return false;
				}
				if(null == ensurePwd || ensurePwd.length() <= 0){
					((EditText) findViewById(R.id.edt_modify_personalinfo_ensure_pwd)).setError("请再次输入密码！");
					return false;
				}
				if(!newPwd.equals(ensurePwd)){
					((EditText) findViewById(R.id.edt_modify_personalinfo_ensure_pwd)).setError("两次密码不一致，请重新输入！");
					return false;
				}
				break;
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private void saveInfo(){
		Message msg = new Message();
		try{
			boolean result = false;
			switch(flag){
			case FLAG_MODIFY_USERNAME:
				String name = ((EditText) findViewById(R.id.edt_modify_personalinfo_name)).getText().toString();
				saveName(name);
				break;
			case FLAG_MODIFY_PASSWORD:
				String oldPwd = ((EditText) findViewById(R.id.edt_modify_personalinfo_old_pwd)).getText().toString();
				String newPwd = ((EditText) findViewById(R.id.edt_modify_personalinfo_new_pwd)).getText().toString();
				String ensurePwd = ((EditText) findViewById(R.id.edt_modify_personalinfo_ensure_pwd)).getText().toString();
				if(newPwd.equals(ensurePwd)){
					setPassword(oldPwd,newPwd);
				}
				break;
			}
		}catch(Exception e){
			msg.what = MSG_SAVE_FAIL;
			msg.obj = e.getMessage();
			handler.sendMessage(msg);
		}
	}
	boolean ispassWord = false;
	private void setPassword(String oldPwd,String newPwd){
		if(ispassWord)
			return;
		ispassWord = true;
		final ProgressDialog pd = new ProgressDialog(PersonalInfoModifyActivity.this);
		pd.setCanceledOnTouchOutside(true);
		pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		pd.setMessage("正在修改中");
		pd.show();
		Users.getInstance().getRemote().userSetPassword(oldPwd, newPwd, new Back.Callback() {
			@Override
			public void onSuccess() {
				pd.dismiss();
				ispassWord = false;
				handler.sendEmptyMessage(MSG_SAVE_OK);
			}

			@Override
			public void onError(int code, String error) {
				pd.dismiss();
				ispassWord = false;
				handler.sendMessage(handler.obtainMessage(MSG_SAVE_FAIL, error));
			}
		});
	}

	private void saveName(final String name){
		Users.getInstance().getUser(Users.getInstance().getCurrentUser().getEntity().getId(), new Back.Result<User>() {
			@Override
			public void onSuccess(User user) {
				Users.getInstance().addUser(user);
				ContactEntity entity = new ContactEntity();
				entity.setUserId(user.getEntity().getId());
				entity.setName(name);
				entity.setAvatarUrl(user.getEntity().getAvatarUrl());
				entity.setCreatedAt(user.getEntity().getCreatedAt());
				entity.setUpdatedAt(user.getEntity().getUpdatedAt());
				entity.setRole(EnumManage.UserRole.user.toString());
				entity.setStatus(EnumManage.UserStatus.active.toString());
				entity.setEmail(user.getEntity().getAccount());
				entity.setPhone("");
				entity.setJobTitle("");
				entity.setAddress("");
				Contact contact = new Contact(Users.getInstance().getCurrentUser(),Users.getInstance().getCurrentUser().getContacts(),entity);
				Users.getInstance().getRemote().store(contact, new AVBack.Result<Contact>() {
					@Override
					public void onSuccess(Contact contact) {
						Users.getInstance().getCurrentUser().getEntity().setName(contact.getEntity().getName());
						handler.sendEmptyMessage(MSG_SAVE_OK);
					}

					@Override
					public void onError(Integer integer) {
						handler.sendMessage(handler.obtainMessage(MSG_SAVE_FAIL, "保存失败！"));
					}
				});
			}

			@Override
			public void onError(int code, String error) {

			}
		});
	}
	

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(null != dialog){
				dialog.dismiss();
			}
			switch(msg.what){
			case MSG_OK:
				initView();
				break;
			case MSG_FAIL:
				String info = (String) msg.obj;
				if(!StringUtil.isEmpty(info))
				ToastUtil.ToastMessage(context, info);
				break;
			case MSG_SAVE_OK:
				switch(flag){
					case FLAG_MODIFY_USERNAME:
						ToastUtil.ToastMessage(context, "修改成功！");
						break;
					case FLAG_MODIFY_PASSWORD:
						try {
							Users.getInstance().getCurrentUser().getImBridges().disConnect();
						}catch (Exception e){
						}
						AppContext.getAppContext().logout();
						finish();
						//ToastUtil.ToastMessage(context, "密码修改成功！");
						break;
				}
				Intent data = new Intent();
				setResult(RESULT_OK, data);
				finish();
				break;
			case MSG_SAVE_FAIL:
				String reason = (String) msg.obj;
				ToastUtil.ToastMessage(context, reason);
				break;
			}
		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}

}
