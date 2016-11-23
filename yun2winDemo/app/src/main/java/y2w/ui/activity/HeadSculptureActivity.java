package y2w.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.y2w.av.lib.AVBack;
import com.y2w.uikit.customcontrols.photoview.PhotoView;
import com.y2w.uikit.utils.ImageUtil;
import com.y2w.uikit.utils.NetUtil;

import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.utils.Json;
import com.yun2win.utils.LogFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.base.Urls;
import y2w.common.AsyncMultiPartPost;
import y2w.common.Config;
import y2w.common.ImagePool;
import y2w.entities.ContactEntity;
import y2w.manage.CurrentUser;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.Session;
import y2w.model.User;
import y2w.model.UserConversation;
import y2w.model.messages.MessageFileReturn;
import y2w.service.Back;

public class HeadSculptureActivity extends Activity implements OnClickListener{

	private final int TAKE_IMAGE = 1;
	private final int SELECT_IMAGE = 2;
	private final int CROP_IMAGE = 3;
	
	private final int MSG_LOAD_IMAGE = 1;
	private final int MSG_UPLOAD_SUCCESS = 2;
	private final int MSG_UPLOAD_FAIL = 3;
	private final int MSG_IMAGE_VIEW = 4;

	public static final String BIG = "_big.jpg";
	public static final String SMALL = "_small.jpg";

	private String url = null;
	private Context context = null;
	
	private PhotoView imgHead = null;
	private ImageView imgNoHead = null;
	private Button btnUpLoad = null;
	private LinearLayout lnlUpLoad = null;
	private LinearLayout lnlPreview = null;

	private String mode; //"view"——仅查看，null或其他——查看并更换上传头像
	private String userId;
	
	private ProgressDialog dialog = null;
	private boolean upLoadFlag = false;
	private String filepath;
	private String type;
	private String sessionId;
	private String fileToken = "?access_token=" + Users.getInstance().getCurrentUser().getToken();
	private Session session;
	private CurrentUser currentUser;
	private String avatarUrl;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_head_sculpture);
			context = this;
			currentUser = Users.getInstance().getCurrentUser();
			try {
				Bundle bundle = getIntent().getExtras();
				if(null != bundle){
					type = bundle.getString("type","");
					mode = bundle.getString("mode");
					sessionId = bundle.getString("sessionId", "");
					userId = bundle.getString("userId", "");
					initActionBar();
					init();
				}
			} catch (Exception e) {
				//ToastUtil.ToastMessage(context, "初始化失败！ 详情：" + e.getMessage());
			}
		}
		catch(Exception ex){
		}
		File file = new File(Config.CACHE_PATH_IMAGE);
		if (!file.exists()) {
			file.mkdir();
		}
	}
	private TextView title,tv_oper;
	private void initActionBar(){
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setCustomView(R.layout.actionbar_chat);
		title = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
		tv_oper = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);
		ImageButton imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
		tv_oper.setText("更换");
		if("view".equals(mode)){
			tv_oper.setVisibility(View.GONE);
		}else{
			tv_oper.setVisibility(View.VISIBLE);
		}
		imageButtonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_oper.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(NetUtil.isNetworkAvailable(context)){
					dialog();
				}else{
					ToastUtil.ToastMessage(context,"请检查网络连接");
				}
			}
		});

	}

	private void init() throws Exception {

		lnlPreview = (LinearLayout)findViewById(R.id.lnl_head_sculpture_preview);
		imgHead = (PhotoView) findViewById(R.id.img_head_sculpture_preview);
		imgNoHead = (ImageView) findViewById(R.id.img_head_sculpture_no);
		lnlUpLoad = (LinearLayout)findViewById(R.id.lnl_head_sculpture_upload);
		btnUpLoad = (Button)findViewById(R.id.btn_head_sculpture_upload);
		btnUpLoad.setOnClickListener(this);
		

		initPortrait();

	}
	
	private void initPortrait() {
		if (EnumManage.SessionType.p2p.toString().equals(type)) {
			if("view".equals(mode)){
				if(user == null){
					Users.getInstance().getRemote().userGet(userId, new Back.Result<User>() {
						@Override
						public void onSuccess(User u) {
							user = u;
							handler.sendEmptyMessage(MSG_IMAGE_VIEW);
						}

						@Override
						public void onError(int code, String error) {
							Users.getInstance().getUser(userId, new Back.Result<User>() {
								@Override
								public void onSuccess(User u) {
									user = u;
									handler.sendEmptyMessage(MSG_IMAGE_VIEW);
								}

								@Override
								public void onError(int code, String error) {

								}
							});
						}
					});
				}else{
					if (user.getEntity().getAvatarUrl().toString().contains("http")) {
						ImagePool.getInstance(AppContext.getAppContext()).load(user.getEntity().getAvatarUrl(), null, imgHead, R.drawable.default_person_icon);
					} else {
						ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + user.getEntity().getAvatarUrl(), fileToken, imgHead, R.drawable.default_person_icon);
					}
				}
			}else{
				if (Users.getInstance().getCurrentUser().getEntity().getAvatarUrl().toString().contains("http")) {
					ImagePool.getInstance(AppContext.getAppContext()).load(Users.getInstance().getCurrentUser().getEntity().getAvatarUrl(), null, imgHead, R.drawable.default_person_icon);
				} else {
					ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + Users.getInstance().getCurrentUser().getEntity().getAvatarUrl(), fileToken, imgHead, R.drawable.default_person_icon);
				}
			}
			title.setText("头像");
		} else if (EnumManage.SessionType.group.toString().equals(type)) {
			if (session == null) {
				currentUser.getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
					@Override
					public void onSuccess(Session s) {
						session = s;
						if (session.getEntity().getAvatarUrl().toString().contains("http")) {
							ImagePool.getInstance(AppContext.getAppContext()).load(session.getEntity().getAvatarUrl(), null, imgHead, R.drawable.default_group_icon);
						} else {
							ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + session.getEntity().getAvatarUrl(), fileToken, imgHead, R.drawable.default_group_icon);
						}
					}

					@Override
					public void onError(int code, String error) {
						ToastUtil.ToastMessage(context, "头像显示失败");
					}
				});
				title.setText("群头像");
			}
		}
	}
	private void dialog() {
		new AlertDialog.Builder(this)
				.setTitle("选择头像")
				.setItems(new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == 0){
							fromAlbum();
						}else{
							fromCamera();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 取消按钮事件
						dialog.dismiss();
					}
				}).show();
	}


	private Bitmap small(Bitmap bitmap) {
		  Matrix matrix = new Matrix(); 
		  matrix.postScale(0.5f,0.5f); //长和宽放大缩小的比例
		  Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		  return resizeBmp;
	}
	 
	class ImagePiece {

			public int index = 0;
			
			public Bitmap bitmap = null;
		
	}
	
	public List<ImagePiece> split(Bitmap bitmap, int xPiece, int yPiece) {
		List<ImagePiece> pieces = new ArrayList<ImagePiece>(xPiece * yPiece);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int pieceWidth = width / 3;
		int pieceHeight = height / 3;
		for (int i = 0; i < yPiece; i++) {
			for (int j = 0; j < xPiece; j++) {
				ImagePiece piece = new ImagePiece();
				piece.index = j + i * xPiece;
				int xValue = j * pieceWidth;
				int yValue = i * pieceHeight;
				piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
						pieceWidth, pieceHeight);
				pieces.add(piece);
			}
		}

		return pieces;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			closeCurrent();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == 0)
			return;
		
		switch(requestCode){
		case TAKE_IMAGE:
			File file = new File(Config.CACHE_PATH_IMAGE, "head_sculpture.jpg");
			startCropImage(Uri.fromFile(file));
			break;
		case SELECT_IMAGE:
			startCropImage(data.getData());
			break;
		case CROP_IMAGE:
			if(null == data)
				return;

			processingResult();
			break;
		}
	}

	private void fromAlbum(){
		try{
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent, SELECT_IMAGE);
		}catch(Exception e){
			ToastUtil.ToastMessage(context, "打开相册失败！详情：" + e.getMessage().toString());
		}
	}
	
	private void fromCamera(){
		try{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Config.CACHE_PATH_IMAGE, "head_sculpture.jpg")));
			startActivityForResult(intent, TAKE_IMAGE);
		}catch(Exception e){
			ToastUtil.ToastMessage(context, "启动相机失败！详情：" + e.getMessage().toString());
		}
	}
	
	private Uri cropUri;
	private void startCropImage(Uri uri){
		try{
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
			intent.putExtra("crop", "true");
			// aspectX aspectY 是宽高的比例
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// outputX outputY 是裁剪图片宽高
			intent.putExtra("outputX", 600);
			intent.putExtra("outputY", 600);
//			intent.putExtra("return-data", true);
			intent.putExtra("return-data", false);
			cropUri = Uri.fromFile(new File(Config.CACHE_PATH_IMAGE + System.currentTimeMillis() + ".JPEG"));
			intent.putExtra("output", cropUri);
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			startActivityForResult(intent, CROP_IMAGE);
		}catch(Exception e){
			ToastUtil.ToastMessage(context, "出现异常！详情：" + e.getMessage().toString());
		}
	}
	
	private void processingResult(){

		try {
			dialog = new ProgressDialog(context);
			dialog.setMessage("头像正在上传中...");
			dialog.setCancelable(false);
			Bitmap photo = BitmapFactory.decodeFile(cropUri.getPath());
			imgHead.setImageBitmap(photo);
			filepath = Config.CACHE_PATH_IMAGE + "head.jpg";
			ImageUtil.saveImageToSD(null, filepath, photo, 100);
			AsyncMultiPartPost post = new AsyncMultiPartPost(context,Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, filepath);
			post.execute();
			post.setCallBack(new AsyncMultiPartPost.CallBack() {

				@Override
				public void update(Integer i) {
					dialog.setProgress(i);
				}
			});
			post.setCallBackMsg(new AsyncMultiPartPost.CallBackMsg() {
				@Override
				public void msg(String param) {
					dialog.dismiss();
					if (param == null)
						return;
					MessageFileReturn fileReturn = MessageFileReturn.parse(new Json(param));
					avatarUrl =Urls.User_Messages_File_DownLoad + MessageFileReturn.getMD5FileUrl(fileReturn.getId(),fileReturn.getMd5());
					saveHeadInfo();
				}
			});
			dialog.show();
		} catch (Exception e) {
			ToastUtil.ToastMessage(context, e.getMessage().toString());
			if(null != dialog)
				dialog.dismiss();
		}
		
	}

	private void saveHeadInfo(){
		if(EnumManage.SessionType.p2p.toString().equals(type)){
			saveUserInfo();
		}else{
			saveSessionInfo();
			if(ChatActivity._context!=null)
				((ChatActivity)ChatActivity._context).sendSystemMessage(Users.getInstance().getCurrentUser().getEntity().getName()+"更改了群头像");
		}
	}

	private void saveUserInfo(){
		Users.getInstance().getUser(Users.getInstance().getCurrentUser().getEntity().getId(), new Back.Result<User>() {
			@Override
			public void onSuccess(User user) {
				Users.getInstance().addUser(user);
				ContactEntity entity = new ContactEntity();
				entity.setUserId(user.getEntity().getId());
				entity.setName(user.getEntity().getName());
				entity.setAvatarUrl(avatarUrl);
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
						Users.getInstance().getCurrentUser().getEntity().setAvatarUrl(contact.getEntity().getAvatarUrl());
						handler.sendEmptyMessage(MSG_UPLOAD_SUCCESS);
					}

					@Override
					public void onError(Integer integer) {
						handler.sendEmptyMessage(MSG_UPLOAD_FAIL);
					}
				});
			}

			@Override
			public void onError(int code, String error) {

			}
		});
	}

	private void saveSessionInfo() {
		session.getEntity().setAvatarUrl(avatarUrl);
		session.getSessions().getRemote().sessionUpdate(session,false, new Back.Result<Session>() {
			@Override
			public void onSuccess(Session s) {
				session = s;
				session.getSessions().refreshSessionHashMap(session);
				AppData.isRefreshConversation = true;
				UserConversation userConversation = currentUser.getUserConversations().get(sessionId,session.getEntity().getType());
				if(userConversation.getEntity() != null){
					userConversation.getEntity().setAvatarUrl(session.getEntity().getAvatarUrl());
					currentUser.getUserConversations().addUserConversation(userConversation);
				}
				handler.sendEmptyMessage(MSG_UPLOAD_SUCCESS);
			}

			@Override
			public void onError(int code, String error) {
				handler.sendEmptyMessage(MSG_UPLOAD_FAIL);
			}
		});

	}
	
	private void closeCurrent(){
		Intent data = new Intent();
		data.putExtra("upLoadState", upLoadFlag);
		setResult(RESULT_OK, data);
		finish();
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			try{
				if(null != dialog){
					dialog.dismiss();
				}
				switch(msg.what){
				case MSG_LOAD_IMAGE:
					Bitmap bmp = (Bitmap) msg.obj;
					break;
				case MSG_UPLOAD_SUCCESS:
					initPortrait();
					ToastUtil.ToastMessage(context, "头像上传成功！");
					break;
				case MSG_UPLOAD_FAIL:
					imgHead.setImageBitmap(null);
					upLoadFlag = false;
					//清除缓存
					try{
						File bigFile = new File(filepath);
						if(bigFile.exists())
							bigFile.delete();
					}catch(Exception e){}
					String info = (String) msg.obj;
					ToastUtil.ToastMessage(context, "头像上传失败！详情：" + info);
					break;
				case MSG_IMAGE_VIEW:
					initPortrait();
					break;
				}
			}catch(Exception e){
				//if(e != null)
				//ToastUtil.ToastMessage(context, "异常：" + e.getMessage().toString());
			}
		};
	};

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			closeCurrent();
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {

	}
	
	private Bitmap compressPhoto(String path){
		try{
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			int width = opts.outWidth;
			int height = opts.outHeight;
			
			DisplayMetrics dm = getResources().getDisplayMetrics();
			int scaleX = width / dm.widthPixels;
			int scaleY = height / dm.heightPixels;
			int scale = 1;
			if(scaleX > scaleY)
				scale = scaleX > 1 ? scaleX : 1;
			else if(scaleX < scaleY)
				scale = scaleY > 1 ? scaleY : 1;
			
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = scale;
			
			return BitmapFactory.decodeFile(path, opts);
		}catch(Exception e){
			ToastUtil.ToastMessage(context, "打开失败！" + e.getMessage());
			return null;
		}
	}
	
}
