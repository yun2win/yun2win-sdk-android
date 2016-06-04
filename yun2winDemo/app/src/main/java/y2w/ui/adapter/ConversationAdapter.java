package y2w.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.imageview.HeadImageView;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.yun2win.demo.R;

import java.util.List;

import y2w.entities.UserConversationEntity;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Session;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.ui.activity.ChatActivity;
import y2w.ui.widget.emoji.Expression;

import com.y2w.uikit.utils.StringUtil;

import org.w3c.dom.Text;

/**
 * Created by maa2 on 2016/1/16.
 */
public class ConversationAdapter extends BaseAdapter {

    private List<UserConversation> conversations;
    private Context context;
    public ConversationAdapter(Context context){
        this.context = context;
    }

    public void updateListView() {
        notifyDataSetChanged();
    }
    public  void setConversations(List<UserConversation> list){
        this.conversations = list;
    }

    @Override
    public int getCount() {
        return conversations == null ? 0 :conversations.size();
    }

    @Override
    public Object getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(position > conversations.size() - 1){
            return view;
        }
       final UserConversation userConversation = conversations.get(position);
        HoldView holdView;
        if (null == view) {
            holdView = new HoldView();
            view = LayoutInflater.from(context).inflate(R.layout.conversation_list_item, null);
            holdView.iv_header = (HeadImageView) view
                    .findViewById(R.id.iv_header);
            holdView.tv_header = (TextView) view
                    .findViewById(R.id.tv_header);
            holdView.tv_title = (TextView) view
                    .findViewById(R.id.tv_title);
            holdView.tv_time = (TextView) view
                    .findViewById(R.id.tv_time);
            holdView.tv_content = (TextView) view
                    .findViewById(R.id.tv_content);
            holdView.tv_count = (TextView) view
                    .findViewById(R.id.tv_count);
            holdView.viewline = view
                    .findViewById(R.id.viewline);
            view.setTag(holdView);
        } else {
            holdView = (HoldView) view.getTag();
        }
        setTitle(holdView,userConversation,position);
        return view;
    }
    private void setTitle(HoldView holdView,UserConversation userConversation,int position){
        holdView.tv_title.setText(userConversation.getEntity().getName());
        Expression.emojiDisplay(context, null, holdView.tv_content, userConversation.getEntity().getLastContext(), Expression.WH_1);
        holdView.tv_time.setText(userConversation.getEntity().getFriendlyTime());
        if(EnumManage.SessionType.p2p.toString().equals(userConversation.getEntity().getType())) {
            holdView.iv_header.loadBuddyAvatarbyurl(userConversation.getEntity().getAvatarUrl(), R.drawable.default_person_icon);
        }else if(EnumManage.SessionType.group.toString().equals(userConversation.getEntity().getType())){
            holdView.iv_header.loadBuddyAvatarbyurl(userConversation.getEntity().getAvatarUrl(), R.drawable.default_group_icon);
        }else{
            holdView.iv_header.loadBuddyAvatarbyurl(userConversation.getEntity().getAvatarUrl(), R.drawable.circle_image_transparent);
        }
        holdView.tv_header.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(userConversation.getEntity().getTargetId())));
        if(userConversation.getEntity().getUnread()==0){
            holdView.tv_count.setVisibility(View.GONE);
        }else {
            holdView.tv_count.setVisibility(View.VISIBLE);
            holdView.tv_count.setText(StringUtil.getMessageNum(userConversation.getEntity().getUnread()));
        }
    }

    class HoldView {
        public HeadImageView iv_header;
        public TextView tv_title;
        public TextView tv_header;
        public TextView tv_time;
        public TextView tv_content;
        public TextView tv_count;
        public View viewline;
    }

}
