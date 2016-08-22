package y2w.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;


import com.y2w.av.base.AVJson;
import com.y2w.av.lib.AVBack;
import com.y2w.av.lib.AVClient;
import com.y2w.av.lib.AVMember;
import com.y2w.av.lib.Channel;
import com.y2w.av.wblib.Utils.BitmapUtils;
import com.y2w.av.wblib.Utils.ScreenUtils;
import com.y2w.av.wblib.bean.StrokeRecord;
import com.y2w.av.wblib.ui.WhiteBoardView;
import com.y2w.uikit.customcontrols.imageview.CircleImageView;
import com.y2w.uikit.customcontrols.view.WBHorizontalScrollView;
import com.y2w.uikit.utils.ImagePool;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;
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
import y2w.model.sission.Container;
import y2w.service.Back;
import y2w.ui.dialog.AvDialog;
import y2w.ui.widget.videocall.AVMemberView;

import android.widget.ScrollView;
import android.widget.SeekBar;
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
    private List<SortModel> modelList;
    private RelativeLayout rl_av_offOn;
    private RelativeLayout rl_av_member;
    private LinearLayout ll_av_member;
    private RelativeLayout rl_whiteboard;
    private RelativeLayout rl_location;
    private RelativeLayout rl_base;
    private ImageView iv_Location;
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
    private AVMember curWhiteBoard;
    private WhiteBoardView curWhiteBoardView;
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

    private PopupWindow strokePopupWindow, eraserPopupWindow, textPopupWindow;//画笔、橡皮擦参数设置弹窗实例
    private View popupStrokeLayout, popupEraserLayout, popupTextLayout;//画笔、橡皮擦弹窗布局
    int pupWindowsDPWidth = 300;//弹窗宽度，单位DP
    int strokePupWindowsDPHeight = 275;//画笔弹窗高度，单位DP
    int eraserPupWindowsDPHeight = 90;//橡皮擦弹窗高度，单位DP

    RadioGroup strokeTypeRG, strokeColorRG;
    private SeekBar strokeSeekBar, strokeAlphaSeekBar, eraserSeekBar;
    private ImageView strokeImageView, strokeAlphaImage, eraserImageView;//画笔宽度，画笔不透明度，橡皮擦宽度IV
    private ImageView iv_wb_draw,iv_wb_settings,iv_wb_eraser,iv_wb_catch,iv_wb_text;//白板工具
    private ImageView iv_location_icon;

    final String COLOR_BLACK = "000000";
    final String COLOR_RED = "ff4444";
    final String COLOR_GREEN = "99cc00";
    final String COLOR_ORANGE = "ffbb33";
    final String COLOR_BLUE = "33b5e5";

    int strokeMode;//模式
    int strokeType;//模式
    public static int curViewHeight;
    int keyboardHeight;
    int textOffX;
    int textOffY;
    private EditText strokeET;//绘制文字的内容

    private int lastX,lastY;
    private int lastL,lastT;

    private int size;

    private float mLastX;

    private WebView webView;

    class Oper{
        public final static int OpenVideo = 1;
        public final static int OpenAudio = 2;
        public final static int OpenVideoOk = 3;
        public final static int OpenAudioOk = 4;
        public final static int RefreshMember = 5;
        public final static int OpenWhiteBoard = 6;
        public final static int OpenWhiteBoardOk = 7;
        public final static int CloseWhiteBoard = 8;
        public final static int WhiteBoardDocument = 9;
        public final static int Toast = 99;
    }
    class MemberOper{
        public final static int Join = 10;
        public final static int OpenVideo = 11;
        public final static int CloseVideo = 12;
        public final static int OpenAudio = 13;
        public final static int CloseAudio = 14;
        public final static int OpenScreen = 15;
        public final static int CloseScreen = 16;
        public final static int OpenWhiteBoard = 17;
        public final static int CloseWhiteBoard = 18;
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
                case Oper.OpenWhiteBoard://开启白板
                    //avChannel.openWhiteBoard();
                    break;
                case Oper.OpenWhiteBoardOk://开启白板成功
                    openWhiteBoardOk();
                    break;
                case Oper.CloseWhiteBoard://关闭白板成功

                    break;
                case Oper.WhiteBoardDocument:
                    Toast.makeText(context, msg.arg1+" : "+msg.arg2, Toast.LENGTH_SHORT).show();
                    refreshWhiteBoard(msg.arg1,msg.arg2);
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
        softKeyBoardInit();
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

    private void softKeyBoardInit(){
        final View rootView = LayoutInflater.from(context).inflate(R.layout.activity_avcall,null);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //下面的代码主要是为了解决软键盘弹出后遮挡住文字录入PopWindow的问题
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);//获取rootView的可视区域
                int screenHeight = rootView.getHeight();//获取rootView的高度
                keyboardHeight = screenHeight - (r.bottom - r.top);//用rootView的高度减去rootView的可视区域高度得到软键盘高度
                if (textOffY > (curViewHeight - keyboardHeight)) {//如果输入焦点出现在软键盘显示的范围内则进行布局上移操作
                    rootView.setTop(-keyboardHeight);//rootView整体上移软键盘高度
                    //更新PopupWindow的位置
                    int x = textOffX;
                    int y = textOffY - curWhiteBoardView.getHeight();
                    textPopupWindow.update(curWhiteBoardView, x, y,
                            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                }
            }
        });
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
        rl_whiteboard = (RelativeLayout) findViewById(R.id.rl_whiteboard);
        rl_base = (RelativeLayout) findViewById(R.id.rl_av_base);
        rl_location = (RelativeLayout) findViewById(R.id.rl_location);
        iv_Location = (ImageView) findViewById(R.id.iv_location);
        iv_location_icon = (ImageView) findViewById(R.id.iv_location_icon);
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

        iv_location_icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action=event.getAction();
                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        lastL = iv_Location.getLeft();
                        lastT = iv_Location.getTop();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;

                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;

                        if (left < iv_Location.getLeft()) {
                            left = iv_Location.getLeft();
                            right = left + v.getWidth();
                        }
                        if (right > iv_Location.getRight()) {
                            right = iv_Location.getRight();
                            left = right - v.getWidth();
                        }
                        if (top < iv_Location.getTop()) {
                            top = iv_Location.getTop();
                            bottom = top + v.getHeight();
                        }
                        if (bottom > iv_Location.getBottom()) {
                            bottom = iv_Location.getBottom();
                            top = bottom - v.getHeight();
                        }
                        v.layout(left, top, right, bottom);
                        Log.i(TAG, "--left,top,right,bottom =" + left + " : " + top + " : " + right + " : " + bottom);
                        if(lastL != left || lastT != top)
                        curWhiteBoardView.location(dx / (float) (iv_Location.getWidth() - iv_location_icon.getWidth()), dy / (float) (iv_Location.getHeight() - iv_location_icon.getHeight()));
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        lastL = left;
                        lastT = top;
                        break;
                    case MotionEvent.ACTION_UP:
                       // noticeShow(webView.getWidth() + " : " + webView.getHeight());
                        break;
                }
                    return true;
            }
        });


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

    private void whiteBoardInit(){
        findView();
        initDrawParams();//初始化绘画参数
        initPopupWindows();//初始化弹框

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

    private void openWhiteBoardOk(){
        if(curWhiteBoard == null){
            Log.i(TAG, "--avMemberList =" + avMemberList.size());
            for(Map.Entry<String,AVMember> entry : avMemberList.entrySet()){
                AVMember avMember = entry.getValue();
                Log.i(TAG, "--avMember.isWhiteBoardOpened() =" +avMember.isWhiteBoardOpened());
                if(avMember.isWhiteBoardOpened()){
                    curWhiteBoard = avMember;
                    curWhiteBoardView = avMember.getWhiteBoardView();
                    curWhiteBoardView.setActivity(this);
                    webView = new WebView(this);
                    Log.i(TAG, "--curWhiteBoardView.getmWidth() getmHeight()=" + curWhiteBoardView.getmWidth() + " : " + curWhiteBoardView.getmHeight());
                    refreshWhiteBoard(curWhiteBoardView.getmWidth(), curWhiteBoardView.getmHeight());
                    break;
                }
            }
        }
    }


    private void refreshWhiteBoard(final int width, final int height){
        if(rl_location.getVisibility() != View.VISIBLE){
            rl_location.setVisibility(View.VISIBLE);
        }
        //webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放
        //webView.getSettings().setSupportZoom(true);
        curWhiteBoardView.setWebView(webView);
        RelativeLayout.LayoutParams webParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        webParams.width = width;
        webParams.height = height;
        rl_whiteboard.addView(webView, webParams);
        rl_whiteboard.addView(curWhiteBoardView);

        curWhiteBoardView.documentShow();

        curWhiteBoardView.setOnDrawChangedListener(new WhiteBoardView.OnDrawChangedListener() {
            @Override
            public void onDrawChanged() {
                //iv_Location.setImageBitmap(getThumbBitmap(iv_av_more));
            }
        });
        whiteBoardInit();
        final ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.wb_action_draw_selected);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = iv_av_more.getLeft()-20;
        params.topMargin = iv_av_more.getBottom() + 40;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_whiteboard.removeView(imageView);
                View view = LayoutInflater.from(context).inflate(R.layout.whiteboard_tool_menu, null);

                iv_wb_draw = (ImageView) view.findViewById(R.id.iv_wb_draw);
                iv_wb_settings = (ImageView) view.findViewById(R.id.iv_wb_settings);
                iv_wb_eraser = (ImageView) view.findViewById(R.id.iv_wb_eraser);
                iv_wb_catch = (ImageView) view.findViewById(R.id.iv_wb_catch);
                iv_wb_text = (ImageView) view.findViewById(R.id.iv_wb_text);

                iv_wb_draw.setOnClickListener(new WbMenuClick());
                iv_wb_settings.setOnClickListener(new WbMenuClick());
                iv_wb_eraser.setOnClickListener(new WbMenuClick());
                iv_wb_catch.setOnClickListener(new WbMenuClick());
                iv_wb_text.setOnClickListener(new WbMenuClick());

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = iv_av_more.getLeft() - 20;
                params.topMargin = iv_av_more.getBottom() + 40;
                rl_whiteboard.addView(view, params);
            }
        });
        rl_whiteboard.addView(imageView, params);

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

    private void initDrawParams() {
        //默认为画笔模式
        strokeMode = StrokeRecord.STROKE_TYPE_DRAW;

        //画笔宽度缩放基准参数
        Drawable circleDrawable = getResources().getDrawable(R.drawable.circle);
        assert circleDrawable != null;
        size = circleDrawable.getIntrinsicWidth();
    }

    private void initPopupWindows() {
        initStrokePop();
        initEraserPop();
        initTextPop();
    }

    private void findView() {
        // popupWindow布局
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity
                .LAYOUT_INFLATER_SERVICE);
        //画笔弹窗布局
        popupStrokeLayout = inflater.inflate(R.layout.popup_sketch_stroke, null);
        strokeImageView = (ImageView) popupStrokeLayout.findViewById(R.id.stroke_circle);
        strokeAlphaImage = (ImageView) popupStrokeLayout.findViewById(R.id.stroke_alpha_circle);
        strokeSeekBar = (SeekBar) (popupStrokeLayout.findViewById(R.id.stroke_seekbar));
        strokeAlphaSeekBar = (SeekBar) (popupStrokeLayout.findViewById(R.id.stroke_alpha_seekbar));
        //画笔颜色
        strokeTypeRG = (RadioGroup) popupStrokeLayout.findViewById(R.id.stroke_type_radio_group);
        strokeColorRG = (RadioGroup) popupStrokeLayout.findViewById(R.id.stroke_color_radio_group);

        //橡皮擦弹窗布局
        popupEraserLayout = inflater.inflate(R.layout.popup_sketch_eraser, null);
        eraserImageView = (ImageView) popupEraserLayout.findViewById(R.id.stroke_circle);
        eraserSeekBar = (SeekBar) (popupEraserLayout.findViewById(R.id.stroke_seekbar));
        //文本录入弹窗布局
        popupTextLayout = inflater.inflate(R.layout.popup_sketch_text, null);
        strokeET = (EditText) popupTextLayout.findViewById(R.id.text_pupwindow_et);
        //getSketchSize();//计算选择图片弹窗的高宽

        curWhiteBoardView.setTextWindowCallback(new WhiteBoardView.TextWindowCallback() {
            @Override
            public void onText(View anchor, StrokeRecord record) {
                textOffX = record.textOffX;
                textOffY = record.textOffY;
                showTextPopupWindow(anchor, record);
            }
        });
    }

    private void initStrokePop() {
        //画笔弹窗
        strokePopupWindow = new PopupWindow(context);
        strokePopupWindow.setContentView(popupStrokeLayout);//设置主体布局
        strokePopupWindow.setWidth(ScreenUtils.dip2px(context, pupWindowsDPWidth));//宽度
//        strokePopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);//高度自适应
        strokePopupWindow.setHeight(ScreenUtils.dip2px(context, strokePupWindowsDPHeight));//高度
        strokePopupWindow.setFocusable(true);
        strokePopupWindow.setBackgroundDrawable(new BitmapDrawable());//设置空白背景
        strokePopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);//动画
        strokeTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int resId = R.drawable.stroke_type_rbtn_draw_checked;
                if (checkedId == R.id.stroke_type_rbtn_draw) {
                    strokeType = StrokeRecord.STROKE_TYPE_DRAW;
                } else if (checkedId == R.id.stroke_type_rbtn_line) {
                    strokeType = StrokeRecord.STROKE_TYPE_LINE;
                    resId = R.drawable.stroke_type_rbtn_line_checked;
                } else if (checkedId == R.id.stroke_type_rbtn_circle) {
                    strokeType = StrokeRecord.STROKE_TYPE_CIRCLE;
                    resId = R.drawable.stroke_type_rbtn_circle_checked;
                } else if (checkedId == R.id.stroke_type_rbtn_rectangle) {
                    strokeType = StrokeRecord.STROKE_TYPE_RECTANGLE;
                    resId = R.drawable.stroke_type_rbtn_rectangle_checked;
                } else if (checkedId == R.id.stroke_type_rbtn_text) {
                    strokeType = StrokeRecord.STROKE_TYPE_TEXT;
                    resId = R.drawable.stroke_type_rbtn_text_checked;
                }
                // btn_stroke.setImageResource(resId);
                curWhiteBoardView.setStrokeType(strokeType);
                strokePopupWindow.dismiss();//切换画笔后隐藏
            }
        });


        strokeColorRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String color = COLOR_BLACK;
                if (checkedId == R.id.stroke_color_black) {
                    color = COLOR_BLACK;
                } else if (checkedId == R.id.stroke_color_red) {
                    color = COLOR_RED;
                } else if (checkedId == R.id.stroke_color_green) {
                    color = COLOR_GREEN;
                } else if (checkedId == R.id.stroke_color_orange) {
                    color = COLOR_ORANGE;
                } else if (checkedId == R.id.stroke_color_blue) {
                    color = COLOR_BLUE;
                }
                Log.i(TAG,"-----strokeColorRG: "+color);
                curWhiteBoardView.setStrokeColor(color);
            }
        });
        //画笔宽度拖动条
        strokeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                setSeekBarProgress(progress, StrokeRecord.STROKE_TYPE_DRAW);
            }
        });
        strokeSeekBar.setProgress(WhiteBoardView.DEFAULT_STROKE_SIZE);
//        strokeColorRG.check(R.id.stroke_color_black);

        //画笔不透明度拖动条
        strokeAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int alpha = (progress * 255) / 100;//百分比转换成256级透明度
                curWhiteBoardView.setStrokeAlpha(alpha);
                strokeAlphaImage.setAlpha(alpha);
            }
        });


        strokeAlphaSeekBar.setProgress(WhiteBoardView.DEFAULT_STROKE_ALPHA);
    }

    private void initTextPop() {
        textPopupWindow = new PopupWindow(context);
        textPopupWindow.setContentView(popupTextLayout);
        textPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);//宽度200dp
        textPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);//高度自适应
        textPopupWindow.setFocusable(true);
        textPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        textPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        textPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!strokeET.getText().toString().equals("")) {
                    StrokeRecord record = new StrokeRecord(strokeType);
                    record.text = strokeET.getText().toString();
                }
            }
        });
    }

    private void initEraserPop() {
        //橡皮擦弹窗
        eraserPopupWindow = new PopupWindow(context);
        eraserPopupWindow.setContentView(popupEraserLayout);//设置主体布局
        eraserPopupWindow.setWidth(ScreenUtils.dip2px(context, pupWindowsDPWidth));//宽度200dp
//        eraserPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);//高度自适应
        eraserPopupWindow.setHeight(ScreenUtils.dip2px(context, eraserPupWindowsDPHeight));//高度自适应
        eraserPopupWindow.setFocusable(true);
        eraserPopupWindow.setBackgroundDrawable(new BitmapDrawable());//设置空白背景
        eraserPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);//动画
        //橡皮擦宽度拖动条
        eraserSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                setSeekBarProgress(progress, StrokeRecord.STROKE_TYPE_ERASER);
            }
        });
        eraserSeekBar.setProgress(WhiteBoardView.DEFAULT_ERASER_SIZE);
    }

    private void showTextPopupWindow(View anchor, final StrokeRecord record) {
        strokeET.requestFocus();
        textPopupWindow.showAsDropDown(anchor, record.textOffX, record.textOffY - curWhiteBoardView.getHeight());
        textPopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        textPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!strokeET.getText().toString().equals("")) {
                    record.text = strokeET.getText().toString();
                    record.textPaint.setTextSize(strokeET.getTextSize());
                    record.textWidth = strokeET.getMaxWidth();
                    curWhiteBoardView.addStrokeRecord(record);
                }
            }
        });
    }


    protected void setSeekBarProgress(int progress, int drawMode) {
        int calcProgress = progress > 1 ? progress : 1;
        int newSize = Math.round((size / 100f) * calcProgress);
        int offset = Math.round((size - newSize) / 2);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(newSize, newSize);
        lp.setMargins(offset, offset, offset, offset);
        if (drawMode == StrokeRecord.STROKE_TYPE_DRAW) {
            strokeImageView.setLayoutParams(lp);
        } else {
            eraserImageView.setLayoutParams(lp);
        }
        curWhiteBoardView.setSize(newSize, drawMode);
    }

    private void showParamsPopupWindow(View anchor, int drawMode) {
        if (BitmapUtils.isLandScreen(context)) {
            if (drawMode == StrokeRecord.STROKE_TYPE_DRAW) {
                strokePopupWindow.showAsDropDown(anchor, ScreenUtils.dip2px(context,-pupWindowsDPWidth), -anchor.getHeight());
            } else {
                eraserPopupWindow.showAsDropDown(anchor, ScreenUtils.dip2px(context, -pupWindowsDPWidth), -anchor.getHeight());
            }
        } else {
            if (drawMode == StrokeRecord.STROKE_TYPE_DRAW) {
//                strokePopupWindow.showAsDropDown(anchor, 0, ScreenUtils.dip2px(activity, -strokePupWindowsDPHeight) - anchor.getHeight());
                strokePopupWindow.showAsDropDown(anchor, 0, 0);
            } else {
//                eraserPopupWindow.showAsDropDown(anchor, 0, ScreenUtils.dip2px(activity, -eraserPupWindowsDPHeight) - anchor.getHeight());
                eraserPopupWindow.showAsDropDown(anchor, 0, 0);
            }
        }
    }

    private Bitmap getThumbBitmap( View view ){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = null;
        try{
            if( null != view.getDrawingCache( ) ){
                bitmap = Bitmap.createScaledBitmap( view.getDrawingCache( ), 256, 192, false );
            }
        }catch( OutOfMemoryError e ){
            e.printStackTrace( );
        }finally{
            view.setDrawingCacheEnabled( false );
            view.destroyDrawingCache( );
        }
        return bitmap;
    }

    private Bitmap getViewBitmap(View view){
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //上面2行必须加入，如果不加如view.getDrawingCache()返回null
        Bitmap bitmap = view.getDrawingCache();
        return BitmapUtils.createBitmapThumbnail(bitmap, true, ScreenUtils.dip2px(context, 200), ScreenUtils.dip2px(context, 200));
    }

    private class WbMenuClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_wb_draw) {
                strokeType = StrokeRecord.STROKE_TYPE_DRAW;
                curWhiteBoardView.setStrokeType(strokeType);
                curWhiteBoardView.setEditMode(WhiteBoardView.EDIT_STROKE);
                showCurSelected(id);
            } else if (id == R.id.iv_wb_settings) {
                if (curWhiteBoardView.getEditMode() == WhiteBoardView.EDIT_STROKE && curWhiteBoardView.getStrokeType() != StrokeRecord.STROKE_TYPE_ERASER) {
                    showParamsPopupWindow(iv_Location, StrokeRecord.STROKE_TYPE_DRAW);
                } else {
                    int checkedId = strokeTypeRG.getCheckedRadioButtonId();
                    if (checkedId == R.id.stroke_type_rbtn_draw) {
                        strokeType = StrokeRecord.STROKE_TYPE_DRAW;
                    } else if (checkedId == R.id.stroke_type_rbtn_line) {
                        strokeType = StrokeRecord.STROKE_TYPE_LINE;
                    } else if (checkedId == R.id.stroke_type_rbtn_circle) {
                        strokeType = StrokeRecord.STROKE_TYPE_CIRCLE;
                    } else if (checkedId == R.id.stroke_type_rbtn_rectangle) {
                        strokeType = StrokeRecord.STROKE_TYPE_RECTANGLE;
                    } else if (checkedId == R.id.stroke_type_rbtn_text) {
                        strokeType = StrokeRecord.STROKE_TYPE_TEXT;
                    }
                    curWhiteBoardView.setStrokeType(strokeType);
                }
                curWhiteBoardView.setEditMode(WhiteBoardView.EDIT_STROKE);
                //showCurSelected(id);
            } else if (id == R.id.iv_wb_eraser) {
                if (curWhiteBoardView.getEditMode() == WhiteBoardView.EDIT_STROKE && curWhiteBoardView.getStrokeType() == StrokeRecord.STROKE_TYPE_ERASER) {
                    showParamsPopupWindow(iv_Location, StrokeRecord.STROKE_TYPE_ERASER);
                } else {
                    curWhiteBoardView.setStrokeType(StrokeRecord.STROKE_TYPE_ERASER);
                }
                curWhiteBoardView.setEditMode(WhiteBoardView.EDIT_STROKE);
                showCurSelected(id);
            }else if (id == R.id.iv_wb_catch) {
                curWhiteBoardView.setStrokeType(StrokeRecord.STROKE_TYPE_TRANSLATE);
                curWhiteBoardView.setEditMode(WhiteBoardView.EDIT_STROKE);
                showCurSelected(id);
            }else if (id == R.id.iv_wb_text) {
                curWhiteBoardView.setStrokeType(StrokeRecord.STROKE_TYPE_TEXT);
                curWhiteBoardView.setEditMode(WhiteBoardView.EDIT_STROKE);
                showCurSelected(id);
            }
        }
    }

    private void showCurSelected(int id) {
        if(id == R.id.iv_wb_draw){
            iv_wb_draw.setBackgroundResource(R.drawable.wb_action_draw_selected);
        }else{
            iv_wb_draw.setBackgroundResource(R.drawable.wb_action_draw_normal);
        }
        if(id == R.id.iv_wb_eraser){
            iv_wb_eraser.setBackgroundResource(R.drawable.wb_action_eraser_selected);
        }else{
            iv_wb_eraser.setBackgroundResource(R.drawable.wb_action_eraser_normal);
        }

        if(id == R.id.iv_wb_text){
            iv_wb_text.setBackgroundResource(R.drawable.wb_action_text_selected);
        }else{
            iv_wb_text.setBackgroundResource(R.drawable.wb_action_text_normal);
        }

        if(id == R.id.iv_wb_catch){
            iv_wb_catch.setBackgroundResource(R.drawable.wb_action_catch_selected);
        }else{
            iv_wb_catch.setBackgroundResource(R.drawable.wb_action_catch_normal);
        }

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
                case MemberOper.OpenWhiteBoard:
                    origin.setWhiteBoardView(avMember.getWhiteBoardView());
                    origin.setWhiteBoardOpened(true);
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
                case MemberOper.CloseWhiteBoard:
                    origin.setWhiteBoardView(null);
                    origin.setWhiteBoardOpened(false);
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
                avChannel.setOnWhiteBoardListener(new WhiteBoardListener());
                avChannel.join();//加入频道
                memberNotice(memberIds);
                if(callType.equals(EnumManage.AvCallType.video.toString())){
                    handler.sendEmptyMessage(Oper.OpenVideo);
                    handler.sendEmptyMessage(Oper.OpenAudio);
                    handler.sendEmptyMessage(Oper.OpenWhiteBoard);
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
        if(currentUser == null){
            return;
        }
        avClient = new AVClient(currentUser.getEntity().getId(),currentUser.getEntity().getName(), currentUser.getEntity().getAvatarUrl(), currentUser.getImToken().getAccessToken());
        avClient.getChannel(channelId, new AVBack.Result<Channel>() {
            @Override
            public void onSuccess(Channel channel) {
                avChannel = channel;
                avChannel.setOnMembersChangedListener(new MembersChangedListener());
                avChannel.setOnVideoListener(new VideoListener());
                avChannel.setOnAudioListener(new AudioListener());
                avChannel.setOnScreenListener(new ScreenListener());
                avChannel.setOnWhiteBoardListener(new WhiteBoardListener());
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

    private class WhiteBoardListener implements Channel.OnWhiteBoardListener{

        @Override
        public void onOpen(AVMember member) {
            memberAdd(member,MemberOper.OpenWhiteBoard);
            handler.sendEmptyMessage(Oper.OpenWhiteBoardOk);
        }

        @Override
        public void onClose(AVMember member) {
            memberAdd(member,MemberOper.CloseWhiteBoard);
            handler.sendEmptyMessage(Oper.CloseWhiteBoard);
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
