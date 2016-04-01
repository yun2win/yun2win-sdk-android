package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.imageview.HeadImageView;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.yun2win.demo.R;

import java.util.List;

import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.pinyinutils.SortModel;

import y2w.manage.EnumManage;

/**
 * Created by maa2 on 2016/1/22.
 */
public class ContactAdapter extends BaseAdapter {

    private List<SortModel> sortModels;
    private Context context;
    public ContactAdapter(Context context){
        this.context = context;
    }

    public void updateListView() {
        notifyDataSetChanged();
    }

    public void setListViewdate(List<SortModel> list){
        this.sortModels = list;
    }

    @Override
    public int getCount() {
        return sortModels == null ? 0 :sortModels.size();
    }

    @Override
    public Object getItem(int position) {
        return sortModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(position > sortModels.size() - 1){
            return view;
        }
        SortModel model = sortModels.get(position);
        HoldView holdView;
        if (null == view) {
            holdView = new HoldView();
            view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, null);
            holdView.img_header = (HeadImageView) view
                    .findViewById(R.id.img_contact_header);
            holdView.tv_name = (TextView) view
                    .findViewById(R.id.tv_contact_name);
            holdView.tv_header = (TextView) view
                    .findViewById(R.id.tv_contact_header);
            holdView.tv_chart = (TextView) view
                    .findViewById(R.id.textchart);
            holdView.view_line=view.findViewById(R.id.viewline);

            view.setTag(holdView);
        } else {
            holdView = (HoldView) view.getTag();
        }
        setTitle(holdView,model,position);
        return view;
    }
    private void setTitle(HoldView holdView,SortModel model,int position){
        if(position==0){//我的群
            holdView.tv_name.setText(model.getName());
            holdView.img_header.setImageResource(R.drawable.default_group_icon);
            holdView.tv_chart.setVisibility(View.GONE);
            holdView.view_line.setVisibility(View.GONE);
            return;
        }

        holdView.tv_name.setText(model.getName());
        holdView.img_header.loadBuddyAvatarbyurl(model.getAvatarUrl(), R.drawable.default_person_icon);
        holdView.tv_header.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(model.getUserId())));
        if(position==1){
            holdView.tv_chart.setVisibility(View.VISIBLE);
            holdView.tv_chart.setText(model.getSortLetters());
        }else{
            if(sortModels.get(position-1).getSortLetters().equals(model.getSortLetters())){
                holdView.tv_chart.setVisibility(View.GONE);
            }else{
                holdView.tv_chart.setVisibility(View.VISIBLE);
                holdView.tv_chart.setText(model.getSortLetters());
            }
        }
        if(sortModels.size()>(position+1)) {
            if (sortModels.get(position + 1).getSortLetters().equals(model.getSortLetters())) {
                holdView.view_line.setVisibility(View.VISIBLE);
            } else {
                holdView.view_line.setVisibility(View.GONE);
            }
        }
    }

    class HoldView {
        public HeadImageView img_header;
        public TextView tv_name;
        public TextView tv_header;
        public TextView tv_chart;
        public View view_line;
    }
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        try{
            if(section == -1)
                return -1;
            for (int i = 0; i < getCount(); i++) {
                String sortStr = sortModels.get(i).getSortLetters();
                if(sortStr != null && !sortStr.equals("")){
                    char firstChar = sortStr.toUpperCase().charAt(0);
                    if (firstChar == section) {
                        return i;
                    }
                }
            }
        }catch(Exception e){

        }
        return -1;
    }
}
