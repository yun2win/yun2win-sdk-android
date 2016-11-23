package y2w.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yun2win.demo.R;

import java.util.List;

import y2w.base.AppContext;

public class MessageAddAdapter extends BaseAdapter{


	private LayoutInflater mInflater;
	private Activity _activity;
	private ViewHolder viewholder;
	private List<String> _items;
	
	
	public MessageAddAdapter(Activity activity, Context context, List<String> _items){
		this._activity = activity;
		this._items=_items;
		if(context!=null) {
			this.mInflater = LayoutInflater.from(context);
		}else{
			this.mInflater = LayoutInflater.from(AppContext.getAppContext());
		}
	}

	@Override
	public int getCount() {
		return _items == null ? 0 : _items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return _items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, final ViewGroup arg2) {
		viewholder=new ViewHolder();
		if(arg1 == null){
			arg1=mInflater.inflate(R.layout.message_list_add_include_item, null);
			viewholder.iv_more_image = (ImageView) arg1.findViewById(R.id.iv_more_item);
			viewholder.tv_more_name= (TextView) arg1.findViewById(R.id.tv_more_name);
			
			arg1.setTag(viewholder);
		}else{
			viewholder=(ViewHolder) arg1.getTag();
		}
		setVoteSelectionCreate(arg0,arg1,viewholder);
		return arg1;
	}
	
	
	private void setVoteSelectionCreate(final int arg0, View arg1, final ViewHolder viewHolder){
		viewholder.tv_more_name.setText(_items.get(arg0));
		viewholder.iv_more_image.setBackgroundResource(getDrawable(arg0));
	}
	
	private int getDrawable(final int arg0){
		if("图片".equals(_items.get(arg0))){
			return R.drawable.message_add_picture_normal;
		}else if("小视频".equals(_items.get(arg0))){
			return R.drawable.message_add_movie_normal;
		}else if("文档".equals(_items.get(arg0))){
			return R.drawable.message_add_storeage_normal;
		}else if("位置".equals(_items.get(arg0))){
			return R.drawable.message_add_location_normal;
		}else if("语音".equals(_items.get(arg0))){
			return R.drawable.message_add_voice_normal;
		}else if("视频".equals(_items.get(arg0))){
			return R.drawable.message_add_video_normal;
		}else if("任务".equals(_items.get(arg0))){
			return R.drawable.message_add_tast_normal;
		}
		return 0;
	}
	public class ViewHolder{
		private ImageView iv_more_image;
		private TextView tv_more_name;
	}
	
}


