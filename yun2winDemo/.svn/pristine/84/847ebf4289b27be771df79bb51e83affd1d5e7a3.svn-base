package y2w.ui.activity;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.listview.AutoRefreshListView;
import com.y2w.uikit.customcontrols.listview.ListViewUtil;
import com.y2w.uikit.customcontrols.listview.MessageListView;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import y2w.base.Urls;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppData;
import y2w.common.CallBackUpdate;
import y2w.common.Config;
import y2w.common.Constants;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.MessageModel;
import y2w.model.QueryDirectionEnum;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.UserConversation;
import y2w.model.messages.MViewHolder;
import y2w.model.messages.MessageCrypto;
import y2w.model.messages.MessageType;
import y2w.service.Back;
import y2w.service.FileSrv;
import y2w.ui.adapter.FragmentAdapter;
import y2w.ui.adapter.MessageAdapter;
import y2w.ui.fragment.MessageFragment;

/**
 * Created by hejie on 2016/3/14.
 * 聊天界面
 */
public class ChatActivity extends FragmentActivity {
	private String TAG = ChatActivity.class.getSimpleName();
	private EditText et_input;
	private MessageListView lv_message;
	private ImageButton ib_add;
	private ImageButton ib_send;
	private ImageButton ib_emoji;

	private RelativeLayout rl_more;
	private ViewPager vp_add;
	private ViewPager vp_emoji;
    private synMessagedate synMessagedate;
	/**adapter**/
	private MessageAdapter adapter;
	private FragmentAdapter pagerAdapter;
	static public Context _context;
	private List<MessageModel> messageList = new ArrayList<MessageModel>();
	private Handler chatHandler;
	private Session _session;
	private String sessionType,sessionId;
	private String othername,otheruserID;

	private CallBackUpdate callBackUpdate;
	//界面更新
	Handler updatechatHandler= new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==0){
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
			}else if(msg.what==1){

			}else if(msg.what ==2){//有新的消息

				if(messageLoading){
					hasNewMessage = true;
				}else{
					loadNewMessage();
				}
			}else if(msg.what ==3){//回撤了一个消息
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
				lv_message.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
				adapter.updateListView(messageList);
			}
		}
	};


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
		callBackUpdate = new CallBackUpdate(updatechatHandler);
		callBackUpdate.setId(sessionId);
		AppData.getInstance().getUpdateHashMap().put(CallBackUpdate.updateType.chatting.toString(), callBackUpdate);
		loadSession();
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
    private void loadSession(){
		Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
			@Override
			public void onSuccess(Session session) {
				_session = session;
				updatechatHandler.sendEmptyMessage(0);
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
				updatechatHandler.sendEmptyMessage(0);
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
		lv_message = (MessageListView) findViewById(R.id.messageListView);
		et_input = (EditText) findViewById(R.id.et_input);
		ib_add = (ImageButton) findViewById(R.id.ib_add);
		ib_send = (ImageButton) findViewById(R.id.ib_send);
		ib_emoji = (ImageButton) findViewById(R.id.ib_emoji);

		rl_more = (RelativeLayout) findViewById(R.id.rl_message_more);
		vp_add = (ViewPager) findViewById(R.id.vp_message_add_viewPager);
		vp_emoji = (ViewPager) findViewById(R.id.vp_message_expression_viewPager);

		show_send();
	}

	private void show_send(){
		ib_add.setVisibility(View.GONE);
		ib_send.setVisibility(View.VISIBLE);
	}
	private void listenerInit(){
		et_input.addTextChangedListener(new messageInputTextChanged());
		et_input.setOnTouchListener(new inputTextTouchListener());
		ib_send.setOnClickListener(new onViewClick());
		ib_add.setOnClickListener(new onViewClick());
		ib_emoji.setOnClickListener(new onViewClick());
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
						messageDisplay();
					}

					@Override
					public void onError(int errorCode,String error) {
						messageDisplay();
					}
				});
				return;
			}
		}
	}
		loadNewMessage();
		messageDisplay();
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
	public void messageDisplay(){
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lv_message.setOnRefreshListener(new MessageLoader());
			}
		});
	}

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
					show_send();
					/*ib_add.setVisibility(View.VISIBLE);
					ib_send.setVisibility(View.GONE);*/
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
			}else if(v.getId() == ib_emoji.getId()){
				emojiOnclick();
			}
		}
	}

	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
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
			lv_message.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
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
			initMoreViewPager();
		}
	}

	private void emojiOnclick(){
		if(vp_emoji.getVisibility() == View.VISIBLE){
			vp_emoji.setVisibility(View.GONE);
			rl_more.setVisibility(View.GONE);
		}else{
			vp_emoji.setVisibility(View.VISIBLE);
			rl_more.setVisibility(View.VISIBLE);
		}
	}
	public void initMoreViewPager() {
		List<Fragment> fragmentList = new ArrayList<Fragment>();
		Fragment firstFragment = MessageFragment.newInstance(this, _context,
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
			ToastUtil.ToastMessage(_context,"正在加载初始数据");
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
		adapter.updateListView(messageList);
		et_input.setText("");
		_session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
			@Override
			public void onSuccess(MessageModel model) {
				temp.getEntity().setId(model.getEntity().getId());
				updatechatHandler.sendEmptyMessage(2);
			}
			@Override
			public void onError(int errorCode,String error) {
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
		final MessageModel temp = _session.getMessages().createMessage(filepath, MessageType.Image);
		FileSrv.getInstance().uploadMessagesFile(_context, Users.getInstance().getCurrentUser().getToken(), Urls.User_Messages_File_UpLoad, filepath);
		messageList.add(temp);
		scrollToBottom();
		adapter.updateListView(messageList);
		_session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
			@Override
			public void onSuccess(MessageModel model) {
				/*entity.setSessionId(temp.getSessionId());
				entity.setType(temp.getType());
				entity.setStatus(MessageEntity.MessageState.stored.toString());
				session.getMessages().addMessage(entity);*/
				updatechatHandler.sendEmptyMessage(2);
			}

			@Override
			public void onError(int errorCode,String error) {

			}
		});
		//refreshViewHolderByIndex(final int index,final MessageModel model)
	}

	private List<String> imagesChoose = new ArrayList<String>();
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2) {// ͼƬ
			if (data != null) {
				imagesChoose = data.getStringArrayListExtra("result");
				if (imagesChoose != null && imagesChoose.size() > 0) {
					for (String string : imagesChoose) {
						sendPictures(string);
					}
				}
			}
		}
	}
	// 刷新消息列表
	public void refreshMessageList() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lv_message.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
				adapter.updateListView(messageList);
			}
		});
	}
	public void scrollToBottom(){
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ListViewUtil.scrollToBottom(lv_message);
			}
		});
	}
	/**
	 * 刷新单条消息
	 *
	 * @param index
	 */
	private void refreshViewHolderByIndex(final int index,final MessageModel model) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (index < 0) {
					return;
				}
				Object tag = ListViewUtil.getViewHolderByIndex(lv_message, index);
				if (tag instanceof MViewHolder) {
					MViewHolder viewHolder = (MViewHolder) tag;
					adapter. messagedisplay(model,viewHolder,index);
				}
			}
		});
	}

	//消息自动刷新
	private class MessageLoader implements AutoRefreshListView.OnRefreshListener {

		private QueryDirectionEnum direction = null;
		private boolean firstLoad = true;

		public MessageLoader() {
			loadFromLocal(QueryDirectionEnum.QUERY_OLD);
		}

		private Back.Result<List<MessageModel>> callback = new Back.Result<List<MessageModel>>() {
			@Override
			public void onSuccess(List<MessageModel> messageModels) {
				if (messageModels != null) {
					onMessageLoaded(messageModels);
				}
			}
			@Override
			public void onError(int errorCode,String error) {
			}
		};

		private void loadFromLocal(QueryDirectionEnum direction) {
			this.direction = direction;
			lv_message.onRefreshStart(direction == QueryDirectionEnum.QUERY_NEW ? AutoRefreshListView.Mode.END : AutoRefreshListView.Mode.START);
          if(firstLoad&&messageList.size()>Config.LOAD_MESSAGE_COUNT){
				  _session.getMessages().getMessages(anchor(), messageList.size(), callback);
		  }else{
			  _session.getMessages().getMessages(anchor(), Config.LOAD_MESSAGE_COUNT, callback);
		  }
		}

		private MessageModel anchor() {
			if (messageList.size() == 0) {
				return null;
			} else {
				return messageList.get(0);
			}
		}

		/**
		 * 历史消息加载处理
		 *
		 * @param messages
		 */
		private void onMessageLoaded(List<MessageModel> messages) {
			int count = messages.size();
             String toptime = "",bottomtime ="";
			if (firstLoad) {
				messageList.clear();
			}
			if (direction == QueryDirectionEnum.QUERY_NEW) {
				messageList.addAll(messages);
			} else {
				if(messageList!=null&&messageList.size()>0) {
					toptime = messageList.get(0).getEntity().getUpdatedAt();
				}
				messageList.addAll(0, messages);
				if(messages!=null&&messages.size()>0) {
					bottomtime = messages.get(messages.size() - 1).getEntity().getUpdatedAt();
				}
			}
			refreshMessageList();
			if (StringUtil.isTimeDisplay(bottomtime, toptime)) {
				lv_message.onRefreshComplete(count, Config.LOAD_MESSAGE_COUNT, true, 0);
			} else {
				lv_message.onRefreshComplete(count, Config.LOAD_MESSAGE_COUNT, true, 100);
			}
			if(firstLoad) {
				scrollToBottom();
			}
			firstLoad = false;
		}

		/**
		 * *************** OnRefreshListener ***************
		 */
		@Override
		public void onRefreshFromStart() {
				loadFromLocal(QueryDirectionEnum.QUERY_OLD);
		}

		@Override
		public void onRefreshFromEnd() {
				loadFromLocal(QueryDirectionEnum.QUERY_NEW);
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
							messageDisplay();
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

	private void loadNewMessage(){
			final int unread = 50;
		    messageLoading = true;
		    hasNewMessage = false;
			_session.getMessages().getRemote().sync(!issyndate,_session.getMessages().getMessageUpdateAt(), unread, new Back.Result<List<MessageModel>>() {
				@Override
				public void onSuccess(List<MessageModel> models) {
					boolean istobottom =false;
					int lastnum =lv_message.getLastVisiblePosition();
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
							loadNewMessage();
						}
					}else{
						loadNewMessage();
					}
				}
				@Override
				public void onError(int errorCode,String error) {
					messageLoading = false;
					if(hasNewMessage){
						loadNewMessage();
					}
				}
			});
			return;
	}


	@Override
	public void finish() {
		super.finish();
		AppData.getInstance().getUpdateHashMap().remove(CallBackUpdate.updateType.chatting.toString());
		AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.userConversation.toString()).updateUI();
		if(synMessagedate!=null&& synMessagedate.isAlive()){
			synMessagedate.interrupt();
			synMessagedate=null;
		}

	}
}
