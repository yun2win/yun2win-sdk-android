package y2w.model.messages;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.imageview.HeadImageView;

import com.y2w.uikit.customcontrols.imageview.RoundAngleImageView;
import com.y2w.uikit.customcontrols.movie.ScalableVideoView;
import com.y2w.uikit.customcontrols.view.RoundProgressBar;

/**
 * 聊天界面，消息展示所有控件集合
 * Created by yangrongfang on 2016/2/23.
 */
public class MViewHolder {

    TextView tvCreateDate;
    TextView tvSystemText;
    // 我方 失败
    ImageView ivMySideSendingError;

    // *******************对方**********************//
    LinearLayout rlOtherSideLayout;
    RelativeLayout rlLayoutItem;
    // 头像
    HeadImageView ivOtherSideIcon;
    TextView tvOtherSideCircleName;
    // 姓名
    TextView tvOtherSideName;
    // 时间
    TextView tvOtherSideMessageTime;

    /******* 文本 ********/
    TextView tvOtherSideText;

    /******* 会议、合同等其他对象控件 ********/
    LinearLayout llOtherSideItem;
    TextView tvOtherSideItemId;
    TextView tvOtherSideItemTitle;
    ImageView ivOtherSideItemIcon;
    TextView tvOtherSideItemContent;

    /******* 图片、视频控件 ********/
    LinearLayout llOtherSideImageItem;
    RoundAngleImageView ivOtherSideImage;
    ScalableVideoView svOtherSideMovie;
    ImageView ivOtherSideImageOpen;
    ProgressBar pbOtherSideImageTransfer;

    /******* 帖图 ********/
    ImageView ivOtherSidePinup;

    /******* 文件 ********/
    ImageView ivOtherSideFileIcon;
    TextView tvOtherSideFileTitle;
    TextView tvOtherSideFileSize;
    TextView tvOtherSideFileState;
    ProgressBar pbOtherSideFile;

    /******* 音频文件 ********/
    LinearLayout llOtherSideAudioFileItem;
    ImageView ivOtherSideAudioFileIcon;
    TextView tvOtherSideAudioFileTitle;
    TextView tvOtherSideAudioFileDescLeft;
    TextView tvOtherSideAudioFileDescRight;
    ProgressBar pbOtherSideAudioFileTransfer;

    /******* 聊天语音对方 ********/
    LinearLayout llOtherSideVoiceItem;
    ImageView ivOtherSideVoice;
    ImageView ivOtherSideVoiceIcon;
    TextView tvOtherSideVoice;

    /******* 一对一语音视频 ********/
    LinearLayout llOtherSideOneVideoItem;
    ImageView ivOtherSideOneVideoIcon;
    TextView tvOtherSideOneVideo;



    // *******************我方**********************//
    LinearLayout rlMySideLayout;
    // 头像
    HeadImageView iv_myside_icon;
    TextView tvMySideCircleName;
    // 时间
    ImageView ivMySideMessageLoading;

    /******* 文本 ********/
    TextView tvMySideText;

    /******* 图片、视频控件 ********/
    LinearLayout llMySideImageItem;
    RoundAngleImageView ivMySideImage;
    ImageView ivMySideImageOpen;
    ScalableVideoView svMySideMovie;
    RoundProgressBar pbMySideImageTransfer;

    /******* 帖图 ********/
    ImageView ivMySidePinup;

    /******* 文件 ********/
    LinearLayout llMySideItem;
    ImageView ivMySideFileIcon;
    TextView tvMySideFileTitle;
    TextView tvMySideFileSize;
    TextView tvMySideFileState;
    ProgressBar pbMySideFile;

    /******* 音频文件 ********/
    LinearLayout llMySideAudioFileItem;
    ImageView ivMySideAudioFileIcon;
    TextView tvMySideAudioFileTitle;
    TextView tvMySideAudioFileDescLeft;
    TextView tvMySideAudioFileDescRight;
    ProgressBar pbMySideAudioFileTransfer;

    /******* 聊天语音我方 ********/
    LinearLayout llMySideVoiceItem;
    ImageView ivMySideVoice;
    ImageView ivMySideVoiceIcon;
    TextView tvMySideVoice;

    /******* 一对一语音视频 ********/
    LinearLayout llMySideOneVideoItem;
    ImageView ivMySideOneVideoIcon;
    TextView tvMySideOneVideo;

    // *********************我方,对方通知标签*********************//
    TextView tv_message_noticelabel;
    TextView tv_noticelabel_time;
}
