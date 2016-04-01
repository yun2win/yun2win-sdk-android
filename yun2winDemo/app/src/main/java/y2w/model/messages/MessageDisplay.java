package y2w.model.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.utils.LogUtil;

import java.util.List;

import y2w.entities.UserEntity;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.base.AppData;
import y2w.common.AsyncMultiPartPost;
import com.y2w.uikit.customcontrols.view.RoundProgressBar;
import y2w.entities.MessageEntity;
import y2w.entities.SessionEntity;
import y2w.model.MessageModel;
import y2w.model.Session;

import com.y2w.uikit.utils.ImagePool;
import com.y2w.uikit.utils.StringUtil;
import y2w.common.UserInfo;
import y2w.model.SessionMember;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.ui.activity.ContactInfoActivity;

/**
 * 聊天界面，消息展示
 * Created by yangrongfang on 2016/2/23.
 */
public class MessageDisplay {

    private String TAG = MessageDisplay.class.getSimpleName();
    private Context _context;
    private Session _session;
    private List<MessageModel> _models;
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
        setMySideTextContent(model, viewHolder,position);
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
            animation.setDuration(3000);//设置动画持续时间
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

        viewHolder.tvMySideText.setText( MessageCrypto.getInstance().decryText(model.getEntity().getContent()));

    }

    private void setOtherSideTextContent(MessageModel model, MViewHolder viewHolder,int position) {
        viewHolder.tvOtherSideText
                .setBackgroundResource(R.drawable.message_text_otherside_style);
        viewHolder.tvOtherSideText.setText( MessageCrypto.getInstance().decryText(model.getEntity().getContent()));
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
        ImagePool.getInstance(_context).load("file://" + model.getEntity().getContent(), viewHolder.ivMySideImage, R.drawable.file_loading);
        final RoundProgressBar pb = viewHolder.pbMySideImageTransfer;
        AsyncMultiPartPost post = AppData.getInstance().getPost(
                model.getEntity().getContent());
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
                if (pb != null) {
                    pb.setVisibility(View.GONE);
                }
                AppData.getInstance().removePost(model.getEntity().getContent());
                LogUtil.getInstance().log(TAG,param,null);
            }
        });
       /* if (FileUtils.checkFilePathExists(mm.getLocalThumbnailsPath())) {
            ImagePool.getInstance().load("file://" + mm.getLocalThumbnailsPath(), viewHolder.ivMySideImage, R.drawable.file_loading);
        }else{
            ImagePool
                    .getInstance()
                    .load(FileLyyUtils.getDownLoadUrl(currentUserId,
                            mm.getLocation1()), viewHolder.ivMySideImage, R.drawable.file_loading);
        }*/

        bindOpenImageEvent(model, viewHolder, position);

    }

    /**
     * 绑定打开图片事件
     *
     * @param imageView
     * @param mm
     * @param isFromOtherSide
     */
    private int degree = 0;
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
     * @param entity
     * @param viewHolder
     * @param position
     */
    private void setOtherSideImageContent(final MessageModel entity, MViewHolder viewHolder,int position) {
        // 设置控件显示

        viewHolder.ivOtherSideImage.setVisibility(View.VISIBLE);
        /*String url = FileLyyUtils.getDownLoadUrl(getOwnerId(mm),
                mm.getLocation1());

        ImagePool.getInstance(_context).getWithListener(url, new AdapterImageLoadingListener(viewHolder.ivMySideImage), 15, R.drawable.file_loading);*/

        viewHolder.llOtherSideImageItem.setVisibility(View.VISIBLE);
        bindOpenImageEvent(entity, viewHolder, position);
    }

}
