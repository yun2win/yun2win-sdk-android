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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.manage.EnumManage;

/**
 * Created by maa2 on 2016/5/13.
 */
public class AvDialog extends Dialog{
    private Context context;
    private AvDialog me;
    private RelativeLayout rl_view;
    private ListView lv_option;
    private MenuAdapter menuAdapter;
    private onOptionClickListener onOptionClickListener;
    private String callType;
    private String[] optionList = new String[]{"handUp", "mute","closeHandsFree","turnOffCamera","changeCamera","addMember"};
    private List<NameState> stateList;

    public AvDialog(Context context,String callType,String chatType) {
        super(context, R.style.MenuDialog);
        this.context = context;
        this.callType = callType;
        if(!chatType.equals(EnumManage.SessionType.group.toString())){//会议
            optionList = new String[]{
                    "handUp","mute","closeHandsFree","turnOffCamera","changeCamera"};
        }
    }

    public AvDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public void setOnOptionClickListener(onOptionClickListener onOptionClickListener) {
        this.onOptionClickListener = onOptionClickListener;
    }

    @Override
    public void show() {
        super.show();
        for(int i = 0;i<optionList.length;i++){
            NameState state = new NameState();
            switch (i){
                case 2:
                case 3:
                    state.select = true;
                    break;
                default:
                    state.select = false;
                    break;
            }
            state.name = optionList[i];
            stateList.add(state);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = this;
        this.setContentView(R.layout.dialog_av);
        rl_view = (RelativeLayout) findViewById(R.id.rl_av_dialog);
        lv_option = (ListView) findViewById(R.id.lv_av_option);
        rl_view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                me.cancel();
                return false;
            }
        });
        stateList = new ArrayList<NameState>();
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
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                boolean selected = !stateList.get(arg2).select;
                stateList.get(arg2).select = selected;
                if (onOptionClickListener != null) {
                    onOptionClickListener.onOptionClick(arg2,selected);
                }
                if(menuAdapter != null)
                    menuAdapter.notifyDataSetChanged();
            }
        });
    }

    class MenuAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return optionList == null ? 0 : optionList.length;
        }

        @Override
        public Object getItem(int arg0) {
            return optionList[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ViewHolder viewHolder=null;
            if(arg1 == null){
                viewHolder = new ViewHolder();
                arg1 = LayoutInflater.from(context).inflate(R.layout.av_dialog_item, null);
                viewHolder.iv_icon = (ImageView) arg1.findViewById(R.id.iv_av_icon);
                viewHolder.tv_name = (TextView) arg1.findViewById(R.id.tv_av_title);
                arg1.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) arg1.getTag();
            }
            if("handUp".equals(stateList.get(arg0).name)){
                viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_handup_normal_right);
                viewHolder.tv_name.setText("挂断");
            }else if("mute".equals(stateList.get(arg0).name)){
                if(stateList.get(arg0).select) {
                    viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_mute_right_selected);
                }else{
                    viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_mute_right);
                }
                viewHolder.tv_name.setText("静音");
            }else if("closeHandsFree".equals(stateList.get(arg0).name)){
                if(stateList.get(arg0).select) {
                    viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_handsfree_right_selected);
                }else{
                    viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_handsfree_right);
                }
                viewHolder.tv_name.setText("扬声器");
            }else if("turnOffCamera".equals(stateList.get(arg0).name)){
                if(stateList.get(arg0).select) {
                    viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_offcamera_right_selected);
                }else{
                    viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_offcamera_right);
                }
                viewHolder.tv_name.setText("摄像头");
            }else if("changeCamera".equals(stateList.get(arg0).name)){
                viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_changecamera_normal_right);
                viewHolder.tv_name.setText("转摄像头");
            }else if("addMember".equals(stateList.get(arg0).name)){
                viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_addmember_normal_right);
            }
            return arg1;
        }
    }

    class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
    }
    class NameState{
        String name;
        boolean select;
    }

    public interface onOptionClickListener{
        public void onOptionClick(int position, boolean selected);
    }


}
