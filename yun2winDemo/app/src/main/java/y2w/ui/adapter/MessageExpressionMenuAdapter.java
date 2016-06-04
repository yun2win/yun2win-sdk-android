package y2w.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yun2win.demo.R;

import java.util.List;

import y2w.base.AppContext;
import y2w.ui.widget.emoji.Expression;

public class MessageExpressionMenuAdapter extends BaseAdapter{


	private List<Expression.ExprMenu> menus;
	private LayoutInflater mInflater;
	private Context _context;
	private ViewHolder viewholder;
	
	public MessageExpressionMenuAdapter(Context context,List<Expression.ExprMenu> _menus){
		
		this._context = context;
		this.menus=_menus;
		this.mInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return menus == null ? 0 : menus.size();
	}

	@Override
	public Object getItem(int arg0) {
		return menus.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, final ViewGroup arg2) {
		viewholder=new ViewHolder();
		if(arg1 == null){
			arg1=mInflater.inflate(R.layout.message_list_expression_menu_item, null);
			viewholder.tv_menu_name=(TextView) arg1.findViewById(R.id.tv_expresstion_menu_name);
			arg1.setTag(viewholder);
		}else{
			viewholder=(ViewHolder) arg1.getTag();
		}
		
		/*if(ChatActivity.titleindex == arg0){
			viewholder.tv_menu_name.setTextColor(Color.parseColor("#353535"));
			viewholder.tv_menu_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, appContext.getResources().getDimensionPixelSize(R.dimen.sp_15));
			viewholder.tv_menu_name.getPaint().setFakeBoldText(true);
		}else{
			viewholder.tv_menu_name.setTextColor(Color.parseColor("#999999"));
			viewholder.tv_menu_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, appContext.getResources().getDimensionPixelSize(R.dimen.sp_14));
			viewholder.tv_menu_name.getPaint().setFakeBoldText(false);
		}*/
		viewholder.tv_menu_name.setText(menus.get(arg0).getName());
		return arg1;
	}
	
	public class ViewHolder{
		private TextView tv_menu_name;
	}

}

