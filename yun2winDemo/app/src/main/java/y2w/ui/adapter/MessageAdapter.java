package y2w.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.model.messages.MessageDisplay;
import y2w.model.messages.MessageType;
import y2w.model.messages.MessageView;
import y2w.model.messages.MViewHolder;
import y2w.common.UserInfo;

/**
 * Created by maa2 on 2016/2/23.
 */
public class MessageAdapter extends BaseAdapter{

    private List<MessageModel> models;
    private Context _context;
    private Session _session;
    private MessageView messageView;
    private MessageDisplay messageDisplay;
    private MViewHolder viewHolder = new MViewHolder();
    public MessageAdapter(Context context,Session session){
        this._context = context;
        this._session = session;
        this.messageView = new MessageView(context);
    }
    public void setSession(Session session){
        this._session = session;
    }
    public void updateListView(List<MessageModel> list) {
        this.models = list;
        this.messageDisplay = new MessageDisplay(_context,models,_session);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return models == null ? 0 : models.size();
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return MessageType.MessageTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        return getMessageViewType(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        int type = getItemViewType(position);
        MessageModel model = this.models.get(position);
        try {
            if (view == null) {
                viewHolder = new MViewHolder();
                switch (type) {
                    /*********** 系统 ***********/
                    case MessageType.TextSystem:
                        view = messageView.systemTextInit(viewHolder);
                        break;
                    /*********** 文本 ***********/
                    case MessageType.TextRight:
                        view = messageView.mySideTextInit(viewHolder);
                        break;
                    case MessageType.TextLeft:
                        view = messageView.otherSideTextInit(viewHolder);
                        break;
                    /*********** 图片 ***********/
                    case MessageType.ImageRight:
                        view = messageView.mySideImageInit(viewHolder);
                        break;
                    case MessageType.ImageLeft:
                        view = messageView.otherSideImageInit(viewHolder);
                        break;
                    default:
                        return new TextView(_context);
                }
                view.setTag(viewHolder);
            }else{
                viewHolder = (MViewHolder) view.getTag();
            }
            messagedisplay(model,viewHolder,position);

        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

   public void messagedisplay(MessageModel model,MViewHolder viewHolder,int position){
       try{
           int type = getItemViewType(position);
           switch (type) {
               /*********** 系统 ***********/
               case MessageType.TextSystem:
                   messageDisplay.systemTextDisplay(model,viewHolder, position);
                   break;
               /*********** 文本 ***********/
               case MessageType.TextRight:
                   messageDisplay.setMySideTextDisplay(model,viewHolder, position);
                   break;
               case MessageType.TextLeft:
                   messageDisplay.setOtherSideTextDisplay(model, viewHolder, position);
                   break;

               /*********** 图片 ***********/
               case MessageType.ImageRight:
                   messageDisplay.setMySideImageDisplay(model,viewHolder, position);
                   break;
               case MessageType.ImageLeft:
                   messageDisplay.setOtherSideImageDisplay(model, viewHolder, position);
                   break;
               default:
                   break;
           }
       }catch (Exception e){
           e.printStackTrace();
       }
   }

    private int getMessageViewType(int position) {

        String type = models.get(position).getEntity().getType();
        if(MessageType.System.equals(type)){
            return MessageType.TextSystem;
        }
        else if (MessageType.Text.equals(type)) {
            return isMySide(position) ? MessageType.TextRight
                    : MessageType.TextLeft;
        }
        else if (MessageType.Image.equals(type)) {
            return isMySide(position) ? MessageType.ImageRight
                    : MessageType.ImageLeft;
        }
        else {
            return -1;
        }

    }

    private boolean isMySide(int position) {
        try {
            if(UserInfo.getUserId().equals(models.get(position).getEntity().getSender())) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

}
