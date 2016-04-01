package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.imageview.HeadImageView;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.util.List;

import y2w.model.SessionMember;
import y2w.model.UserSession;

/**
 * Created by maa2 on 2016/1/22.
 */
public class GroupMemberAdapter extends BaseAdapter {

    private List<SessionMember> sessionMembers;
    private Context context;
    public GroupMemberAdapter(Context context){
        this.context = context;
    }

    public void updateListView() {
        notifyDataSetChanged();
    }

    public void setListViewdate(List<SessionMember> list){
        this.sessionMembers = list;
    }

    @Override
    public int getCount() {
        return sessionMembers == null ? 0 :sessionMembers.size();
    }

    @Override
    public Object getItem(int position) {
        return sessionMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(position > sessionMembers.size() - 1){
            return view;
        }
        SessionMember member = sessionMembers.get(position);
        HoldView holdView;
        if (null == view) {
            holdView = new HoldView();
            view = LayoutInflater.from(context).inflate(R.layout.groupmember_list_item, null);
            holdView.img_header = (HeadImageView) view
                    .findViewById(R.id.img_contact_header);
            holdView.tv_name = (TextView) view
                    .findViewById(R.id.membername);
            holdView.tv_header = (TextView) view
                    .findViewById(R.id.tv_contact_header);
            view.setTag(holdView);
        } else {
            holdView = (HoldView) view.getTag();
        }
        setTitle(holdView,member,position);
        return view;
    }
    private void setTitle(HoldView holdView,SessionMember member,int position){
        holdView.tv_name.setText(member.getEntity().getName());
        holdView.img_header.loadBuddyAvatarbyurl(member.getEntity().getAvatarUrl(), R.drawable.default_person_icon);
        holdView.tv_header.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(member.getEntity().getId())));
    }

    class HoldView {
        public HeadImageView img_header;
        public TextView tv_name;
        public TextView tv_header;
    }
}
