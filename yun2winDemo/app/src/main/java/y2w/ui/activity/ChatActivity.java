package y2w.ui.activity;

import android.app.ActionBar;
import android.app.NotificationManager;
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

import com.alibaba.fastjson.JSONObject;
import com.y2w.uikit.customcontrols.listview.ILoadingLayout;
import com.y2w.uikit.customcontrols.listview.PullToRefreshBase;
import com.y2w.uikit.customcontrols.listview.PullToRefreshListView;
import com.y2w.uikit.customcontrols.record.RecordButton;
import com.y2w.uikit.utils.Commethod;
import com.y2w.uikit.utils.ImageUtil;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ThreadPool;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.utils.Json;
import com.yun2win.utils.LogUtil;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.base.Urls;
import y2w.common.CallBackUpdate;
import y2w.common.Config;
import y2w.common.Constants;
import y2w.common.NoticeUtil;
import y2w.common.SendUtil;
import y2w.db.DaoManager;
import y2w.db.UserConversationDb;
import y2w.entities.LocalFileEntity;
import y2w.entities.MessageEntity;
import y2w.entities.SessionMemberEntity;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.SyncMessagesModel;
import y2w.model.UserConversation;
import y2w.model.messages.MessageCrypto;
import y2w.model.messages.MessageType;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.FileSrv;
import y2w.ui.adapter.FragmentAdapter;
import y2w.ui.adapter.MessageAdapter;
import y2w.ui.fragment.MessageFragment;
import y2w.ui.widget.emoji.ChatEmoji;
import y2w.ui.widget.emoji.Expression;

/**
 * Created by hejie on 2016/3/14.
 * 交流界面
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
	private String targetId;
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
	Handler userConversationHandeler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
            if(msg.arg1 ==1){
				setMessagenum(0);
				if(!StringUtil.isEmpty(startCreateTime)){
					afterlocal(startCreateTime,true,false);
				}else{
					updatelocal(Config.LOAD_MESSAGE_COUNT,true,true);
				}
				UserConversation userConversation = (UserConversation) msg.obj;
				getLastMessage(userConversation);
				if(userConversation!=null&&userConversation.getEntity()!=null) {
					String extraDate = userConversation.getEntity().getExtraDate();
					if (et_input != null && !StringUtil.isEmpty(extraDate)) {
						Json json = new Json(extraDate);
						et_input.setText(json.getStr("draftmsg"));
					}
				}
			}else if(msg.arg1==2){

			}else if(msg.arg1 ==3){//弹出输入键盘获得焦点
				et_input.setFocusable(true);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				et_input.setSelection(msg.arg2);
			}else if(msg.arg1==4){//任务消息
				String sendtext = et_input.getText().toString().trim();
				if(StringUtil.isEmpty(sendtext) || !sendtext.startsWith("/task")){
					sendtext = "/task"+sendtext;
				}
				rl_more.setVisibility(ViewPager.GONE);
				et_input.setText(sendtext);
				Message msginput = new Message();
				msginput.arg1=3;
				msginput.arg2 = sendtext.length();
				userConversationHandeler.sendMessageDelayed(msginput,200);
			}
		}
	};
	//界面更新
	Handler updateChatHandler= new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what){
				case 0:
					adapter.setSession(_session);
					if(firstload) {
						firstload =false;
						final String targetId = EnumManage.SessionType.p2p.toString().equals(sessionType) ? otheruserID:sessionId;
						ThreadPool.getThreadPool().executUI(new Runnable() {
							@Override
							public void run() {
								UserConversation userConversation = Users.getInstance().getCurrentUser().getUserConversations().get(targetId,sessionType);
								Message msgconversation = new Message();
								msgconversation.obj = userConversation;
								msgconversation.arg1 = 1;
								userConversationHandeler.sendMessage(msgconversation);
							}
						});
					}
					break;
				case 1:
					break;
				case 2://有新的消息
					String sessionId = (String) msg.obj;
					if(_session!=null && !StringUtil.isEmpty(sessionId)) {
						if(sessionId.equals("updatemessage") || sessionId.indexOf(_session.getEntity().getId())!=-1) {
							if (messageLoading) {
								hasNewMessage = true;
							} else {
								loadNewMessage();
							}
						}
					}else{
						hasNewMessage = true;
					}
					break;
				case 3:
					MessageModel entity = (MessageModel) msg.obj;
					for(int i =0;i<messageList.size();i++){
						if(messageList.get(i).getEntity().getId().equals(entity.getEntity().getId())){
							messageList.get(i).getEntity().setIsDelete(true);
							break;
						}
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
					String userid = msg.obj.toString();
					if(!StringUtil.isEmpty(userid)) {
						boolean find = false;
						if(memberEntitys!=null&&memberEntitys.size()>0){
							for(int i = 0;i<memberEntitys.size();i++){
								if(memberEntitys.get(i).getUserId().equals(userid)){
									find = true;
									et_input.setText(et_input.getText() + " @"+memberEntitys.get(i).getName()+" ");
									et_input.setSelection(et_input.getText().length());
									break;
								}
							}
						}
                        if(!find) {
							if (members != null && members.size() > 0) {
								for (int i = 0; i < members.size(); i++) {
									if (members.get(i).getEntity().getUserId().equals(userid)) {
										memberEntitys.add(members.get(i).getEntity());
										et_input.setText(et_input.getText() + " @"+members.get(i).getEntity().getName()+" ");
										et_input.setSelection(et_input.getText().length());
										break;
									}
								}
							}
						}

					}
					break;
				case 7:
					fl_record.setVisibility(View.GONE);
					rb_voice.setText("按住 说话");
					break;
				case 8://消息推到底部
					if (messageList != null && messageList.size() > 0) {
						lv_message.getRefreshableView().setSelection(
								messageList.size());
					}
					//refreshComplete();
				case 9://同步会话和成员
					String mysessionID = (String) msg.obj;
					if(_session!=null && !StringUtil.isEmpty(mysessionID)) {
						if(mysessionID.indexOf(_session.getEntity().getId())!=-1) {
							if(syncSessionADmembers){//正在同步等同步完成再同步
								needSyncADmembers = true;
							}else{
								syncSessionADmembers();
							}
						}
					}
					break;
				default:
					break;
			}
		}
	};
  //消息变动
	Handler messageChangeHandler = new Handler(){
	  @Override
	  public void handleMessage(Message msg) {
		  super.handleMessage(msg);
         if(msg.what ==1){//添加一条消息
           MessageModel temp = (MessageModel) msg.obj;
			 if(temp!=null){
				 messageList.add(temp);
				 adapter.updateListView(messageList);
				 updateChatHandler.sendEmptyMessageDelayed(8, 300);
			 }
		 }else if(msg.what ==2){//拉历史消息
			 List<MessageModel> list  = (List<MessageModel>) msg.obj;
			 if(list!=null&&list.size()>0) {
				 localMorerefresh(list);
			 }
		 }else if(msg.what ==3){//重新获取消息
			 List<MessageModel> list  = (List<MessageModel>) msg.obj;
			 if(list!=null&&list.size()>0) {
				 messageList.clear();
				 messageList.addAll(list);
				 refreshMessageList();
				 updateChatHandler.sendEmptyMessageDelayed(8, 300);
			 }
		 }else if(msg.what ==4){
			 List<MessageModel> list  = (List<MessageModel>) msg.obj;
            int isclear = msg.arg1;
			 int istobottom = msg.arg2;
			 if(list!=null&&list.size()>0) {
				 if(isclear ==1){
					 messageList.clear();
					 messageList.addAll(list);
				 }else{
					 messageList.addAll(0, list);
				 }
				 refreshMessageList();
				 if(istobottom==1) {
					 updateChatHandler.sendEmptyMessageDelayed(8, 300);
				 }
			 }
		 }else if(msg.what ==5){
			 List<MessageModel> tmpMessages  = (List<MessageModel>) msg.obj;
			 int istobottom = msg.arg1;
			 messageList.addAll(tmpMessages);
			 refreshMessageList();
			 if(istobottom==1) {
				 updateChatHandler.sendEmptyMessageDelayed(8, 300);
			 }
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
		try{
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
			clearCurSessionNotice();
		}catch (Exception e){
		}
	}

	/**
	 * 若当前会话有通知栏，去掉
	 */
	private void clearCurSessionNotice() {
		if (NoticeUtil.WHOSE.equals(sessionId)) {// 去掉通知栏
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			mNotificationManager.cancel(NoticeUtil.NOTIFICATION_ID);
			NoticeUtil.WHOSE = "NONE";
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
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
		messageList = new ArrayList<MessageModel>();

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
		TextView tv_right_oper = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);
		if(EnumManage.SessionType.p2p.toString().equals(sessionType)){
			imageButtongroup.setVisibility(View.GONE);
			tv_right_oper.setVisibility(View.VISIBLE);
			tv_right_oper.setText("文件");
			tv_right_oper.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ChatActivity.this, StrongWebViewActivity.class);
					intent.putExtra("webUrl", Config.File_Host+"?title=来往文件&sessionId="+sessionId+"&token="+Users.getInstance().getCurrentUser().getToken());
					startActivity(intent);
				}
			});
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
				if(_session==null || _session.getEntity()==null){
				   ToastUtil.ToastMessage(_context,"正在加载初始数据");
					return;
				}
				Intent intent = new Intent(_context,GroupInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("sessionId", _session.getEntity().getId());
				bundle.putString("sessionName", _session.getEntity().getName());
				bundle.putString("sessionAvatar", _session.getEntity().getAvatarUrl());
				intent.putExtras(bundle);
				startActivityForResult(intent,ResultCode.CODE_SESSION_NAME_CHANGED);
			}
		});
	}
    private boolean firstload =true;
	private String startCreateTime;
	private void getExtras(Bundle bundle){
		if(bundle != null) {
			sessionType =bundle.getString("sessiontype");
			sessionId =bundle.getString("sessionid");
			othername = bundle.getString("name");
			otheruserID = bundle.getString("otheruserId");
			startCreateTime = bundle.getString("createTime");
			firstload =true;
		}
	}

	private void initEmoji(){
		chatEmoji = new ChatEmoji(this,this,_session,this.getSupportFragmentManager());
		chatEmoji.initView(vp_emoji, gv_emoji_menu, gv_emoji_index);
	}
    private void loadSession(){
		targetId = EnumManage.SessionType.p2p.toString().equals(sessionType) ? otheruserID:sessionId;
		Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
			@Override
			public void onSuccess(Session session) {
				_session = session;
				updateChatHandler.sendEmptyMessage(0);
				syncMembers();
			}

			@Override
			public void onError(int Code, String error) {
				if(Code==133&& !StringUtil.isEmpty(error)){
					ToastUtil.ToastMessage(_context,error);
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


		rl_more = (RelativeLayout) findViewById(R.id.rl_message_more);
		vp_add = (ViewPager) findViewById(R.id.vp_message_add_viewPager);
		vp_emoji = (ViewPager) findViewById(R.id.vp_message_emoji_viewPager);

		gv_emoji_index = (GridView) findViewById(R.id.gv_expression_index);
		gv_emoji_menu = (GridView) findViewById(R.id.gv_expression_menu);
		ll_emoji_menu = (LinearLayout) findViewById(R.id.ll_emoji_menu);
		initfreshlistview();
		adapter = new MessageAdapter(ChatActivity.this,_context, _session,messageList);
		lv_message.setAdapter(adapter);

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
					try {
						localMore();
					}catch (Exception e){
					}
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
	   List<SessionMember> members;
	List<SessionMemberEntity> memberEntitys = new ArrayList<SessionMemberEntity>();
	private void syncMembers(){
		_session.getMembers().localAllMembers(sessionId, new Back.Result<List<SessionMember>>() {
			@Override
			public void onSuccess(List<SessionMember> sessionMembers) {
				members =sessionMembers;
			}
			@Override
			public void onError(int code, String error) {

			}
		});
	}
	/*
	***进入界面获得同步消息
	*/
	private void getLastMessage(UserConversation userConversation){
		if(_session.getMessages().getMessageUpdateAt().equals(Constants.TIME_ORIGIN)){//第一次同步消息
			recentMessage();
			return;
		}

	 if(userConversation!=null&&userConversation.getEntity()!=null){
		int unread = userConversation.getEntity().getUnread();
		if(unread>0) {
			AppData.isRefreshConversation = true;
			if (unread > Config.LOAD_MESSAGE_COUNT) {//消息太多先显示最近一页
				recentMessage();
				return;
			} else {
				loadNewMessage();
				isfreshlist = true;
				return;
			}
		}
	}
		loadNewMessage();
		isfreshlist = true;
	}

	//获得最近消息
	private void recentMessage(){
		scrollbool = true;
		_session.getMessages().getRemote().getLastMessages(Config.LOAD_MESSAGE_COUNT, new Back.Result<List<MessageModel>>() {
			@Override
			public void onSuccess(List<MessageModel> messageModels) {
				if(messageModels!=null&&messageModels.size()>0){
					Message msg = new Message();
					msg.what = 3;
					msg.obj = messageModels;
					messageChangeHandler.sendMessage(msg);
					syncLastMessageTime = messageModels.get(messageModels.size()-1).getEntity().getUpdatedAt();
				}
			}
			@Override
			public void onError(int errorCode,String error) {
				if(errorCode==133&& !StringUtil.isEmpty(error)){
					ToastUtil.ToastMessage(_context,error);
				}
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
			if(EnumManage.SessionType.group.toString().equals(sessionType)) {
				if (count == 1 && s.charAt(start) == '@') {
                    Intent intent = new Intent(ChatActivity.this,ChooseAnswerActivity.class);
					intent.putExtra("sessionId",sessionId);
					startActivityForResult(intent,ResultCode.CODE_MESSEGE_CHOOSEMEMBER);
				}
				if(s.length()==0&&memberEntitys!=null){
					memberEntitys.clear();
				}
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
				//show();
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
			ToastUtil.ToastMessage(_context, "没有这个权限");
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
		public void onSuccess(String path, int code,int second) {
			switch (code) {
				case RecordButton.RECORD_LESS_THAN_MIN:
					ToastUtil.ToastMessage(ChatActivity.this, "录音时间太短。");
					fl_record.setVisibility(View.GONE);
					if(new File(path).exists()){
						new File(path).deleteOnExit();
					}
					break;
				case RecordButton.RECORD_CANCEL:
					fl_record.setVisibility(View.GONE);
					new File(path).deleteOnExit();
					rb_voice.setText("按住 说话");
					break;
				case RecordButton.RECORD_TIMEOUT:
					ToastUtil.ToastMessage(ChatActivity.this, "录音达到最大长度。");
				case RecordButton.RECORD_NORMAL:
					LogUtil.getInstance().log(TAG, "path =" + path, null);
					sendVoice(path,second);
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
			//updateChatHandler.sendEmptyMessageDelayed(8, 300);
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
			updateChatHandler.sendEmptyMessageDelayed(8, 300);
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
			chatEmoji.initExpressionViewPager();
		}
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
	//发送系统消息
	public void sendSystemMessage(String systemMessage){
		if(_session==null){
			ToastUtil.ToastMessage(_context, "正在加载初始数据");
			return;
		}
		if(systemMessage.length()==0) {
			return;
		}
		final MessageModel temp = _session.getMessages().createMessage(MessageCrypto.getInstance().encryText(systemMessage), MessageType.System);
		Message changemsg = new Message();
		changemsg.what =1;
		changemsg.obj =temp;
		messageChangeHandler.sendMessage(changemsg);
		_session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
			@Override
			public void onSuccess(MessageModel model) {
				Message msg = new Message();
				msg.obj=_session.getEntity().getId();
				msg.what =2;
				updateChatHandler.sendMessage(msg);
			}

			@Override
			public void onError(int errorCode, String error) {
				//model.getEntity().setStatus(MessageEntity.MessageState.storeFailed.toString());
			}
		});

	}
	//添加任务
	public void addtaskMessage(){
		Message msg = new Message();
		msg.arg1=4;
		userConversationHandeler.sendMessage(msg);
	}
	//发送文字消息
	private void sendMessage(){
		if(_session==null||StringUtil.isEmpty(_session.getEntity().getId())){
			ToastUtil.ToastMessage(_context, "正在加载初始数据");
			return;
		}
		String sendtext = et_input.getText().toString().trim();
		if(sendtext.length()==0) {
			et_input.setText("");
			return;
		}
		String sendtype = MessageType.Text;

		final String sendmsgContext = MessageCrypto.getInstance().encryTextMembers(sendtext,memberEntitys);
		final MessageModel temp = _session.getMessages().createMessage(sendmsgContext, sendtype);
		et_input.setText("");
		Message changemsg = new Message();
		changemsg.what =1;
		changemsg.obj =temp;
		messageChangeHandler.sendMessage(changemsg);
		sendMessageModel(temp);
	}

	private void sendMessageModel(final MessageModel temp){
		_session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
			@Override
			public void onSuccess(MessageModel model) {
				Message msg = new Message();
				msg.obj=_session.getEntity().getId();
				msg.what =2;
				updateChatHandler.sendMessage(msg);
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
		String thumbnail = SendUtil.getImageThumbnail(compressPath);
		int[] wh = ImageUtil.getImageWidthHeight(thumbnail);
		String content = MessageCrypto.getInstance().encryImage(compressPath, thumbnail, thumbnail, wh[0], wh[1],System.currentTimeMillis()+"");
		final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Image);
		Message changemsg = new Message();
		changemsg.what =1;
		changemsg.obj =temp;
		messageChangeHandler.sendMessage(changemsg);

		FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, compressPath);
	}
	//发送语音消息
	private void sendVoice(final String voicePath,int second) {
		try {
			if(StringUtil.isEmpty(voicePath)){
				return;
			}
			File file = new File(voicePath);
			if (file.exists()) {
				final MessageModel temp = _session.getMessages().createMessage(MessageCrypto.getInstance().encryAudio(voicePath,voicePath, 10,"音频",System.currentTimeMillis()+""), MessageType.Audio);
				Message changemsg = new Message();
				changemsg.what =1;
				changemsg.obj =temp;
				messageChangeHandler.sendMessage(changemsg);
				FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, voicePath,second + "");
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
				final MessageModel temp = _session.getMessages().createMessage(MessageCrypto.getInstance().encryMovie(moviePath, moviePath, moviePath, 0, 0, "小视频",System.currentTimeMillis()+""), MessageType.Video);
				Message changemsg = new Message();
				changemsg.what =1;
				changemsg.obj =temp;
				messageChangeHandler.sendMessage(changemsg);
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

				String compressPath = filePath;
				int[] wh = ImageUtil.getImageWidthHeight(result[0]);
				String content = MessageCrypto.getInstance().encryImage(compressPath, result[0], result[0], wh[0], wh[1],System.currentTimeMillis()+"");

				final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Location);
				Message changemsg = new Message();
				changemsg.what =1;
				changemsg.obj =temp;
				messageChangeHandler.sendMessage(changemsg);
				FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, file.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendFile(final LocalFileEntity localFileEntity) {
		try {
			String filepath = localFileEntity.getFilePath();
			File file = new File(filepath);
			if (file.exists()) {
                 String extraName = localFileEntity.getExtraName();
				 String filename = localFileEntity.getFileName();
				if(extraName!=null&&extraName.equals("apk")){
					filename = filename+".apk";
				}
				String content = MessageCrypto.getInstance().encryFile(filepath,filepath,filepath,0,0,filename,localFileEntity.getFileSize()+"",System.currentTimeMillis()+"");
				final MessageModel temp = _session.getMessages().createMessage(content, MessageType.File);
				Message changemsg = new Message();
				changemsg.what =1;
				changemsg.obj =temp;
				messageChangeHandler.sendMessage(changemsg);
				FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, filepath);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> imagesChoose = new ArrayList<String>();
	private ArrayList<LocalFileEntity> fileChoose = new ArrayList<LocalFileEntity>();
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
				File file = new File(moviePath);
				if(file.exists() && file.length() > 1024){
					sendMovie(moviePath);
				}
			}
		}else if (requestCode == ResultCode.CODE_FILE&&data != null){ //文档
			Bundle bundle = data.getExtras();
			if(bundle!=null) {
				fileChoose = (ArrayList<LocalFileEntity>) bundle.getSerializable("localFileEntity");
				for(LocalFileEntity localFileEntity : fileChoose){
					sendFile(localFileEntity);
				}
			}
		}else if (requestCode == ResultCode.CODE_LOCATION){ //位置
			if(data != null) {
				String locationPath = data.getStringExtra("result");
				sendLocation(locationPath);
				LogUtil.getInstance().log(TAG,locationPath,null);
			}
		}else if (requestCode == ResultCode.CODE_SESSION_NAME_CHANGED){ //group session修改名称
			if(AppData.isRefreshConversation) {
				Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
					@Override
					public void onSuccess(Session s) {
						texttitle.setText(s.getEntity().getName());
					}

					@Override
					public void onError(int code, String error) {

					}
				});
			}
		}else if (requestCode == ResultCode.CODE_MESSEGE_CHOOSEMEMBER){ //@选择人
			if(data != null) {
				String userid = data.getStringExtra("result");
				if(!StringUtil.isEmpty(userid)) {
					boolean find = false;
					String tapinput = et_input.getText().toString();
					tapinput = tapinput.substring(0,tapinput.length()-1);

					if(memberEntitys!=null&&memberEntitys.size()>0){
						for(int i = 0;i<memberEntitys.size();i++){
							if(memberEntitys.get(i).getUserId().equals(userid)){
								find = true;
								et_input.setText(tapinput + " @"+memberEntitys.get(i).getName()+" ");
								break;
							}
						}
					}
					if(!find) {
						if (members != null && members.size() > 0) {
							for (int i = 0; i < members.size(); i++) {
								if (members.get(i).getEntity().getUserId().equals(userid)) {
									memberEntitys.add(members.get(i).getEntity());
									et_input.setText(tapinput + " @"+members.get(i).getEntity().getName()+" ");
									break;
								}
							}
						}
					}
				}
				String input = et_input.getText().toString();
				Message msg = new Message();
				msg.arg1=3;
				msg.arg2 = input.length();
				userConversationHandeler.sendMessageDelayed(msg,200);
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

	private void localMore(){
			if(messageList.size() < 1){
				return ;
			}
		   scrollbool = true;
			MessageModel firstModel;
			if (messageList.size() == 0) {
				firstModel = null;
			} else {
				firstModel = messageList.get(0);
			}
		final MessageModel mpfirstModel = firstModel;
           ThreadPool.getThreadPool().executUI(new Runnable() {
			   @Override
			   public void run() {
				   List<MessageModel> list = _session.getMessages().getMessages(mpfirstModel, Config.LOAD_MESSAGE_COUNT);
				   if (list != null && list.size() > 0) {
					   if (messageList.size() > 0) {
						   List<MessageModel> deletelist = new ArrayList<MessageModel>();
						   for (int i = 0; i < list.size(); i++) {
							   for (int j = 0; j < list.size(); j++) {
								   if (messageList.get(j).getEntity().getId().equals(list.get(i).getEntity().getId())) {
									   deletelist.add(list.get(i));
								   }
							   }
						   }
						   list.removeAll(deletelist);
						   if (list.size() <= 0)
							   return;
					   }
				   }
				   Message msg = new Message();
				   msg.what = 2;
				   msg.obj = list;
				   messageChangeHandler.sendMessage(msg);
			   }
		   });
	}
	private void localMorerefresh(List<MessageModel> list){
		try {
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
		refreshComplete();
		if (lv_message != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(220);
					} catch (Exception e) {
						e.printStackTrace();
					}
					scrollbool = false;
				}
			}).start();
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
		}
	}

	private void updatelocal(int count,boolean isclear,boolean istobottom){
		MessageModel firstModel;
		if(isclear){
			firstModel = null;
		}else {
			if (messageList.size() == 0) {
				firstModel = null;
			} else {
				firstModel = messageList.get(0);
			}
		}
		final List<MessageModel> list =_session.getMessages().getMessages(firstModel,count);

		Message msg = new Message();
		msg.what = 4;
		msg.obj =list;
		if(isclear){
			msg.arg1 = 1;
		}else{
			msg.arg1 = 0;
		}
		if(istobottom){
			msg.arg2 = 1;
		}else{
			msg.arg2 = 0;
		}
		messageChangeHandler.sendMessage(msg);
	}
	private void afterlocal(String createtime,boolean isclear,boolean istobottom){
		List<MessageModel> list =_session.getMessages().getafterTimeMessages(createtime);
		Message msg = new Message();
		msg.what = 4;
		msg.obj =list;
		if(isclear){
			msg.arg1 = 1;
		}else{
			msg.arg1 = 0;
		}
		if(istobottom){
			msg.arg2 = 1;
		}else{
			msg.arg2 = 0;
		}
		messageChangeHandler.sendMessage(msg);
	}
	private void setMessagenum(int count){
		if(StringUtil.isEmpty(targetId)) {
			targetId = EnumManage.SessionType.p2p.toString().equals(sessionType) ? otheruserID : sessionId;
		}
		UserConversation userConversation =Users.getInstance().getCurrentUser().getUserConversations().get(targetId,sessionType);
		if(userConversation!=null&&userConversation.getEntity()!=null){
			int unread = userConversation.getEntity().getUnread();
			unread =unread-count;
			if(unread<0)
				unread =0;
			//userConversation.getEntity().setUnread(unread);
			userConversation.getEntity().setUnread(0);
			_session.getSessions().getUser().getUserConversations().addUserConversation(userConversation);
			AppData.isRefreshConversation = true;
		}
	}
	private void setMessageCoversation(int count,String sendmsg){
		if(StringUtil.isEmpty(targetId)) {
			targetId = EnumManage.SessionType.p2p.toString().equals(sessionType) ? otheruserID : sessionId;
		}
		UserConversation userConversation =Users.getInstance().getCurrentUser().getUserConversations().get(targetId,sessionType);
		if(userConversation!=null&&userConversation.getEntity()!=null){
			userConversation.getEntity().setUnread(count);
			if(userConversation.getEntity().getLastType().equals(MessageType.Draft)){
				if(!StringUtil.isEmpty(sendmsg)){
					userConversation.getEntity().setLastSender(Users.getInstance().getCurrentUser().getEntity().getId());
					userConversation.getEntity().setLastContext(sendmsg);
					userConversation.getEntity().setLastType(MessageType.Draft);
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String crrunttime= df.format(new Date());
					userConversation.getEntity().setUpdatedAt(crrunttime);
					String extraDate = userConversation.getEntity().getExtraDate();
					if(!StringUtil.isEmpty(extraDate)) {
						Json json = new Json(extraDate);
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("lastsender", json.getStr("lastsender"));
						jsonObject.put("lastcontext", json.getStr("lastcontext"));
						jsonObject.put("lasttype", json.getStr("lasttype"));
						jsonObject.put("updateat", json.getStr("updateat"));
						jsonObject.put("draftmsg", sendmsg);
						userConversation.getEntity().setExtraDate(jsonObject.toJSONString());
					}
				}else{
					String extraDate = userConversation.getEntity().getExtraDate();
					if(!StringUtil.isEmpty(extraDate)){
						Json json = new Json(extraDate);
						userConversation.getEntity().setLastSender(json.getStr("lastsender"));
						userConversation.getEntity().setLastContext(json.getStr("lastcontext"));
						userConversation.getEntity().setLastType(json.getStr("lasttype"));
						userConversation.getEntity().setUpdatedAt(json.getStr("updateat"));
						userConversation.getEntity().setExtraDate("update");
					}
				}
			}else{
				if(!StringUtil.isEmpty(sendmsg)){

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("lastsender",userConversation.getEntity().getLastSender());
					jsonObject.put("lastcontext",userConversation.getEntity().getLastContext());
					jsonObject.put("lasttype",userConversation.getEntity().getLastType());
					jsonObject.put("updateat",userConversation.getEntity().getUpdatedAt());
					jsonObject.put("draftmsg",sendmsg);
					userConversation.getEntity().setExtraDate(jsonObject.toJSONString());
					userConversation.getEntity().setLastSender(Users.getInstance().getCurrentUser().getEntity().getId());
					userConversation.getEntity().setLastContext(sendmsg);
					userConversation.getEntity().setLastType(MessageType.Draft);
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String crrunttime= df.format(new Date());
					userConversation.getEntity().setUpdatedAt(crrunttime);
				}else{
					String extraDate = userConversation.getEntity().getExtraDate();
					if(!StringUtil.isEmpty(extraDate)){
						Json json = new Json(extraDate);
						userConversation.getEntity().setLastSender(json.getStr("lastsender"));
						userConversation.getEntity().setLastContext(json.getStr("lastcontext"));
						userConversation.getEntity().setLastType(json.getStr("lasttype"));
						userConversation.getEntity().setUpdatedAt(json.getStr("updateat"));
						userConversation.getEntity().setExtraDate("update");
					}
				}
			}
			UserConversationDb.addUserConversation(userConversation.getEntity());
			AppData.isRefreshConversation = true;
		}
	}
	//同步会话和群成员
	boolean syncSessionADmembers = false;
	boolean needSyncADmembers = false;
	public void ifSyncADmembers(){
		Message msg = new Message();
		msg.obj=_session.getEntity().getId();
		msg.what =9;
		updateChatHandler.sendMessage(msg);
	}

	public void syncSessionADmembers(){
		syncSessionADmembers= true;
		needSyncADmembers = false;
        if(StringUtil.isEmpty(targetId)){
			targetId = EnumManage.SessionType.p2p.toString().equals(sessionType) ? otheruserID : sessionId;
		}
		_session.getSessions().getRemote().getSession(targetId,sessionType, new Back.Result<Session>() {
			@Override
			public void onSuccess(Session session) {
				_session =session;
				_session.getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
					@Override
					public void onSuccess(List<SessionMember> sessionMembers) {
						members =sessionMembers;
						if(needSyncADmembers){
							syncSessionADmembers();
						}else{
							syncSessionADmembers = false;
						}
						adapter.setSession(_session);
						refreshMessageList();
					}
					@Override
					public void onError(int code, String error) {
						if(code==ErrorCode.EC_UNKNOWN &&error!=null&&error.equals(ChatActivity.this.getResources().getString(R.string.ec_newwork_error))){
							syncSessionADmembers = false;
							needSyncADmembers = true;
						}else{
							syncSessionADmembers();
						}

					}
				});
			}
			@Override
			public void onError(int code, String error) {
				if(code==ErrorCode.EC_UNKNOWN &&error!=null&&error.equals(ChatActivity.this.getResources().getString(R.string.ec_newwork_error))){
					syncSessionADmembers = false;
					needSyncADmembers = true;
				}else{
					syncSessionADmembers();
				}
			}
		});
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
			_session.getMessages().getRemote().sync(true,_session.getMessages().getMessageUpdateAt(), Config.LOAD_MESSAGE_COUNT, new Back.Result<SyncMessagesModel>() {
					@Override
					public void onSuccess(SyncMessagesModel syncMessagesModel) {
						List<MessageModel> models =syncMessagesModel.getMessageModels();
						if (models==null || models.size() < Config.LOAD_MESSAGE_COUNT) {//同步完成
							issyndate=false;
							isfreshlist = true;
							int count = messageList.size();
							if(count<Config.LOAD_MESSAGE_COUNT)
								count =Config.LOAD_MESSAGE_COUNT;
							updatelocal(count,true,true);
							scrollbool = false;
						} else {
							run();
						}
						//setMessagenum(models.size());
						if(models!=null&&models.size()>0){
							_session.getMessages().getRemote().sendupdateMembers();
						}
						if(StringUtil.timeCompare(_session.getEntity().getUpdateMTS(),syncMessagesModel.getSessionUpdatedAt()) > 0){
							ifSyncADmembers();
						}
					}
					@Override
					public void onError(int errorCode,String error) {
					}
				});
		}
	}
     //是否重新清空刷新
	private String syncLastMessageTime;
	private void loadNewMessage(){
			final int unread = 50;
		    messageLoading = true;
		    hasNewMessage = false;
		    String updateat =  _session.getMessages().getMessageUpdateAt();
			if(issyndate){
				updateat = syncLastMessageTime;
			   }
			   _session.getMessages().getRemote().sync(!issyndate,updateat , unread, new Back.Result<SyncMessagesModel>() {
				   @Override
				   public void onSuccess(SyncMessagesModel syncMessagesModel) {
					   List<MessageModel> models = syncMessagesModel.getMessageModels();
					   if (models != null && models.size() > 0) {
						   _session.getMessages().getRemote().sendupdateMembers();
					   }
					   if (StringUtil.timeCompare(_session.getEntity().getUpdateMTS(), syncMessagesModel.getSessionUpdatedAt()) > 0) {
						   ifSyncADmembers();
					   }

					   boolean istobottom = false;
					   int lastnum = lv_message.getRefreshableView().getLastVisiblePosition();
					   if (lastnum >= (messageList.size() - 1))
						   istobottom = true;
					   if (models != null && models.size() > 0) {
						   List<MessageModel> tmpMessages = new ArrayList<MessageModel>();
						   for (int j = 0; j < models.size(); j++) {
							   boolean find = false;
							   for (int i = (messageList.size() - 1); i >= 0; i--) {
								   if(messageList.get(i).getEntity().getStatus()!=null&& MessageEntity.MessageState.storing.toString().equals(messageList.get(i).getEntity().getStatus())){
									    String oldtime = MessageCrypto.getInstance().decryTimestamp(messageList.get(i).getEntity().getContent());
									   String newtime =  MessageCrypto.getInstance().decryTimestamp(models.get(j).getEntity().getContent());
                                       if(!StringUtil.isEmpty(newtime) && oldtime.equals(newtime)){
										   find = true;
										   messageList.get(i).getEntity().setId(models.get(j).getEntity().getId());
										   messageList.get(i).getEntity().setUpdatedAt(models.get(j).getEntity().getUpdatedAt());
										   messageList.get(i).getEntity().setCreatedAt(models.get(j).getEntity().getCreatedAt());
										   messageList.get(i).getEntity().setContent(models.get(j).getEntity().getContent());
										   messageList.get(i).getEntity().setIsDelete(models.get(j).getEntity().isDelete());
										   messageList.get(i).getEntity().setMyId(models.get(j).getEntity().getMyId());
										   messageList.get(i).getEntity().setSender(models.get(j).getEntity().getSender());
										   messageList.get(i).getEntity().setSimpchinacontent(models.get(j).getEntity().getSimpchinacontent());
										   messageList.get(i).getEntity().setStatus(models.get(j).getEntity().getStatus());
										   messageList.get(i).getEntity().setType(models.get(j).getEntity().getType());
										   break;
									   }
								   }else {
									   if (models.get(j).getEntity().getId().equals(messageList.get(i).getEntity().getId())) {
										   find = true;
										   messageList.get(i).getEntity().setId(models.get(j).getEntity().getId());
										   messageList.get(i).getEntity().setUpdatedAt(models.get(j).getEntity().getUpdatedAt());
										   messageList.get(i).getEntity().setCreatedAt(models.get(j).getEntity().getCreatedAt());
										   messageList.get(i).getEntity().setContent(models.get(j).getEntity().getContent());
										   messageList.get(i).getEntity().setIsDelete(models.get(j).getEntity().isDelete());
										   messageList.get(i).getEntity().setMyId(models.get(j).getEntity().getMyId());
										   messageList.get(i).getEntity().setSender(models.get(j).getEntity().getSender());
										   messageList.get(i).getEntity().setSimpchinacontent(models.get(j).getEntity().getSimpchinacontent());
										   messageList.get(i).getEntity().setStatus(models.get(j).getEntity().getStatus());
										   messageList.get(i).getEntity().setType(models.get(j).getEntity().getType());
										   break;
									   }
								   }
							   }
							   if (!find) {
								   tmpMessages.add(models.get(j));
							   }
						   }
						   Message msg = new Message();
						   msg.what = 5;
						   msg.obj = tmpMessages;
						   if (istobottom) {
							   msg.arg1 = 1;
						   } else {
							   msg.arg1 = 0;
						   }
						   messageChangeHandler.sendMessage(msg);
						   if(issyndate&&tmpMessages!=null&&tmpMessages.size()>0){
							   syncLastMessageTime = tmpMessages.get(tmpMessages.size()-1).getEntity().getUpdatedAt();
						   }
					   }

					   if (models == null || models.size() < unread) {
						   messageLoading = false;
						   if (hasNewMessage) {
							   loadNewMessage();
						   }
					   } else {
						   loadNewMessage();
					   }
				   }

				   @Override
				   public void onError(int errorCode, String error) {
					   if (errorCode == 133 && !StringUtil.isEmpty(error)) {
						   ToastUtil.ToastMessage(_context, error);
					   } else {
						   messageLoading = false;
						   if (hasNewMessage) {
							   loadNewMessage();
						   }
					   }
				   }
			   });
	}

	@Override
	protected void onResume() {
		super.onResume();
		String account = Users.getInstance().getCurrentUser().getEntity().getAccount();
		if(StringUtil.isEmpty(account)) {
			if (AppData.getInstance().getMainActivity() != null) {
				try {
					Users.getInstance().getCurrentUser().getImBridges().disConnect();
				} catch (Exception e) {
				}
				Intent intent = new Intent(AppData.getInstance().getMainActivity(), SplashActivity.class);
				AppData.getInstance().getMainActivity().startActivity(intent);
				DaoManager.getInstance(AppContext.getAppContext()).close();
				AppData.getInstance().getMainActivity().finish();
				finish();
			}
		}
		if(!firstload&&_session!=null) {
			Message msg = new Message();
			msg.obj = _session.getEntity().getId();
			msg.what = 2;
			updateChatHandler.sendMessage(msg);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {
		super.finish();
		try {
			String sendtext = et_input.getText().toString().trim();
			setMessageCoversation(0,sendtext);

			AppData.getInstance().getUpdateHashMap().remove(CallBackUpdate.updateType.chatting.toString());
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
		public static final int CODE_SESSION_NAME_CHANGED = 6;
		public static final int CODE_MESSEGE_CHOOSEMEMBER = 7;
	}

	public static class RefreshCode{
		public static final int CODE_EMOJI_ADD = 101;
		public static final int CODE_EMOJI_DELETE = 102;
		public static final int CODE_OTHER_AT = 103;

	}
}
