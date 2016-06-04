package y2w.ui.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.listview.ILoadingLayout;
import com.y2w.uikit.customcontrols.listview.PullToRefreshBase;
import com.y2w.uikit.customcontrols.listview.PullToRefreshListView;
import com.y2w.uikit.customcontrols.record.RecordButton;
import com.y2w.uikit.utils.Commethod;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.utils.LogUtil;

import y2w.base.Urls;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import y2w.base.AppData;
import y2w.common.CallBackUpdate;
import y2w.common.Config;
import y2w.common.Constants;
import y2w.common.SendUtil;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.UserConversation;
import y2w.model.messages.MessageCrypto;
import y2w.model.messages.MessageType;
import y2w.service.Back;
import y2w.service.FileSrv;
import y2w.ui.adapter.FragmentAdapter;
import y2w.ui.adapter.MessageAdapter;
import y2w.ui.fragment.MessageFragment;
import y2w.ui.widget.emoji.ChatEmoji;
import y2w.ui.widget.emoji.Expression;

/**
 * Created by hejie on 2016/3/14.
 * 聊天界面
 */
public class ChatActivity extends FragmentActivity {
	private String TAG = ChatActivity.class.getSimpleName();
	private EditText et_input;
	private RecordButton rb_voice;
	private PullToRefreshListView lv_message;
	private FrameLayout fl_record;
	private ImageView iv_record_volume;
	private ImageButton ib_add;
	private ImageButton ib_send;
	private ImageButton ib_emoji;
	private ImageButton ib_voice;
	private boolean isfreshlist = false;

	private ViewPager vp_emoji_pager;

	private RelativeLayout rl_more;
	private ViewPager vp_add;
	private ViewPager vp_emoji;
	private GridView gv_emoji_index;
	private GridView gv_emoji_menu;
	private LinearLayout ll_emoji_menu;
    private synMessagedate synMessagedate;
	/**adapter**/
	private MessageAdapter adapter;
	private FragmentAdapter pagerAdapter;
	static public Context _context;
	private List<MessageModel> messageList = new ArrayList<MessageModel>();
	public static Handler chatHandler;
	private Session _session;
	private String sessionType,sessionId;
	private String othername,otheruserID;
	private boolean scrollbool = false;
	private CallBackUpdate callBackUpdate;
	private ChatEmoji chatEmoji;
	public  int titleIndex = 0;
	GestureDetector mGestureDetector;
	MessageModel firstVisibleMM;
	//界面更新
	Handler updateChatHandler= new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what){
				case 0:
					if (adapter == null) {
						adapter = new MessageAdapter(_context, _session);
						lv_message.setAdapter(adapter);
					} else {
						adapter.setSession(_session);
					}
					if(firstload) {
						firstload =false;
						getLastMessage();
					}
					break;
				case 1:
					break;
				case 2://有新的消息
					if(messageLoading){
						hasNewMessage = true;
					}else{
						loadNewMessage(false);
					}
					break;
				case 3:
					MessageModel entity = (MessageModel) msg.obj;
					boolean find = false;
					for(int i =0;i<messageList.size();i++){
						if(messageList.get(i).getEntity().getId().equals(entity.getEntity().getId())){
							find = true;
							break;
						}
					}
					if(find){
						messageList.remove(entity);
					}
					lv_message.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
					refreshComplete();
					adapter.updateListView(messageList);
					break;
				case 4:
					Expression.emojiEditTextDisplay(_context, et_input,
							msg.obj.toString(),
							Expression.dip2px(_context, Expression.WH_0));
					break;
				case 5:
					Expression.editTextExpressionDelete(et_input);
					break;
				case 6:
					et_input.setText(et_input.getText() + msg.obj.toString());
					break;
				case 7:
					fl_record.setVisibility(View.GONE);
					rb_voice.setText("按住 说话");
					break;
				default:
					break;
			}
		}
	};


	class ChatThread extends Thread {
		@Override
		public void run() {
			try {
				// 建立消息循环的步骤
				Looper.prepare();// 1、初始化Looper
				chatHandler = new Handler() {// 2、绑定handler到ChatThread实例的Looper对象
					public void handleMessage(Message msg) {// 3、定义处理消息的方法
						switch (msg.what) {
							case RefreshCode.CODE_EMOJI_ADD:// 表情添加
									Message message = new Message();
									message.what = 4;
									message.obj = msg.obj.toString();
									updateChatHandler.sendMessage(message);
								break;
							case RefreshCode.CODE_EMOJI_DELETE:// 表情删除
									message = new Message();
									message.what = 5;
									updateChatHandler.sendMessage(message);
								break;
							case RefreshCode.CODE_OTHER_AT:// 引用
								message = new Message();
								message.what = 6;
								message.obj = msg.obj;
								updateChatHandler.sendMessage(message);
								break;
						}
					}
				};
				Looper.loop();// 4、启动消息循环
			} catch (Exception ex) {

			}
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyy_message_fragment);
		_context = this;
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getExtras(this.getIntent().getExtras());
		controlsInit();
		listenerInit();
		initActionBar();
		callBackUpdate = new CallBackUpdate(updateChatHandler);
		callBackUpdate.setId(sessionId);
		AppData.getInstance().getUpdateHashMap().put(CallBackUpdate.updateType.chatting.toString(), callBackUpdate);
		loadSession();
		initEmoji();
		new ChatThread().start();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getExtras(intent.getExtras());
		if(synMessagedate!=null&& synMessagedate.isAlive()){
			synMessagedate.interrupt();
			synMessagedate=null;
		}

		issyndate =false;
		hasNewMessage =true;
		messageList.clear();

		if(adapter == null){
			return;
		}
		refreshComplete();
		adapter.updateListView(messageList);
		texttitle.setText(othername);
		if(EnumManage.SessionType.p2p.toString().equals(sessionType)){
			imageButtongroup.setVisibility(View.GONE);
		}else{
			imageButtongroup.setVisibility(View.VISIBLE);
		}
		callBackUpdate.setId(sessionId);
		loadSession();
	}

	/*
	*自定义aciontbar
	*/
	private TextView texttitle;
	private ImageButton imageButtongroup;
	private void initActionBar(){
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setCustomView(R.layout.actionbar_chat);
		 texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
		ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
		 imageButtongroup = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_add);
		texttitle.setText(othername);
		if(EnumManage.SessionType.p2p.toString().equals(sessionType)){
			imageButtongroup.setVisibility(View.GONE);
		}else{
			imageButtongroup.setVisibility(View.VISIBLE);
		}

		imageButtonclose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		imageButtongroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(_session==null){
				   ToastUtil.ToastMessage(_context,"正在加载初始数据");
					return;
				}
				Intent intent = new Intent(_context,GroupInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("sessionId", _session.getEntity().getId());
				bundle.putString("sessionName", _session.getEntity().getName());
				bundle.putString("sessionAvatar", _session.getEntity().getAvatarUrl());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
    private boolean firstload =true;
	private void getExtras(Bundle bundle){
		if(bundle != null) {
			sessionType =bundle.getString("sessiontype");
			sessionId =bundle.getString("sessionid");
			othername = bundle.getString("name");
			otheruserID = bundle.getString("otheruserId");
			firstload =true;
		}
	}

	private void initEmoji(){
		chatEmoji = new ChatEmoji(this,this,_session,this.getSupportFragmentManager());
		chatEmoji.initView(vp_emoji,gv_emoji_menu,gv_emoji_index);
	}
    private void loadSession(){
		Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
			@Override
			public void onSuccess(Session session) {
				_session = session;
				updateChatHandler.sendEmptyMessage(0);
			}

			@Override
			public void onError(int Code, String error) {
			}
		});

		String targetId = EnumManage.SessionType.p2p.toString().equals(sessionType) ? otheruserID:sessionId;
		Users.getInstance().getCurrentUser().getSessions().getRemote().getSession(targetId, sessionType, new Back.Result<Session>() {
			@Override
			public void onSuccess(Session session) {
				if (_session == null) {
					_session = session;
				} else {
					_session.getEntity().setId(session.getEntity().getId());
					_session.getEntity().setType(session.getEntity().getType());
					_session.getEntity().setAvatarUrl(session.getEntity().getAvatarUrl());
					_session.getEntity().setUpdatedAt(session.getEntity().getUpdatedAt());
					_session.getEntity().setDescription(session.getEntity().getDescription());
					_session.getEntity().setName(session.getEntity().getName());
					_session.getEntity().setOtherSideId(session.getEntity().getOtherSideId());
					_session.getEntity().setSecureType(session.getEntity().getSecureType());
				}
				updateChatHandler.sendEmptyMessage(0);
				syncMembers();
			}

			@Override
			public void onError(int Code, String error) {
				if (_session == null) {
					ToastUtil.ToastMessage(_context, "初始化失败,请重新打开");
				}
			}
		});
	}
	private void controlsInit(){
		lv_message = (PullToRefreshListView) findViewById(R.id.messageListView);
		fl_record = (FrameLayout) findViewById(R.id.fl_record_volume);
		iv_record_volume = (ImageView) findViewById(R.id.iv_record_volume);
		et_input = (EditText) findViewById(R.id.et_input);
		rb_voice = (RecordButton) findViewById(R.id.rb_voice);
		ib_voice = (ImageButton) findViewById(R.id.ib_keybord_voice);
		ib_emoji = (ImageButton) findViewById(R.id.ib_emoji);
		ib_add = (ImageButton) findViewById(R.id.ib_add);
		ib_send = (ImageButton) findViewById(R.id.ib_send);
		ib_emoji = (ImageButton) findViewById(R.id.ib_emoji);

		rl_more = (RelativeLayout) findViewById(R.id.rl_message_more);
		vp_add = (ViewPager) findViewById(R.id.vp_message_add_viewPager);
		vp_emoji = (ViewPager) findViewById(R.id.vp_message_emoji_viewPager);

		gv_emoji_index = (GridView) findViewById(R.id.gv_expression_index);
		gv_emoji_menu = (GridView) findViewById(R.id.gv_expression_menu);
		ll_emoji_menu = (LinearLayout) findViewById(R.id.ll_emoji_menu);
		initfreshlistview();
		//show_send();


	}

	private void initfreshlistview(){
		lv_message.setFooterLoadingViewAble(false);
		lv_message.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {

			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
			}
		});

		lv_message.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
			}
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				if(messageList != null && messageList.size() > arg1){
					firstVisibleMM = messageList.get(arg1);
				}
				if(arg1 == 0 && !scrollbool){
					scrollbool = true;
					refreshMore();
				}
			}
		});

		lv_message.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				lv_message.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
			}
		});
		ILoadingLayout startLabels = lv_message
				.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("正在载入...");// 刷新时
		startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

	}

	private void show_send(){
		ib_add.setVisibility(View.GONE);
		ib_send.setVisibility(View.VISIBLE);
	}
	private void listenerInit(){
		et_input.addTextChangedListener(new messageInputTextChanged());
		et_input.setOnTouchListener(new inputTextTouchListener());
		ib_voice.setOnClickListener(new onViewClick());
		ib_send.setOnClickListener(new onViewClick());
		ib_add.setOnClickListener(new onViewClick());
		ib_emoji.setOnClickListener(new onViewClick());
		rb_voice.setOnRecordListener(new onRecordListener());
	}
	   /*
        ***获取成员
        */
	private void syncMembers(){
		_session.getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
			@Override
			public void onSuccess(List<SessionMember> sessionMembers) {
			}
			@Override
			public void onError(int Code, String error) {
			}
		});
	}
	/*
	***进入界面获得同步消息
	*/
	private void getLastMessage(){
		if(_session.getMessages().getMessageUpdateAt().equals(Constants.TIME_ORIGIN)){//第一次同步消息
			recentMessage();
			return;
		}
		String targetId = EnumManage.SessionType.p2p.toString().equals(sessionType) ? otheruserID:sessionId;
		UserConversation userConversation =Users.getInstance().getCurrentUser().getUserConversations().get(targetId);
	 if(userConversation.getEntity()!=null){
		int unread = userConversation.getEntity().getUnread();
		if(unread>0) {
			if (unread > Config.LOAD_MESSAGE_COUNT) {//消息太多先显示最近一页
				recentMessage();
				return;
			} else {
				if(unread<Config.LOAD_MESSAGE_COUNT)
					unread = Config.LOAD_MESSAGE_COUNT;
				_session.getMessages().getRemote().sync(true,_session.getMessages().getMessageUpdateAt(), unread, new Back.Result<List<MessageModel>>() {
					@Override
					public void onSuccess(List<MessageModel> models) {
						setMessagenum(models.size());
						isfreshlist = true;
						updatelocal(Config.LOAD_MESSAGE_COUNT);
					}

					@Override
					public void onError(int errorCode,String error) {
						isfreshlist = true;
						updatelocal(Config.LOAD_MESSAGE_COUNT);
					}
				});
				return;
			}
		}
	}
		loadNewMessage(true);
		isfreshlist = true;
	}

	//获得最近消息
	private void recentMessage(){
		_session.getMessages().getRemote().getLastMessages(Config.LOAD_MESSAGE_COUNT, new Back.Result<List<MessageModel>>() {
			@Override
			public void onSuccess(List<MessageModel> messageModels) {
				messageList.clear();
				messageList.addAll(messageModels);
				refreshMessageList();
				scrollToBottom();
			}
			@Override
			public void onError(int errorCode,String error) {
			}
		});
		synMessagedate =new synMessagedate();
		synMessagedate.start();
	}
    /******************************************监听*******************************************/
	private final class messageInputTextChanged implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {
				if (s.length() > 0) {
					ib_add.setVisibility(View.GONE);
					ib_send.setVisibility(View.VISIBLE);
				} else {
					//show_send();
					ib_add.setVisibility(View.VISIBLE);
					ib_send.setVisibility(View.GONE);
				}
		}
	}

	private class onViewClick implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v.getId() == ib_send.getId()){
				sendMessage();
			}else if(v.getId() == ib_add.getId()){
				addOnclick();
			}else if(v.getId() == ib_voice.getId()){
				show();
				voiceOnclick();
			}else if(v.getId() == ib_emoji.getId()){
				emojiOnclick();
			}
		}
	}

	private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
		@Override
		public void onPageSelected(int arg0) {
		}
	}

	private void show(){
		String permission = "android.permission.RECORD_AUDIO"; //你要判断的权限名字
		int res =_context.checkCallingOrSelfPermission(permission);
		if (res == PackageManager.PERMISSION_GRANTED) {
			ToastUtil.ToastMessage(_context,"有这个权限");
		}else {
			ToastUtil.ToastMessage(_context, "木有这个权限");
		}

	}
	private boolean isCancel;
	private class onRecordListener implements RecordButton.OnRecordListener {
		@Override
		public void onRecordStart() {
			rb_voice.setText("松开 发送");
			iv_record_volume.setBackgroundResource(R.drawable.record_volume1);
			fl_record.setVisibility(View.VISIBLE);
		}
		@Override
		public void onCancel() {
			isCancel = true;
			iv_record_volume.setBackgroundResource(R.drawable.record_cancel);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					updateChatHandler.sendEmptyMessage(7);
				}
			}).start();

		}

		@Override
		public void onSuccess(String path, int code) {
			switch (code) {
				case RecordButton.RECORD_LESS_THAN_MIN:
					ToastUtil.ToastMessage(ChatActivity.this, "录音时间太短。");
				case RecordButton.RECORD_CANCEL:
					fl_record.setVisibility(View.GONE);
					new File(path).deleteOnExit();
					rb_voice.setText("按住 说话");
					break;
				case RecordButton.RECORD_TIMEOUT:
					ToastUtil.ToastMessage(ChatActivity.this, "录音达到最大长度。");
				case RecordButton.RECORD_NORMAL:
					LogUtil.getInstance().log(TAG, "path =" + path, null);
					sendVoice(path);
					fl_record.setVisibility(View.GONE);
					rb_voice.setText("按住 说话");
					break;
				default:
					break;
			}
		}

		@Override
		public void onError(String msg) {
			fl_record.setVisibility(View.GONE);
		}

		@Override
		public void onVolumeChange(int volume) {
			if (rb_voice.isCancel() || isCancel) {
				// btn_voice.setBackgroundResource(R.drawable.record_cancel);
				return;
			}
			switch (volume) {
				case 0:
					iv_record_volume.setBackgroundResource(R.drawable.record_volume1);
					break;
				case 1:
					iv_record_volume.setBackgroundResource(R.drawable.record_volume1);
					break;
				case 2:
					iv_record_volume.setBackgroundResource(R.drawable.record_volume2);
					break;
				case 3:
					iv_record_volume.setBackgroundResource(R.drawable.record_volume3);
					break;
				case 4:
					iv_record_volume.setBackgroundResource(R.drawable.record_volume4);
					break;
				case 5:
					iv_record_volume.setBackgroundResource(R.drawable.record_volume5);
					break;
				case 6:
					iv_record_volume.setBackgroundResource(R.drawable.record_volume6);
					break;
			}
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (rl_more.getVisibility() == View.VISIBLE) {
				rl_more.setVisibility(View.GONE);
				return false;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	// 输入框焦点发送变化
	private final class inputTextTouchListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			//scrollToBottom();
			lv_message.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			rl_more.setVisibility(ViewPager.GONE);
			return false;
		}
	}
	/**********************************************click*******************************************/
	private void addOnclick(){
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(et_input.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		if(vp_add.getVisibility() == View.VISIBLE){
			vp_add.setVisibility(View.GONE);
			rl_more.setVisibility(View.GONE);
		}else{
			scrollToBottom();
			vp_add.setVisibility(View.VISIBLE);
			rl_more.setVisibility(View.VISIBLE);
			ll_emoji_menu.setVisibility(View.GONE);
			initMoreViewPager();
		}
	}

	private void voiceOnclick(){
		if(et_input.getVisibility() == View.VISIBLE){
			et_input.setVisibility(View.GONE);
			rb_voice.setVisibility(View.VISIBLE);
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(et_input.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}else{
			et_input.setVisibility(View.VISIBLE);
			rb_voice.setVisibility(View.GONE);
		}

	}

	private void emojiOnclick(){
		if(vp_emoji.getVisibility() == View.VISIBLE){
			vp_emoji.setVisibility(View.GONE);
			rl_more.setVisibility(View.GONE);
		}else{
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(et_input.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			vp_emoji.setVisibility(View.VISIBLE);
			rl_more.setVisibility(View.VISIBLE);
			ll_emoji_menu.setVisibility(View.VISIBLE);
		}
		chatEmoji.initExpressionViewPager();
	}
	public void initMoreViewPager() {
		List<Fragment> fragmentList = new ArrayList<Fragment>();
		Fragment firstFragment = MessageFragment.newInstance(this, _context,_session,
				"add_first");
		fragmentList.add(firstFragment);


		pagerAdapter = new FragmentAdapter(
				this.getSupportFragmentManager(), fragmentList);

		vp_add.setAdapter(pagerAdapter);
		vp_add.setCurrentItem(0);
		vp_add.setOnPageChangeListener(new MyOnPageChangeListener());
		//setPagerIndex(1, 0);
		vp_add.setVisibility(View.VISIBLE);

		vp_emoji.setVisibility(View.GONE);
	}

	/**********************************************function*******************************************/
	//发送文字消息
	private void sendMessage(){
		if(_session==null){
			ToastUtil.ToastMessage(_context, "正在加载初始数据");
			return;
		}
		String sendtext = et_input.getText().toString().trim();
		if(sendtext.length()==0) {
			et_input.setText("");
			return;
		}
		final MessageModel temp = _session.getMessages().createMessage(MessageCrypto.getInstance().encryText(sendtext), MessageType.Text);
		messageList.add(temp);
		scrollToBottom();
		refreshComplete();
		adapter.updateListView(messageList);
		et_input.setText("");
		_session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
			@Override
			public void onSuccess(MessageModel model) {
				temp.getEntity().setId(model.getEntity().getId());
				updateChatHandler.sendEmptyMessage(2);
			}

			@Override
			public void onError(int errorCode, String error) {
				//model.getEntity().setStatus(MessageEntity.MessageState.storeFailed.toString());
			}
		});

	}
   //发送图片消息
	private void sendPictures(String filepath){
		if(_session==null){
			ToastUtil.ToastMessage(_context,"正在加载初始数据");
			return;
		}
		//压缩
		String compressPath = SendUtil.compressOriginPicture(_context, filepath);

		final MessageModel temp = _session.getMessages().createMessage(compressPath, MessageType.Image);
		messageList.add(temp);
		scrollToBottom();
		refreshComplete();
		adapter.updateListView(messageList);

		FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, compressPath);
	}
	//发送语音消息
	private void sendVoice(final String voicePath) {
		try {
			if(StringUtil.isEmpty(voicePath)){
				return;
			}
			File file = new File(voicePath);
			if (file.exists()) {
				final MessageModel temp = _session.getMessages().createMessage(voicePath, MessageType.Audio);
				messageList.add(temp);
				scrollToBottom();
				refreshComplete();
				adapter.updateListView(messageList);
				FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, voicePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//发送小视频消息
	private void sendMovie(final String moviePath) {
		try {
			File file = new File(moviePath);
			if (file.exists()) {
				final MessageModel temp = _session.getMessages().createMessage(moviePath, MessageType.Video);
				messageList.add(temp);
				scrollToBottom();
				refreshComplete();
				adapter.updateListView(messageList);
				FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, moviePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//发送地理位置消息
	private void sendLocation(final String filePath) {
		try {
			String[] result = filePath.split(";");
			if(result.length < 1){
				return;
			}
			File file = new File(result[0]);
			if (file.exists()) {
				final MessageModel temp = _session.getMessages().createMessage(filePath, MessageType.Location);
				messageList.add(temp);
				scrollToBottom();
				refreshComplete();
				adapter.updateListView(messageList);
				FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, file.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendFile(final String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				final MessageModel temp = _session.getMessages().createMessage(filePath, MessageType.File);
				messageList.add(temp);
				scrollToBottom();
				refreshComplete();
				adapter.updateListView(messageList);
				FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, filePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> imagesChoose = new ArrayList<String>();
	private List<String> fileChoose = new ArrayList<String>();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ResultCode.CODE_IMAGE) { //图片
			if (data != null) {
				imagesChoose = data.getStringArrayListExtra("result");
				if (imagesChoose != null && imagesChoose.size() > 0) {
					for (String string : imagesChoose) {
						sendPictures(string);
					}
				}
			}
		}else if (requestCode == ResultCode.CODE_MOVIE){ //小视频
			if(data != null) {
				String moviePath = data.getStringExtra("result");
				sendMovie(moviePath);
				ToastUtil.ToastMessage(_context, "movie:" + moviePath);
			}
		}else if (requestCode == ResultCode.CODE_FILE){ //文档
			if(data != null) {
				fileChoose = data.getStringArrayListExtra("result");
				for(String path : fileChoose){
					sendFile(path);
					LogUtil.getInstance().log(TAG,path,null);
				}
			}
		}else if (requestCode == ResultCode.CODE_LOCATION){ //位置
			if(data != null) {
				String locationPath = data.getStringExtra("result");
				sendLocation(locationPath);
				LogUtil.getInstance().log(TAG,locationPath,null);
			}
		}
	}
	// 刷新消息列表
	public void refreshMessageList() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lv_message.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
				//int position =lv_message.getRefreshableView().getSelectedItemPosition();
				//position=position+messageList.size()-adapter.getCount();
				//lv_message.getRefreshableView().setSelection(position);
				refreshComplete();
				adapter.updateListView(messageList);
			}
		});
	}
	public void scrollToBottom(){
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (messageList != null && messageList.size() > 0) {
					lv_message.getRefreshableView().setSelection(
							messageList.size() - 1);
				}
			}
		});
	}

	private void refreshMore(){
		localMore();
		refreshComplete();
	}


	private void localMore(){
		try {
			if(messageList.size() < 1){
				return ;
			}
			MessageModel firstModel;
			if (messageList.size() == 0) {
				firstModel = null;
			} else {
				firstModel = messageList.get(0);
			}
			List<MessageModel> list =_session.getMessages().getMessages(firstModel, Config.LOAD_MESSAGE_COUNT);
            if(list.size()<Config.LOAD_MESSAGE_COUNT){
				lv_message.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
			}
			if(list.size() > 0){
				messageList.addAll(0, list);
				lv_message.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
				adapter.updateListView(messageList);

				for (int i = 0; i < messageList.size(); i++) {
					MessageModel mm = messageList.get(i);
					if(firstVisibleMM != null){
						if(firstVisibleMM.getEntity().getId().equals(mm.getEntity().getId())) {
							lv_message.getRefreshableView().setSelection(i+2);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			Log.i("more", "e:"+e);
		}
	}

	private void refreshComplete() {
		if (lv_message != null) {
			Commethod.setTimout(new Commethod.Action() {

				@Override
				public void exec() {
					lv_message.onRefreshComplete();
				}
			}, 100);

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(220);
						scrollbool = false;
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}).start();
		}
	}

	private void updatelocal(int count){
		MessageModel firstModel;
		if (messageList.size() == 0) {
			firstModel = null;
		} else {
			firstModel = messageList.get(0);
		}
		List<MessageModel> list =_session.getMessages().getMessages(firstModel,count);
		messageList.addAll(0, list);
		if(messageList.size()>0) {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					lv_message.getRefreshableView().setSelection(messageList.size() - 1);
					refreshComplete();
					adapter.updateListView(messageList);
				}
			});
		}
	}
	private void setMessagenum(int count){
		if(count==0)
            return;
		String targetId = EnumManage.SessionType.p2p.toString().equals(sessionType) ? otheruserID:sessionId;
		UserConversation userConversation =Users.getInstance().getCurrentUser().getUserConversations().get(targetId);
		if(userConversation.getEntity()!=null){
			int unread = userConversation.getEntity().getUnread();
			unread =unread-count;
			if(unread<0)
				unread =0;
			userConversation.getEntity().setUnread(unread);
			_session.getSessions().getUser().getUserConversations().addUserConversation(userConversation);
		}
	}
	/*
	***消息太多 同步处理
	 */
	boolean issyndate =false;//是否正在同步
	boolean hasNewMessage =true;//有新的消息
	private boolean messageLoading =false;//是否正在更新数据
	class synMessagedate extends Thread{//同步消息
		@Override
		public void run() {
			super.run();
			issyndate =true;
			_session.getMessages().getRemote().sync(true,_session.getMessages().getMessageUpdateAt(), Config.LOAD_MESSAGE_COUNT, new Back.Result<List<MessageModel>>() {
					@Override
					public void onSuccess(List<MessageModel> models) {
						if (models.size() < Config.LOAD_MESSAGE_COUNT) {//同步完成
							issyndate=false;
							isfreshlist = true;
							int count = messageList.size();
							if(count<Config.LOAD_MESSAGE_COUNT)
								count =Config.LOAD_MESSAGE_COUNT;
							messageList.clear();
							updatelocal(count);
						} else {
							run();
						}
						setMessagenum(models.size());
					}
					@Override
					public void onError(int errorCode,String error) {
					}
				});
		}
	}
     //是否重新清空刷新
	private void loadNewMessage(final boolean isreload){
			final int unread = 50;
		    messageLoading = true;
		    hasNewMessage = false;
			_session.getMessages().getRemote().sync(!issyndate,_session.getMessages().getMessageUpdateAt(), unread, new Back.Result<List<MessageModel>>() {
				@Override
				public void onSuccess(List<MessageModel> models) {
					if(isreload&&!issyndate){
						int count = messageList.size();
						if(count<Config.LOAD_MESSAGE_COUNT)
							count =Config.LOAD_MESSAGE_COUNT;
						messageLoading = false;
						messageList.clear();
						updatelocal(count);
						return;
					}
					boolean istobottom =false;
					int lastnum =lv_message.getRefreshableView().getLastVisiblePosition();
					if(lastnum >=(messageList.size()-1))
						istobottom =true;
					if(models!=null&&models.size()>0){
						boolean find =false;
						List<MessageModel> tempmodels = new ArrayList<MessageModel>();
						for(int i =0;i<messageList.size();i++){
							if(!find) {
								if (models.get(0).getEntity().getId().equals(messageList.get(i).getEntity().getId())) {
									find = true;
									tempmodels.add(messageList.get(i));
								}
							}else{
								tempmodels.add(messageList.get(i));
							}
						}
						if(find){
							messageList.removeAll(tempmodels);
						}
						messageList.addAll(models);
						refreshMessageList();
						if(istobottom)
							scrollToBottom();
					}
					if(!issyndate){
						setMessagenum(models.size());
					}
					if(models==null||models.size()<unread){
						messageLoading = false;
						if(hasNewMessage){
							loadNewMessage(false);
						}
					}else{
						loadNewMessage(false);
					}
				}
				@Override
				public void onError(int errorCode,String error) {
					messageLoading = false;
					if(hasNewMessage){
						loadNewMessage(false);
					}
				}
			});
			return;
	}


	@Override
	public void finish() {
		super.finish();
		try {
			AppData.getInstance().getUpdateHashMap().remove(CallBackUpdate.updateType.chatting.toString());
			AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.userConversation.toString()).updateUI();
			if(synMessagedate!=null&& synMessagedate.isAlive()){
				synMessagedate.interrupt();
				synMessagedate=null;
			}
			rb_voice.stopRecording();
		}catch(Exception e){
		}
	}

	public static class ResultCode{
		public static final int CODE_IMAGE = 1;
		public static final int CODE_MOVIE = 2;
		public static final int CODE_FILE = 3;
		public static final int CODE_LOCATION = 4;
		public static final int CODE_VIDEO_CALL = 5;
	}

	public static class RefreshCode{
		public static final int CODE_EMOJI_ADD = 101;
		public static final int CODE_EMOJI_DELETE = 102;
		public static final int CODE_OTHER_AT = 103;

	}
}
