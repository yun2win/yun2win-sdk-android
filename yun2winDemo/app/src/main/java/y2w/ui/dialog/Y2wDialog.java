package y2w.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 纵向列表Dialog
 * Created by yangrongfang on 2016/4/9.
 */
public class Y2wDialog extends Dialog{
	private Context context;
	private Y2wDialog me;
	private RelativeLayout rl_view;
	private ListView lv_option;
	private MenuAdapter menuAdapter;
	private List<String> optionList;
	private onOptionClickListener onOptionClickListener;

	public Y2wDialog(Context context) {
		super(context, R.style.MenuDialog);
		this.context = context;
	}

	public Y2wDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setOnOptionClickListener(onOptionClickListener onOptionClickListener) {
		this.onOptionClickListener = onOptionClickListener;
	}

	public void addOption(String option){
		if(optionList == null){
			optionList = new ArrayList<String>();
		}
		optionList.add(option);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		me = this;
		this.setContentView(R.layout.dialog_message);
		rl_view = (RelativeLayout) findViewById(R.id.rl_message_dialog);
		lv_option = (ListView) findViewById(R.id.lv_message_deal);
		rl_view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				me.cancel();
				return false;
			}
		});
		/*WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.alpha = 0.9f;//透明度设置
		getWindow().setAttributes(lp);*/
		optionShow();
	}

	private void optionShow(){
		menuAdapter = new MenuAdapter();
		lv_option.setAdapter(menuAdapter);
		lv_option.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				String option = optionList.get(arg2);
				if(onOptionClickListener != null){
					onOptionClickListener.onOptionClick(option, arg2);
				}
				me.cancel();
			}
		});
	}

	class MenuAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return optionList == null ? 0 : optionList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return optionList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			String option = optionList.get(arg0);
			ViewHolder viewHolder=null;
			if(arg1 == null){
				viewHolder = new ViewHolder();
				arg1 = LayoutInflater.from(context).inflate(R.layout.message_dialog_item, null);
				viewHolder.tv_name = (TextView) arg1.findViewById(R.id.tv_option);
				arg1.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) arg1.getTag();
			}
			viewHolder.tv_name.setText(option);
			return arg1;
		}
		
	}
	
	class ViewHolder{
		TextView tv_name;
	}

	public interface onOptionClickListener{
		public void onOptionClick(String option, int position);
	}

}