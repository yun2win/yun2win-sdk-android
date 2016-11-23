package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.util.List;

import com.y2w.uikit.utils.pinyinutils.SortModel;

import y2w.base.Urls;
import y2w.common.HeadImageView;

/**
 * Created by maa2 on 2016/2/19.
 */
public class SessionStartAdapter extends BaseAdapter {

    private List<SortModel> sortModels;
    private Context context;
    private boolean isavatar = true;
    private boolean showselect = true;

    public SessionStartAdapter(Context context){
        this.context = context;
    }

    public boolean isavatar() {
        return isavatar;
    }

    public void setIsavatar(boolean isavatar) {
        this.isavatar = isavatar;
    }

    public boolean isShowselect() {
        return showselect;
    }

    public void setShowselect(boolean showselect) {
        this.showselect = showselect;
    }

    public void setListView(List<SortModel> list) {
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
        SessionHoldView holdView;
        if (null == view) {
            holdView = new SessionHoldView();
            view = LayoutInflater.from(context).inflate(R.layout.sessionstart_list_item, null);
            holdView.img_header = (HeadImageView) view
                    .findViewById(R.id.img_contact_header);
            holdView.tv_name = (TextView) view
                    .findViewById(R.id.tv_contact_name);
            holdView.tv_header = (TextView) view
                    .findViewById(R.id.tv_contact_header);
            holdView.tv_chart = (TextView) view
                    .findViewById(R.id.textchart);
            holdView.view_line=view.findViewById(R.id.viewline);
            holdView.iv_select = (ImageView) view.findViewById(R.id.iv_contact_select);
            holdView.relativeLayouthead = (RelativeLayout) view.findViewById(R.id.relativeLayout11);
            view.setTag(holdView);
        } else {
            holdView = (SessionHoldView) view.getTag();
        }
        setIndexview(holdView,model,position);
        if(!showselect){
            holdView.iv_select.setVisibility(View.GONE);
        }
        return view;
    }
    public void setIndexview(SessionHoldView holdView,SortModel model,int position){
        holdView.tv_name.setText(model.getName());

        if(isavatar) {
            holdView.img_header.loadBuddyAvatarbyurl(model.getAvatarUrl(), R.drawable.default_person_icon);
            holdView.tv_header.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(model.getUserId())));
        }else{
            holdView.relativeLayouthead.setVisibility(View.GONE);
        }
        if(position==0){
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
       if(model.isChoice()){
           holdView.iv_select.setImageResource(R.drawable.checked);
       }else{
           holdView.iv_select.setImageResource(R.drawable.unchecked);
       }
    }

  public class SessionHoldView {
        public HeadImageView img_header;
        public TextView tv_name;
        public TextView tv_header;
        public TextView tv_chart;
        public View view_line;
        public ImageView iv_select;
        public RelativeLayout relativeLayouthead;
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

