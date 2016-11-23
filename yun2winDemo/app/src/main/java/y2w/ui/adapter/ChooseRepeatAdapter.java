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

import y2w.common.HeadImageView;
import y2w.model.Contact;
import y2w.model.UserConversation;

/**
 * Created by maa2 on 2016/1/22.
 */
public class ChooseRepeatAdapter extends BaseAdapter {

    private List<UserConversation> conversations;
    private List<Contact> contacts;
    private Context context;
    private String type;//conversation contact
    public ChooseRepeatAdapter(Context context){
        this.context = context;
    }

    public void updateListView() {
        notifyDataSetChanged();
    }

    public void setListViewdate(List<UserConversation> conversations,List<Contact> contacts,String type){
        if(type!=null&&type.equals("conversation")){
            this.conversations = conversations;
        }else{
            this.contacts = contacts;
        }
      this.type = type;
        updateListView();
    }

    @Override
    public int getCount() {
        if(type!=null&&type.equals("conversation")) {
            return conversations == null ? 0 : conversations.size();
        }else{
            return contacts == null ? 0 : contacts.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if(type!=null&&type.equals("conversation")) {
            return conversations.get(position);
        }else{
            return contacts.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(type!=null&&type.equals("conversation")) {
            if(position > conversations.size() - 1){
                return view;
             }
        }else{
            if(position > contacts.size() - 1){
                return view;
            }
        }

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
        if(type!=null&&type.equals("conversation")) {
            UserConversation model = conversations.get(position);
            setTitle(holdView, model.getEntity().getName(),model.getEntity().getAvatarUrl(),model.getEntity().getId(), position);
        }else{
            Contact model = contacts.get(position);
            setTitle(holdView, model.getEntity().getName(),model.getEntity().getAvatarUrl(),model.getEntity().getId(), position);
        }
        return view;
    }
    private void setTitle(HoldView holdView,String name,String avatarurl,String id,int position){


        holdView.tv_name.setText(name);
        holdView.img_header.loadBuddyAvatarbyurl(avatarurl, R.drawable.default_person_icon);

        holdView.tv_header.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(id)));
        holdView.tv_chart.setVisibility(View.GONE);

    }

    class HoldView {
        public HeadImageView img_header;
        public TextView tv_name;
        public TextView tv_header;
        public TextView tv_chart;
        public View view_line;
    }
}
