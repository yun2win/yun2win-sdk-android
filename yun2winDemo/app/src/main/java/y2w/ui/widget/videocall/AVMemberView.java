package y2w.ui.widget.videocall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.y2w.av.lib.AVMember;
import com.yun2win.demo.R;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

/**
 * Created by maa2 on 2016/5/21.
 */
public class AVMemberView {

    private View convertView;
    private ViewHolder viewHolder;
    private Context context;
    private EglBase rootEglBase;
    private AVMemberView avMemberView;
    private AVMember avMember;
    private String trackType;
    private OnMemberViewClickListener onMemberViewClickListener;

    public static enum TrackType{
        video,screen
    }

    public AVMemberView(final Context context,EglBase rootEglBase,AVMember avMember, String trackType){
        this.context = context;
        this.rootEglBase = rootEglBase;
        this.avMember = avMember;
        this.trackType = trackType;
        convertView = LayoutInflater.from(context).inflate(R.layout.avcall_member_preview_item, null);
        viewHolder = new ViewHolder();
        viewHolder.sfv_video= (SurfaceViewRenderer)convertView.findViewById(R.id.svr_video_item);
        viewHolder.iv_header = (ImageView) convertView.findViewById(R.id.iv_av_member_avatar);
        viewHolder.rl_bg = (RelativeLayout) convertView.findViewById(R.id.rl_bg);
        viewHolder.sfv_video.init(rootEglBase.getEglBaseContext(), null);
        viewHolder.sfv_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        viewHolder.sfv_video.setMirror(false);
        viewHolder.sfv_video.setZOrderMediaOverlay(true);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMemberViewClickListener != null)
                    onMemberViewClickListener.itemClick(avMemberView);
            }
        });
        avMemberView = this;
        setRenderer();
    }

    private void setRenderer(){
        if(TrackType.video.toString().equals(trackType)){
            if(avMember.getVideoTrack() != null){
                VideoRenderer localRender = new VideoRenderer(viewHolder.sfv_video);
                avMember.getVideoTrack().addRenderer(localRender);
                viewHolder.iv_header.setVisibility(View.GONE);
            }else{
            /*ImagePool.getInstance().load(
                    viewHolder.iv_header, R.drawable.circle_image_transparent);*/
                viewHolder.iv_header.setVisibility(View.VISIBLE);
            }
        }else{
            if(avMember.getScreenTrack() != null){
                VideoRenderer localRender = new VideoRenderer(viewHolder.sfv_video);
                avMember.getScreenTrack().addRenderer(localRender);
                viewHolder.iv_header.setVisibility(View.GONE);
            }else{
            /*ImagePool.getInstance().load(
                    viewHolder.iv_header, R.drawable.circle_image_transparent);*/
                viewHolder.iv_header.setVisibility(View.VISIBLE);
            }
        }

    }

    public View getView(){
        return convertView;
    }

    public AVMember getAvMember() {
        return avMember;
    }

    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    public String getTrackType() {
        return trackType;
    }

    public class ViewHolder {
        private SurfaceViewRenderer sfv_video;
        public RelativeLayout rl_bg;
        private ImageView iv_header;

        public RelativeLayout getRl_bg() {
            return rl_bg;
        }
    }

    public void setOnMemberViewClickListener(OnMemberViewClickListener onMemberViewClickListener) {
        this.onMemberViewClickListener = onMemberViewClickListener;
    }

    public static interface OnMemberViewClickListener{
        public void itemClick(AVMemberView memberView);
    }
}
