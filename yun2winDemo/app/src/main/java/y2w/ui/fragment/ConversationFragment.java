package y2w.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.y2w.uikit.utils.ThreadPool;
import com.yun2win.demo.R;
import com.yun2win.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

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
	Handler updatesessionHandler= new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what ==0){
				conversationAdapter.updateListView();
			}else if(msg.what==1){//更新全部
			ThreadPool.getThreadPool().executUI(new Runnable() {
				@Override
				public void run() {
					conversations = Users.getInstance().getCurrentUser().getUserConversations().getUserConversations();
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
	private ConversationAdapter conversationAdapter;
	private List<UserConversation> conversations =new ArrayList<UserConversation>();
	public void conversaionInit(View view){
		lv_conversation = (ListView) view.findViewById(R.id.lv_conversation);
		conversationAdapter = new ConversationAdapter(context);
		lv_conversation.setAdapter(conversationAdapter);
		callBackUpdate = new CallBackUpdate(updatesessionHandler);

		lv_conversation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//ToastUtil.ToastMessage(context,"点了");
				if (isGetSession) {
					return;
				}
				isGetSession = true;
				if (conversations != null && conversations.size() > position){
					final UserConversation userConversation = conversations.get(position);
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
						startActivity(intent);
						isGetSession = false;
						//ToastUtil.ToastMessage(context,"成功");
					}

					@Override
					public void onError(int errorCode, String error) {
						isGetSession = false;
						//ToastUtil.ToastMessage(context,"失败");
					}
					});
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
					dialog.addOption("置顶");
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
								userConversation.getEntity().setTop(true);
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



	@Override
	public void onDestroy() {
		super.onDestroy();
		removeCallBackUpdate();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}
}

