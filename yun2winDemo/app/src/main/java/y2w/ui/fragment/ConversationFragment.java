package y2w.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ThreadPool;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.ui.activity.StrongWebViewActivity;
import y2w.ui.dialog.Y2wDialog;
import y2w.manage.Users;
import y2w.base.AppData;
import y2w.common.CallBackUpdate;
import y2w.entities.UserConversationEntity;
import y2w.model.DataSaveModuel;
import y2w.model.Session;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.ui.activity.ChatActivity;
import y2w.ui.activity.MainActivity;
import y2w.ui.adapter.ConversationAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 会话界面
 */
public class ConversationFragment extends Fragment{
	private String TAG = ConversationFragment.class.getSimpleName();
    private String type;
    private String DEFAULT = "default";
    private static Activity activity;
    private static Context context;
	private CallBackUpdate callBackUpdate;
	private boolean isGetSession = false;
	private LinearLayout ll_notice_net ;
	private TextView tv_notice_net;
	Handler mqttConnectHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1){//没有网络
				tv_notice_net.setText("网络不可用,检查设置");
				ll_notice_net.setVisibility(View.VISIBLE);
			}else if(msg.what==2){//连接中
				tv_notice_net.setText("网络不给力,正在重连服务器..");
				ll_notice_net.setVisibility(View.VISIBLE);
			}else if(msg.what==3){//连接成功
				ll_notice_net.setVisibility(View.GONE);
			}
		}
	};
	Handler updatesessionHandler= new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what ==0){
				conversationAdapter.updateListView();
			}else if(msg.what==1){//更新全部
				ThreadPool.getThreadPool().executLow(new Runnable() {
					@Override
					public void run() {
						List<UserConversation>  tempconversations = Users.getInstance().getCurrentUser().getUserConversations().getUserConversations();
						List<UserConversation>  topconversations = new ArrayList<UserConversation>();
						List<UserConversation>  bottomconversations = new ArrayList<UserConversation>();
						if(tempconversations!=null&&tempconversations.size()>0){
                             for(int i =0;i<tempconversations.size();i++){
								if(tempconversations.get(i).getEntity().isTop()){
									topconversations.add(tempconversations.get(i));
								}else{
									bottomconversations.add(tempconversations.get(i));
								}
							}
							lv_conversation.setVisibility(View.VISIBLE);
							noConversation.setVisibility(View.GONE);
						}else{
							lv_conversation.setVisibility(View.GONE);
							noConversation.setVisibility(View.VISIBLE);
							return;
						}
						conversations =topconversations;
						conversations.addAll(bottomconversations);
						DataSaveModuel.getInstance().conversations = conversations;
						conversationAdapter.setConversations(conversations);
						updatesessionHandler.sendEmptyMessage(0);
					}
				});
			}else if(msg.what ==2){//添加一条
				UserConversation conversation = (UserConversation) msg.obj;
				 boolean find = false;
				if(conversations==null)
					conversations = new ArrayList<UserConversation>();
				  for(int i =0;i<conversations.size();i++){
					 if(conversations.get(i).getEntity().getId().equals(conversation.getEntity().getId())){
						 conversations.get(i).getEntity().setLastContext(conversation.getEntity().getLastContext());
						 conversations.get(i).getEntity().setUpdatedAt(conversation.getEntity().getUpdatedAt());
						 conversations.get(i).getEntity().setUnread(conversation.getEntity().getUnread());
						 find = true;
						 conversations.set(0,conversations.get(i));
						 break;
					 }
				 }
				if(!find){
					conversations.add(0,conversation);
				}
				conversationAdapter.updateListView();
			}else if(msg.what ==3){//删除一条
				UserConversationEntity entity = (UserConversationEntity) msg.obj;
				if(conversations==null)
					return;
				boolean find = false;
				for(int i =0;i<conversations.size();i++){
					if(conversations.get(i).getEntity().getId().equals(entity.getId())){
						find = true;
						break;

					}
				}
				if(find){
					conversations.remove(entity);
					conversationAdapter.updateListView();
				}
			}
			int num = 0;
			for(int i = 0;i<conversations.size();i++){
				num = num +conversations.get(i).getEntity().getUnread();
			}
			((MainActivity)activity).updatemessagenum(num);
		}
	};

	public static ConversationFragment newInstance(Activity _activity,Context _context){
		
			ConversationFragment newFragment = new ConversationFragment();
	        Bundle bundle = new Bundle();
	        //bundle.putString("type", str);
	        newFragment.setArguments(bundle);
	        activity = _activity;
	        context = _context;
	        return newFragment;
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	        Bundle args = getArguments();
	       // type = args != null ? args.getString("type") : DEFAULT;
	        View view = null;
		    view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
		    conversaionInit(view);
		    addCallBackUpdate();
		    Users.getInstance().getCurrentUser().getImBridges().setHandler(mqttConnectHandler);
	        return view;
	}
   private void addCallBackUpdate(){
	   AppData.getInstance().getUpdateHashMap().put(CallBackUpdate.updateType.userConversation.toString(),callBackUpdate);
	   callBackUpdate.updateUI();
   }
	public void removeCallBackUpdate(){
		AppData.getInstance().getUpdateHashMap().remove(CallBackUpdate.updateType.userConversation.toString());
	}

	private ListView lv_conversation;
	private TextView noConversation;
	private ConversationAdapter conversationAdapter;
	private List<UserConversation> conversations =new ArrayList<UserConversation>();
	public void conversaionInit(View view){
		ll_notice_net = (LinearLayout)view.findViewById(R.id.ll_notice_net);
		tv_notice_net = (TextView) view.findViewById(R.id.tv_notice_net);
		ll_notice_net.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//跳转安卓系统设置界面
				Intent intent =  new Intent(Settings.ACTION_SETTINGS);
				startActivity(intent);
			}
		});
		lv_conversation = (ListView) view.findViewById(R.id.lv_conversation);
		noConversation = (TextView) view.findViewById(R.id.noconversation);
		conversationAdapter = new ConversationAdapter(activity,context);
		lv_conversation.setAdapter(conversationAdapter);
		callBackUpdate = new CallBackUpdate(updatesessionHandler);

		Users.getInstance().getCurrentUser().getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
			@Override
			public void onSuccess(List<UserConversation> userConversationList) {
				Users.getInstance().getCurrentUser().getUserConversations().add(userConversationList);
				if(userConversationList!=null&&userConversationList.size()>0){
					callBackUpdate.updateUI();
				}
			}

			@Override
			public void onError(int errorCode,String error) {

			}
		});

		lv_conversation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isGetSession) {
					isGetSession = false;
					return;
				}
				isGetSession = true;
				if (conversations != null && conversations.size() > position){
					final UserConversation userConversation = conversations.get(position);
					if(userConversation.getEntity().getType().equals("app")){
						isGetSession = false;
						String url = userConversation.getEntity().getTargetId();
						if(StringUtil.isEmpty(url) || !url.startsWith("http")) {
							ToastUtil.ToastMessage(AppContext.getAppContext(),"参数不可用");
						}else {
							Intent intent = new Intent(context, StrongWebViewActivity.class);
							intent.putExtra("webUrl", url);
							activity.startActivity(intent);
						}
					}else{
						Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(userConversation.getEntity().getTargetId(), userConversation.getEntity().getType(), new Back.Result<Session>() {
							@Override
							public void onSuccess(final Session session) {
								Intent intent = new Intent(context, ChatActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("sessionid", session.getEntity().getId());
								bundle.putString("sessiontype", session.getEntity().getType());
								bundle.putString("otheruserId", session.getEntity().getOtherSideId());
								bundle.putString("name", userConversation.getEntity().getName());
								intent.putExtras(bundle);
								activity.startActivityForResult(intent, MainActivity.MainResultCode.CODE_CONVERSATION_REFRESH);
								isGetSession = false;
								//ToastUtil.ToastMessage(context,"成功");
							}

							@Override
							public void onError(int errorCode, String error) {
								isGetSession = false;
								//ToastUtil.ToastMessage(context,"失败");
							}
						});
					}
		     	}else{
					isGetSession = false;
				}
			}
		});

		lv_conversation.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (conversations != null && conversations.size() > position) {
					final UserConversation userConversation = conversations.get(position);
					Y2wDialog dialog = new Y2wDialog(context);
					dialog.addOption("删除");
					if(userConversation.getEntity().isTop()){
						dialog.addOption("取消置顶");
					}else {
						dialog.addOption("置顶");
					}
					dialog.show();
					dialog.setOnOptionClickListener(new Y2wDialog.onOptionClickListener() {
						@Override
						public void onOptionClick(String option, int position) {
							if(position == 0){
								Users.getInstance().getCurrentUser().getUserConversations().getRemote().deleteUserConversation(userConversation.getEntity().getId(), new Back.Callback() {

									@Override
									public void onSuccess() {
										Users.getInstance().getCurrentUser().getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
											@Override
											public void onSuccess(List<UserConversation> userConversationList) {
												updatesessionHandler.sendEmptyMessage(1);
												LogUtil.getInstance().log(TAG,"删除："+userConversationList.size(),null);
											}

											@Override
											public void onError(int code, String error) {
												LogUtil.getInstance().log(TAG,"code 1："+error,null);
											}
										});
									}

									@Override
									public void onError(int code, String error) {
										LogUtil.getInstance().log(TAG,"code 0："+error,null);
									}
								});
							}else if(position == 1){
								userConversation.getEntity().setTop(!userConversation.getEntity().isTop());
								Users.getInstance().getCurrentUser().getUserConversations().getRemote().updateUserConversation(userConversation, new Back.Result<UserConversation>() {
									@Override
									public void onSuccess(UserConversation userConversation) {
										Users.getInstance().getCurrentUser().getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
											@Override
											public void onSuccess(List<UserConversation> userConversationList) {
												updatesessionHandler.sendEmptyMessage(1);
												LogUtil.getInstance().log(TAG,"置顶："+userConversationList.size(),null);
											}

											@Override
											public void onError(int code, String error) {
												LogUtil.getInstance().log(TAG,"code 1："+error,null);
											}
										});
									}

									@Override
									public void onError(int code, String error) {
										LogUtil.getInstance().log(TAG,"code 0："+error,null);
									}
								});
							}
						}
					});
				}
				return true;
			}
		});
	}

	public void refreshAll(){
		if(updatesessionHandler != null)
		updatesessionHandler.sendEmptyMessage(1);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		removeCallBackUpdate();
	}

	@Override
	public void onResume() {
		super.onResume();
		if(AppData.isRefreshConversation)
			refreshAll();
		AppData.isRefreshConversation = false;

	}

	@Override
	public void onPause() {
		super.onPause();
	}
}

