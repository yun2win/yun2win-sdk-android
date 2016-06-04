package y2w.model.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.imageview.CircleImageView;
import com.y2w.uikit.customcontrols.imageview.HeadImageView;
import com.y2w.uikit.customcontrols.movie.ScalableVideoView;
import com.yun2win.demo.R;

import com.y2w.uikit.customcontrols.imageview.RoundAngleImageView;
import com.y2w.uikit.customcontrols.view.RoundProgressBar;

/**
 * 聊天界面布局定义与控件实例化
 * Created by yangrongfang on 2016/2/23.
 */
public class MessageView {

    private Context _context;
    public MessageView(Context context){
        this._context = context;
    }


    private void messageCreateDateInit(MViewHolder viewHolder,View view) {
        viewHolder.tvCreateDate = (TextView) view
                .findViewById(R.id.message_createdate);
        viewHolder.tvCreateDate.setVisibility(View.GONE);
        viewHolder.tvSystemText = (TextView) view
                .findViewById(R.id.message_system);
        viewHolder.tvSystemText.setVisibility(View.GONE);
    }
    /*******************************system***********************************/
    public View systemTextInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_text_system, null);
        messageCreateDateInit(viewHolder, view);
        return view;
    }
    /*******************************text***********************************/
    public View mySideTextInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_text_right, null);
        messageCreateDateInit(viewHolder,view);
        viewHolder.ivMySideSendingError = (ImageView) view
                .findViewById(R.id.iv_myside_sending_error);
        viewHolder.tvMySideText = (TextView) view
                .findViewById(R.id.tv_myside_message_text);
        viewHolder.ivMySideMessageLoading = (ImageView) view
                .findViewById(R.id.iv_myside_message_text_loading);
        viewHolder.iv_myside_icon = (HeadImageView) view
                .findViewById(R.id.iv_myside_icon);
        viewHolder.tvMySideCircleName = (TextView) view
                .findViewById(R.id.tv_myside_image_name);
        viewHolder.ivMySideSendingError.setVisibility(View.GONE);
        viewHolder.ivMySideMessageLoading.setVisibility(View.GONE);
        return view;
    }

    public View otherSideTextInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_text_left, null);
        messageCreateDateInit(viewHolder,view);
        viewHolder.tvOtherSideText = (TextView) view
                .findViewById(R.id.tv_otherside_message_text);
        viewHolder.ivOtherSideIcon = (HeadImageView) view
                .findViewById(R.id.iv_otherside_icon);
        viewHolder.tvOtherSideCircleName= (TextView) view
        .findViewById(R.id.tv_otherside_image_name);
        viewHolder.tvOtherSideName = (TextView) view
                .findViewById(R.id.tv_otherside_name);
        return view;
    }


    /*******************************image***********************************/
    public View mySideImageInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_image_right, null);
        messageCreateDateInit(viewHolder,view);
        viewHolder.llMySideImageItem = (LinearLayout) view
                .findViewById(R.id.myside_message_image_item);
        viewHolder.ivMySideImage = (RoundAngleImageView) view
                .findViewById(R.id.iv_myside_image);
        viewHolder.ivMySideImageOpen = (ImageView) view
                .findViewById(R.id.iv_myside_image_open);
        viewHolder.pbMySideImageTransfer = (RoundProgressBar) view
                .findViewById(R.id.pb_myside_image_transfer);
        viewHolder.iv_myside_icon = (HeadImageView) view
                .findViewById(R.id.iv_myside_icon);
        viewHolder.tvMySideCircleName = (TextView) view
                .findViewById(R.id.tv_myside_image_name);
        viewHolder.ivMySideSendingError = (ImageView) view
                .findViewById(R.id.iv_myside_sending_error);
        viewHolder.ivMySideSendingError.setVisibility(View.GONE);
        viewHolder.pbMySideImageTransfer.setVisibility(View.GONE);
        return view;
    }

    public View otherSideImageInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_image_left, null);
        messageCreateDateInit(viewHolder,view);
        viewHolder.llOtherSideImageItem = (LinearLayout) view
                .findViewById(R.id.otherside_message_image_item);
        viewHolder.ivOtherSideImage = (RoundAngleImageView) view
                .findViewById(R.id.iv_otherside_image);
        viewHolder.ivOtherSideImageOpen = (ImageView) view
                .findViewById(R.id.iv_otherside_image_open);
        viewHolder.ivOtherSideIcon = (HeadImageView) view
                .findViewById(R.id.iv_otherside_icon);
        viewHolder.tvOtherSideCircleName= (TextView) view
                .findViewById(R.id.tv_otherside_image_name);
        viewHolder.tvOtherSideName = (TextView) view
                .findViewById(R.id.tv_otherside_name);
        viewHolder.tvOtherSideMessageTime = (TextView) view
                .findViewById(R.id.tv_otherside_message_text_time);

        return view;
    }

    /*******************************语音*******************************/
    public View mySideVoiceInit(MViewHolder viewHolder) {

        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_voice_right, null);
        messageCreateDateInit(viewHolder,view);
        viewHolder.llMySideVoiceItem = (LinearLayout) view
                .findViewById(R.id.layout_myside_message_voice);
        viewHolder.ivMySideVoice = (ImageView) view
                .findViewById(R.id.iv_myside_message_voice);
        viewHolder.ivMySideVoiceIcon = (ImageView) view
                .findViewById(R.id.iv_myside_message_voice_icon);
        viewHolder.tvMySideVoice = (TextView) view
                .findViewById(R.id.tv_myside_message_voice_text);
        viewHolder.iv_myside_icon = (HeadImageView) view
                .findViewById(R.id.iv_myside_icon);
        viewHolder.tvMySideCircleName = (TextView) view
                .findViewById(R.id.tv_myside_image_name);
        viewHolder.ivMySideMessageLoading = (ImageView) view
                .findViewById(R.id.iv_myside_message_text_loading);
        viewHolder.ivMySideSendingError = (ImageView) view
                .findViewById(R.id.iv_myside_sending_error);
        viewHolder.ivMySideSendingError.setVisibility(View.GONE);

        return view;
    }

    public View otherSideVoiceInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_voice_left, null);
        messageCreateDateInit(viewHolder,view);
        viewHolder.llOtherSideVoiceItem = (LinearLayout) view
                .findViewById(R.id.layout_otherside_message_voice);
        viewHolder.ivOtherSideVoice = (ImageView) view
                .findViewById(R.id.iv_otherside_message_voice);
        viewHolder.ivOtherSideVoiceIcon = (ImageView) view
                .findViewById(R.id.iv_otherside_message_voice_icon);
        viewHolder.tvOtherSideVoice = (TextView) view
                .findViewById(R.id.tv_otherside_message_voice_text);
        viewHolder.ivOtherSideIcon = (HeadImageView) view
                .findViewById(R.id.iv_otherside_icon);
        viewHolder.tvOtherSideCircleName = (TextView) view.findViewById(R.id.tv_otherside_image_name);
        viewHolder.tvOtherSideName = (TextView) view
                .findViewById(R.id.tv_otherside_name);
        viewHolder.tvOtherSideMessageTime = (TextView) view
                .findViewById(R.id.tv_otherside_message_text_time);
        return view;
    }


    /*******************************movie***********************************/
    public View mySideMovieInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_movie_right, null);
        messageCreateDateInit(viewHolder,view);
        viewHolder.llMySideImageItem = (LinearLayout) view
                .findViewById(R.id.myside_message_image_item);
        viewHolder.ivMySideImage = (RoundAngleImageView) view
                .findViewById(R.id.iv_myside_image);
        viewHolder.svMySideMovie = (ScalableVideoView) view.findViewById(R.id.sv_myside_movie);
        viewHolder.ivMySideImageOpen = (ImageView) view
                .findViewById(R.id.iv_myside_image_open);
        viewHolder.pbMySideImageTransfer = (RoundProgressBar) view
                .findViewById(R.id.pb_myside_image_transfer);
        viewHolder.iv_myside_icon = (HeadImageView) view
                .findViewById(R.id.iv_myside_icon);
        viewHolder.tvMySideCircleName = (TextView) view
                .findViewById(R.id.tv_myside_image_name);
        viewHolder.ivMySideSendingError = (ImageView) view
                .findViewById(R.id.iv_myside_sending_error);
        viewHolder.ivMySideSendingError.setVisibility(View.GONE);
        viewHolder.pbMySideImageTransfer.setVisibility(View.GONE);
        viewHolder.svMySideMovie.setVisibility(View.GONE);
        viewHolder.ivMySideImage.setVisibility(View.GONE);
        return view;
    }

    public View otherSideMovieInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_movie_left, null);
        messageCreateDateInit(viewHolder,view);
        viewHolder.llOtherSideImageItem = (LinearLayout) view
                .findViewById(R.id.otherside_message_image_item);
        viewHolder.ivOtherSideImage = (RoundAngleImageView) view
                .findViewById(R.id.iv_otherside_image);
        viewHolder.svOtherSideMovie = (ScalableVideoView) view.findViewById(R.id.sv_otherside_movie);
        viewHolder.ivOtherSideImageOpen = (ImageView) view
                .findViewById(R.id.iv_otherside_image_open);
        viewHolder.ivOtherSideIcon = (HeadImageView) view
                .findViewById(R.id.iv_otherside_icon);
        viewHolder.tvOtherSideCircleName= (TextView) view
                .findViewById(R.id.tv_otherside_image_name);
        viewHolder.tvOtherSideName = (TextView) view
                .findViewById(R.id.tv_otherside_name);
        viewHolder.tvOtherSideMessageTime = (TextView) view
                .findViewById(R.id.tv_otherside_message_text_time);
        viewHolder.svOtherSideMovie.setVisibility(View.GONE);
        viewHolder.ivOtherSideImage.setVisibility(View.GONE);

        return view;
    }

    /*******************************file***********************************/
    public View mySideFileInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_file_right, null);
        messageCreateDateInit(viewHolder, view);
        viewHolder.llMySideItem = (LinearLayout) view.findViewById(R.id.myside_message_item);
        viewHolder.iv_myside_icon = (HeadImageView) view
                .findViewById(R.id.iv_myside_icon);
        viewHolder.tvMySideCircleName= (TextView) view
                .findViewById(R.id.tv_myside_image_name);
        viewHolder.ivMySideFileIcon = (ImageView) view
                .findViewById(R.id.iv_myside_file_icon);
        viewHolder.tvMySideFileTitle = (TextView) view
                .findViewById(R.id.tv_myside_file_title);
        viewHolder.tvMySideFileSize = (TextView) view
                .findViewById(R.id.tv_myside_file_size);
        viewHolder.tvMySideFileState = (TextView) view
                .findViewById(R.id.tv_myside_file_state);
        viewHolder.ivMySideSendingError = (ImageView) view
                .findViewById(R.id.iv_myside_sending_error);
        viewHolder.pbMySideFile = (ProgressBar) view.findViewById(R.id.pb_myside_file);
        return view;
    }

    public View otherSideFileInit(MViewHolder viewHolder) {
        View view = LayoutInflater.from(_context).inflate(
                R.layout.message_view_type_file_left, null);
        messageCreateDateInit(viewHolder,view);
        viewHolder.llOtherSideItem = (LinearLayout) view
                .findViewById(R.id.otherside_message_item);
        viewHolder.ivOtherSideIcon = (HeadImageView) view
                .findViewById(R.id.iv_otherside_icon);
        viewHolder.tvOtherSideCircleName= (TextView) view
                .findViewById(R.id.tv_otherside_image_name);
        viewHolder.tvOtherSideName= (TextView) view
                .findViewById(R.id.tv_otherside_name);
        viewHolder.ivOtherSideFileIcon = (ImageView) view
                .findViewById(R.id.iv_otherside_file_icon);
        viewHolder.tvOtherSideFileTitle = (TextView) view
                .findViewById(R.id.tv_otherside_file_title);
        viewHolder.tvOtherSideFileSize = (TextView) view
                .findViewById(R.id.tv_otherside_file_size);
        viewHolder.tvOtherSideFileState = (TextView) view
                .findViewById(R.id.tv_otherside_file_state);
        viewHolder.pbOtherSideFile = (ProgressBar) view.findViewById(R.id.pb_otherside_file);
        return view;
    }
}
