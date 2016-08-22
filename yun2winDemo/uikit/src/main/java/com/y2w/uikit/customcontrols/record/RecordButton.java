package com.y2w.uikit.customcontrols.record;


import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

/**
 * 录音控件，样式可根据需要重写
 */
public class RecordButton extends Button implements OnTouchListener{


	OnRecordListener onRecordListener;
	RecordUtil util;
	private boolean isCancel = false;
	private static final int MIN_INTERVAL_TIME = 2000; // 录音最短时间
	private static final int MAX_INTERVAL_TIME = 60000; // 录音最长时间

	public static final int RECORD_NORMAL = 1;
	public static final int RECORD_TIMEOUT = 0;
	public static final int RECORD_LESS_THAN_MIN = -1;
	public static final int RECORD_CANCEL = 2;
	
	private long mStartTime;
	long stopTime;

	public interface OnRecordListener {
		void onRecordStart();
		void onCancel();
		void onSuccess(String path, int code);
		void onError(String msg);
		void onVolumeChange(int volume);
	}

	
	public boolean isCancel() {
		return isCancel;
	}

	public void setOnRecordListener(OnRecordListener onRecordListener) {
		this.onRecordListener = onRecordListener;
	}

	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.util = RecordUtil.getSigleton();
	}

	// 取消录音
//	private void cancelRecord() {
//		
//	}



	ObtainDecibelThread thread;
	// 开始录音
	private void startRecording() {
		try {
			onRecordListener.onRecordStart();//开始回调
			mStartTime=System.currentTimeMillis();
			util.recordAudio();
			thread=new ObtainDecibelThread();
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
			onRecordListener.onError("录音出错");
		}
	}

	// 停止录音
	public void stopRecording() {
		if(util != null)
		util.stopRecord();
	}

	boolean isTimeOut = false;
	private class ObtainDecibelThread extends Thread {
        private volatile boolean running = true;

        public void exit() {
            running = false;
        }
        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if ((stopTime=System.currentTimeMillis()) - mStartTime >= MAX_INTERVAL_TIME) {
                    // 如果超过最长录音时间
                	mHandler.sendEmptyMessage(-1);
                	exit();
                }
                if (util != null && running) {
                    // 如果用户仍在录音
                    int volumn = util.getVolumn();
                    mHandler.sendEmptyMessage(volumn);
                }
            }
        }
    }
	
	
	Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			int what=msg.what;
			if(what==-1){//录音超时
				stopRecording();
				isTimeOut=true;
				onRecordListener.onSuccess(util.getmAudioPath(),RECORD_TIMEOUT);
			}else{//音量
				onRecordListener.onVolumeChange(what);
			}
		};
	};


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.onTouchEvent(event);
		return true;
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:		
				startRecording();
				break;
			case MotionEvent.ACTION_MOVE:
				if(event.getY()<-200 && util.ismIsRecording()){
					isCancel=true;
					onRecordListener.onCancel();
					return true;
				}else{
					isCancel=false;
				}
				break;
			case MotionEvent.ACTION_UP:
				if(!isTimeOut){
					if(thread!=null && thread.isAlive()){
						thread.exit();
					}
					stopRecording();
					if(isCancel){
						isCancel=false;
						onRecordListener.onSuccess(util.getmAudioPath(),RECORD_CANCEL);
					}else{
						long count = stopTime - mStartTime;
						if(count <= MIN_INTERVAL_TIME){
							onRecordListener.onSuccess(util.getmAudioPath(),RECORD_LESS_THAN_MIN);
			            }else{
							onRecordListener.onSuccess(util.getmAudioPath(),RECORD_NORMAL);
			            }
					}
				}
				isTimeOut = false;
				break;
			case MotionEvent.ACTION_CANCEL:
				long count = stopTime - mStartTime;
				if(count <= MIN_INTERVAL_TIME){
					onRecordListener.onSuccess(util.getmAudioPath(),RECORD_LESS_THAN_MIN);
				}
				break;
		}
		return true;
	}

	
	
	
	
}
