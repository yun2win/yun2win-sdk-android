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
import y2w.entities.SessionMemberEntity;
import y2w.entities.searchEntities.SearchMessage;
import y2w.entities.searchEntities.SearchUserConversation;
import y2w.model.Contact;
import y2w.model.NewDataModel;
import y2w.model.messages.MessageCrypto;

/**
 * Created by Administrator on 2016/4/25.
 */
public class ResultListViewAdapter extends BaseAdapter {

    private List<NewDataModel> list;
    private String userInput ="";


    private Context context;
    public ResultListViewAdapter(List<NewDataModel> list,Context context){
        this.list = list;
        this.context = context;
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
        NewDataModel model = list.get(position);
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
            if(model.getType().equals("contactentity")){
                holder.text_class.setText(context.getResources().getString(R.string.contacts));
            }else if(model.getType().equals("userconversationentity")){
                holder.text_class.setText(context.getResources().getString(R.string.groupchat));
            }else if(model.getType().equals("messageentity")){
                holder.text_class.setText(context.getResources().getString(R.string.messagechat));
            }
        }else{
            if(!model.getType().equals(list.get(position-1).getType())){
                holder.text_class.setVisibility(View.VISIBLE);
                if(model.getType().equals("contactentity")){
                    holder.text_class.setText(context.getResources().getString(R.string.contacts));
                }else if(model.getType().equals("userconversationentity")){
                    holder.text_class.setText(context.getResources().getString(R.string.groupchat));
                }else if(model.getType().equals("messageentity")){
                    holder.text_class.setText(context.getResources().getString(R.string.messagechat));
                }
            }else{
                holder.text_class.setVisibility(View.GONE);
            }
        }
        if(list.size()>(position+1)) {
            if (!list.get(position + 1).getType().equals(model.getType())) {
                holder.viewline.setVisibility(View.GONE);
            } else {
                holder.viewline.setVisibility(View.VISIBLE);
            }
        }

        String text_name="";
        if(model.getType().equals("contactentity")){
            Contact contactmodel = model.getSearchContact();
            if(contactmodel!=null){
                holder.list_name.setVisibility(View.VISIBLE);
                holder.linear_text.setVisibility(View.GONE);
                holder.tv_more.setVisibility(View.GONE);
                holder.relativeLayout1.setVisibility(View.VISIBLE);
                text_name = model.getSearchContact().getEntity().getName();
                setKeytext(holder.list_name,text_name);
                holder.head_icon.loadBuddyAvatarbyurl(contactmodel.getEntity().getAvatarUrl(), R.drawable.default_person_icon);
                holder.head_text.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(contactmodel.getEntity().getId())));
            }else{
                holder.tv_more.setVisibility(View.VISIBLE);
                holder.tv_more.setText("更多联系人");
                holder.relativeLayout1.setVisibility(View.GONE);
            }
        }else if(model.getType().equals("userconversationentity")){
            SearchUserConversation searchUserConversationmodel= model.getSearchUserconversation();
            if(searchUserConversationmodel!=null){
                holder.tv_more.setVisibility(View.GONE);
                holder.relativeLayout1.setVisibility(View.VISIBLE);
                if(searchUserConversationmodel.getSessionMembers().size()==0) {
                    holder.list_name.setVisibility(View.VISIBLE);
                    holder.linear_text.setVisibility(View.GONE);
                    text_name = searchUserConversationmodel.getUserConversation().getEntity().getName();
                    setKeytext(holder.list_name, text_name);
                }else{
                    holder.list_name.setVisibility(View.GONE);
                    holder.linear_text.setVisibility(View.VISIBLE);
                    holder.tv_title.setText(searchUserConversationmodel.getUserConversation().getEntity().getName());
                    List<SessionMemberEntity> members =searchUserConversationmodel.getSessionMembers();
                    text_name ="";
                    for(int i =0;i<members.size();i++){
                        if(!StringUtil.isEmpty(text_name)){
                            text_name=text_name+",";
                        }
                        text_name=text_name+ members.get(i).getName();
                    }
                    setKeytext(holder.tv_content, text_name);
                }
                holder.head_icon.loadBuddyAvatarbyurl(searchUserConversationmodel.getUserConversation().getEntity().getAvatarUrl(), R.drawable.default_person_icon);
                holder.head_text.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(searchUserConversationmodel.getUserConversation().getEntity().getId())));
            }else{
                holder.tv_more.setVisibility(View.VISIBLE);
                holder.tv_more.setText("更多群聊");
                holder.relativeLayout1.setVisibility(View.GONE);
            }
        }else if(model.getType().equals("messageentity")){
            SearchMessage searchmessageModel= model.getSearchMessage();
            if(searchmessageModel!=null){
                holder.tv_more.setVisibility(View.GONE);
                holder.relativeLayout1.setVisibility(View.VISIBLE);
                holder.list_name.setVisibility(View.GONE);
                holder.linear_text.setVisibility(View.VISIBLE);
                holder.tv_title.setText(searchmessageModel.getUserConversation().getEntity().getName());
                if(model.getSearchMessage().getMessages().size()>1){
                    holder.tv_content.setText(model.getSearchMessage().getMessages().size()+"条相关的交流记录");
                }else{
                    text_name = model.getSearchMessage().getMessages().get(0).getContent();
                    setKeytext(holder.tv_content, MessageCrypto.getInstance().decryText(text_name));
                }
                holder.head_icon.loadBuddyAvatarbyurl(searchmessageModel.getUserConversation().getEntity().getAvatarUrl(), R.drawable.default_person_icon);

                holder.head_text.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(searchmessageModel.getUserConversation().getEntity().getId())));
            }else{
                holder.tv_more.setVisibility(View.VISIBLE);
                holder.tv_more.setText("更多交流记录");
                holder.relativeLayout1.setVisibility(View.GONE);
            }
        }
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
