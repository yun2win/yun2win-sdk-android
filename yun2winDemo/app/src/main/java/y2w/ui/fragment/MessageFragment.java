package y2w.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.manage.Users;
import y2w.model.Session;
import y2w.ui.activity.ChatActivity;
import y2w.ui.activity.ImageSendChooseActivity;
import y2w.ui.activity.LocationActivity;
import y2w.ui.activity.MovieRecorderActivity;
import y2w.ui.activity.StoreageFileSelectActivity;
import y2w.ui.adapter.MessageAddAdapter;
import y2w.ui.adapter.MessageFragementDisplayPagerAdapter;
import y2w.ui.widget.emoji.ChatEmoji;

/**
 * Created by hejie on 2016/3/14.
 * 消息界面
 */
public class MessageFragment extends Fragment {
	
    private String y2wChat;
    private String DEFAULT = "default";
    private static Activity _activity;
	private static Context _context;
	private static Session _session;
	private ChatEmoji _chatEmoji;
    private List<String> moreItems=new ArrayList<String>();

   
    
	public static MessageFragment newInstance(Activity activity,Context context,Session session,String str){
		_activity = activity;
		_context = context;
		_session = session;
		MessageFragment newFragment = new MessageFragment();
		Bundle bundle = new Bundle();
		bundle.putString("y2wChat", str);
		newFragment.setArguments(bundle);

		return newFragment;
		
	}

	public void setChatEmoji(ChatEmoji chatEmoji) {
		_chatEmoji = chatEmoji;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			Bundle args = getArguments();
			y2wChat = args != null ? args.getString("y2wChat") : DEFAULT;
			View view = null;
		if("base".equals(y2wChat)){
			view = inflater.inflate(R.layout.message_list_include, container, false);
			ViewPager vp_pager = (ViewPager) view.findViewById(R.id.vp_message_viewPager_include);
			initExpressionViewPager(vp_pager,"基本");
		}else if("add_first".equals(y2wChat)){
	        	view = inflater.inflate(R.layout.message_list_add_include, container, false);
	        	GridView gridView=(GridView) view.findViewById(R.id.gv_message_add_more);
	        	initMoreGridView(gridView);
		}
	        
	        return view;
	}


	/*
	 * 初始化大表情 ViewPager
	 */
	public void initExpressionViewPager(ViewPager vp_pager,String ePackage) {
		List<Fragment> fragmentList = new ArrayList<Fragment>();
		long count = Users.getInstance().getCurrentUser().getEmojis().getEmojiCountByPackage(ePackage);
		int num = (int)(count / 20);
		if(num > 0){
			for(int i=1;i<num + 1;i++) {
				Fragment fragment = MessageDisplayFragment.newInstance(_activity, _context,
						ePackage, i);
				fragmentList.add(fragment);
			}
			// 给ViewPager设置适配器
			MessageFragementDisplayPagerAdapter pagerAdapter = new MessageFragementDisplayPagerAdapter(
					getChildFragmentManager(), fragmentList);
			vp_pager.setAdapter(pagerAdapter);
			vp_pager.setOnPageChangeListener(new MyOnPageChangeListener());
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
			_chatEmoji.setPagerIndex(2,arg0);
		}
	}

	/*
	 * 初始化更多
	 */
	public static GridView _gv_more;
	public void initMoreGridView(GridView gv_more) {
		if(gv_more != null){
			_gv_more = gv_more;
			setMessageMoreDatas();
			MessageAddAdapter adapter = new MessageAddAdapter( _activity,_context, moreItems);
			_gv_more.setAdapter(adapter);
			_gv_more.setNumColumns(4);
			_gv_more.setOnItemClickListener(new messageMoreItemOnClick());
		}
	}

	private class messageMoreItemOnClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(_activity == null){
				return;
			}
			if("图片".equals(moreItems.get(arg2))){
				Intent intent = new Intent(_context, ImageSendChooseActivity.class);
				intent.putExtra("imgnum", 0);
				_activity.startActivityForResult(intent, ChatActivity.ResultCode.CODE_IMAGE);
			}else if("小视频".equals(moreItems.get(arg2))){
				Intent intent = new Intent(_context, MovieRecorderActivity.class);
				_activity.startActivityForResult(intent, ChatActivity.ResultCode.CODE_MOVIE);
			}else if("文档".equals(moreItems.get(arg2))){
				Intent intent = new Intent(_context, StoreageFileSelectActivity.class);
				Bundle extras = new Bundle();
				extras.putString("type", "chat");
				intent.putExtras(extras);
				_activity.startActivityForResult(intent, ChatActivity.ResultCode.CODE_FILE);
			}else if("位置".equals(moreItems.get(arg2))){
				Intent intent = new Intent(_context, LocationActivity.class);
				Bundle extras = new Bundle();
				extras.putInt("type", LocationActivity.LocationType.locationSend);
				intent.putExtras(extras);
				_activity.startActivityForResult(intent, ChatActivity.ResultCode.CODE_LOCATION);
			}
		}
		
	}
	private void setMessageMoreDatas(){
		 moreItems.clear();
		 moreItems.add("图片");
		 moreItems.add("小视频");
		 moreItems.add("文档");
		 moreItems.add("位置");
		
	}

}
