package com.y2w.uikit.customcontrols.record;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.y2w.uikit.utils.StringUtil;

import java.io.File;
import java.io.IOException;

public class RecordUtil {
	
    public static final String AUDOI_DIR = Environment.getExternalStorageDirectory()
    		.getAbsolutePath() + "/y2w/voice/"; // 录音音频保存根路径

    //private String mAudioPath = AUDOI_DIR+"/"+System.currentTimeMillis()+".amr"; // 要播放的声音的路径
    
    private String mAudioPath;
    
	private boolean mIsRecording = false;// 是否正在录音
	
    private MediaRecorder mRecorder;
    
    public String getmAudioPath() {
		return mAudioPath;
	}
    
    

	public boolean ismIsRecording() {
		return mIsRecording;
	}



	public void setmAudioPath(String mAudioPath) {
		this.mAudioPath = mAudioPath;
	}
    
    private RecordUtil(){
    	File file=new File(AUDOI_DIR);
    	if(!file.exists()){
    		file.mkdirs();
    	}
    }
    private static RecordUtil util=new RecordUtil();
    
    public static RecordUtil getSigleton(){
		return util;
    }
    
    // 初始化 录音器
    private void initRecorder() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        if(StringUtil.isEmpty(mAudioPath) || new File(mAudioPath).exists()){
			mAudioPath=AUDOI_DIR+System.currentTimeMillis()+".aac";
		}
        mRecorder.setOutputFile(mAudioPath);
        //mRecorder.get
        mIsRecording = true;
    }

    /** 开始录音，并保存到文件中 
     * @throws IOException 
     * @throws IllegalStateException 
     * */
    public void recordAudio() throws IllegalStateException, IOException {
        initRecorder(); 
        mRecorder.prepare();
        mRecorder.start();
       
    }

    /** 获取音量值，只是针对录音音量 */
    public int getVolumn() {
        int volumn = 0;
        // 录音
       
        try{
	        if (mRecorder != null && mIsRecording) {
            volumn = mRecorder.getMaxAmplitude() / 2700;
               Log.i("recordutil", "--volume =" + mRecorder.getMaxAmplitude(), null);
//            if (volumn != 0)
//                volumn = (int) (10 * Math.log(volumn) / Math.log(10)) / 2700;
	        }
        }catch(Exception e){
        	
        }
        return volumn;
    }

    /** 停止录音 */
    public void stopRecord() {
    	try{
    		if (mRecorder != null && mIsRecording) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                mIsRecording = false;
            }
    	}catch(Exception e){
    	}
        
    }
    
    private MediaPlayer mPlayer;
    private boolean mIsPlaying;// 是否正在播放
    
    public void startPlay(String audioPath , final OnPlayListener listener) {
  
        if (!mIsPlaying) {
            if (!StringUtil.isEmpty(audioPath)) {
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(audioPath);
                    mPlayer.setVolume(0.7f, 0.7f);
                    mPlayer.prepare();
                    mPlayer.start();
                    if (listener != null) {
                        listener.starPlay();
                    }
                    mIsPlaying = true;
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (listener != null) {
                            	listener.stopPlay();
                            }
                            mp.release();
                            mPlayer = null;
                            mIsPlaying = false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //AppContext.showToastShort(R.string.record_sound_notfound);
            }
        } // end playing
    }
    
    public void stopPlay(){
    	try{
    		if(mIsPlaying){
        		mPlayer.release();
        		mPlayer = null;
                mIsPlaying = false;
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
    
    public interface OnPlayListener {
        /** 播放声音结束时调用 */
        void stopPlay();

        /**  播放声音开始时调用 */
        void starPlay();
    }
    
    /**
     * 为了防止一些杀毒软件弹出权限窗口，可先调用此方法
     */
    public void crossIntercept(){
    	try {
			recordAudio();
			stopRecord();
			File file=new File(getmAudioPath());
			file.deleteOnExit();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

    
}
