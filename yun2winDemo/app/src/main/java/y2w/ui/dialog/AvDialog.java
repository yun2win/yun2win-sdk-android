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
import y2w.ui.activity.AVCallActivity;

/**
 * Created by maa2 on 2016/5/13.
 */
public class AvDialog extends Dialog{
    private Context context;
    private AvDialog me;
    private RelativeLayout rl_view;
    private ListView lv_option;
    private MenuAdapter menuAdapter;
    private List<AvMenu> optionList;
    private onOptionClickListener onOptionClickListener;
    private String callType;
    private String[] options = new String[]{
            "handUp","mute","closeHandsFree","turnOffCamera","changeCamera","addMember"
    };

    public AvDialog(Context context,String callType) {
        super(context, R.style.MenuDialog);
        this.context = context;
        this.callType = callType;
    }

    public AvDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public void setOnOptionClickListener(onOptionClickListener onOptionClickListener) {
        this.onOptionClickListener = onOptionClickListener;
    }

    private void addOption(AvMenu option){
        if(optionList == null){
            optionList = new ArrayList<AvMenu>();
        }
        optionList.add(option);
    }

    @Override
    public void show() {
        for(String s : options){
            AvMenu avMenu = new AvMenu();
            avMenu.setOption(s);
            avMenu.setName(getName(s));
            addOption(avMenu);
        }
        super.show();
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
                AvMenu avMenu = optionList.get(arg2);
                if (onOptionClickListener != null) {
                    onOptionClickListener.onOptionClick(avMenu.getName(), arg2);
                }
                clickRefresh(arg2);
                //me.cancel();
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
            AvMenu avMenu = optionList.get(arg0);
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
            if("handUp".equals(avMenu.getOption())){
                viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_handup_normal_right);
            }else if("mute".equals(avMenu.getOption())){
                viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_mute_normal_right);
            }else if("closeHandsFree".equals(avMenu.getOption())){
                viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_handsfree_normal_right);
            }else if("turnOffCamera".equals(avMenu.getOption())){
                viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_offcamera_normal_right);
            }else if("changeCamera".equals(avMenu.getOption())){
                viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_changecamera_normal_right);
            }else if("addMember".equals(avMenu.getOption())){
                viewHolder.iv_icon.setBackgroundResource(R.drawable.av_action_addmember_normal_right);
            }
            viewHolder.tv_name.setText(avMenu.getName());
            return arg1;
        }

    }

    private String getName(String option){
        if("handUp".equals(option)){
            return "挂断";
        }else if("mute".equals(option)){
            return "开启静音";
        }else if("closeHandsFree".equals(option)){
            return "关闭免提";
        }else if("turnOffCamera".equals(option)){
            return EnumManage.AvCallType.video.toString().equals(callType) ? "关摄像头":"开摄像头";
        }else if("changeCamera".equals(option)){
            return "转摄像头";
        }else if("addMember".equals(option)){
            return "添加成员";
        }
        return "";
    }

    private void clickRefresh(int position){
        if(optionList.get(position).getName().equals("开启静音")){
            optionList.get(position).setName("关闭静音");
        }else if(optionList.get(position).getName().equals("关闭静音")){
            optionList.get(position).setName("开启静音");
        }else if(optionList.get(position).getName().equals("关闭免提")){
            optionList.get(position).setName("开启免提");
        }else if(optionList.get(position).getName().equals("开启免提")){
            optionList.get(position).setName("关闭免提");
        }else if(optionList.get(position).getName().equals("关摄像头")){
            optionList.get(position).setName("开摄像头");
        }else if(optionList.get(position).getName().equals("开摄像头")){
            optionList.get(position).setName("关摄像头");
        }
        if(menuAdapter != null)
            menuAdapter.notifyDataSetChanged();
    }

    public class AvMenu{
        String option;
        String name;

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
    }

    public interface onOptionClickListener{
        public void onOptionClick(String option, int position);
    }


}
