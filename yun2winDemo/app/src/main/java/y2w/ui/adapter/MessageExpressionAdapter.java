package y2w.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yun2win.demo.R;

import java.io.File;
import java.util.List;

import y2w.base.AppContext;
import y2w.common.Config;
import y2w.model.Emoji;

public class MessageExpressionAdapter extends BaseAdapter{


	private List<Emoji> emojis;
	private LayoutInflater mInflater;
	private Context context;
	private ViewHolder viewholder;
	private String type;
	
	public MessageExpressionAdapter(Context context, List<Emoji> emojis, String type){
		
		this.context = context;
		this.emojis = emojis;
		this.type = type;
		this.mInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return emojis == null ? 0 : emojis.size();
	}

	@Override
	public Object getItem(int arg0) {
		return emojis.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, final ViewGroup arg2) {
		viewholder=new ViewHolder();
		Emoji emoji = emojis.get(arg0);
		if(arg1 == null){
			arg1=mInflater.inflate(R.layout.message_list_expression_item, null);
			viewholder.iv_expression=(ImageView) arg1.findViewById(R.id.iv_message_expression);
			viewholder.iv_pinup=(ImageView) arg1.findViewById(R.id.iv_message_pinup);
			arg1.setTag(viewholder);
		}else{
			viewholder=(ViewHolder) arg1.getTag();
		}
		if("emoji".equals(type)){
			viewholder.iv_expression.setVisibility(View.VISIBLE);
			if(emoji.getEntity().getTotal() < 0){
				viewholder.iv_expression.setBackgroundResource(R.drawable.message_expression_delete_normal);
			}else{
				Drawable drawable = Drawable.createFromPath(new File(Config.CACHE_PATH_EMOJI+"base", emoji.getEntity().getName()+".png").getAbsolutePath());
				viewholder.iv_expression.setImageDrawable(drawable);
			}
		}else{
		/*	viewholder.iv_pinup.setVisibility(View.VISIBLE);
			viewholder.iv_pinup.setBackgroundResource(expressions.get(arg0));*/
		}
		
		return arg1;
	}
	
	public class ViewHolder{
		private ImageView iv_expression;
		private ImageView iv_pinup;
	}
	

}
