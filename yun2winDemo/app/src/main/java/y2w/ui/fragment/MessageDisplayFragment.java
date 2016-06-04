package y2w.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yun2win.demo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.base.Urls;
import y2w.common.Config;
import y2w.entities.EmojiEntity;
import y2w.manage.Users;
import y2w.model.Emoji;
import y2w.ui.activity.ChatActivity;
import y2w.ui.adapter.MessageExpressionAdapter;
import y2w.ui.widget.emoji.Expression;

public class MessageDisplayFragment extends Fragment {
	
    private int pagerIndex;
    private String DEFAULT = "default";
    private ImageButton iv_storeage;
    private ImageButton iv_takepicture;
    private ImageButton iv_readdelete;
    private ImageButton iv_file;
    private ImageButton iv_vote;
    private TextView tv_vote_title;
    private ImageButton iv_time_machine;
    private TextView tv_time_machine_title;
    private GridView gv_expression;
    private MessageExpressionAdapter expressionAdapter;
    private static Context _context;
    private static Activity _activity;
    private List<Emoji> emojiList;
    private static String _type;
    private Dialog dialog;
	private ChatActivity chatActivity;

	public static MessageDisplayFragment newInstance(Activity activity, Context context, String type,int pagerIndex){
		_context = context;
		_type = type;
		_activity = activity;
		MessageDisplayFragment newFragment = new MessageDisplayFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("pagerIndex", pagerIndex);
		newFragment.setArguments(bundle);

		return newFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			Bundle args = getArguments();
			pagerIndex = args != null ? args.getInt("pagerIndex") : -1;
	        View view = null;
	        if(pagerIndex > 0){
	        	view = inflater.inflate(R.layout.message_list_expression_include, container, false);
	        	gv_expression = (GridView) view.findViewById(R.id.gv_message_expression);
	        	gv_expression.setNumColumns(7);
	        	gv_expression.setOnItemClickListener(new emojiOnItemClick());
	        	initExpression(pagerIndex);
	        }
	        return view;
	}
	
	private void initExpression(int index){
		emojiList=new ArrayList<Emoji>();
		List<Emoji> list = Users.getInstance().getCurrentUser().getEmojis().getEmojiByPackage(_type);
		if(list.size() > 0) {
			int start = 0;
			int end = 0;
			start = (index - 1) * 20;
			end = (start + 20) < list.size() ? start + 20 : list.size();
			for (int i = start; i < end; i++) {

				emojiList.add(list.get(i));
			}
			EmojiEntity entity = new EmojiEntity();
			entity.setTotal(-1);
			emojiList.add(new Emoji(entity));
			expressionAdapter = new MessageExpressionAdapter(_context, emojiList, "emoji");
			gv_expression.setAdapter(expressionAdapter);
		}
	}
	

	private class emojiOnItemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (ChatActivity.chatHandler != null) {
				handlerNotice(arg2);
			}
			
		}
		
	}

	private void handlerNotice(int arg){
		Emoji emoji = emojiList.get(arg);
		if(emoji.getEntity().getTotal() < 0){
			Message msg = new Message();
			msg.what = ChatActivity.RefreshCode.CODE_EMOJI_DELETE;
			ChatActivity.chatHandler
					.sendMessage(msg);
		}else{
			Message msg = new Message();
			msg.what = ChatActivity.RefreshCode.CODE_EMOJI_ADD;
			msg.obj = "["+emoji.getEntity().getName()+"]";
			ChatActivity.chatHandler
					.sendMessage(msg);
		}
	}

}
