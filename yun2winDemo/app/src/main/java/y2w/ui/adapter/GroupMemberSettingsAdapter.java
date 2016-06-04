package y2w.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yun2win.demo.R;

import java.util.List;

/**
 * Created by maa2 on 2016/5/4.
 */
public class GroupMemberSettingsAdapter extends BaseAdapter {


    private List<String> settings;
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder viewholder;
    private String type;

    public GroupMemberSettingsAdapter(Context context,List<String> settings,String type){
        this.context = context;
        this.settings = settings;
        this.mInflater = LayoutInflater.from(context);
        this.type = type;
    }

    @Override
    public int getCount() {
        return settings == null ? 0 : settings.size();
    }

    @Override
    public Object getItem(int arg0) {
        return settings.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, final ViewGroup arg2) {
        viewholder=new ViewHolder();
        if(arg1 == null){
            arg1=mInflater.inflate(R.layout.group_member_settings_item, null);
            viewholder.tv_selection=(TextView) arg1.findViewById(R.id.tv_selection);
            arg1.setTag(viewholder);
        }else{
            viewholder=(ViewHolder) arg1.getTag();
        }
        if("memberManager".equals(type)){
            viewholder.tv_selection.setText(settings.get(arg0));
        }else if("memberOper".equals(type)){
            String info = settings.get(arg0);
            viewholder.tv_selection.setText(info);
            if(info.contains("取消") || info.contains("删除")){
                viewholder.tv_selection.setTextColor(Color.parseColor("#ea4e56"));
            }else{
                viewholder.tv_selection.setTextColor(Color.parseColor("#1cc09f"));
            }
        }
        return arg1;
    }

    public class ViewHolder{
        private TextView tv_selection;
    }

}
