package y2w.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.util.List;

import y2w.base.Urls;
import y2w.common.HeadImageView;
import y2w.entities.MessageEntity;
import y2w.entities.SessionMemberEntity;
import y2w.entities.searchEntities.SearchMessage;
import y2w.entities.searchEntities.SearchUserConversation;
import y2w.model.Contact;
import y2w.model.NewDataModel;
import y2w.model.UserConversation;
import y2w.model.messages.MessageCrypto;

/**
 * Created by Administrator on 2016/4/25.
 */
public class MessageSearchAdapter extends BaseAdapter {

    private List<MessageEntity> list;
    private String userInput ="";
    private UserConversation userConversation;

    private Context context;
    public MessageSearchAdapter(List<MessageEntity> list, Context context){
        this.list = list;
        this.context = context;
    }
    public void setUserConversation(UserConversation userConversation){
        this.userConversation =userConversation;
    }
    public void setUserInput(String userInput){
        this.userInput = userInput;
    }
    public String getUserInput(){
        return  userInput;
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
        MessageEntity model = list.get(position);
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.result_listview_item,null);
            holder = new ViewHolder();
            holder.text_class = (TextView)convertView.findViewById(R.id.text_class);
            holder.head_text = (TextView)convertView.findViewById(R.id.tv_text_head);
            holder.list_name = (TextView)convertView.findViewById(R.id.tv_name);
            holder.head_icon = (HeadImageView)convertView.findViewById(R.id.img_icon_head);
            holder.viewline = (View)convertView.findViewById(R.id.viewline);
            holder.linear_text = (LinearLayout) convertView.findViewById(R.id.linear_text);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_more = (TextView) convertView.findViewById(R.id.tv_more);
            holder.relativeLayout1 = (RelativeLayout) convertView.findViewById(R.id.relativeLayout1);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }
        if(position==0){
            holder.text_class.setVisibility(View.VISIBLE);
            holder.text_class.setText("关键字'"+userInput+"'"+"搜索");
        }else{
            holder.text_class.setVisibility(View.GONE);
        }
        holder.tv_more.setVisibility(View.GONE);
        holder.relativeLayout1.setVisibility(View.VISIBLE);
        holder.list_name.setVisibility(View.GONE);
        holder.linear_text.setVisibility(View.VISIBLE);
        holder.tv_title.setText(userConversation.getEntity().getName());
        String text_name = model.getContent();
        setKeytext(holder.tv_content, MessageCrypto.getInstance().decryText(text_name));
        holder.head_icon.loadBuddyAvatarbyurl(userConversation.getEntity().getAvatarUrl(), R.drawable.default_person_icon);
        holder.head_text.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(userConversation.getEntity().getId())));
        return convertView;
    }
    private void setKeytext(TextView textview,String str){
        if (str != null && !StringUtil.isEmpty(userInput) && str.contains(userInput)) {
            int index = str.indexOf(userInput);
            int len = userInput.length();
            Spanned temp = Html.fromHtml(str.substring(0, index)
                    + "<u><font color=#FFC125>"
                    + str.substring(index, index + len) + "</font></u>"
                    + str.substring(index + len, str.length()));
            textview.setText(temp);
        } else {
            textview.setText(str);
        }
    }
    class ViewHolder{
        TextView text_class;
        TextView head_text;
        HeadImageView head_icon;
        TextView list_name;
        LinearLayout linear_text;
        TextView tv_title;
        TextView tv_content;
        TextView tv_more;
        RelativeLayout relativeLayout1;
        View viewline;
    }
}
