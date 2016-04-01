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

import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.common.Config;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.base.AppData;
import y2w.common.CallBackUpdate;
import y2w.entities.UserConversationEntity;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.ui.activity.ChatActivity;
import y2w.ui.activity.MainActivity;
import y2w.ui.adapter.ConversationAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 会话界面
 */
public class ConversationFragment extends Fragment{
	
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
				conversations = Users.getInstance().getCurrentUser().getUserConversations().getUserConversations();
				conversationAdapter.setCOnversations(conversations);
				updatesessionHandler.sendEmptyMessage(0);
			}else if(msg.what ==2){//添加一条
				UserConversationEntity entity = (UserConversationEntity) msg.obj;
				 boolean find = false;
				if(conversations==null)
					conversations = new ArrayList<UserConversationEntity>();
				  for(int i =0;i<conversations.size();i++){
					 if(conversations.get(i).getId().equals(entity.getId())){
						 conversations.get(i).setLastContext(entity.getLastContext());
						 conversations.get(i).setUpdatedAt(entity.getUpdatedAt());
						 conversations.get(i).setUnread(entity.getUnread());
						 find = true;
						 conversations.set(0,conversations.get(i));
						 break;
					 }
				 }
				if(!find){
					conversations.add(0,entity);
				}
				conversationAdapter.updateListView();
			}else if(msg.what ==3){//删除一条
				UserConversationEntity entity = (UserConversationEntity) msg.obj;
				if(conversations==null)
					return;
				boolean find = false;
				for(int i =0;i<conversations.size();i++){
					if(conversations.get(i).getId().equals(entity.getId())){
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
				num = num +conversations.get(i).getUnread();
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
	private List<UserConversationEntity> conversations =new ArrayList<UserConversationEntity>();
	public void conversaionInit(View view){
		lv_conversation = (ListView) view.findViewById(R.id.lv_conversation);
		conversationAdapter = new ConversationAdapter(context);
		lv_conversation.setAdapter(conversationAdapter);
		callBackUpdate = new CallBackUpdate(updatesessionHandler);

		lv_conversation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isGetSession) {
					return;
				}
				isGetSession = true;
				if (conversations != null && conversations.size() > position){
					final UserConversationEntity entity = conversations.get(position);
				   Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(entity.getTargetId(), entity.getType(), new Back.Result<Session>() {
					@Override
					public void onSuccess(final Session session) {
						Intent intent = new Intent(context, ChatActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("sessionid", session.getEntity().getId());
						bundle.putString("sessiontype", session.getEntity().getType());
						bundle.putString("otheruserId", session.getEntity().getOtherSideId());
						bundle.putString("name", entity.getName());
						intent.putExtras(bundle);
						startActivity(intent);
						isGetSession = false;
					}

					@Override
					public void onError(int errorCode, String error) {
						isGetSession = false;
					}
				});
		     	}else{
					isGetSession = false;
				}
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

