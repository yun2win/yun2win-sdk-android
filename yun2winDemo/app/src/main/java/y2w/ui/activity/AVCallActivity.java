package y2w.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.y2w.av.lib.AVBack;
import com.y2w.av.lib.AVClient;
import com.y2w.av.lib.AVMember;
import com.y2w.av.lib.Channel;
import com.y2w.uikit.customcontrols.imageview.CircleImageView;
import com.y2w.uikit.utils.ImagePool;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;
import com.yun2win.utils.LogUtil;
import com.yun2win.demo.R;
import org.webrtc.EglBase;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import y2w.Bridge.CmdBuilder;
import y2w.common.Config;
import y2w.manage.CurrentUser;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Session;
import y2w.model.User;
import y2w.service.Back;
import y2w.ui.dialog.AvDialog;
import y2w.ui.widget.videocall.AVMemberView;

import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by maa2 on 2016/4/28.
 */
public class AVCallActivity extends Activity{

    private String TAG = AVCallActivity.class.getSimpleName();
    private static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO", "android.permission.INTERNET" };

    private AVClient avClient;
    private Channel avChannel;
    private Context context;
    private SurfaceViewRenderer svr_video;
    private EglBase rootEglBase;
    private HorizontalScrollView hs_preview;
    private List<SortModel> modelList;
    private RelativeLayout rl_av_offOn;
    private RelativeLayout rl_av_member;
    private LinearLayout ll_av_member;
    private LinearLayout ll_member_me;
    private ImageView iv_av_bg;
    private ImageView iv_av_on;
    private ImageView iv_av_off;
    private ImageView iv_av_more;
    private ImageView iv_av_min;
    private TextView tv_av_notice;
    private ImageView iv_av_callType;
    private ImageView iv_av_off_middle;
    private String type = "";
    private String sessionId = "";
    private String sessionName = "";
    private String otherSideId = "";
    private String otherSideName = "";
    private String memberIds;

    private String curViewerId = "";
    private String curTrackType = "";
    private AVMemberView curMemberView;
    private AVMember curBigMember;
    private VideoRenderer localRenderer;
    private VideoRenderer remoteRenderer;
    private RelativeLayout rl_av_header;
    private CircleImageView civ_header;
    private AvDialog avDialog;
    private String channelId;
    private String chatType;
    private String callType;

    private CurrentUser currentUser;
    private Session _session;
    private Map<String,AVMember> avMemberList = new HashMap<String,AVMember>();
    private Map<String,AVMember> avMemberScreenList = new HashMap<String,AVMember>();

    class Oper{
        public final static int OpenVideo = 1;
        public final static int OpenAudio = 2;
        public final static int OpenVideoOk = 3;
        public final static int OpenAudioOk = 4;
        public final static int RefreshMember = 5;
        public final static int Toast = 99;
    }
    class MemberOper{
        public final static int Join = 10;
        public final static int OpenVideo = 11;
        public final static int OpenAudio = 12;
        public final static int OpenScreen = 13;
        public final static int CloseVideo = 14;
        public final static int CloseAudio = 15;
        public final static int CloseScreen = 16;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Oper.OpenVideo://开启视频
                    avChannel.openVideo();
                    break;
                case Oper.OpenAudio://开启音频
                    avChannel.openAudio();
                    break;
                case Oper.OpenVideoOk://开启视频成功
                    stopPlay();
                    openVideoOk();
                    break;
                case Oper.OpenAudioOk://开启音频成功
                    stopPlay();
                    openAudioOk();
                    break;
                case Oper.RefreshMember://成员变更
                    refreshMemberViews();
                    break;
                case Oper.Toast://提示
                    String toast = (String) msg.obj;
                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_avcall);
        context = this;
        currentUser = Users.getInstance().getCurrentUser();
        initControls(this.getIntent().getExtras());

        init();
        startPlay();
        syncSession();
        if(callType.equals(EnumManage.AvCallType.video.toString())){
            if(type.equals(EnumManage.AvType.launch.toString())){
                if(chatType.equals(EnumManage.SessionType.group.toString())){
                    iv_av_bg.setVisibility(View.GONE);
                    ImagePool.getInstance(context).load(currentUser.getEntity().getAvatarUrl(),
                            civ_header, R.drawable.circle_image_transparent);
                    tv_av_notice.setText("视频开启中...");
                    createChannel();
                }else{
                    iv_av_bg.setVisibility(View.GONE);
                    Users.getInstance().getUser(otherSideId, new Back.Result<User>() {
                        @Override
                        public void onSuccess(User user) {
                            String url = user.getEntity().getAvatarUrl().contains("http") ? user.getEntity().getAvatarUrl() : Config.Host_Port + "/" + user.getEntity().getAvatarUrl();
                            ImagePool.getInstance(context).load(url,civ_header, R.drawable.circle_image_transparent);
                        }

                        @Override
                        public void onError(int code, String error) {

                        }
                    });
                    tv_av_notice.setText("视频开启中...");
                    createChannel();
                }
            }else if(type.equals(EnumManage.AvType.agree.toString())){
                iv_av_bg.setVisibility(View.VISIBLE);
                rl_av_offOn.setVisibility(View.VISIBLE);
                tv_av_notice.setVisibility(View.GONE);
                svr_video.setVisibility(View.VISIBLE);
                rl_av_member.setVisibility(View.VISIBLE);
                iv_av_off_middle.setVisibility(View.GONE);
                tv_av_notice.setText("邀请您进行视频通话");
                iv_av_callType.setBackgroundResource(R.drawable.av_calltype_video);
            }
        }else{
            if(type.equals(EnumManage.AvType.launch.toString())){
                if(chatType.equals(EnumManage.SessionType.group.toString())){
                    iv_av_bg.setVisibility(View.GONE);
                    ImagePool.getInstance(context).load(currentUser.getEntity().getAvatarUrl(),
                            civ_header, R.drawable.circle_image_transparent);
                    tv_av_notice.setText("语音开启中...");
                    createChannel();
                }else{
                    iv_av_bg.setVisibility(View.GONE);
                    Users.getInstance().getUser(otherSideId, new Back.Result<User>() {
                        @Override
                        public void onSuccess(User user) {
                            String url = user.getEntity().getAvatarUrl().contains("http")?user.getEntity().getAvatarUrl() : Config.Host_Port + "/" + user.getEntity().getAvatarUrl();
                            ImagePool.getInstance(context).load(url,civ_header, R.drawable.circle_image_transparent);
                        }

                        @Override
                        public void onError(int code, String error) {

                        }
                    });
                    tv_av_notice.setText("语音开启中...");
                    createChannel();
                }

            }else if(type.equals(EnumManage.AvType.agree.toString())){
                iv_av_bg.setVisibility(View.VISIBLE);
                rl_av_offOn.setVisibility(View.VISIBLE);
                tv_av_notice.setVisibility(View.GONE);
                svr_video.setVisibility(View.VISIBLE);
                rl_av_member.setVisibility(View.VISIBLE);
                iv_av_off_middle.setVisibility(View.GONE);
                tv_av_notice.setText("邀请您进行语音通话");
            }
            iv_av_callType.setBackgroundResource(R.drawable.av_calltype_audio);
        }

    }


    private void initControls(Bundle bundle){
        type = bundle.getString("type", EnumManage.AvType.launch.toString());
        callType = bundle.getString("callType", "");
        chatType = bundle.getString("chatType", "");
        channelId = bundle.getString("channelId", "");
        sessionId = bundle.getString("sessionId", "");
        sessionName = bundle.getString("sessionName", "");
        otherSideId = bundle.getString("otherSideId", "");
        otherSideName = bundle.getString("otherSideName", "");
        memberIds = bundle.getString("memberIds", "");
        svr_video = (SurfaceViewRenderer) findViewById(R.id.svr_video);
        rl_av_offOn = (RelativeLayout) findViewById(R.id.rl_av_offOn);

        iv_av_bg = (ImageView) findViewById(R.id.iv_av_bg);
        iv_av_more = (ImageView) findViewById(R.id.iv_av_more);
        iv_av_min = (ImageView) findViewById(R.id.iv_av_min);
        rl_av_header = (RelativeLayout) findViewById(R.id.rl_av_header);
        civ_header = (CircleImageView) findViewById(R.id.civ_av_header);
        iv_av_on = (ImageView) findViewById(R.id.iv_av_on);
        iv_av_off = (ImageView) findViewById(R.id.iv_av_off);
        tv_av_notice = (TextView) findViewById(R.id.tv_av_notice);
        iv_av_callType = (ImageView) findViewById(R.id.iv_callType_icon);
        iv_av_off_middle = (ImageView) findViewById(R.id.iv_av_handup_middle);
        rl_av_member = (RelativeLayout) findViewById(R.id.rl_av_member);
        ll_av_member = (LinearLayout) findViewById(R.id.ll_av_member);
        ll_member_me = (LinearLayout) findViewById(R.id.ll_member_me);
        iv_av_more.setVisibility(View.GONE);
        iv_av_min.setVisibility(View.GONE);
        // Check for mandatory permissions.
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }

        iv_av_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_av_more.setVisibility(View.GONE);
                AvDialog avDialog = new AvDialog(context,callType);
                avDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        iv_av_more.setVisibility(View.VISIBLE);
                    }
                });
                avDialog.setOnOptionClickListener(new AvDialog.onOptionClickListener() {
                    @Override
                    public void onOptionClick(String option, int position) {
                        rightMenuClick(option,position);
                    }
                });
                avDialog.show();
            }
        });
        iv_av_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avClose();
            }
        });
        iv_av_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChannel(channelId);
            }
        });
        iv_av_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avClose();
            }
        });
        iv_av_off_middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avClose();
            }
        });
    }

    private void init(){
        rootEglBase =  EglBase.create();
        svr_video.init(rootEglBase.getEglBaseContext(), null);
        svr_video.setZOrderMediaOverlay(false);
    }


    private void syncSession(){
        currentUser.getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
            @Override
            public void onSuccess(Session session) {
                _session = session;
            }

            @Override
            public void onError(int Code, String error) {
                noticeShow("创建频道失败");
            }
        });
    }

    private synchronized void refreshMemberViews(){
        if(avChannel == null){
            return ;
        }
        if(ll_av_member != null){
            ll_av_member.removeAllViews();
        }else{
            return;
        }
        if(chatType.equals(EnumManage.SessionType.group.toString())){
            if(StringUtil.isEmpty(curViewerId)){
                AVMember me = avMemberList.get(currentUser.getEntity().getId());
                if(me != null && me.getVideoTrack() != null){
                    localRenderer = new VideoRenderer(svr_video);
                    me.getVideoTrack().addRenderer(localRenderer);
                    curViewerId = currentUser.getEntity().getId();
                }
                curTrackType = AVMemberView.TrackType.video.toString();
                curBigMember = me;
            }
            for(Map.Entry<String,AVMember> entry : avMemberList.entrySet()){
                AVMember avMember = entry.getValue();
                if(avMember.getScreenTrack() != null){
                    avMemberScreenList.put(avMember.getUid(),avMember);
                }
            }
            for(Map.Entry<String,AVMember> entry : avMemberScreenList.entrySet()){//屏幕共享
                AVMember avMember = entry.getValue();
                AVMemberView avMemberView = new AVMemberView(context,rootEglBase,avMember,AVMemberView.TrackType.screen.toString());
                if(curViewerId.equals(avMember.getUid()) && curTrackType.equals(avMemberView.getTrackType())){
                    curMemberView = avMemberView;
                    avMemberView.getViewHolder().getRl_bg().setBackgroundResource(R.drawable.bg_av_member_view_selected);
                }else{
                    avMemberView.getViewHolder().getRl_bg().setBackgroundResource(R.drawable.bg_av_member_view_normal);
                }
                avMemberView.setOnMemberViewClickListener(new AVMemberView.OnMemberViewClickListener() {
                    @Override
                    public void itemClick(AVMemberView memberView) {
                        curViewChange(memberView);
                    }
                });
                ll_av_member.addView(avMemberView.getView());
            }

            for(Map.Entry<String,AVMember> entry : avMemberList.entrySet()){//视频
                AVMember avMember = entry.getValue();
                AVMemberView avMemberView = new AVMemberView(context,rootEglBase,avMember,AVMemberView.TrackType.video.toString());
                if(StringUtil.isEmpty(curViewerId)){
                    curViewerId = currentUser.getEntity().getId();
                }
                if(curViewerId.equals(avMember.getUid())){
                    curMemberView = avMemberView;
                    avMemberView.getViewHolder().getRl_bg().setBackgroundResource(R.drawable.bg_av_member_view_selected);
                }else{
                    avMemberView.getViewHolder().getRl_bg().setBackgroundResource(R.drawable.bg_av_member_view_normal);
                }
                avMemberView.setOnMemberViewClickListener(new AVMemberView.OnMemberViewClickListener() {
                    @Override
                    public void itemClick(AVMemberView memberView) {
                        curViewChange(memberView);
                    }
                });
                ll_av_member.addView(avMemberView.getView());
            }
        }else{
            if(StringUtil.isEmpty(curViewerId)){
                AVMember me = avMemberList.get(currentUser.getEntity().getId());
                curViewerId = currentUser.getEntity().getId();
                if(me != null && me.getVideoTrack() != null){
                    AVMemberView avMemberView = new AVMemberView(context,rootEglBase,me,AVMemberView.TrackType.video.toString());
                    curMemberView = avMemberView;
                    avMemberView.getViewHolder().getRl_bg().setBackgroundResource(R.drawable.bg_av_member_view_selected);
                    avMemberView.setOnMemberViewClickListener(new AVMemberView.OnMemberViewClickListener() {
                        @Override
                        public void itemClick(AVMemberView memberView) {
                            //curViewChange(memberView);
                        }
                    });
                    ll_member_me.addView(avMemberView.getView());
                }
            }
            for(Map.Entry<String,AVMember> entry : avMemberList.entrySet()){
                AVMember avMember = entry.getValue();
                if(!currentUser.getEntity().getId().equals(avMember.getUid())){
                    if(avMember.getVideoTrack() != null){
                        localRenderer = new VideoRenderer(svr_video);
                        avMember.getVideoTrack().addRenderer(localRenderer);
                        iv_av_bg.setVisibility(View.GONE);
                        rl_av_header.setVisibility(View.GONE);
                    }
                }
            }
        }

    }

    private void curViewChange(AVMemberView memberView){
        if(curViewerId.equals(memberView.getAvMember().getUid()) && curTrackType.equals(memberView.getTrackType())){
            return ;
        }
       if(memberView.getTrackType().equals(AVMemberView.TrackType.video.toString())){
           if(memberView.getAvMember().getVideoTrack() == null) return;
       }else{
           if(memberView.getAvMember().getScreenTrack() == null) return;
       }
        curViewerId = memberView.getAvMember().getUid();
        memberView.getViewHolder().getRl_bg().setBackgroundResource(R.drawable.bg_av_member_view_selected);
        if(curMemberView != null){
            curMemberView.getViewHolder().getRl_bg().setBackgroundResource(R.drawable.bg_av_member_view_normal);
        }
        if(curTrackType.equals(AVMemberView.TrackType.video.toString())){
            if(curBigMember.getVideoTrack() != null)
            curBigMember.getVideoTrack().removeRenderer(localRenderer);
        }else{
            if(curBigMember.getScreenTrack() != null)
            curBigMember.getScreenTrack().removeRenderer(localRenderer);
        }
        localRenderer = new VideoRenderer(svr_video);
        if(memberView.getTrackType().equals(AVMemberView.TrackType.video.toString())){
            memberView.getAvMember().getVideoTrack().addRenderer(localRenderer);
        }else{
            memberView.getAvMember().getScreenTrack().addRenderer(localRenderer);
        }
        curTrackType = memberView.getTrackType();
        curMemberView = memberView;
        curBigMember = memberView.getAvMember();
    }


    private synchronized void openVideoOk(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(chatType.equals(EnumManage.SessionType.group.toString())){
            iv_av_bg.setVisibility(View.GONE);
            rl_av_offOn.setVisibility(View.GONE);
            tv_av_notice.setVisibility(View.GONE);
            rl_av_header.setVisibility(View.GONE);
            iv_av_callType.setVisibility(View.GONE);
            iv_av_off_middle.setVisibility(View.GONE);
            iv_av_min.setVisibility(View.VISIBLE);
            iv_av_more.setVisibility(View.VISIBLE);
            rl_av_member.setVisibility(View.VISIBLE);
            svr_video.setVisibility(View.VISIBLE);
        }else{
            iv_av_bg.setVisibility(View.VISIBLE);
            rl_av_header.setVisibility(View.VISIBLE);
            svr_video.setVisibility(View.VISIBLE);
        }
        refreshMemberViews();

    }
    private synchronized void openAudioOk(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rl_av_offOn.setVisibility(View.GONE);
        tv_av_notice.setVisibility(View.GONE);
        iv_av_callType.setVisibility(View.VISIBLE);
        iv_av_off_middle.setVisibility(View.GONE);
        svr_video.setVisibility(View.GONE);
        if(chatType.equals(EnumManage.SessionType.group.toString())){
            iv_av_more.setVisibility(View.VISIBLE);
            iv_av_min.setVisibility(View.VISIBLE);
            rl_av_member.setVisibility(View.VISIBLE);
            refreshMemberViews();
        }else{
            iv_av_more.setVisibility(View.GONE);
            iv_av_min.setVisibility(View.GONE);
            rl_av_member.setVisibility(View.GONE);
        }
    }

    private synchronized void closeVideoOk(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        iv_av_bg.setVisibility(View.VISIBLE);
        rl_av_header.setVisibility(View.VISIBLE);
        iv_av_callType.setVisibility(View.VISIBLE);
        svr_video.setVisibility(View.GONE);
    }

    private void memberNotice(String memberIds){
        if(chatType.equals(EnumManage.SessionType.group.toString())){
            memberGroupNotice(memberIds);
        }else{
            memberP2PNotice();
        }
    }
    private void noticeShow(String content){
        Message message = new Message();
        message.what = Oper.Toast;
        message.obj = content;
        handler.sendMessage(message);
    }

    private void memberGroupNotice(String memberIds){
        if(_session == null) {
            return;
        }
        String message = CmdBuilder.AvCall(EnumManage.AvCmdType.groupavcall.toString(),avChannel.getChannelId(),sessionId,currentUser.getEntity().getId(),memberIds,callType);
        Users.getInstance().getCurrentUser().getImBridges().avCallMessage(_session, message, new IMClient.SendCallback() {
            @Override
            public void onReturnCode(int i, IMSession imSession, String s) {

            }
        });
    }

    private void memberP2PNotice(){
        if(_session == null) {
            return;
        }
        String message = CmdBuilder.AvCall(EnumManage.AvCmdType.singleavcall.toString(),avChannel.getChannelId(),sessionId,currentUser.getEntity().getId(),otherSideId+";",callType);
        Users.getInstance().getCurrentUser().getImBridges().avCallMessage(_session, message, new IMClient.SendCallback() {
            @Override
            public void onReturnCode(int i, IMSession imSession, String s) {

            }
        });
    }

    private void rightMenuClick(String option,int position){
        switch (position) {
            case 0:
                avClose();
                Toast.makeText(context, "通话已结束", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                boolean value1 = "开启静音".equals(option) ? true : false;
                avChannel.setMute(value1);
                break;
            case 2:
                boolean value2 = "关闭免提".equals(option) ? false : true;
                avChannel.setSpeaker(value2);
                break;
            case 3:
                if("关摄像头".equals(option)){
                    try {
                        avChannel.closeVideo();
                        closeVideoOk();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    handler.sendEmptyMessage(Oper.OpenVideo);
                }
                break;
            case 4:
                avChannel.switchCamera();
                break;
            case 5:
                Intent intent = new Intent(context, AVMemberSelectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sessionId", sessionId);
                bundle.putString("sessionName", sessionName);
                bundle.putString("memberIds", memberIds);
                bundle.putBoolean("isCreate", false);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
                if(avDialog != null)
                    avDialog.cancel();
                break;
            default:
                break;
        }
    }

    private MediaPlayer player;
    private void startPlay(){
        player  =   new MediaPlayer().create(this,R.raw.chatvideo);
        player.setLooping(true);
        player.start();
    }
    private void stopPlay(){
        if(player!=null&&player.isPlaying()){
            player.stop();
        }
    }

    private void memberAdd(AVMember avMember,int oper){
        if(avMemberList.containsKey(avMember.getUid())){
            AVMember origin = avMemberList.get(avMember.getUid());
            switch (oper){
                case MemberOper.OpenVideo:
                    origin.setVideoTrack(avMember.getVideoTrack());
                    origin.setVideoOpened(true);
                    break;
                case MemberOper.OpenAudio:
                    origin.setAudioOpened(true);
                    break;
                case MemberOper.OpenScreen:
                    origin.setScreenTrack(avMember.getScreenTrack());
                    origin.setScreenOpened(true);
                    break;
                case MemberOper.CloseVideo:
                    origin.setVideoTrack(null);
                    origin.setVideoOpened(false);
                    break;
                case MemberOper.CloseAudio:
                    origin.setAudioOpened(false);
                    break;
                case MemberOper.CloseScreen:
                    origin.setScreenTrack(null);
                    origin.setScreenOpened(false);
                    break;
                default:break;

            }
            avMemberList.remove(avMember.getUid());
            avMemberList.put(avMember.getUid(), origin);
        }else{
            avMemberList.put(avMember.getUid(), avMember);
        }
    }

    private void memberRemove(AVMember avMember){
        if(avMemberList.containsKey(avMember.getUid())){
            avMemberList.remove(avMember.getUid());
        }
    }

    private void createChannel(){
        avClient = new AVClient(currentUser.getEntity().getId(), currentUser.getEntity().getName(), currentUser.getEntity().getAvatarUrl(),currentUser.getImToken().getAccessToken());
        avClient.createChannel(new AVBack.Result<Channel>() {
            @Override
            public void onSuccess(Channel channel) {
                avChannel = channel;
                avChannel.setOnMembersChangedListener(new MembersChangedListener());
                avChannel.setOnVideoListener(new VideoListener());
                avChannel.setOnAudioListener(new AudioListener());
                avChannel.setOnScreenListener(new ScreenListener());
                avChannel.join();//加入频道
                memberNotice(memberIds);
                if(callType.equals(EnumManage.AvCallType.video.toString())){
                    handler.sendEmptyMessage(Oper.OpenVideo);
                    handler.sendEmptyMessage(Oper.OpenAudio);
                }else{
                    handler.sendEmptyMessage(Oper.OpenAudio);
                }

            }

            @Override
            public void onError(Integer integer) {
                noticeShow("创建频道失败");
            }
        });
    }

    private void getChannel(String channelId){
        avClient = new AVClient(currentUser.getEntity().getId(),currentUser.getEntity().getName(), currentUser.getEntity().getAvatarUrl(), currentUser.getImToken().getAccessToken());
        avClient.getChannel(channelId, new AVBack.Result<Channel>() {
            @Override
            public void onSuccess(Channel channel) {
                avChannel = channel;
                avChannel.setOnMembersChangedListener(new MembersChangedListener());
                avChannel.setOnVideoListener(new VideoListener());
                avChannel.setOnAudioListener(new AudioListener());
                avChannel.setOnScreenListener(new ScreenListener());
                avChannel.join();//加入频道
                if (callType.equals(EnumManage.AvCallType.video.toString())) {
                    handler.sendEmptyMessage(Oper.OpenVideo);
                    handler.sendEmptyMessage(Oper.OpenAudio);
                } else {
                    handler.sendEmptyMessage(Oper.OpenAudio);
                }
            }

            @Override
            public void onError(Integer integer) {
                noticeShow("获取频道失败");
            }
        });
    }


    private class MembersChangedListener implements Channel.OnMembersChangedListener{

        @Override
        public void onJoin(AVMember member) {
            memberAdd(member,MemberOper.Join);
            handler.sendEmptyMessage(Oper.RefreshMember);
        }

        @Override
        public void onLeave(AVMember member) {
            memberRemove(member);
            handler.sendEmptyMessage(Oper.RefreshMember);
        }
    }

    private class AudioListener implements Channel.OnAudioListener{

        @Override
        public void onOpen(AVMember member) {
            memberAdd(member,MemberOper.OpenAudio);
            if (currentUser.getEntity().getId().equals(member.getUid())) {
                handler.sendEmptyMessage(Oper.OpenAudioOk);
            } else {
                handler.sendEmptyMessage(Oper.RefreshMember);
            }
        }

        @Override
        public void onClose(AVMember member) {
            memberAdd(member,MemberOper.CloseAudio);
        }

        @Override
        public void OnMute(AVMember member) {

        }

        @Override
        public void onError(int code) {

        }
    }


    private class VideoListener implements Channel.OnVideoListener{

        @Override
        public void onOpen(AVMember member) {
            memberAdd(member,MemberOper.OpenVideo);
            handler.sendEmptyMessage(Oper.OpenVideoOk);
        }

        @Override
        public void onClose(AVMember member) {
            memberAdd(member,MemberOper.CloseVideo);
            handler.sendEmptyMessage(Oper.RefreshMember);
        }

        @Override
        public void onError(int code) {

        }
    }

    private class ScreenListener implements Channel.OnScreenListener{

        @Override
        public void onOpen(AVMember member) {
            memberAdd(member,MemberOper.OpenScreen);
            handler.sendEmptyMessage(Oper.OpenVideoOk);
        }

        @Override
        public void onClose(AVMember member) {
            memberAdd(member,MemberOper.CloseScreen);
            handler.sendEmptyMessage(Oper.RefreshMember);
        }

        @Override
        public void onError(int code) {

        }
    }



    private void avClose() {
        if(ll_av_member != null){
            ll_av_member.removeAllViews();
        }
        stopPlay();
        finish();
    }

    @Override
    protected void onDestroy() {
        if(avChannel != null){
            avChannel.closeAudio();
            avChannel.closeVideo();
            avChannel.leave();
            avChannel = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            avClose();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            avClose();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(data != null) {
                memberIds += data.getExtras().getString("memberIds","");
                memberNotice(data.getExtras().getString("memberIds",""));
                Toast.makeText(context,"邀请已发出,等待加入...",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
