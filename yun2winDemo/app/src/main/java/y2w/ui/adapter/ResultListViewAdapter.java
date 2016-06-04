package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.imageview.HeadImageView;
import com.yun2win.demo.R;

import java.util.List;

import y2w.model.NewDataModel;
import y2w.model.UserSession;

/**
 * Created by Administrator on 2016/4/25.
 */
public class ResultListViewAdapter extends BaseAdapter {

    private List<NewDataModel> list;
    private Context context;
    public ResultListViewAdapter(List<NewDataModel> list,Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        NewDataModel model = list.get(position);
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.result_listview_item,null);
            holder = new ViewHolder();
            holder.group_name = (TextView)convertView.findViewById(R.id.group_name_show);
            holder.head_icon = (HeadImageView)convertView.findViewById(R.id.head_icon);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }
        //
        holder.group_name.setText(model.getName());
        holder.head_icon.loadBuddyAvatarbyurl(model.getHead_url(), R.drawable.default_group_icon);
        return convertView;
    }
    class ViewHolder{
        HeadImageView head_icon;
        TextView group_name;
    }
}
