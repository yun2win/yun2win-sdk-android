package y2w.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.model.Session;
import y2w.ui.activity.ImageSendChooseActivity;
import y2w.ui.adapter.MessageAddAdapter;
/**
 * Created by hejie on 2016/3/14.
 * 消息界面
 */
public class MessageFragment extends Fragment {
	
    private String y2wChat;
    private String DEFAULT = "default";
    private static Activity _activity;
	private static Context _context;
    private List<String> moreItems=new ArrayList<String>();

   
    
	public static MessageFragment newInstance(Activity activity,Context context,String str){
		_activity = activity;
		_context = context;
		MessageFragment newFragment = new MessageFragment();
		Bundle bundle = new Bundle();
		bundle.putString("y2wChat", str);
		newFragment.setArguments(bundle);

		return newFragment;
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			Bundle args = getArguments();
			y2wChat = args != null ? args.getString("y2wChat") : DEFAULT;
			View view = null;
			if("add_first".equals(y2wChat)){
	        	view = inflater.inflate(R.layout.message_list_add_include, container, false);
	        	GridView gridView=(GridView) view.findViewById(R.id.gv_message_add_more);
	        	initMoreGridView(gridView);
	        }
	        
	        return view;
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
				_activity.startActivityForResult(intent, 2);
			}else if("本地文档".equals(moreItems.get(arg2))){
				/*Intent intent = new Intent(appContext, StoreageFileSelectActivity.class);
				Bundle extras = new Bundle();
				extras.putString("type", "chat");
				intent.putExtras(extras);
				_activity.startActivityForResult(intent, 4);*/
			}
		}
		
	}
	private void setMessageMoreDatas(){
		 moreItems.clear();
		 moreItems.add("图片");
		 //moreItems.add("本地文档");
	}

}
