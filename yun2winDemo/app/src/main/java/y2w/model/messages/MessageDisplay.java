package y2w.model.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.movie.ScalableType;
import com.y2w.uikit.customcontrols.record.RecordUtil;
import com.y2w.uikit.customcontrols.movie.ScalableVideoView;
import com.y2w.uikit.utils.FileUtil;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.utils.Json;
import com.yun2win.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import y2w.base.Urls;
import y2w.common.AsyncMultiPartGet;
import y2w.common.CallBackUpdate;
import y2w.common.Config;
import y2w.common.SendUtil;
import y2w.ui.dialog.Y2wDialog;
import y2w.entities.UserEntity;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.base.AppData;
import y2w.common.AsyncMultiPartPost;
import com.y2w.uikit.customcontrols.view.RoundProgressBar;
import y2w.entities.MessageEntity;
import y2w.model.MessageModel;
import y2w.model.Session;

import com.y2w.uikit.utils.ImagePool;
import com.y2w.uikit.utils.StringUtil;
import y2w.common.UserInfo;
import y2w.model.SessionMember;
import y2w.service.Back;
import y2w.ui.activity.ChatActivity;
import y2w.ui.activity.ContactInfoActivity;
import y2w.ui.activity.LocationActivity;
import y2w.ui.widget.emoji.Expression;

/**
 * 聊天界面，消息展示
 * Created by yangrongfang on 2016/2/23.
 */
public class MessageDisplay {

    private String TAG = MessageDisplay.class.getSimpleName();
    private Context _context;
    private Session _session;
    private List<MessageModel> _models;
    private List<String> moviePlayPathList = new ArrayList<String>();
    private String currentUserId = UserInfo.getUserId();
    public MessageDisplay(Context context,List<MessageModel> models,Session session){
        this._context = context;
        this._models = models;
        this._session = session;
    }


    /********************* 我方，对方日期，头像，消息时间等 基本信息填充 **********************/
    public void setMySideCommonInfo(MessageModel model,
                                     MViewHolder viewHolder,int position) {
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

    /********************* 设置时间**********************/
    public String getDisplayTime(String sDate){
        if(!StringUtil.isEmpty(sDate) && sDate.length() > 16){
            sDate = sDate.substring(0,16);
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
            if (StringUtil.isTimeDisplay(lastModel.getEntity().getUpdatedAt(), model.getEntity().getUpdatedAt())) {
                viewHolder.tvCreateDate.setVisibility(View.VISIBLE);
                viewHolder.tvCreateDate.setText(getDisplayTime(model.getEntity().getUpdatedAt()));
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
                    Intent intent = new Intent(_context,ContactInfoActivity.class);
                    Bundle bundle = new Bundle();
                    UserEntity userEntity = Users.getInstance().getCurrentUser().getEntity();
                    bundle.putString("otheruserid", userEntity.getId());
                    bundle.putString("avatarUrl",userEntity.getAvatarUrl());
                    bundle.putString("username",userEntity.getName());
                    bundle.putString("account",userEntity.getAccount());
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

        _session.getMembers().getMember(model.getEntity().getSender(), new Back.Result<SessionMember>() {
            @Override
            public void onSuccess(SessionMember sessionMember) {
                viewHolder.ivOtherSideIcon.loadBuddyAvatarbyurl(sessionMember.getEntity().getAvatarUrl(), R.drawable.chat_default_icon);
                viewHolder.tvOtherSideCircleName.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(sessionMember.getEntity().getUserId())));
                if (EnumManage.SessionType.group.toString().equals(_session.getEntity().getType())) {
                    viewHolder.tvOtherSideName.setText(sessionMember.getEntity().getName());
                    viewHolder.tvOtherSideName.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tvOtherSideName.setText("");
                    viewHolder.tvOtherSideName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int errorCode, String error) {
                viewHolder.ivOtherSideIcon.setImageResource(R.drawable.chat_default_icon);
                viewHolder.tvOtherSideCircleName.setBackgroundResource(HeadTextBgProvider.getTextBg(0));
                viewHolder.tvOtherSideName.setText("");
                viewHolder.tvOtherSideName.setVisibility(View.GONE);
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
                    if(ChatActivity.chatHandler != null){
                        Message msg = new Message();
                        msg.what = ChatActivity.RefreshCode.CODE_OTHER_AT;
                        msg.obj = "@"+viewHolder.tvOtherSideName.getText()+" ";
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
            update.addDateUI("");
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
    }

    public void setOtherSideTextDisplay(MessageModel model,
                                         MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideTextContent(model, viewHolder, position);
    }

    private void setMySideTextSendAnimation(MessageModel model,MViewHolder viewHolder,int position) {
        /** 设置旋转动画 */
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            final RotateAnimation animation =new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF,0.5f);
            animation.setDuration(5000);//设置动画持续时间
            /** 常用方法 */
            //animation.setRepeatCount(int repeatCount);//设置重复次数
            //animation.setFillAfter(boolean);//动画执行完后是否停留在执行完的状态
            //animation.setStartOffset(long startOffset);//执行前的等待时间
            viewHolder.ivMySideMessageLoading.setAnimation(animation);
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
        Expression.emojiDisplay(_context, null, viewHolder.tvMySideText, MessageCrypto.getInstance().decryText(model.getEntity().getContent()), Expression.WH_2);
        bindTextOnLongClickEvent(model, viewHolder, position);
        //viewHolder.tvMySideText.setText(MessageCrypto.getInstance().decryText(model.getEntity().getContent()));
    }

    private void setOtherSideTextContent(MessageModel model, MViewHolder viewHolder,int position) {
        viewHolder.tvOtherSideText
                .setBackgroundResource(R.drawable.message_text_otherside_style);
        Expression.emojiDisplay(_context, null, viewHolder.tvOtherSideText, MessageCrypto.getInstance().decryText(model.getEntity().getContent()), Expression.WH_2);
        bindTextOnLongClickEvent(model, viewHolder, position);
        //viewHolder.tvOtherSideText.setText(MessageCrypto.getInstance().decryText(model.getEntity().getContent()));
    }

    private void bindTextOnLongClickEvent(final MessageModel model, MViewHolder viewHolder,int position){
        final TextView tv_text = currentUserId.equals(model.getEntity().getSender()) ? viewHolder.tvMySideText : viewHolder.tvOtherSideText;
        tv_text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Y2wDialog dialog = new Y2wDialog(_context);
                dialog.addOption("复制");
                dialog.addOption("删除");
                dialog.addOption("回撤");
                dialog.show();
                dialog.setOnOptionClickListener(new Y2wDialog.onOptionClickListener() {
                    @Override
                    public void onOptionClick(String option, int position) {
                        if (position == 0) {//复制
                            AppData.getInstance().getClipboardManager(_context)
                                    .setText(MessageCrypto.getInstance().decryText(model.getEntity().getContent()));
                        }else if (position == 1) {

                        }else if (position == 2) {//回撤
                            String name = Users.getInstance().getCurrentUser().getEntity().getName();
                            model.getEntity().setContent(name+"回撤了一条消息");
                            model.getEntity().setType(MessageType.System);
                            _session.getMessages().getRemote().updateMessage(model, new Back.Result<MessageModel>() {
                                @Override
                                public void onSuccess(MessageModel model) {
                                    sendSuccessRefresh();
                                }

                                @Override
                                public void onError(int code, String error) {
                                    ToastUtil.ToastMessage(_context,error);
                                }
                            });
                        }
                    }
                });
                return true;
            }
        });
    }


    /********************* 图片 *************************/

    public void setMySideImageDisplay(MessageModel model, MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideImageContent(model, viewHolder, position);
    }

    public void setOtherSideImageDisplay(MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideImageContent(model, viewHolder, position);
    }

    private void setMySideImageContent(final MessageModel model, MViewHolder viewHolder,int position) {

        viewHolder.llMySideImageItem.setVisibility(View.VISIBLE);
        viewHolder.ivMySideImage.setVisibility(View.VISIBLE);
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            ImagePool.getInstance(_context).load("file://" + model.getEntity().getContent(), viewHolder.ivMySideImage, R.drawable.file_loading);
            final RoundProgressBar pb = viewHolder.pbMySideImageTransfer;
            AsyncMultiPartPost post = AppData.getInstance().getPost(
                    model.getEntity().getContent());
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
                    String thumbnail = SendUtil.getImageThumbnail(model.getEntity().getContent());
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
                            String content = MessageCrypto.getInstance().encryImage(MessageFileReturn.getFileUrl(imageOrigin.getId()), MessageFileReturn.getFileUrl(image.getId()), 200, 202);
                            final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Image);
                            _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                                @Override
                                public void onSuccess(MessageModel model) {
                                    sendSuccessRefresh();
                                }

                                @Override
                                public void onError(int errorCode, String error) {
                                    ToastUtil.ToastMessage(_context, "发送失败");
                                }
                            });
                        }
                    });
                }
            });
        }else{
            String thumbnail = new Json(model.getEntity().getContent()).getStr("thumbnail");
            ImagePool.getInstance(_context).load(Urls.User_Messages_File_DownLoad + thumbnail+"?access_token="+_session.getSessions().getUser().getToken(), viewHolder.ivMySideImage, R.drawable.file_loading);
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
                /*Intent intent = new Intent(_context, ImagePreviewDialog.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", entity);
                intent.putExtras(bundle);
                _context.startActivity(intent);*/
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
        String thumbnail = new Json(model.getEntity().getContent()).getStr("thumbnail");
        ImagePool.getInstance(_context).load(Urls.User_Messages_File_DownLoad + thumbnail + "?access_token=" + _session.getSessions().getUser().getToken(), viewHolder.ivOtherSideImage, R.drawable.file_loading);
        viewHolder.llOtherSideImageItem.setVisibility(View.VISIBLE);
        bindOpenImageEvent(model, viewHolder, position);
    }


    public Context get_context() {
        return _context;
    }

    /********************* 语音 ************************/

    public void setMySideVoiceDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideVoiceContent(model, viewHolder, position);
    }

    public void setOtherSideVoiceDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideVoiceContent(model, viewHolder, position);
    }

    /**
     * 设置我方语音聊天显示
     * @param model
     * @param viewHolder
     * @param position
     */
    private void setMySideVoiceContent(final MessageModel model, final MViewHolder viewHolder,int position) {
        // 设置控件显示
        viewHolder.ivMySideVoiceIcon.setTag(model);
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())) {
            String filePath = model.getEntity().getContent();
            final String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
            final RotateAnimation animation =new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF,0.5f);
            animation.setDuration(5000);//设置动画持续时间
            /** 常用方法 */
            //animation.setRepeatCount(int repeatCount);//设置重复次数
            //animation.setFillAfter(boolean);//动画执行完后是否停留在执行完的状态
            //animation.setStartOffset(long startOffset);//执行前的等待时间
            viewHolder.ivMySideMessageLoading.setAnimation(animation);
            viewHolder.ivMySideMessageLoading.setVisibility(View.VISIBLE);
            AsyncMultiPartPost post = AppData.getInstance().getPost(
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
                    String content = MessageCrypto.getInstance().encryAudio(MessageFileReturn.getFileUrl(audio.getId()),20,filename);
                    final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Audio);
                    _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                        @Override
                        public void onSuccess(MessageModel model) {
                            viewHolder.ivMySideMessageLoading.setVisibility(View.GONE);
                            sendSuccessRefresh();
                        }

                        @Override
                        public void onError(int errorCode, String error) {
                            ToastUtil.ToastMessage(_context, "发送失败");
                        }
                    });
                }
            });

        }else{
            viewHolder.ivMySideMessageLoading.setVisibility(View.GONE);
            Json json = new Json(model.getEntity().getContent());
            String url = json.getStr("src");
            String audioName = json.getStr("name");
            if(!StringUtil.isEmpty(audioName) && new File(RecordUtil.AUDOI_DIR+audioName).exists()){
                LogUtil.getInstance().log(TAG, "ok", null);
                viewHolder.ivMySideVoiceIcon.setOnClickListener(new PlayVoiceOnClick());
            }else{
                audioDownLoad(url,audioName);
            }
        }

    }

    /**
     * 设置对方语音聊天显示
     * @param model
     * @param viewHolder
     * @param position
     */
    private void setOtherSideVoiceContent(final MessageModel model, MViewHolder viewHolder,int position) {
        viewHolder.ivOtherSideVoiceIcon.setTag(model);
        Json json = new Json(model.getEntity().getContent());
        String url = json.getStr("src");
        String audioName = json.getStr("name");
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
     * 播放语音聊天
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
            String audioName = new Json(mm.getEntity().getContent()).getStr("name");
            File file = new File(RecordUtil.AUDOI_DIR + audioName);
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
    }

    public void setOtherSideMovieDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideMovieContent(model, viewHolder, position);
    }

    private void setMySideMovieContent(final MessageModel model, final MViewHolder viewHolder,int position) {
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            viewHolder.ivMySideImage.setVisibility(View.VISIBLE);
            String filePath = model.getEntity().getContent();
            final String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
            final String thumbnail = SendUtil.getMovieThumbnail(filePath);
            ImagePool.getInstance(_context).load("file://" + thumbnail, viewHolder.ivMySideImage, R.drawable.file_loading);
            final RoundProgressBar pb = viewHolder.pbMySideImageTransfer;
            AsyncMultiPartPost post = AppData.getInstance().getPost(
                    model.getEntity().getContent());
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
                            String content = MessageCrypto.getInstance().encryMovie(MessageFileReturn.getFileUrl(movie.getId()), MessageFileReturn.getFileUrl(image.getId()), 200, 202, filename);
                            final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Video);
                            _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                                @Override
                                public void onSuccess(MessageModel model) {
                                    sendSuccessRefresh();
                                }

                                @Override
                                public void onError(int errorCode, String error) {
                                    ToastUtil.ToastMessage(_context, "发送失败");
                                }
                            });
                        }
                    });
                }
            });
        }else{
            final String thumbnail = new Json(model.getEntity().getContent()).getStr("thumbnail");
            String videoName = new Json(model.getEntity().getContent()).getStr("name");
            final String filePath = Config.CACHE_PATH_MOVIE + videoName;
            final File file = new File(filePath);
            if(file.exists()){
                setVideo(viewHolder.svMySideMovie, file.getAbsolutePath());
            }else{
                viewHolder.ivMySideImage.setVisibility(View.VISIBLE);
                ImagePool.getInstance(_context).load(Urls.User_Messages_File_DownLoad + thumbnail + "?access_token=" + _session.getSessions().getUser().getToken(), viewHolder.ivMySideImage, R.drawable.file_loading);
            }
        }
    }
    /**
     * 设置对方小视频显示
     * @param model
     * @param viewHolder
     * @param position
     */
    private void setOtherSideMovieContent(final MessageModel model, MViewHolder viewHolder,int position) {
        // 设置控件显示
        final String thumbnail = new Json(model.getEntity().getContent()).getStr("thumbnail");
        String videoName = new Json(model.getEntity().getContent()).getStr("name");
        final String filePath = Config.CACHE_PATH_MOVIE + videoName;
        final File file = new File(filePath);
        if(file.exists()){
            setVideo(viewHolder.svOtherSideMovie, file.getAbsolutePath());
        }else{
            viewHolder.ivOtherSideImage.setVisibility(View.VISIBLE);
            viewHolder.llOtherSideImageItem.setVisibility(View.VISIBLE);
            ImagePool.getInstance(_context).load(Urls.User_Messages_File_DownLoad + thumbnail + "?access_token=" + _session.getSessions().getUser().getToken(), viewHolder.ivOtherSideImage, R.drawable.file_loading);
        }
    }

    private void setVideo(final ScalableVideoView videoView,String filePath) {
        try {
            videoView.setVisibility(View.VISIBLE);
            videoView.setDataSource(filePath);
            videoView.setVolume(0, 0);
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

    /********************* 文件 ************************/

    public void setMySideFileDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setMySideCommonInfo(model, viewHolder, position);
        setMySideFileContent(model, viewHolder, position);
    }

    public void setOtherSideFileDisplay(final MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideFileContent(model, viewHolder, position);
    }

    private void setMySideFileContent(final MessageModel model, final MViewHolder viewHolder,int position) {
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            String filePath = model.getEntity().getContent();
            final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            viewHolder.tvMySideFileTitle.setText(fileName);
            setFileTypeImage(viewHolder.ivMySideFileIcon, fileName);
            final File file = new File(model.getEntity().getContent());
            if(file.exists()){
                viewHolder.tvMySideFileSize.setText(StringUtil.getFriendlyFileSize(file.length()));
            }
            viewHolder.tvMySideFileState.setText("0%");
            final ProgressBar pb = viewHolder.pbMySideFile;
            pb.setVisibility(View.VISIBLE);
            AsyncMultiPartPost post = AppData.getInstance().getPost(
                    model.getEntity().getContent());
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
                    FileUtil.fileCopy(model.getEntity().getContent(), Config.CACHE_PATH_FILE + fileName);
                    String content = MessageCrypto.getInstance().encryFile(MessageFileReturn.getFileUrl(fileReturn.getId()), "", 200, 202, fileName, file.length()+"");
                    final MessageModel temp = _session.getMessages().createMessage(content, MessageType.File);
                    _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                        @Override
                        public void onSuccess(MessageModel model) {
                            sendSuccessRefresh();
                        }

                        @Override
                        public void onError(int errorCode, String error) {
                            ToastUtil.ToastMessage(_context, "发送失败");
                        }
                    });
                }
            });
        }else{
            final String url = new Json(model.getEntity().getContent()).getStr("src");
            final String fileName = new Json(model.getEntity().getContent()).getStr("name");
            String fileSize = new Json(model.getEntity().getContent()).getStr("size");
            viewHolder.pbMySideFile.setVisibility(View.GONE);
            viewHolder.tvMySideFileTitle.setText(fileName);
            viewHolder.tvMySideFileState.setText("已发送");
            viewHolder.llMySideItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(Config.CACHE_PATH_FILE + fileName);
                    if (!file.exists()) {
                        AsyncMultiPartGet get = new AsyncMultiPartGet(_session.getSessions().getUser().getToken(), Urls.User_Messages_File_DownLoad + url, Config.CACHE_PATH_FILE, fileName);
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
                        FileUtil.openFile(_context, Config.CACHE_PATH_FILE + fileName);
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
            viewHolder.pbOtherSideFile.setVisibility(View.GONE);
            viewHolder.tvOtherSideFileTitle.setText(fileName);
            File file = new File(Config.CACHE_PATH_FILE+fileName);
             if(!file.exists()){
                 viewHolder.tvOtherSideFileState.setText("未下载");
             }else{
                 viewHolder.tvOtherSideFileState.setText("已下载");
             }
            setFileTypeImage(viewHolder.ivOtherSideFileIcon, fileName);
            viewHolder.tvOtherSideFileSize.setText(StringUtil.getFriendlyFileSize(Long.parseLong(fileSize)));
            viewHolder.llOtherSideItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(Config.CACHE_PATH_FILE+fileName);
                    if(!file.exists()){
                        AsyncMultiPartGet get = new AsyncMultiPartGet(_session.getSessions().getUser().getToken(),Urls.User_Messages_File_DownLoad+url, Config.CACHE_PATH_FILE, fileName);
                        get.execute();
                        get.setCallBack(new AsyncMultiPartGet.CallBack() {
                            @Override
                            public void update(Integer i) {
                                viewHolder.pbOtherSideFile.setVisibility(View.VISIBLE);
                                viewHolder.pbOtherSideFile.setProgress(i);
                                viewHolder.tvOtherSideFileState.setText(i+"%");
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
                    }else{
                        FileUtil.openFile(_context, Config.CACHE_PATH_FILE+fileName);
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
    }

    public void setOtherSideLocationDisplay(MessageModel model, MViewHolder viewHolder,int position) {
        setOtherSideCommonInfo(model, viewHolder, position);
        setOtherSideLocationContent(model, viewHolder, position);
    }

    private void setMySideLocationContent(final MessageModel model, MViewHolder viewHolder,int position) {

        viewHolder.llMySideImageItem.setVisibility(View.VISIBLE);
        viewHolder.ivMySideImage.setVisibility(View.VISIBLE);
        if(MessageEntity.MessageState.storing.toString().equals(model.getEntity().getStatus())){
            final String[] result = model.getEntity().getContent().split(";");//文件路径,纬度,经度,地址
            if(result.length < 1){
                return;
            }
            ImagePool.getInstance(_context).load("file://" + result[0], viewHolder.ivMySideImage, R.drawable.file_loading);
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
                    String content = MessageCrypto.getInstance().encryLocation(MessageFileReturn.getFileUrl(thumbnail.getId()), 80, 100,result[1],result[2]);
                    final MessageModel temp = _session.getMessages().createMessage(content, MessageType.Location);
                    _session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
                        @Override
                        public void onSuccess(MessageModel model) {
                            sendSuccessRefresh();
                        }

                        @Override
                        public void onError(int errorCode, String error) {
                            ToastUtil.ToastMessage(_context, "发送失败");
                        }
                    });
                }
            });
        }else{
            String thumbnail = new Json(model.getEntity().getContent()).getStr("thumbnail");
            ImagePool.getInstance(_context).load(Urls.User_Messages_File_DownLoad + thumbnail + "?access_token=" + _session.getSessions().getUser().getToken(), viewHolder.ivMySideImage, R.drawable.file_loading);
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
        String thumbnail = new Json(model.getEntity().getContent()).getStr("thumbnail");
        ImagePool.getInstance(_context).load(Urls.User_Messages_File_DownLoad + thumbnail + "?access_token=" + _session.getSessions().getUser().getToken(), viewHolder.ivOtherSideImage, R.drawable.file_loading);
        viewHolder.llOtherSideImageItem.setVisibility(View.VISIBLE);
        bindOpenLocationEvent(model, viewHolder, position);
    }

}
