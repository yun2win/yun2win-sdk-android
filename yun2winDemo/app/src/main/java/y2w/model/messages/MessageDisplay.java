package y2w.model.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.y2w.uikit.customcontrols.imageview.RoundAngleImageView;
import com.y2w.uikit.customcontrols.movie.ScalableType;
import com.y2w.uikit.customcontrols.movie.ScalableVideoView;
import com.y2w.uikit.customcontrols.record.RecordUtil;
import com.y2w.uikit.customcontrols.view.RoundProgressBar;
import com.y2w.uikit.utils.FileUtil;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.ImageUtil;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;
import com.yun2win.utils.Json;
import com.yun2win.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.base.Urls;
import y2w.common.AsyncMultiPartGet;
import y2w.common.AsyncMultiPartPost;
import y2w.common.CallBackUpdate;
import y2w.common.Config;
import y2w.common.ImagePool;
import y2w.common.SendUtil;
import y2w.entities.MessageEntity;
import y2w.entities.UserEntity;
import y2w.httpApi.messages.AvSystemMsg;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.User;
import y2w.service.Back;

import y2w.ui.activity.ChatActivity;
import y2w.ui.activity.ChooseSessionActivity;
import y2w.ui.activity.ContactInfoActivity;
import y2w.ui.activity.ImageBrowseActivity;
import y2w.ui.activity.LocationActivity;
import y2w.ui.activity.ReadMessageActivity;
import y2w.ui.activity.StrongWebViewActivity;
import y2w.ui.activity.WebViewActivity;
import y2w.ui.dialog.Y2wDialog;
import y2w.ui.widget.emoji.Expression;

/**
 * 交流界面，消息展示
 * Created by yangrongfang on 2016/2/23.
 */
public class MessageDisplay {

    private String TAG = MessageDisplay.class.getSimpleName();
    private Activity _activity;
    private Context _context;
    private Session _session;
    private List<MessageModel> _models;
    private List<String> moviePlayPathList = new ArrayList<String>();
    private String currentUserId = Users.getInstance().getCurrentUser().getEntity().getId();
    private boolean urlbool,midbool;
    private String first,middle,third;
    private String fileToken;
    private int fileMaxWidth =0,fileMaxHeight =0;
    public MessageDisplay(Activity activity,Context context,List<MessageModel> models,Session session){
        this._activity = activity;
        this._context = context;
        this._models = models;
        this._session = session;
        if(_session!=null) {
            this.fileToken = "?access_token=" + _session.getSessions().getUser().getToken();
        }
        WindowManager wm = activity.getWindowManager();
        fileMaxHeight =wm.getDefaultDisplay().getWidth();
        fileMaxWidth =fileMaxHeight *2/3;
    }
    public void setMessageModels(List<MessageModel> _models){
        this._models = _models;
    }
    public void setSession(Session session){
        this._session = session;
        if(_session!=null) {
            this.fileToken = "?access_token=" + _session.getSessions().getUser().getToken();
        }
    }

    /********************* 我方，对方日期，头像，消息时间等 基本信息填充 **********************/
    public void setMySideCommonInfo(MessageModel model,
                                    MViewHolder viewHolder, int position) {
        setMessageTime(model, viewHolder, position);
        setMySideHeader(model, viewHolder, position);
       /*
        mySideSendStateDisplay(position, mm, viewHolder, view);*/
    }

    public void setOtherSideCommonInfo(MessageModel model,
                                       MViewHolder viewHolder,int position) {
        setMessageTime(model, viewHolder, position);
        setOtherSideHeaderandName(model, viewHolder, position);

       /* setOtherSideMessageTimeInfo(mm, viewHolder, view);*/
    }
    /********************* 消息已读未读**********************/
    public void setreadStatues(final MessageModel model,
                               final TextView readtextView, int position){
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            readtextView.setVisibility(View.GONE);
        }else{

            if(EnumManage.SessionType.group.toString().equals(_session.getEntity().getType())) {
                readtextView.setEnabled(true);
                readtextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(_context, ReadMessageActivity.class);
                        intent.putExtra("sessionId",_session.getEntity().getId());
                        intent.putExtra("updateAt",model.getEntity().getCreatedAt());
                        _context.startActivity(intent);
                    }
                });
            }else{
                readtextView.setEnabled(false);
            }
            final Handler readhandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        List<SessionMember> sessionMembers = (List<SessionMember>) msg.obj;
                        if (sessionMembers != null && sessionMembers.size() > 0) {
                            int unread = 0;
                            for (int i = 0; i < sessionMembers.size(); i++) {
                                if (!currentUserId.equals(sessionMembers.get(i).getEntity().getUserId())) {
                                    if (StringUtil.timeCompare(sessionMembers.get(i).getEntity().getUpdatedAt(), model.getEntity().getCreatedAt()) > 0) {//未读
                                        unread++;
                                    }
                                }
                            }
                            readtextView.setVisibility(View.VISIBLE);
                            if (EnumManage.SessionType.p2p.toString().equals(_session.getEntity().getType())) {
                                if (unread == 0) {
                                    readtextView.setText("已读");
                                } else {
                                    readtextView.setText("未读");
                                }
                            } else {
                                if (unread == 0) {
                                    readtextView.setText("全部已读");
                                } else {
                                    readtextView.setText(unread + "人未读");
                                }
                            }
                        }else{
                            readtextView.setVisibility(View.GONE);
                        }
                    }else{
                        readtextView.setVisibility(View.GONE);
                    }
                }
            };
            _session.getMembers().localAllMembers(_session.getEntity().getId(),new Back.Result<List<SessionMember>>() {
                @Override
                public void onSuccess(List<SessionMember> sessionMembers){

                    Message msg = new Message();
                    msg.what=1;
                    msg.obj = sessionMembers;
                    readhandler.sendMessage(msg);
                }

                @Override
                public void onError(int code, String error) {
                    Message msg = new Message();
                    msg.what=-1;
                    readhandler.sendMessage(msg);
                }
            });
        }
    }
    /********************* 设置时间**********************/
    public String getDisplayTime(String sDate){
        if(!StringUtil.isEmpty(sDate) && sDate.length() >= 16){
            sDate = sDate.substring(0,16);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String date = df.format(new Date());
            if(sDate.indexOf("date")!=-1){
                sDate = sDate.substring(10,16);
            }
        }
        return sDate;
    }
    public void setMessageTime(MessageModel model,
                               MViewHolder viewHolder,int position) {
        if (position == 0) {
            viewHolder.tvCreateDate.setText(getDisplayTime(model.getEntity().getCreatedAt()));
            viewHolder.tvCreateDate.setVisibility(View.VISIBLE);
        } else if(position > 0){
            MessageModel lastModel = _models.get(position-1);
            if (StringUtil.isTimeDisplay(lastModel.getEntity().getCreatedAt(), model.getEntity().getCreatedAt())) {
                viewHolder.tvCreateDate.setVisibility(View.VISIBLE);
                viewHolder.tvCreateDate.setText(getDisplayTime(model.getEntity().getCreatedAt()));
            } else {
                viewHolder.tvCreateDate.setVisibility(View.GONE);
            }
        }
    }

    /********************* 头像**********************/
    /**
     * 设置我方头像
     * @param model
     * @param viewHolder
     * @param position
     */
    private void setMySideHeader(MessageModel model,
                                 MViewHolder viewHolder,int position) {
        viewHolder.iv_myside_icon.loadBuddyAvatarbyurl(Users.getInstance().getCurrentUser().getEntity().getAvatarUrl(), R.drawable.chat_default_icon);
        viewHolder.tvMySideCircleName.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(model.getEntity().getSender())));
        viewHolder.iv_myside_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(_context, ContactInfoActivity.class);
                Bundle bundle = new Bundle();
                UserEntity userEntity = Users.getInstance().getCurrentUser().getEntity();
                bundle.putString("otheruserid", userEntity.getId());
                bundle.putString("avatarUrl", userEntity.getAvatarUrl());
                bundle.putString("username", userEntity.getName());
                bundle.putString("account", userEntity.getAccount());
                intent.putExtras(bundle);
                _context.startActivity(intent);
            }
        });
    }

    /**
     * 设置对方用户头像
     * @param model
     * @param viewHolder
     * @param position
     */

    private void setOtherSideHeaderandName(final MessageModel model,
                                           final MViewHolder viewHolder, int position) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    User user = (User) msg.obj;
                    if(user.getEntity() == null || viewHolder.ivOtherSideIcon == null || viewHolder.tvOtherSideName == null || viewHolder.tvOtherSideCircleName == null)
                        return;
                    viewHolder.ivOtherSideIcon.loadBuddyAvatarbyurl(user.getEntity().getAvatarUrl(), R.drawable.chat_default_icon);
                    viewHolder.tvOtherSideCircleName.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(user.getEntity().getId())));
                    if (EnumManage.SessionType.group.toString().equals(_session.getEntity().getType())) {
                        viewHolder.tvOtherSideName.setText(user.getEntity().getName());
                        viewHolder.tvOtherSideName.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvOtherSideName.setText("");
                        viewHolder.tvOtherSideName.setVisibility(View.GONE);
                    }
                }else{
                    if(viewHolder.ivOtherSideIcon == null || viewHolder.tvOtherSideName == null || viewHolder.tvOtherSideCircleName == null)
                        return;
                    viewHolder.ivOtherSideIcon.setImageResource(R.drawable.chat_default_icon);
                    viewHolder.tvOtherSideCircleName.setBackgroundResource(HeadTextBgProvider.getTextBg(0));
                    viewHolder.tvOtherSideName.setText("");
                    viewHolder.tvOtherSideName.setVisibility(View.GONE);
                }
            }
        };
        Users.getInstance().getUser(model.getEntity().getSender(), new Back.Result<User>() {
            @Override
            public void onSuccess(User user) {
                Message message = new Message();
                message.what = 1;
                message.obj = user;
                handler.sendMessage(message);

            }

            @Override
            public void onError(int code, String error) {
                Message message = new Message();
                message.what = -1;
                handler.sendMessage(message);
            }
        });
        // 绑定事件
        viewHolder.ivOtherSideIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                _session.getMembers().getMember(model.getEntity().getSender(), new Back.Result<SessionMember>() {
                    @Override
                    public void onSuccess(SessionMember sessionMember) {
                        Intent intent = new Intent(_context, ContactInfoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("otheruserid", sessionMember.getEntity().getUserId());
                        bundle.putString("avatarUrl", sessionMember.getEntity().getAvatarUrl());
                        bundle.putString("username", sessionMember.getEntity().getName());
                        bundle.putInt("flag", ContactInfoActivity.chat);
                        intent.putExtras(bundle);
                        _context.startActivity(intent);
                    }

                    @Override
                    public void onError(int errorCode, String error) {
                        ToastUtil.ToastMessage(_context, "查看资料失败");
                    }
                });
            }
        });
        bindOtherHeaderOnLongClickEvent(model, viewHolder, position);
    }

    private void bindOtherHeaderOnLongClickEvent(final MessageModel model,final MViewHolder viewHolder, int position){
        if(EnumManage.SessionType.group.toString().equals(_session.getEntity().getType()) && viewHolder.ivOtherSideIcon != null){
            viewHolder.ivOtherSideIcon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (ChatActivity.chatHandler != null) {
                        Message msg = new Message();
                        msg.what = ChatActivity.RefreshCode.CODE_OTHER_AT;
                        msg.obj = model.getEntity().getSender();
                        ChatActivity.chatHandler
                                .sendMessage(msg);
                    }
                    return true;
                }
            });
        }
    }
    /********************* 公共**********************/

    private void sendSuccessRefresh(){
        CallBackUpdate update = AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.chatting.toString());
        if(update != null){
            update.addDateUI(_session.getEntity().getId());
        }
    }

    /********************* 系统 **********************/

    public void systemTextDisplay(MessageModel model,
                                  MViewHolder viewHolder,int position) {
        setMessageTime(model, viewHolder, position);
        viewHolder.tvSystemText.setVisibility(View.VISIBLE);
        viewHolder.tvSystemText.setText(MessageCrypto.getInstance().decryText(model.getEntity().getContent()));
    }
    /********************* 文本 **********************/

    public void setMySideTextDisplay(MessageModel model,
                                     MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideTextContent(model, viewHolder, position);
        setreadStatues(model, viewHolder.tvMySideMessageRead, position);
    }

    public void setOtherSideTextDisplay(MessageModel model,
                                        MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideTextContent(model, viewHolder, position);
    }

    private void setMySideTextSendAnimation(MessageModel model,MViewHolder viewHolder,int position) {
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            viewHolder.ivMySideMessageLoading.setVisibility(View.VISIBLE);
        }else{
            viewHolder.ivMySideMessageLoading.setVisibility(View.GONE);
        }
    }


    private void setMySideTextContent(MessageModel model,MViewHolder viewHolder,int position
    ) {
        setMySideTextSendAnimation(model, viewHolder, position);
        viewHolder.tvMySideText
                .setBackgroundResource(R.drawable.message_text_myside_style);
        Expression.emojiDisplay(_context, null, viewHolder.tvMySideText, MessageCrypto.getInstance().decryText(model.getEntity().getContent()), Expression.getEmojiScale(_activity,2));
        bindTextOnLongClickEvent(model, viewHolder, position);
        bindOpenURLEvent(model,viewHolder);
        setMessageURL(viewHolder.tvMySideText, MessageCrypto.getInstance().decryText(model.getEntity().getContent()), "normal");
        //viewHolder.tvMySideText.setText(MessageCrypto.getInstance().decryText(model.getEntity().getContent()));
    }

    private void setOtherSideTextContent(MessageModel model, MViewHolder viewHolder,int position) {
        viewHolder.tvOtherSideText
                .setBackgroundResource(R.drawable.message_text_otherside_style);
        Expression.emojiDisplay(_context, null, viewHolder.tvOtherSideText, MessageCrypto.getInstance().decryText(model.getEntity().getContent()), Expression.getEmojiScale(_activity,2));
        bindTextOnLongClickEvent(model, viewHolder, position);
        bindOpenURLEvent(model,viewHolder);
        setMessageURL(viewHolder.tvOtherSideText, MessageCrypto.getInstance().decryText(model.getEntity().getContent()), "normal");
        //viewHolder.tvOtherSideText.setText(MessageCrypto.getInstance().decryText(model.getEntity().getContent()));
        if(MessageCrypto.getInstance().decryMyuserId(model.getEntity().getContent(),currentUserId)){
            viewHolder.tvOtherSideText.setTextColor(Color.parseColor("#ffc261"));
        }else{
            viewHolder.tvOtherSideText.setTextColor(Color.parseColor("#353535"));
        }
    }

    private void bindTextOnLongClickEvent(final MessageModel model, MViewHolder viewHolder,int position){
        final TextView tv_text = currentUserId.equals(model.getEntity().getSender()) ? viewHolder.tvMySideText : viewHolder.tvOtherSideText;
        tv_text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Y2wDialog dialog = new Y2wDialog(_context);
                dialog.addOption("复制");
                dialog.addOption("转发");
                if(currentUserId.equals(model.getEntity().getSender())){
                    dialog.addOption("回撤");
                }
                dialog.show();
                dialog.setOnOptionClickListener(new Y2wDialog.onOptionClickListener() {
                    @Override
                    public void onOptionClick(String option, int position) {
                        if (position == 0) {//复制
                            AppData.getInstance().getClipboardManager(_context)
                                    .setText(MessageCrypto.getInstance().decryText(model.getEntity().getContent()));
                        }else if(position==1){//转发
                            Intent intent = new Intent(_context, ChooseSessionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("repeatMessage",model.getEntity());
                            bundle.putString("type","conversation");
                            intent.putExtras(bundle);
                            _context.startActivity(intent);
                        }else if (position == 2) {//回撤
                            String name = Users.getInstance().getCurrentUser().getEntity().getName();
                            model.getEntity().setContent(name + "回撤了一条消息");
                            model.getEntity().setType(MessageType.System);
                            _session.getMessages().getRemote().updateMessage(model, new Back.Result<MessageModel>() {
                                @Override
                                public void onSuccess(MessageModel model) {
                                    sendSuccessRefresh();
                                    _session.getMessages().getRemote().sendMessage("",false, new IMClient.SendCallback() {
                                        @Override
                                        public void onReturnCode(int i, IMSession imSession, String s) {

                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, String error) {
                                    ToastUtil.ToastMessage(_context, error);
                                }
                            });
                        }
                    }
                });
                return true;
            }
        });
    }

    /**
     * 判断是否以链接开头
     *
     * @param text
     * @return
     */
    public static boolean isURL(String text) {
        Pattern p = Pattern
                .compile("((http://)|(https://)|(www.)){1}[\\w\\.\\-/:]+");
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 设置消息链接
     *
     * @param textView
     * @param text
     * @param type
     */
    private void setMessageURL(TextView textView, String text, String type) {

        boolean value = isURL(text);
        if (value == true) {
            urlbool = true;
            midbool = true;
            String context = null;
            //text第一个不是连接的字符位置
            int lastIndex = 0;
            int leng = text.length();
            while(lastIndex < leng){
                char cha = text.charAt(lastIndex);
                if(cha >= '#' && cha <='z'){
                    lastIndex = lastIndex +1;
                }else{
                    break;
                }
            }
            if ("normal".equals(type)) {
                if (textView.getId() == R.id.tv_otherside_message_text) {
                    context = "<u><font color=#1cc09f>" + text.substring(0, lastIndex) + "</font></u>" + text.substring(lastIndex);
                } else {
                    //context = "<u><font color=#ffef9f>" + text.substring(0, lastIndex) + "</font></u>"+ text.substring(lastIndex);
                    context = "<u><font color=#20124d>" + text.substring(0, lastIndex) + "</font></u>"+ text.substring(lastIndex);
                }
            } else {
                if (textView.getId() == R.id.tv_otherside_message_text) {
                    context = "<u><font color=#189b7b>" + text.substring(0, lastIndex) + "</font></u>"+ text.substring(lastIndex);
                }else{
                    context = "<u><font color=#a61c00>" + text.substring(0, lastIndex) + "</font></u>"+ text.substring(lastIndex);
                }
            }
            textView.setText(Html.fromHtml(context));
            middle = text;
        } else {
            int start = text.indexOf("http://");
            if(start <0 ){
                start = text.indexOf("https://");
            }
            if(start <0 ){
                start = text.indexOf("www.");
            }
            if (start >= 0) {

                int lastIndex = start;
                int leng = text.length();
                while(lastIndex < leng){
                    char cha = text.charAt(lastIndex);
                    if(cha >= '#' && cha <='z'){
                        lastIndex = lastIndex +1;
                    }else{
                        break;
                    }
                }
                urlbool = true;
                int blanket = lastIndex;
                if (blanket > start) {
                    midbool = true;
                    first = text.substring(0, start);
                    middle = text.substring(start, blanket);
                    third = text.substring(blanket);
                } else {
                    midbool = false;
                    first = text.substring(0, start);
                    third = text.substring(start);
                }
                String context = null;
                if (midbool == true) {
                    if ("normal".equals(type)) {

                        if (textView.getId() == R.id.tv_otherside_message_text) {
                            context = "<font color=#101010>" + first
                                    + "</font> " + "<u><font color=#1cc09f>"
                                    + middle + "</font></u>"
                                    + "<font color=#101010>" + third
                                    + "</font> ";
                        } else {
                            context = "<font color=#101010>" + first
                                    + "</font> " + "<u><font color=#20124d>"
                                    + middle + "</font></u>"
                                    + "<font color=#101010>" + third
                                    + "</font> ";
                        }

                    } else {
                        if (textView.getId() == R.id.tv_otherside_message_text) {
                            context = "<font color=#101010>" + first
                                    + "</font> " + "<u><font color=#189b7b>"
                                    + middle + "</font></u>"
                                    + "<font color=#101010>" + third
                                    + "</font> ";
                        } else {
                            context = "<font color=#101010>" + first
                                    + "</font> " + "<u><font color=#a61c00>"
                                    + middle + "</font></u>"
                                    + "<font color=#101010>" + third
                                    + "</font> ";
                        }
                    }
                } else {
                    if ("normal".equals(type)) {
                        if (textView.getId() == R.id.tv_otherside_message_text) {
                            context = "<font color=#101010>" + first
                                    + "</font> " + "<u><font color=#1cc09f>"
                                    + third + "</font></u>";
                        } else {
                            context = "<font color=#101010>" + first
                                    + "</font> " + "<u><font color=#20124d>"
                                    + third + "</font></u>";
                        }

                    } else {
                        if (textView.getId() == R.id.tv_otherside_message_text) {
                            context = "<font color=#101010>" + first
                                    + "</font> " + "<u><font color=#189b7b>"
                                    + third + "</font></u>";
                        } else {
                            context = "<font color=#101010>" + first
                                    + "</font> " + "<u><font color=#a61c00>"
                                    + third + "</font></u>";
                        }
                    }
                }
                textView.setText(Html.fromHtml(context));
            } else {
                urlbool = false;
                textView.setTextColor(Color.parseColor("#101010"));
            }
        }
    }

    private void bindOpenURLEvent(final MessageModel mm,
                                  final MViewHolder viewHolder) {
        final Boolean isFromOtherSide = currentUserId.equals(mm.getEntity().getSender()) ? false
                : true;
        final TextView textView = isFromOtherSide == true ? viewHolder.tvOtherSideText
                : viewHolder.tvMySideText;

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MessageType.Task.equals(mm.getEntity().getType())){
                    String weburl =MessageCrypto.getInstance().decryWebUrl(mm.getEntity().getContent());
                    if(!StringUtil.isEmpty(weburl)&&weburl.startsWith("http")){
                        Intent intent = new Intent(_context, StrongWebViewActivity.class);
                        intent.putExtra("webUrl", weburl);
                        _context.startActivity(intent);
                    }
                }else {

                    setMessageURL(textView, MessageCrypto.getInstance().decryText(mm.getEntity().getContent()), "pressed");
                    if (urlbool == true) {
                        String url = "";
                        if (midbool == true) {
                            url = middle;
                        } else {
                            url = third;
                        }
                        if (url != null) {
                            if(url.contains("192.168.0")){
                                Intent intent = new Intent(_context, StrongWebViewActivity.class);
                                intent.putExtra("webUrl", url);
                                _context.startActivity(intent);
                            }else {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("url", url);
                                intent.putExtras(bundle);
                                intent.setClass(_context, WebViewActivity.class);
                                _context.startActivity(intent);
                            }
                        }
                    } else {
                        try {
                            if (MessageCrypto.getInstance().decryMyuserId(mm.getEntity().getContent(), currentUserId)) {
                                textView.setTextColor(Color.parseColor("#ffc261"));
                            } else {
                                textView.setTextColor(Color.parseColor("#353535"));
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
    }

    /********************* 图片 *************************/

    public void setMySideImageDisplay(MessageModel model, MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideImageContent(model, viewHolder, position);
        bindRetreatOnLongClickEvent(model, viewHolder, position);
        setreadStatues(model, viewHolder.tvMySideMessageRead, position);
    }

    public void setOtherSideImageDisplay(MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideImageContent(model, viewHolder, position);
        bindotherRetreatOnLongClickEvent(model, viewHolder, position);
    }

    private void setMySideImageContent(final MessageModel model, MViewHolder viewHolder,int position) {

        viewHolder.llMySideImageItem.setVisibility(View.VISIBLE);
        viewHolder.ivMySideImage.setVisibility(View.VISIBLE);
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            Json jsonContent = new Json(model.getEntity().getContent());
            String imgurl = jsonContent.getStr("src");
            final String thumbnail = jsonContent.getStr("thumbnail");
            int imgWidth =jsonContent.getInt("width");
            int imgHeight =jsonContent.getInt("height");
            final String timestamp = jsonContent.getStr("timestamp");
            setViewSize(viewHolder.ivMySideImage,imgWidth,imgHeight);
            ImagePool.getInstance(AppContext.getAppContext()).load("file://" + thumbnail, null,viewHolder.ivMySideImage, R.drawable.file_loading);
            final RoundProgressBar pb = viewHolder.pbMySideImageTransfer;
            AsyncMultiPartPost post = AppData.getInstance().getPost(
                    imgurl);
            post.execute();
            post.setCallBack(new AsyncMultiPartPost.CallBack() {

                @Override
                public void update(Integer i) {
                    if (pb != null) {
                        /*if(i > 80)
                            i = 80;*/
                        pb.setProgress(i);
                        LogUtil.getInstance().log(TAG, "i =" + i, null);
                        if (i == 100) {
                            pb.setVisibility(View.GONE);
                        } else {
                            pb.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            post.setCallBackMsg(new AsyncMultiPartPost.CallBackMsg() {
                @Override
                public void msg(String param) {
                    LogUtil.getInstance().log(TAG, "param =" + param, null);
                    final MessageFileReturn imageOrigin = MessageFileReturn.parse(new Json(param));
                    AsyncMultiPartPost thumbPost = new AsyncMultiPartPost(_context,_session.getSessions().getUser().getToken(), Urls.User_Messages_File_UpLoad, thumbnail);
                    thumbPost.execute();
                    thumbPost.setCallBack(new AsyncMultiPartPost.CallBack() {
                        @Override
                        public void update(Integer i) {
                            if (pb != null) {
                              /*  if(i > 80)*/
                                pb.setProgress(i);
                                if (i == 100) {
                                    pb.setVisibility(View.GONE);
                                } else {
                                    pb.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });

                    thumbPost.setCallBackMsg(new AsyncMultiPartPost.CallBackMsg() {
                        @Override
                        public void msg(String param) {
                            if (pb != null) {
                                pb.setVisibility(View.GONE);
                            }
                            MessageFileReturn image = MessageFileReturn.parse(new Json(param));
                            int[] wh = ImageUtil.getImageWidthHeight(thumbnail);
                            if(wh.length > 1) {
                                String content = MessageCrypto.getInstance().encryImage(MessageFileReturn.getFileUrl(imageOrigin.getId()), thumbnail, MessageFileReturn.getFileUrl(image.getId()), wh[0], wh[1],timestamp);
                                final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Image);
                                _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                                    @Override
                                    public void onSuccess(MessageModel netmodel) {
                                        sendSuccessRefresh();
                                    }

                                    @Override
                                    public void onError(int errorCode, String error) {
                                        ToastUtil.ToastMessage(_context, "发送失败");
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else{
            Json jsonContent = new Json(model.getEntity().getContent());
            String localsrc = jsonContent.getStr("localsrc");
            int imgWidth =jsonContent.getInt("width");
            int imgHeight =jsonContent.getInt("height");
            setViewSize(viewHolder.ivMySideImage,imgWidth,imgHeight);
            if(FileUtil.checkFilePathExists(localsrc)){
                ImagePool.getInstance(AppContext.getAppContext()).load("file://"+localsrc, null, viewHolder.ivMySideImage, R.drawable.file_loading);
            }else{
                String thumbnail = jsonContent.getStr("thumbnail");
                ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + thumbnail, fileToken, viewHolder.ivMySideImage, R.drawable.file_loading);
            }
        }
        bindOpenImageEvent(model, viewHolder, position);

    }

    private void bindOpenImageEvent(final MessageModel model, MViewHolder viewHolder,int position) {
        final Boolean isFromOtherSide = currentUserId.equals(model.getEntity().getSender()) ? false
                : true;
        final ImageView imageView = isFromOtherSide == true ? viewHolder.ivOtherSideImage
                : viewHolder.ivMySideImage;
        // 如果是对方发来的消息，处理阅后即焚
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, ImageBrowseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sessionId", model.getEntity().getSessionId());
                bundle.putString("messageId", model.getEntity().getId());
                intent.putExtras(bundle);
                _context.startActivity(intent);
            }
        });

    }

    /**
     * 设置对方纯图片显示
     * @param model
     * @param viewHolder
     * @param position
     */
    private void setOtherSideImageContent(final MessageModel model, MViewHolder viewHolder,int position) {
        // 设置控件显示
        viewHolder.ivOtherSideImage.setVisibility(View.VISIBLE);
        Json jsonContent = new Json(model.getEntity().getContent());
        String thumbnail = jsonContent.getStr("thumbnail");

        int imgWidth =jsonContent.getInt("width");
        int imgHeight =jsonContent.getInt("height");
        setViewSize(viewHolder.ivOtherSideImage,imgWidth,imgHeight);
        ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + thumbnail, fileToken, viewHolder.ivOtherSideImage, R.drawable.file_loading);
        viewHolder.llOtherSideImageItem.setVisibility(View.VISIBLE);
        bindOpenImageEvent(model, viewHolder, position);
    }

    private void setViewSize(View view,int width,int height){
        if(width>0&&height>0) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if(fileMaxWidth<width&&fileMaxHeight>=height){
                height = height*fileMaxWidth/width;
                width =fileMaxWidth;

            }else if(fileMaxWidth>=width&&fileMaxHeight<height){
                width = width*fileMaxHeight/height;
                height =fileMaxHeight;
            }else if(fileMaxWidth<width&&fileMaxHeight<height){
                if(((width*1.000)/height)>((fileMaxWidth*1.000)/fileMaxHeight)){
                    height = height*fileMaxWidth/width;
                    width =fileMaxWidth;
                }else{
                    width = width*fileMaxHeight/height;
                    height =fileMaxHeight;
                }
            }
            lp.width = width;
            lp.height =height;
            view.setLayoutParams(lp);
        }
    }

    public Context get_context() {
        return _context;
    }

    /********************* 语音 ************************/

    public void setMySideVoiceDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideVoiceContent(model, viewHolder, position);
        bindRetreatOnLongClickEvent(model, viewHolder, position);
        setreadStatues(model, viewHolder.tvMySideMessageRead, position);
    }

    public void setOtherSideVoiceDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideVoiceContent(model, viewHolder, position);
        bindotherRetreatOnLongClickEvent(model, viewHolder, position);
    }

    /**
     * 设置我方语音交流显示
     * @param model
     * @param viewHolder
     * @param position
     */
    private void setMySideVoiceContent(final MessageModel model, final MViewHolder viewHolder,int position) {
        viewHolder.ivMySideVoiceIcon.setTag(model);
        // 设置控件显示
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())) {
            Json json = new Json(model.getEntity().getContent());
            final String filePath = json.getStr("localsrc");
            final String timestamp = json.getStr("timestamp");
            final String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
            viewHolder.ivMySideMessageLoading.setVisibility(View.VISIBLE);
            final AsyncMultiPartPost post = AppData.getInstance().getPost(
                    filePath);
            post.execute();
            post.setCallBack(new AsyncMultiPartPost.CallBack() {
                @Override
                public void update(Integer i) {
                    LogUtil.getInstance().log(TAG, "i =" + i, null);
                }
            });
            post.setCallBackMsg(new AsyncMultiPartPost.CallBackMsg() {
                @Override
                public void msg(String param) {
                    LogUtil.getInstance().log(TAG, "param =" + param, null);
                    MessageFileReturn audio = MessageFileReturn.parse(new Json(param));
                    int second = 10;
                    try{
                        second = Integer.parseInt(post.getRemark());
                    }catch (Exception e){
                    }
                    String content = MessageCrypto.getInstance().encryAudio(MessageFileReturn.getFileUrl(audio.getId()),filePath,second,filename,timestamp);
                    final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Audio);
                    _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                        @Override
                        public void onSuccess(MessageModel netmodel) {
                            sendSuccessRefresh();
                        }

                        @Override
                        public void onError(int errorCode, String error) {
                            ToastUtil.ToastMessage(_context, "发送失败");
                        }
                    });
                }
            });
            if(FileUtil.checkFilePathExists(filePath)){
                viewHolder.ivMySideVoiceIcon
                        .setOnClickListener(new PlayVoiceOnClick());
            }
        }else{
            viewHolder.ivMySideMessageLoading.setVisibility(View.GONE);
            Json json = new Json(model.getEntity().getContent());
            String url = json.getStr("src");
            String localsrc = json.getStr("localsrc");
            if(FileUtil.checkFilePathExists(localsrc)){
                viewHolder.ivMySideVoiceIcon.setOnClickListener(new PlayVoiceOnClick());
            }else {
                String audioName = model.getEntity().getId()+json.getStr("name");
                String path = RecordUtil.AUDOI_DIR + audioName;
                if (!StringUtil.isEmpty(audioName) && new File(RecordUtil.AUDOI_DIR + audioName).exists()) {
                    LogUtil.getInstance().log(TAG, "ok", null);
                    viewHolder.ivMySideVoiceIcon.setOnClickListener(new PlayVoiceOnClick());
                } else {
                    audioDownLoad(url, audioName);
                }
            }
        }
    }

    /**
     * 设置对方语音交流显示
     * @param model
     * @param viewHolder
     * @param position
     */
    private void setOtherSideVoiceContent(final MessageModel model, MViewHolder viewHolder,int position) {
        viewHolder.ivOtherSideVoiceIcon.setTag(model);
        Json json = new Json(model.getEntity().getContent());
        String url = json.getStr("src");
        String audioName = model.getEntity().getId()+json.getStr("name");
        File file = new File(RecordUtil.AUDOI_DIR + audioName);
        if (!StringUtil.isEmpty(audioName) && file.exists()) {
            viewHolder.ivOtherSideVoiceIcon
                    .setOnClickListener(new PlayVoiceOnClick());
        } else {
            audioDownLoad(url,audioName);
        }
    }

    private void audioDownLoad(String url,String audioName){
        if(!StringUtil.isEmpty(audioName)) {
            AsyncMultiPartGet get = new AsyncMultiPartGet(_session.getSessions().getUser().getToken(),Urls.User_Messages_File_DownLoad+url, RecordUtil.AUDOI_DIR, audioName);
            get.execute();
            get.setCallBack(new AsyncMultiPartGet.CallBack() {
                @Override
                public void update(Integer i) {
                    LogUtil.getInstance().log(TAG, "down load i =" + i, null);
                }
            });
            get.setCallBackMsg(new AsyncMultiPartGet.CallBackMsg() {
                @Override
                public void msg(String result) {
                    LogUtil.getInstance().log(TAG, "result =" + result, null);
                }
            });
        }
    }

    private View ivPlaying = null;// 当前播放
    private RecordUtil util = RecordUtil.getSigleton();
    /**
     * 播放语音交流
     */
    private final class PlayVoiceOnClick implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (ivPlaying != null) {
                setStopPlayRecord(ivPlaying);
                if (ivPlaying == v) {
                    ivPlaying = null;
                    return;
                }
            }
            ivPlaying = v;
            final AnimationDrawable anima = (AnimationDrawable) ((LinearLayout) v
                    .getParent()).getChildAt(1).getBackground();
            final MessageModel mm = (MessageModel) v.getTag();
            File file;
            Json content = new Json(mm.getEntity().getContent());
            String localsrc = content.getStr("localsrc");
            if (FileUtil.checkFilePathExists(localsrc)) {
                file = new File(localsrc);
            } else {
                String audioName = content.getStr("name");
                file = new File(RecordUtil.AUDOI_DIR + audioName);
            }

            final RecordUtil.OnPlayListener listener = new RecordUtil.OnPlayListener() {
                @Override
                public void stopPlay() {
                    if (currentUserId
                            .equals(mm.getEntity().getSender())) {
                        ((ImageView) v)
                                .setImageResource(R.drawable.voice_icon_myside_play);
                    } else {
                        ((ImageView) v)
                                .setImageResource(R.drawable.voice_icon_otherside_play);
                    }
                    anima.selectDrawable(0);
                    anima.stop();
                    ivPlaying = null;
                }

                @Override
                public void starPlay() {
                    if (currentUserId.equals(mm.getEntity().getSender())) {
                        ((ImageView) v)
                                .setImageResource(R.drawable.voice_icon_myside_stop);
                    } else {
                        ((ImageView) v)
                                .setImageResource(R.drawable.voice_icon_otherside_stop);
                    }
                    anima.start();
                }
            };
            if (file.exists() && file.length() >= 10) {
                util.startPlay(file.getAbsolutePath(), listener);
            } else {
                file.deleteOnExit();
            }

        }

    }

    /**
     * 停止语音播放
     */
    private void setStopPlayRecord(View view) {
        AnimationDrawable anima = (AnimationDrawable) ((LinearLayout) view
                .getParent()).getChildAt(1).getBackground();
        MessageModel mm = (MessageModel) view.getTag();
        if (currentUserId.equals(mm.getEntity().getSender())) {
            ((ImageView) view)
                    .setImageResource(R.drawable.voice_icon_myside_play);
        } else {
            ((ImageView) view)
                    .setImageResource(R.drawable.voice_icon_otherside_play);
        }
        anima.selectDrawable(0);
        anima.stop();
        util.stopPlay();
    }

    /********************* 小视频 ************************/

    public void setMySideMovieDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideMovieContent(model, viewHolder, position);
        bindRetreatOnLongClickEvent(model, viewHolder, position);
        setreadStatues(model, viewHolder.tvMySideMessageRead, position);
    }

    public void setOtherSideMovieDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideMovieContent(model, viewHolder, position);
        bindotherRetreatOnLongClickEvent(model, viewHolder, position);
    }

    private void setMySideMovieContent(final MessageModel model, final MViewHolder viewHolder,int position) {
        viewHolder.ivMySideImageOpen.setImageResource(R.drawable.voice_icon_play);
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            viewHolder.svMySideMovie.setVisibility(View.GONE);
            viewHolder.ivMySideImage.setVisibility(View.VISIBLE);
            viewHolder.ivMySideImageOpen.setVisibility(View.GONE);
            Json jsonContent = new Json(model.getEntity().getContent());
            final String timestamp = jsonContent.getStr("timestamp");
            final String filePath = jsonContent.getStr("localsrc");

            final String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
            final String thumbnail = SendUtil.getMovieThumbnail(filePath);
            final File file = new File(filePath);
            final int[] wh = ImageUtil.getImageWidthHeight(thumbnail);
            if(!file.exists()){
                ToastUtil.ToastMessage(_context,"文件不存在");
                return;
            }else{
                setViewSize(viewHolder.svMySideMovie,wh[0],wh[1]);
                setViewSize(viewHolder.ivMySideImage,wh[0],wh[1]);
                ImagePool.getInstance(AppContext.getAppContext()).load("file://" + thumbnail, fileToken, viewHolder.ivMySideImage, R.drawable.file_loading);
            }
            final RoundProgressBar pb = viewHolder.pbMySideImageTransfer;
            AsyncMultiPartPost post = AppData.getInstance().getPost(
                    filePath);

            post.execute();
            post.setCallBack(new AsyncMultiPartPost.CallBack() {

                @Override
                public void update(Integer i) {
                    if (pb != null) {
                        pb.setProgress(i);
                        LogUtil.getInstance().log(TAG, "i =" + i, null);
                        if (i == 100) {
                            pb.setVisibility(View.GONE);
                        } else {
                            pb.setVisibility(View.VISIBLE);
                            viewHolder.ivMySideImageOpen.setVisibility(View.GONE);
                        }
                    }
                }
            });
            post.setCallBackMsg(new AsyncMultiPartPost.CallBackMsg() {
                @Override
                public void msg(String param) {
                    LogUtil.getInstance().log(TAG, "param =" + param, null);
                    final MessageFileReturn movie = MessageFileReturn.parse(new Json(param));
                    AsyncMultiPartPost thumbPost = new AsyncMultiPartPost(_context, _session.getSessions().getUser().getToken(), Urls.User_Messages_File_UpLoad, thumbnail);
                    thumbPost.execute();
                    thumbPost.setCallBack(new AsyncMultiPartPost.CallBack() {
                        @Override
                        public void update(Integer i) {
                            if (pb != null) {
                                pb.setProgress(i);
                                if (i == 100) {
                                    pb.setVisibility(View.GONE);
                                    viewHolder.ivMySideImageOpen.setVisibility(View.VISIBLE);
                                } else {
                                    pb.setVisibility(View.VISIBLE);
                                    viewHolder.ivMySideImageOpen.setVisibility(View.GONE);
                                }
                            }
                        }
                    });

                    thumbPost.setCallBackMsg(new AsyncMultiPartPost.CallBackMsg() {
                        @Override
                        public void msg(String param) {
                            if (pb != null) {
                                pb.setVisibility(View.GONE);
                            }
                            MessageFileReturn image = MessageFileReturn.parse(new Json(param));

                            if(wh.length > 1) {
                                String content = MessageCrypto.getInstance().encryMovie(MessageFileReturn.getFileUrl(movie.getId()), filePath, MessageFileReturn.getFileUrl(image.getId()), wh[0], wh[1], filename,timestamp);
                                final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Video);
                                _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                                    @Override
                                    public void onSuccess(MessageModel netmodel) {
                                        sendSuccessRefresh();
                                    }

                                    @Override
                                    public void onError(int errorCode, String error) {
                                        ToastUtil.ToastMessage(_context, "发送失败");
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else{
            Json jsonContent = new Json(model.getEntity().getContent());
            final String thumbnail = jsonContent.getStr("thumbnail");
            int imgWidth =jsonContent.getInt("width");
            int imgHeight =jsonContent.getInt("height");
            setViewSize(viewHolder.ivMySideImage,imgWidth,imgHeight);
            setViewSize(viewHolder.svMySideMovie,imgWidth,imgHeight);
            viewHolder.svMySideMovie.setVisibility(View.GONE);
            viewHolder.ivMySideImage.setVisibility(View.VISIBLE);
            viewHolder.ivMySideImageOpen.setVisibility(View.VISIBLE);
            final String localsrc = jsonContent.getStr("localsrc");
            if(FileUtil.checkFilePathExists(localsrc)){
                String locthumbnail = SendUtil.getMovieThumbnail(localsrc);
                ImagePool.getInstance(AppContext.getAppContext()).load("file://" + locthumbnail, fileToken, viewHolder.ivMySideImage, R.drawable.file_loading);
            }else {
                ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + thumbnail, fileToken, viewHolder.ivMySideImage, R.drawable.file_loading);
            }
        }
        viewHolder.llMySideImageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.svMySideMovie.getVisibility()==View.GONE){
                    final String localsrc = new Json(model.getEntity().getContent()).getStr("localsrc");
                    if(FileUtil.checkFilePathExists(localsrc)){
                        viewHolder.ivMySideImage.setVisibility(View.GONE);
                        viewHolder.ivMySideImageOpen.setVisibility(View.GONE);
                        final File file = new File(localsrc);
                        setVideo(viewHolder.svMySideMovie, file.getAbsolutePath());
                    }else{
                        String videoName = MessageType.Video.toString()+"_"+model.getEntity().getId()+".mp4";
                        final String filePath = Config.CACHE_PATH_MOVIE + videoName;
                        final File file = new File(filePath);
                        if(file.exists()) {
                            viewHolder.ivMySideImage.setVisibility(View.GONE);
                            viewHolder.ivMySideImageOpen.setVisibility(View.GONE);
                            setVideo(viewHolder.svMySideMovie, file.getAbsolutePath());
                        }else{
                            viewHolder.ivMySideImageOpen.setVisibility(View.GONE);
                            movieDownLoad(model,viewHolder.svMySideMovie,viewHolder.pbMySideImageTransfer,viewHolder.ivMySideImage,viewHolder.ivMySideImageOpen);
                        }
                    }
                }else{
                    viewHolder.svMySideMovie.stop();
                    viewHolder.svMySideMovie.setVisibility(View.GONE);
                    viewHolder.ivMySideImage.setVisibility(View.VISIBLE);
                    if(MessageEntity.MessageState.stored.toString().equals(model.getEntity().getStatus())) {
                        viewHolder.ivMySideImageOpen.setVisibility(View.VISIBLE);
                    }
                    Json jsonContent = new Json(model.getEntity().getContent());
                    String thumbnail = jsonContent.getStr("thumbnail");
                    final String localsrc = jsonContent.getStr("localsrc");
                    if(FileUtil.checkFilePathExists(localsrc)){
                        String locthumbnail = SendUtil.getMovieThumbnail(localsrc);
                        ImagePool.getInstance(AppContext.getAppContext()).load("file://" + locthumbnail, fileToken, viewHolder.ivMySideImage, R.drawable.file_loading);
                    }else {
                        ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + thumbnail, fileToken, viewHolder.ivMySideImage, R.drawable.file_loading);
                    }
                }
            }
        });
    }
    /**
     * 设置对方小视频显示
     * @param model
     * @param viewHolder
     * @param position
     */
    private void setOtherSideMovieContent(final MessageModel model, final MViewHolder viewHolder, int position) {
        // 设置控件显示
        Json jsonContent = new Json(model.getEntity().getContent());
        final String thumbnail = jsonContent.getStr("thumbnail");

        int imgWidth =jsonContent.getInt("width");
        int imgHeight =jsonContent.getInt("height");
        setViewSize(viewHolder.ivOtherSideImage,imgWidth,imgHeight);
        setViewSize(viewHolder.svOtherSideMovie,imgWidth,imgHeight);

        String videoName = MessageType.Video.toString()+"_"+model.getEntity().getId()+".mp4";
        final String filePath = Config.CACHE_PATH_MOVIE + videoName;
        final File file = new File(filePath);
        //String videoName = new Json(model.getEntity().getContent()).getStr("name");
        viewHolder.ivOtherSideImageOpen.setImageResource(R.drawable.voice_icon_play);
        viewHolder.svOtherSideMovie.setVisibility(View.GONE);
        viewHolder.ivOtherSideImage.setVisibility(View.VISIBLE);
        viewHolder.ivOtherSideImageOpen.setVisibility(View.VISIBLE);
        ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + thumbnail, fileToken, viewHolder.ivOtherSideImage, R.drawable.file_loading);
        viewHolder.llOtherSideImageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.svOtherSideMovie.getVisibility()==View.GONE){
                    if(file.exists()) {
                        viewHolder.ivOtherSideImage.setVisibility(View.GONE);
                        viewHolder.ivOtherSideImageOpen.setVisibility(View.GONE);
                        setVideo(viewHolder.svOtherSideMovie, file.getAbsolutePath());
                    }else{
                        viewHolder.ivOtherSideImageOpen.setVisibility(View.GONE);
                        movieDownLoad(model,viewHolder.svOtherSideMovie,viewHolder.pbOtherSideImageTransfer,viewHolder.ivOtherSideImage,viewHolder.ivOtherSideImageOpen);
                    }
                }else{
                    viewHolder.svOtherSideMovie.stop();
                    viewHolder.svOtherSideMovie.setVisibility(View.GONE);
                    viewHolder.ivOtherSideImage.setVisibility(View.VISIBLE);
                    viewHolder.ivOtherSideImageOpen.setVisibility(View.VISIBLE);
                    ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + thumbnail, fileToken, viewHolder.ivOtherSideImage, R.drawable.file_loading);
                }
            }
        });
    }

    private void setVideo(final ScalableVideoView videoView,String filePath) {
        try {
            videoView.setVisibility(View.VISIBLE);
            videoView.setDataSource(filePath);
            //videoView.setVolume(0, 0);
            videoView.setLooping(true);
            videoView.setScalableType(ScalableType.CENTER_CROP);
            videoView.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                }
            });
        } catch (IOException ioe) {
            //ignore
        }
    }

    private void movieDownLoad(final MessageModel model, final ScalableVideoView scalableVideoView,RoundProgressBar roundProgressBar, final RoundAngleImageView roundAngleImageView, final ImageView imageView){
        //String videoName = new Json(model.getEntity().getContent()).getStr("name");

        final String videoName = MessageType.Video.toString()+"_"+model.getEntity().getId()+".mp4";
        String url = new Json(model.getEntity().getContent()).getStr("src");
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what ==1){
                    setVideo(scalableVideoView, Config.CACHE_PATH_MOVIE + videoName);
                }
            }
        };
        AsyncMultiPartGet get = new AsyncMultiPartGet(_session.getSessions().getUser().getToken(), Urls.User_Messages_File_DownLoad + url, Config.CACHE_PATH_MOVIE, videoName);
        get.execute();

        final RoundProgressBar pb =roundProgressBar;
        get.setCallBack(new AsyncMultiPartGet.CallBack() {
            @Override
            public void update(Integer i) {
                if (pb != null) {
                    pb.setProgress(i);
                    if (i == 100) {
                        pb.setVisibility(View.GONE);
                        roundAngleImageView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        //handler.sendEmptyMessage(1);
                        //setVideo(viewHolder.svOtherSideMovie, Config.CACHE_PATH_MOVIE + videoName);
                    } else {
                        pb.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    /********************* 文件 ************************/

    public void setMySideFileDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideFileContent(model, viewHolder, position);
        bindRetreatOnLongClickEvent(model, viewHolder, position);
        setreadStatues(model, viewHolder.tvMySideMessageRead, position);
    }

    public void setOtherSideFileDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideFileContent(model, viewHolder, position);
        bindotherRetreatOnLongClickEvent(model, viewHolder, position);
    }

    private void setMySideFileContent(final MessageModel model, final MViewHolder viewHolder,int position) {
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){

            Json jsonContext = new Json(model.getEntity().getContent());
            final String filePath = jsonContext.getStr("src");
            final String fileName = jsonContext.getStr("name");
            final String timestamp = jsonContext.getStr("timestamp");

            if(fileName.endsWith("apk")){
                viewHolder.tvMySideFileTitle.setText(fileName.substring(0,fileName.length()-4));
            }else {
                viewHolder.tvMySideFileTitle.setText(fileName);
            }
            setFileTypeImage(viewHolder.ivMySideFileIcon, fileName);
            final File file = new File(filePath);
            if(file.exists()){
                viewHolder.tvMySideFileSize.setText(StringUtil.getFriendlyFileSize(file.length()));
            }
            viewHolder.tvMySideFileState.setText("0%");
            final ProgressBar pb = viewHolder.pbMySideFile;
            pb.setVisibility(View.VISIBLE);
            AsyncMultiPartPost post = AppData.getInstance().getPost(
                    filePath);
            post.execute();
            post.setCallBack(new AsyncMultiPartPost.CallBack() {

                @Override
                public void update(Integer i) {
                    if (pb != null) {
                        pb.setProgress(i);
                        if (i == 100) {
                            viewHolder.tvMySideFileState.setText("已发送");
                            pb.setVisibility(View.GONE);
                        } else {
                            viewHolder.tvMySideFileState.setText(i+"%");
                            pb.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            post.setCallBackMsg(new AsyncMultiPartPost.CallBackMsg() {
                @Override
                public void msg(String param) {
                    LogUtil.getInstance().log(TAG, "param =" + param, null);
                    final MessageFileReturn fileReturn = MessageFileReturn.parse(new Json(param));
                    if(StringUtil.isEmpty(fileReturn.getId())){
                        return;
                    }

                    FileUtil.fileCopy(filePath, Config.CACHE_PATH_FILE + fileName);
                    String content = MessageCrypto.getInstance().encryFile(MessageFileReturn.getFileUrl(fileReturn.getId()),filePath, "", 200, 202, fileName, file.length()+"",timestamp);
                    final MessageModel temp = _session.getMessages().createMessage(content, MessageType.File);
                    _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                        @Override
                        public void onSuccess(MessageModel netmodel) {
                            sendSuccessRefresh();
                        }

                        @Override
                        public void onError(int errorCode, String error) {
                            ToastUtil.ToastMessage(_context, "发送失败");
                        }
                    });
                }
            });
            viewHolder.llMySideItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(model.getEntity().getContent());
                    if (!file.exists()) {
                        ToastUtil.ToastMessage(_context,"文件不存在");
                    }else{
                        FileUtil.openFile(_context, file.getAbsolutePath());
                    }
                }});
        }else {
            Json jsonContext = new Json(model.getEntity().getContent());
            final String localsrc = jsonContext.getStr("localsrc");
            final String url = jsonContext.getStr("src");
            final String fileName = jsonContext.getStr("name");
            String fileSize = jsonContext.getStr("size");
            viewHolder.pbMySideFile.setVisibility(View.GONE);
            if(fileName.endsWith("apk")){
                viewHolder.tvMySideFileTitle.setText(fileName.substring(0,fileName.length()-4));
            }else {
                viewHolder.tvMySideFileTitle.setText(fileName);
            }
            viewHolder.tvMySideFileState.setText("已发送");
            final String modelId =  model.getEntity().getId();
            viewHolder.llMySideItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FileUtil.checkFilePathExists(localsrc)) {
                        File file = new File(localsrc);
                        FileUtil.openFile(_context, file.getAbsolutePath());
                    }else {
                        File file = new File(Config.CACHE_PATH_FILE +modelId+ fileName);
                        if (!file.exists()) {
                            AsyncMultiPartGet get = new AsyncMultiPartGet(_session.getSessions().getUser().getToken(), Urls.User_Messages_File_DownLoad + url, Config.CACHE_PATH_FILE, modelId+fileName);
                            get.execute();
                            get.setCallBack(new AsyncMultiPartGet.CallBack() {
                                @Override
                                public void update(Integer i) {
                                    viewHolder.pbMySideFile.setVisibility(View.VISIBLE);
                                    viewHolder.pbMySideFile.setProgress(i);
                                    viewHolder.tvMySideFileState.setText(i + "%");
                                    LogUtil.getInstance().log(TAG, "down load i =" + i, null);
                                }
                            });
                            get.setCallBackMsg(new AsyncMultiPartGet.CallBackMsg() {
                                @Override
                                public void msg(String result) {
                                    viewHolder.pbMySideFile.setVisibility(View.GONE);
                                    viewHolder.tvMySideFileState.setText("已下载");
                                    LogUtil.getInstance().log(TAG, "result =" + result, null);
                                }
                            });
                        } else {
                            FileUtil.openFile(_context, Config.CACHE_PATH_FILE + modelId+fileName);
                        }
                    }
                }
            });
            setFileTypeImage(viewHolder.ivMySideFileIcon, fileName);
            viewHolder.tvMySideFileSize.setText(StringUtil.getFriendlyFileSize(Long.parseLong(fileSize)));
        }
    }

    private void setOtherSideFileContent(final MessageModel model, final MViewHolder viewHolder,int position) {
        Json json = new Json(model.getEntity().getContent());
        final String url = json.getStr("src");
        final String fileName = json.getStr("name");
        String fileSize = json.getStr("size");
        long longsize = Long.parseLong(fileSize);
        viewHolder.pbOtherSideFile.setVisibility(View.GONE);
        if(fileName.endsWith("apk")){
            viewHolder.tvOtherSideFileTitle.setText(fileName.substring(0,fileName.length()-4));
        }else {
            viewHolder.tvOtherSideFileTitle.setText(fileName);
        }
        final String modelId =  model.getEntity().getId();
        File file = new File(Config.CACHE_PATH_FILE+modelId+fileName);
        if(!file.exists() || file.length()<longsize){
            viewHolder.tvOtherSideFileState.setText("未下载");
        }else{
            viewHolder.tvOtherSideFileState.setText("已下载");
        }
        setFileTypeImage(viewHolder.ivOtherSideFileIcon, fileName);
        viewHolder.tvOtherSideFileSize.setText(StringUtil.getFriendlyFileSize(Long.parseLong(fileSize)));
        viewHolder.llOtherSideItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Config.CACHE_PATH_FILE +modelId+ fileName);
                if (!file.exists()) {
                    AsyncMultiPartGet get = new AsyncMultiPartGet(_session.getSessions().getUser().getToken(), Urls.User_Messages_File_DownLoad + url, Config.CACHE_PATH_FILE, modelId+fileName);
                    get.execute();
                    get.setCallBack(new AsyncMultiPartGet.CallBack() {
                        @Override
                        public void update(Integer i) {
                            viewHolder.pbOtherSideFile.setVisibility(View.VISIBLE);
                            viewHolder.pbOtherSideFile.setProgress(i);
                            viewHolder.tvOtherSideFileState.setText(i + "%");
                            LogUtil.getInstance().log(TAG, "down load i =" + i, null);
                        }
                    });
                    get.setCallBackMsg(new AsyncMultiPartGet.CallBackMsg() {
                        @Override
                        public void msg(String result) {
                            viewHolder.pbOtherSideFile.setVisibility(View.GONE);
                            viewHolder.tvOtherSideFileState.setText("已下载");
                            LogUtil.getInstance().log(TAG, "result =" + result, null);
                        }
                    });
                } else {
                    FileUtil.openFile(_context, Config.CACHE_PATH_FILE +modelId+ fileName);
                }
            }
        });
    }

    private void setFileTypeImage(ImageView imageView,String fileName){
        String extensionName = StringUtil.getFileExtensionName(fileName);

        if (StringUtil.isPdfFileWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_pdf);
        } else if (StringUtil.isImageWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_pic);
        } else if (StringUtil.isAudioWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_audio);
        } else if (StringUtil.isVideoWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_video);
        } else if (StringUtil.isApkFileWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_apk);
        } else if (StringUtil.isPPTFileWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_ppt);
        } else if (StringUtil.isDocFileWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_doc);
        } else if (StringUtil.isXlsFileWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_xls);
        } else if (StringUtil.isZIPFileWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_zip);
        }else if (StringUtil.isTxtFileWithSuffixName(extensionName)) {
            imageView.setImageResource(R.drawable.message_file_txt);
        } else {
            imageView.setImageResource(R.drawable.message_file_unknow);
        }
    }

    /********************* 位置 ************************/

    public void setMySideLocationDisplay(MessageModel model, MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideLocationContent(model, viewHolder, position);
        bindRetreatOnLongClickEvent(model, viewHolder, position);
        setreadStatues(model, viewHolder.tvMySideMessageRead, position);
    }

    public void setOtherSideLocationDisplay(MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideLocationContent(model, viewHolder, position);
        bindotherRetreatOnLongClickEvent(model, viewHolder, position);
    }

    private void setMySideLocationContent(final MessageModel model, MViewHolder viewHolder,int position) {

        viewHolder.llMySideImageItem.setVisibility(View.VISIBLE);
        viewHolder.ivMySideImage.setVisibility(View.VISIBLE);
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            Json jsonContent = new Json(model.getEntity().getContent());
            String locationinfo = jsonContent.getStr("src");//包含经纬度
            String thumbnail = jsonContent.getStr("thumbnail");
            final String timestamp = jsonContent.getStr("timestamp");

            final String[] result = locationinfo.split(";");//文件路径,纬度,经度,地址
            if(result.length < 1){
                return;
            }
            int imgWidth = jsonContent.getInt("width");
            int imgHeight = jsonContent.getInt("height");
            setViewSize(viewHolder.ivMySideImage, imgWidth, imgHeight);
            ImagePool.getInstance(AppContext.getAppContext()).load("file://" + thumbnail,null, viewHolder.ivMySideImage, R.drawable.file_loading);
            final RoundProgressBar pb = viewHolder.pbMySideImageTransfer;
            AsyncMultiPartPost post = AppData.getInstance().getPost(
                    result[0]);
            post.execute();
            post.setCallBack(new AsyncMultiPartPost.CallBack() {

                @Override
                public void update(Integer i) {
                    if (pb != null) {
                        pb.setProgress(i);
                        LogUtil.getInstance().log(TAG, "i =" + i, null);
                        if (i == 100) {
                            pb.setVisibility(View.GONE);
                        } else {
                            pb.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            post.setCallBackMsg(new AsyncMultiPartPost.CallBackMsg() {
                @Override
                public void msg(String param) {
                    LogUtil.getInstance().log(TAG, "param =" + param, null);
                    final MessageFileReturn thumbnail = MessageFileReturn.parse(new Json(param));
                    int[] wh = ImageUtil.getImageWidthHeight(result[0]);
                    if(wh.length > 1) {
                        String content = MessageCrypto.getInstance().encryLocation(MessageFileReturn.getFileUrl(thumbnail.getId()),result[0], wh[0], wh[1], result[1], result[2],timestamp);
                        final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Location);
                        _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                            @Override
                            public void onSuccess(MessageModel netmodel) {
                                sendSuccessRefresh();
                            }

                            @Override
                            public void onError(int errorCode, String error) {
                                ToastUtil.ToastMessage(_context, "发送失败");
                            }
                        });
                    }
                }
            });
        }else{
            Json jsonContent = new Json(model.getEntity().getContent());
            String thumbnail = jsonContent.getStr("thumbnail");
            String localsrc = jsonContent.getStr("localsrc");
            int imgWidth = jsonContent.getInt("width");
            int imgHeight = jsonContent.getInt("height");
            setViewSize(viewHolder.ivMySideImage, imgWidth, imgHeight);
            if(FileUtil.checkFilePathExists(localsrc)){
                ImagePool.getInstance(AppContext.getAppContext()).load("file://"+localsrc,null, viewHolder.ivMySideImage, R.drawable.file_loading);
            }else {
                ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + thumbnail, fileToken, viewHolder.ivMySideImage, R.drawable.file_loading);
            }
        }
        bindOpenLocationEvent(model, viewHolder, position);

    }



    /**
     * 绑定打开图片事件
     *
     * @param imageView
     * @param mm
     * @param isFromOtherSide
     */
    private int degree = 0;
    private void bindOpenLocationEvent(final MessageModel model, MViewHolder viewHolder,int position) {
        final Boolean isFromOtherSide = currentUserId.equals(model.getEntity().getSender()) ? false
                : true;
        final ImageView imageView = isFromOtherSide == true ? viewHolder.ivOtherSideImage
                : viewHolder.ivMySideImage;
        // 如果是对方发来的消息，处理阅后即焚
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Json json = new Json(model.getEntity().getContent());
                String latitude = json.getStr("latitude");
                String longitude = json.getStr("longitude");
                if(StringUtil.isEmpty(latitude)){
                    final String[] result = model.getEntity().getContent().split(";");//文件路径,纬度,经度,地址
                    if(result.length < 1){
                        return;
                    }
                    latitude = result[1];
                    longitude = result[2];
                }
                Intent intent = new Intent(_context, LocationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type",LocationActivity.LocationType.locationDisplay);
                bundle.putString("latitude", latitude);
                bundle.putString("longitude", longitude);
                intent.putExtras(bundle);
                _context.startActivity(intent);
            }
        });

    }

    /**
     * 设置对方纯图片显示
     * @param model
     * @param viewHolder
     * @param position
     */
    private void setOtherSideLocationContent(final MessageModel model, MViewHolder viewHolder,int position) {
        // 设置控件显示
        viewHolder.ivOtherSideImage.setVisibility(View.VISIBLE);
        Json jsonContent = new Json(model.getEntity().getContent());
        String thumbnail = jsonContent.getStr("thumbnail");

        int imgWidth =jsonContent.getInt("width");
        int imgHeight =jsonContent.getInt("height");
        setViewSize(viewHolder.ivOtherSideImage,imgWidth,imgHeight);

        ImagePool.getInstance(AppContext.getAppContext()).load(Urls.User_Messages_File_DownLoad + thumbnail, fileToken, viewHolder.ivOtherSideImage, R.drawable.file_loading);
        viewHolder.llOtherSideImageItem.setVisibility(View.VISIBLE);
        bindOpenLocationEvent(model, viewHolder, position);
    }

    /********************* 音视频 ************************/

    public void setMySideAVDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideAVContent(model, viewHolder, position);
        setreadStatues(model, viewHolder.tvMySideMessageRead, position);
    }

    public void setOtherSideAVDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideAVContent(model, viewHolder, position);
    }

    private void setMySideAVContent(final MessageModel model, final MViewHolder viewHolder,int position) {
        String content = model.getEntity().getContent();
        if(content != null){
            final AvSystemMsg avSystemMsg = new Gson().fromJson(content,AvSystemMsg.class);
            if(avSystemMsg != null){
                viewHolder.tvMySideAV.setText(avSystemMsg.getText());
                viewHolder.ivMySideAVIcon.setImageResource(R.drawable.message_type_av);
                viewHolder.llMySideAVItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }

    private void setOtherSideAVContent(final MessageModel model, final MViewHolder viewHolder,int position) {
        String content = model.getEntity().getContent();
        if(content != null){
            final AvSystemMsg avSystemMsg = new Gson().fromJson(content,AvSystemMsg.class);
            if(avSystemMsg != null){
                viewHolder.tvOtherSideAV.setText(avSystemMsg.getText());
                viewHolder.ivOtherSideAVIcon.setImageResource(R.drawable.message_type_av);
                viewHolder.llOtherSideAVItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }

    /********************* 消息回撤 ************************/
    private void bindRetreatOnLongClickEvent(final MessageModel model, MViewHolder viewHolder,int position){
        if(MessageType.Image.toString().equals(model.getEntity().getType())){
            viewHolder.ivMySideImage.setOnLongClickListener(new retreatOnLongClick(model));
        }else if(MessageType.Audio.toString().equals(model.getEntity().getType())){
            viewHolder.llMySideVoiceItem.setOnLongClickListener(new retreatOnLongClick(model));
        }else if(MessageType.Video.toString().equals(model.getEntity().getType())){
            viewHolder.llMySideImageItem.setOnLongClickListener(new retreatOnLongClick(model));
        }else if(MessageType.Location.toString().equals(model.getEntity().getType())){
            viewHolder.ivMySideImage.setOnLongClickListener(new retreatOnLongClick(model));
        }else if(MessageType.File.toString().equals(model.getEntity().getType())){
            viewHolder.llMySideItem.setOnLongClickListener(new retreatOnLongClick(model));
        }
    }
    private void bindotherRetreatOnLongClickEvent(final MessageModel model, MViewHolder viewHolder,int position){
        if(MessageType.Image.toString().equals(model.getEntity().getType())){
            viewHolder.ivOtherSideImage.setOnLongClickListener(new retreatOnLongClick(model));
        }else if(MessageType.Audio.toString().equals(model.getEntity().getType())){
            viewHolder.llOtherSideVoiceItem.setOnLongClickListener(new retreatOnLongClick(model));
        }else if(MessageType.Video.toString().equals(model.getEntity().getType())){
            viewHolder.llOtherSideImageItem.setOnLongClickListener(new retreatOnLongClick(model));
        }else if(MessageType.Location.toString().equals(model.getEntity().getType())){
            viewHolder.ivOtherSideImage.setOnLongClickListener(new retreatOnLongClick(model));
        }else if(MessageType.File.toString().equals(model.getEntity().getType())){
            viewHolder.llOtherSideItem.setOnLongClickListener(new retreatOnLongClick(model));
        }
    }
    private class retreatOnLongClick implements View.OnLongClickListener{
        private MessageModel model;
        public retreatOnLongClick(MessageModel m){
            model = m;
        }
        @Override
        public boolean onLongClick(View v) {

            Y2wDialog dialog = new Y2wDialog(_context);
            dialog.addOption("转发");
            if(currentUserId.equals(model.getEntity().getSender())) {
                dialog.addOption("回撤");
            }
            dialog.show();
            dialog.setOnOptionClickListener(new Y2wDialog.onOptionClickListener() {
                @Override
                public void onOptionClick(String option, int position) {
                    if(position==0){//转发
                        Intent intent = new Intent(_context, ChooseSessionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("repeatMessage",model.getEntity());
                        bundle.putString("type","conversation");
                        intent.putExtras(bundle);
                        _context.startActivity(intent);
                    }else if (position == 1) {//回撤
                        String name = Users.getInstance().getCurrentUser().getEntity().getName();
                        model.getEntity().setContent(name + "回撤了一条消息");
                        model.getEntity().setType(MessageType.System);
                        _session.getMessages().getRemote().updateMessage(model, new Back.Result<MessageModel>() {
                            @Override
                            public void onSuccess(MessageModel model) {
                                sendSuccessRefresh();
                                _session.getMessages().getRemote().sendMessage("",false, new IMClient.SendCallback() {
                                    @Override
                                    public void onReturnCode(int i, IMSession imSession, String s) {

                                    }
                                });
                            }

                            @Override
                            public void onError(int code, String error) {
                                ToastUtil.ToastMessage(_context, error);
                            }
                        });
                    }
                }
            });
            return true;
        }
    }

}
