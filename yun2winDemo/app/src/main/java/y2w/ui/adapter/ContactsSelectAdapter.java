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
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;

import java.util.List;

import y2w.model.UserSession;

/**
 * Created by maa2 on 2016/1/22.
 */
public class ContactsSelectAdapter extends BaseAdapter {

    private  List<SortModel> choiceContacts;
    private Context context;
    public ContactsSelectAdapter(Context context){
        this.context = context;
    }

    public void updateListView() {
        notifyDataSetChanged();
    }

    public void setListViewdate(List<SortModel> list){
        this.choiceContacts = list;
    }

    @Override
    public int getCount() {
        return choiceContacts == null ? 0 :choiceContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return choiceContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(position > choiceContacts.size() - 1){
            return view;
        }
        SortModel model = choiceContacts.get(position);
        HoldView holdView;
        if (null == view) {
            holdView = new HoldView();
            view = LayoutInflater.from(context).inflate(R.layout.contact_select_item, null);
            holdView.img_header = (HeadImageView) view
                    .findViewById(R.id.img_contact_header);
            holdView.tv_header = (TextView) view
                    .findViewById(R.id.tv_contact_header);

            view.setTag(holdView);
        } else {
            holdView = (HoldView) view.getTag();
        }
        setTitle(holdView,model,position);
        return view;
    }
    private void setTitle(HoldView holdView,SortModel model,int position){
        holdView.img_header.loadBuddyAvatarbyurl(model.getAvatarUrl(), R.drawable.default_person_icon);
        holdView.tv_header.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(model.getUserId())));
    }

    class HoldView {
        public HeadImageView img_header;
        public TextView tv_header;
    }
}
