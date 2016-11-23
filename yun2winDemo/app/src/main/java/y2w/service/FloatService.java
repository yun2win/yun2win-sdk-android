package y2w.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.WindowManager;

import y2w.base.AppContext;

public class FloatService extends Service {
    public static int userId = 0;

    /**
     * 用户状态
     */
    public class userState{
        public boolean audio;
        public boolean video;
        public String name;

        public userState(boolean Audio,boolean Video,String name){
            this.audio = Audio;
            this.video = Video;
            this.name = name;
        }
        public void setName(String name){
            this.name = name;
        }
    }

    public FloatService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
//        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.TOP | Gravity.RIGHT;
        wmParams.x = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        wmParams.y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        wmParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        wmParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());
        wmParams.format = PixelFormat.TRANSPARENT;

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 声网接口回调
     */
//    class MessageHandler extends BaseEngineEventHandler {
//        @Override
//        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
//            AVCallActivity.videoViewHashMap.put(0,new AVCallActivity.userState(true,true,"自己"));
//        }
//
//        @Override
//        public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
//            AVCallActivity.videoViewHashMap.clear();
//            stopSelf();
//        }
//
//        @Override
//        public void onUserJoined(final int uid, int elapsed) {
//            stopPlay();
//            videoViewHashMap.put(uid,new userState(true,false,uid+""));
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    addRemoteView(uid);
//                    refreshVideoViews(uid);
//                    changViewOfRemoteJoin();
//                }
//            });
//        }
//
//        @Override
//        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
//            userState state = videoViewHashMap.get(uid);
//            state.video = true;
//            state.audio = true;
//            rtcEngine.setupRemoteVideo(new VideoCanvas(findSurfaceViewByTag(uid), VideoCanvas.RENDER_MODE_ADAPTIVE,uid));
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    refreshVideoViews(uid);
//                    //个人呼叫，接通后画面转换
//                    if(!chatType.equals(EnumManage.SessionType.group.toString()) && (int)mLocalView.getTag() == 0){
//                        exchangVideoView(uid,0);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onUserOffline(final int uid) {
//            if(isFinishing()){
//                return;
//            }
//            if(ll_av_member==null){
//                return;
//            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    removeRemoteVideo(uid);
//                    if (ll_av_member.getChildCount() == 0) {
//                        avClose();
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onUserMuteAudio(final int uid, boolean muted) {
//            userState state = videoViewHashMap.get(uid);
//            state.audio = true;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    refreshVideoViews(uid);
//                }
//            });
//        }
//
//        @Override
//        public void onUserMuteVideo(final int uid, final boolean muted) {
//            if(isFinishing()){
//                return;
//            }
//            if(ll_av_member==null){
//                return;
//            }
//            rtcEngine.muteRemoteVideoStream(uid,muted);
//            userState state = videoViewHashMap.get(uid);
//            state.video = !muted;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    refreshVideoViews(uid);
//                }
//            });
//        }
//
//        @Override
//        public void onCameraReady() {
//
//        }
//
//        @Override
//        public void onError(int err) {
//
//        }
//    }
}
