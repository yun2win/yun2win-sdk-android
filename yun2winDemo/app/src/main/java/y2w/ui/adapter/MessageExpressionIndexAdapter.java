package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.yun2win.demo.R;

import java.util.List;

import y2w.base.AppContext;

public class MessageExpressionIndexAdapter extends BaseAdapter{


	private List<Boolean> indexs;
	private LayoutInflater mInflater;
	private Context _context;
	private ViewHolder viewholder;
	
	public MessageExpressionIndexAdapter(Context context,List<Boolean> booleans){
		
		this._context = context;
		this.indexs=booleans;
		this.mInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return indexs == null ? 0 : indexs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return indexs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, final ViewGroup arg2) {
		viewholder=new ViewHolder();
		if(arg1 == null){
			arg1=mInflater.inflate(R.layout.message_list_expression_index_item, null);
			viewholder.iv_expression=(ImageView) arg1.findViewById(R.id.iv_message_expression_index);
			arg1.setTag(viewholder);
		}else{
			viewholder=(ViewHolder) arg1.getTag();
		}
		if(indexs.get(arg0)){
			viewholder.iv_expression.setBackgroundResource(R.drawable.message_expression_on);
		}else{
			viewholder.iv_expression.setBackgroundResource(R.drawable.message_expression_off);
		}

		return arg1;
	}
	
	public class ViewHolder{
		private ImageView iv_expression;
	}

}

