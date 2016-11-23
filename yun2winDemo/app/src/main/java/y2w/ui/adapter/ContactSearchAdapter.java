package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.util.List;

import y2w.base.Urls;
import y2w.common.HeadImageView;
import y2w.entities.ContactEntity;

/**
 * Created by maa2 on 2016/1/29.
 */
public class ContactSearchAdapter extends BaseAdapter {

    private List<ContactEntity> contactEntities;
    private Context context;
    public ContactSearchAdapter(Context context){
        this.context = context;
    }

    public void updateListView(List<ContactEntity> list) {
        this.contactEntities = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contactEntities == null ? 0 :contactEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return contactEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(position > contactEntities.size() - 1){
            return view;
        }
        ContactEntity model = contactEntities.get(position);
        HoldView holdView;
        if (null == view) {
            holdView = new HoldView();
            view = LayoutInflater.from(context).inflate(R.layout.conversation_list_item, null);
            holdView.iv_header = (HeadImageView) view
                    .findViewById(R.id.iv_header);
            holdView.tv_title = (TextView) view
                    .findViewById(R.id.tv_title);
            holdView.tv_time = (TextView) view
                    .findViewById(R.id.tv_time);
            holdView.tv_content = (TextView) view
                    .findViewById(R.id.tv_content);
            holdView.tv_header= (TextView) view
                    .findViewById(R.id.tv_header);
            view.setTag(holdView);
        } else {
            holdView = (HoldView) view.getTag();
        }
        setTitle(holdView, model);
        return view;
    }
    private void setTitle(HoldView holdView,ContactEntity entity){
        holdView.tv_title.setText(entity.getName());
        holdView.tv_time.setVisibility(View.GONE);
        holdView.tv_content.setText("账号:"+entity.getEmail());

        holdView.iv_header.loadBuddyAvatarbyurl(entity.getAvatarUrl() , R.drawable.default_person_icon);

        holdView.tv_header.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(entity.getId())));
    }

    class HoldView {
        public HeadImageView iv_header;
        public TextView tv_title;
        public TextView tv_time;
        public TextView tv_content;
        public TextView tv_header;
    }

}
