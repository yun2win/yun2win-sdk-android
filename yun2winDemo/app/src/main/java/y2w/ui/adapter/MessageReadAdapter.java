package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;

import java.util.List;

import y2w.base.Urls;
import y2w.common.HeadImageView;
import y2w.model.SessionMember;

/**
 * Created by maa2 on 2016/1/22.
 */
public class MessageReadAdapter extends BaseAdapter {

    private List<SessionMember> sessionmembers;
    private Context context;
    public MessageReadAdapter(Context context){
        this.context = context;
    }

    public void updateListView() {
        notifyDataSetChanged();
    }

    public void setListViewdate(List<SessionMember> list){
        this.sessionmembers = list;
    }

    @Override
    public int getCount() {
        return sessionmembers == null ? 0 :sessionmembers.size();
    }

    @Override
    public Object getItem(int position) {
        return sessionmembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(position > sessionmembers.size() - 1){
            return view;
        }
        SessionMember model = sessionmembers.get(position);
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
    private void setTitle(HoldView holdView,SessionMember model,int position){

        holdView.tv_name.setText(model.getEntity().getName());
        holdView.img_header.loadBuddyAvatarbyurl(model.getEntity().getAvatarUrl() , R.drawable.default_person_icon);

        holdView.tv_header.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(model.getEntity().getUserId())));
    }

    class HoldView {
        public HeadImageView img_header;
        public TextView tv_name;
        public TextView tv_header;
        public TextView tv_chart;
        public View view_line;
    }
}
